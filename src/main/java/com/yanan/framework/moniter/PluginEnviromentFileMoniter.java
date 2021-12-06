package com.yanan.framework.moniter;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;

import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.plugin.autowired.enviroment.Variable;
import com.yanan.utils.resource.FileUtils;
import com.yanan.utils.resource.ResourceManager;
import com.yanan.utils.resource.scanner.Path;

/**
 * 文件监控
 * 
 * @author yanan
 */
@Register(layInit = false)
public class PluginEnviromentFileMoniter implements Runnable{
	public List<ContextPath> contextPathList = new ArrayList<ContextPath>();// 扫描上下文路劲
	@Service
	public Logger logger;
	private boolean first;// 是否已经扫描
	private List<String> scanner = new ArrayList<String>(100);// 用于存储已经扫描到的文件
	static Map<String, FileToken> fileToken = new HashMap<String, FileToken>(100);// 用于存文件hash
	static Map<Object, Object> proxy = new HashMap<Object, Object>();// 用于存储映射关系
	Map<String, String> nameMappings = new HashMap<String, String>();// 用于存储已改变的类的与映射名的关系
	@Variable(value="plugin.update",required = false)
	private boolean enable;
	
	Thread thread;
	@PostConstruct
	public void init() {
		if(enable && thread == null) {
			logger.info("enable plugin development file monitor server");
			thread = new Thread(this);
			thread.setDaemon(true);
			this.addContextPath(ResourceManager.classPaths());
			thread.start();
		}
	}

	/**
	 * 文件hash
	 * 
	 * @param file  类文件
	 * @param bytes 字节流
	 * @return hash值
	 */
	public static long hash(File file, byte[] bytes) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(bytes, 0, bytes.length);
			BigInteger bigInt = new BigInteger(1, md.digest());
			long h = bigInt.longValue();
			return h < 0l ? ~h + 1 : h;
		} catch (NoSuchAlgorithmException e) {
			return file.hashCode() + file.lastModified();
		}
	}
	public void record(File file) {
		fileToken.put(file.getAbsolutePath(), new FileToken(hash(file, FileUtils.getBytes(file)), file.lastModified()));
		scanner.add(file.getAbsolutePath());
	}
	public void found(File file,String scanPath) {
		if(file.isDirectory())
			return;
		if (!first) {
			record(file);
			return;
		}
		//判断文件是否存在
		FileToken token = fileToken.get(file.getAbsolutePath());
		//不存在为新增文件
		if(token == null) {
			List<PluginFileChanageListener> listenerList = PlugsFactory.getPluginsInstanceListByAttribute(PluginFileChanageListener.class,
					file.getAbsolutePath());
			for(PluginFileChanageListener listener : listenerList) {
				listener.onAdded(file, scanPath);
			}
		}else {
			//对比文件
			if(isChange(file,token)) {
				List<PluginFileChanageListener> listenerList = PlugsFactory.getPluginsInstanceListByAttribute(PluginFileChanageListener.class,
						file.getAbsolutePath());
				for(PluginFileChanageListener listener : listenerList) {
					listener.onChange(file, scanPath);
				}
			}
		}
		record(file);
	}
	
	private boolean isChange(File file, FileToken token) {
		return file.lastModified() != token.getLastModif();
	}

	@Override
	public void run() {
		final ContextPath currentContextPath = new ContextPath(null, null);
		BiConsumer<File,String> consumer = (item,path)->{
			try {
				found(item,path);
			}catch(Exception e) {
				logger.error("a error occur,sorry !",e);
			}
		};
		while (true) {
			if (this.contextPathList.isEmpty())
				break;
			scanner.clear();
			for (ContextPath contextPath : contextPathList) {
				Path path = new Path(contextPath.getContextPath());
				path.filter(contextPath.getFilter());
				currentContextPath.setContextPath(contextPath.getContextPath());
				currentContextPath.setFilter(contextPath.getFilter());
				path.scanner(file->consumer.accept(file,contextPath.contextPath));
				// 如果是第一次之后的扫描 需要将已删除的文件找出来
			}
			foundRemove();
			try {
				first = true;
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void foundRemove() {
		if (first && fileToken.size() != scanner.size()) {
			Iterator<Entry<String, FileToken>> iterator = fileToken.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, FileToken> entry = iterator.next();
				String fileName = entry.getKey();
				if (!scanner.contains(fileName)) {
					iterator.remove();
					List<PluginFileChanageListener> listenerList = PlugsFactory.getPluginsInstanceListByAttribute(PluginFileChanageListener.class,
							fileName);
					for(PluginFileChanageListener listener : listenerList) {
						listener.onDelete(fileName);
					}
				}
			}
			scanner.clear();
		}
	}

	private void addContextPath(String... contextPaths) {
		for (String contextPath : contextPaths) {
			if(contextPath.indexOf("!") != -1)
				continue;
			String context = contextPath;
			String filte = "**";
			int sp = context.indexOf(".", context.lastIndexOf("/"));
			if (sp > 0) {
				context = contextPath.substring(0, sp);
				filte = contextPath.substring(sp + 1);
				if (!filte.endsWith(".class")) {
					filte = filte.concat(".class");
				}
			}
			this.contextPathList.add(new ContextPath(context, filte));
		}
	}

	public static class ContextPath {
		public String getContextPath() {
			return contextPath;
		}

		public void setContextPath(String contextPath) {
			this.contextPath = contextPath;
		}

		public String getFilter() {
			return filter;
		}

		public void setFilter(String filter) {
			this.filter = filter;
		}

		public ContextPath(String contextPath, String filter) {
			super();
			this.contextPath = contextPath;
			this.filter = filter;
		}

		private String contextPath;
		private String filter;
	}
}