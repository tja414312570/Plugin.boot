package com.YaNan.framework.boot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.utils.StringUtil;
import com.YaNan.frame.utils.resource.AbstractResourceEntry;
import com.YaNan.frame.utils.resource.ResourceManager;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Plugin 引导服务
 * 
 * @author yanan
 *
 */
public class PluginBootServer {
	static Logger log = LoggerFactory.getLogger(PluginBootServer.class);
	/**
	 * 运行服务
	 * 
	 * @param configure
	 * @throws LifecycleException
	 */
	public static void run(String... args) {
		log.info("Plugin Boot Snapshot Version!");
		printLogo();
		long startTime = System.currentTimeMillis();
		//获取主类路径
		String mainClassPath = ResourceManager.classPath();
		log.info("Application main class path : "+mainClassPath);
		//获取主类
		Class<?> mainClass = deduceMainClass();
		log.info("Application main class : "+mainClass);
		//获取环境
		Environment environment = Environment.getEnviroment();
		//设置主类路径，主类到环境
		environment.setVariable(Environment.MAIN_CLASS, mainClass);
		environment.setVariable(Environment.MAIN_CLASS_PATH, mainClassPath);
		//设置一个默认的全局配置
		Map<String,String> globalConfigureMap = new HashMap<String,String>();
		globalConfigureMap.put("-boot-server", "plugin-boot");
		globalConfigureMap.put("-boot-server-version", "0.0.1-snapshot");
		Config globalConfig = ConfigFactory.parseMap(globalConfigureMap);
		environment.mergeConfig(globalConfig);
		//解析参数
		resolverBootArguments(args,environment,mainClass);
		//获取基本配置
		PluginBoot pluginBoot = mainClass.getAnnotation(PluginBoot.class);
		addPluginContext(mainClass, pluginBoot);
		//获取其它配置注释
		EnvironmentBoot envoronmentBoot = dedudeEnvironment(environment);
		//加载组件工厂
//		PlugsFactory facaoty = PlugsFactory.getInstance();
		//加载配置
		envoronmentBoot.start(environment);
		//初始化环境
		
		//引导启动
		long times = System.currentTimeMillis()-startTime;
		log.info("Application started at "+times+"[ms]");
	}
	private static void printLogo() {
		log.debug("");
		log.debug("       PLUGINPLUGID     PLUE         PLUA       PLUR      PLUGINPLUGIY    PLUGINPLUGIN   PLUG         PLUG");
		log.debug("      PLUG      PLUW   PLUA         PLUN       PLUG    PLUG                  PLUG       PLUGPLUG     PLUG");
		log.debug("     PLUG      PLUH   PLUO         PLUN       PLUG   PLUG                   PLUG       PLUG PLUG    PLUG");
		log.debug("    PLUG      PLUY   PLUA         PLUN       PLUG   PLUG                   PLUG       PLUG  PLUG   PLUG");
		log.debug("   PLUGPLUGPLUGI    PLUG         PLUG       PLUG   PLUG      PLUGINP      PLUG       PLUG   PLUG  PLUG");
		log.debug("  PLUN             PLUE         PLUV       PLUE   PLUR         PLUG      PLUG       PLUG    PLUG PLUG");
		log.debug(" PLUG             PLUI         PLUV       PLUE     PLUU       PLUP      PLUG       PLUG     PLUGINPL");
		log.debug("PLUY             PLUGINPLUGO   PLUGINPLUGINU        PLUGINPLUGIG    PLUGINPLUGIE  PLUG         PLUG");
		log.debug("");
	}
	//获取环境引导
	private static EnvironmentBoot dedudeEnvironment(Environment environment) {
		EnvironmentBoot enviromentBoot = null;
		Class<?> enviromentBootClass = null;
		//从参数获取引导
		String environmentBootArg = environment.getVariable("-environment-boot");
		if(environmentBootArg != null) {
			try {
				enviromentBootClass = Class.forName(environmentBootArg);
			} catch (ClassNotFoundException e) {
				throw new PluginBootException("failed to load environment boot class:"+environmentBootArg,e);
			}
		}else {
			try {
				enviromentBootClass = Class.forName("com.YaNan.framework.boot.web.WebEnvironmentBoot");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				enviromentBootClass = StandEnvironmentBoot.class;
			}
		}
		log.info("Plugin enviroment boot:"+enviromentBootClass.getName());
		try {
			enviromentBoot = (EnvironmentBoot) enviromentBootClass.newInstance();
		} catch (Exception e) {
			throw new PluginBootException("failed to instance environment boot class:"+enviromentBootClass,e);
		}
		return enviromentBoot;
	}
	//将引导参数解析到环境中
	private static void resolverBootArguments(String[] args, Environment environment,Class<?> mainClass) {
		if(mainClass != null) {
			BootArgs[] bootArgs = mainClass.getAnnotationsByType(BootArgs.class);
			for(BootArgs bootArg : bootArgs) {
				environment.setVariable(bootArg.name(),bootArg.value());
			}
		}
		for(String arg : args) {
			String[] argArray = arg.split("=");
			environment.setVariable(argArray[0],argArray.length>1? argArray[1]:null);
		}
		AbstractResourceEntry resource;
		String disableConfig = environment.getVariable("-boot-disabled");
		if(StringUtil.isNotBlank(disableConfig)) {
			environment.removeVariable(disableConfig.split(","));
		}
		String bootYc = environment.getVariable("-boot-configure");
		if(!StringUtil.isEmpty(bootYc)) {
			resource = ResourceManager.getResource(bootYc);
			if(resource == null)
				resource = ResourceManager.getResource("classpath:"+bootYc);
			if(resource == null)
				throw new PluginBootException("Could not found config resource :"+bootYc);
		}else {
			resource = ResourceManager.getResource("classpath:boot.yc");
		}
		if(resource != null) {
			InputStream inputStream = null;
			Reader reader = null;
			try {
				inputStream = resource.getInputStream();
				reader = new InputStreamReader(resource.getInputStream());
				Config config = ConfigFactory.parseReader(reader);
				environment.mergeConfig(config);
				config.entrySet().forEach(entry->{
					environment.setVariable(entry.getKey(), entry.getValue().unwrapped());
				});;
			}finally {
				if(reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				if(inputStream != null)
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			disableConfig = environment.getVariable("-boot-disabled");
			if(StringUtil.isNotBlank(disableConfig)) {
				environment.removeVariable(disableConfig.split(","));
			}
		}
	}
	private static Class<?> deduceMainClass() {
		StackTraceElement[] stacks = new RuntimeException().getStackTrace();
		Class<?> mainClass = null;
		for(StackTraceElement stack : stacks) {
			if(StringUtil.equals(stack.getMethodName(), "main")) {
				try {
					mainClass = Class.forName(stack.getClassName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return mainClass;
	}
	/**
     * 添加Plugin的扫描上下文
     * @param configure
     * @param pluginBoot
     */
	public static void addPluginContext(Class<?> configure, PluginBoot pluginBoot) {
		if (pluginBoot == null || pluginBoot.contextClass().length == 0) {
			// 将class文件上下文添加到Plug中
			PlugsFactory.getInstance().addScanPath(configure);
			log.info("Plugin Application Context Path "+Arrays.toString(ResourceManager.getClassPath(configure)));
		} else {
			PlugsFactory.getInstance().addScanPath(pluginBoot.contextClass());
			log.info("Plugin Application Context Path "+Arrays.toString(ResourceManager.getClassPath(pluginBoot.contextClass())));
		}
		PlugsFactory.init("classpath:boot.yc");
	}
}
