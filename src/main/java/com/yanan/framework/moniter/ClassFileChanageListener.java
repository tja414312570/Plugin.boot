package com.yanan.framework.moniter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.slf4j.Logger;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.plugin.builder.PluginDefinitionBuilderFactory;
import com.yanan.framework.plugin.definition.RegisterDefinition;
import com.yanan.utils.reflect.AppClassLoader;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.reflect.cache.ClassHelper;
import com.yanan.utils.resource.FileUtils;

@Register(attribute = "*.class")
public class ClassFileChanageListener implements PluginFileChanageListener {
	private Map<String, String> classNameCache = new HashMap<>();
	static Map<Object, Object> proxy = new HashMap<Object, Object>();// 用于存储映射关系
	Map<String, String> nameMappings = new HashMap<String, String>();// 用于存储已改变的类的与映射名的关系
	@Service
	private Logger logger;
	@Override
	public void onDelete(String fileName) {
//		String clzzName = getClassName(newFile.getAbsolutePath(), scanPath);
//		clzzName = clzzName.substring(0, clzzName.length() - 6);
//		Class<?> clzz = null;
//		try {
//			clzz = Class.forName(clzzName);
//			Class<?> cp = (Class<?>) proxy.get(clzz);
//			if (cp != null)
//				clzz = cp;
//			logger.debug(clzzName + "  remvoe success!"
//					+ (clzz == null ? "" : "proxy class:" + clzz.getName()));
//			proxy.remove(clzz);
//			notifyListener(clzz, null, null, null);// 通知删除
//		} catch (Throwable e) {
//			if (clzz != null)
//				logger.error("failed to update class " + clzz.getName(), e);
//			else
//				logger.error("failed to load class file " + newFile, e);
//		}
	}
	@Override
	public void onChange(File newFile,String scanPath) {
		String clzzName = getClassName(newFile.getAbsolutePath(), scanPath);
		try {
			Class<?> clzz = null;
			try {
				clzz = Class.forName(clzzName);
			} catch (Exception e) {
			}
			ClassLoader loader = clzz == null ? this.getClass().getClassLoader()
					: clzz.getClassLoader();
			if(clzz != null) {
					byte[] content = FileUtils.getBytes(newFile);
					String className = clzzName + "$PLUGIN_" + System.currentTimeMillis();
					// 无轮是否加载成功都应该记录
					Class<?> nc = loadClass(loader, className, clzzName, content);
					if (!checkClass(nc))
						return;
					RegisterDefinition registerDescription = PlugsFactory.getInstance()
							.getRegisterDefinition(clzz);
					if (registerDescription != null) {
						if (registerDescription.getLinkRegister() != null)
							clzz = registerDescription.getLinkRegister().getRegisterClass();
						//删除原容器
						RegisterDefinition newRegisterDefinition = PluginDefinitionBuilderFactory
								.builderRegisterDefinition(nc);
						PlugsFactory.getInstance().removeRegister(registerDescription);
						PlugsFactory.getInstance().addRegisterDefinition(newRegisterDefinition);
						PlugsFactory.getInstance().refresh();
					} else
						tryAddPlugs(nc);
					proxy.put(clzz, nc);
					notifyListener(clzz, nc, new AppClassLoader().loadClass(clzzName, content), newFile);// 通知修改
					logger.debug(clzzName + "  update success!");

			} else {// 如果之前没有加载该类
				Class<?> nc = clzz;
				if (nc == null)
					nc = loadClass(loader, clzzName, FileUtils.getBytes(newFile));
				if (!checkClass(nc))
					return;
				tryAddPlugs(nc);
				notifyListener(null, nc, null, newFile);// 通知添加
				logger.debug(clzzName + "  add success!");
			}
		} catch (Throwable e) {
			logger.error("failed to update class \"" + clzzName + "\"", e);
		}
	}
	private String getClassName(String fileName, String contextPath) {
		String className = classNameCache.get(fileName);
		if (className == null) {
			className = fileName.substring(contextPath.length(), fileName.length() - 6).replace(File.separator,
					".");
			classNameCache.put(fileName, className);
		}
		return className;
	}
	protected Class<?> loadClass(java.lang.ClassLoader loader, String className, String clzzName, byte[] content)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Class<?> nc = null;
		try {// 这一步为了容错
			nc = Class.forName(className);
		} catch (Throwable e) {
			ClassReader reader = new ClassReader(content, 0, content.length);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			nameMappings.put(clzzName.replace(".", "/"), className.replace(".", "/"));
			Remapper mapper = new SimpleRemapper(nameMappings);
			ClassVisitor remapper = new ClassRemapper(cw, mapper);
			reader.accept(remapper, ClassReader.SKIP_FRAMES);
			byte[] bytes = cw.toByteArray();
			nc = loadClass(loader, className, bytes);
		}
		return nc;
	}
	/**
	 * 加载类
	 * 
	 * @param loader    class loader
	 * @param className class name
	 * @param bytes     class bytes
	 * @return class
	 * @throws NoSuchMethodException     ex
	 * @throws SecurityException         ex
	 * @throws IllegalAccessException    ex
	 * @throws IllegalArgumentException  ex
	 * @throws InvocationTargetException ex
	 */
	protected Class<?> loadClass(java.lang.ClassLoader loader, String className, byte[] bytes)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = java.lang.ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class,
				int.class, int.class);
		method.setAccessible(true);
		Class<?> nc = (Class<?>) method.invoke(loader, className, bytes, 0, bytes.length);
		method.setAccessible(false);
		return nc;
	}
	protected boolean checkClass(Class<?> nc) {
		try {
			ClassHelper.getClassHelper(nc);
			return true;
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}

	/**
	 * 尝试添加组件
	 * 
	 * @param nc 新类
	 */
	protected void tryAddPlugs(Class<?> nc) {
		if (nc.getAnnotationsByType(Service.class) != null || nc.getAnnotation(Register.class) != null)
			PlugsFactory.getInstance().addPlugininDefinition(nc);
	}
	/**
	 * 通知类状态修改监听类
	 * 
	 * @param clzz class
	 * @param nc   new class
	 * @param oc   origin class
	 * @param file class file
	 */
	protected void notifyListener(Class<?> clzz, Class<?> nc, Class<?> oc, File file) {
		try {
			List<ClassUpdateListener> updaterList = PlugsFactory
					.getPluginsInstanceListByAttribute(ClassUpdateListener.class, nc.getName());
			for (ClassUpdateListener listener : updaterList) {
				if ((clzz != null && ReflectUtils.implementOf(clzz, ClassUpdateListener.class))
						|| (nc != null && ReflectUtils.implementOf(nc, ClassUpdateListener.class))
						|| (oc != null && ReflectUtils.implementOf(oc, ClassUpdateListener.class)))
					continue;
				listener.updateClass(clzz, nc, oc, file);
			}
		} catch (Throwable t) {
			if (nc != null)
				logger.error("can't found updater listener for class " + nc.getName(), t);
		}

	}
}
