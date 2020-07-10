package com.YaNan.framework.boot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.autowired.property.PropertyManager;
import com.YaNan.frame.utils.reflect.cache.ClassHelper;
import com.YaNan.frame.utils.resource.AbstractResourceEntry;
import com.YaNan.frame.utils.resource.Resource;
import com.YaNan.frame.utils.resource.ResourceManager;
import com.YaNan.frame.utils.resource.ResourceNotFoundException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.SimpleConfigObject;

/**
 * 标准环境引导
 * 用于支持非Web服务器
 * @author yanan
 *
 */
public class StandEnvironmentBoot implements EnvironmentBoot{
	static Logger log = LoggerFactory.getLogger(StandEnvironmentBoot.class);
	public void loadModelPlugin(Environment environment) {
		Config config = environment.getConfigure();
		config.allowKeyNull();
		ConfigList configList = config.getList("plugins");
		if(configList != null) {
			configList.forEach(pConfig->{
				PlugsFactory.getInstance().addPlugByConfig(pConfig.valueType()==ConfigValueType.STRING?pConfig.unwrapped():pConfig);
			});
		}
		List<String> includeList = config.getStringList("includes");
		for(String includePath : includeList) {
			List<AbstractResourceEntry> ares = ResourceManager.getResourceList(includePath);
			if(ares == null || ares.isEmpty()) {
				ares = ResourceManager.getResourceList("classpath:"+includePath);
			}
			if(ares == null)
				throw new ResourceNotFoundException("colud not found resource at "+includePath);
			for(AbstractResourceEntry resourceEntry : ares) {
				loadModelFromResource(resourceEntry,environment);
			}
		
		}
	}
	public void loadEnvironmentProperties(Environment environment,Config config) {
		List<String> includeList = config.getStringList("properties");
		for(String includePath : includeList) {
			List<AbstractResourceEntry> ares = ResourceManager.getResourceList(includePath);
			if(ares == null || ares.isEmpty()) {
				ares = ResourceManager.getResourceList("classpath:"+includePath);
			}
			if(ares == null)
				throw new ResourceNotFoundException("colud not found resource at "+includePath);
//			loadModelFromResource(are,environment);
			for(AbstractResourceEntry resourceEntry : ares) {
				Properties properties = new Properties();
				try {
					log.info("loaded properties from "+resourceEntry.getPath());
					properties.load(resourceEntry.getInputStream());
					Config propertiesConfig = ConfigFactory.parseProperties(properties);
					environment.getConfigure().merge(propertiesConfig);
					PropertyManager.getInstance().put(properties);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void loadEnvironmentProperties(Environment environment) {
		Config config = environment.getConfigure();
		config.allowKeyNull();
		loadEnvironmentProperties(environment,config);
	}
	public void loadModelFromResource(final Resource resource,Environment environment) {
		if(resource == null)
			throw new IllegalArgumentException("resource is null");
		log.info("loaded plugin from "+resource.getPath());
		InputStream inputStream;
		try {
			inputStream = resource.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			Config modelConfig = ConfigFactory.parseReader(inputStreamReader);
			environment.mergeConfig(modelConfig);
			modelConfig.allowKeyNull();
			loadEnvironmentProperties(environment,modelConfig);
			ConfigList configList = modelConfig.getList("plugins");
			if(configList != null) {
				configList.forEach(config->{
					PlugsFactory.getInstance().addPlugByConfig(config.valueType()==ConfigValueType.STRING?config.unwrapped():config);
				});
			}
			PlugsFactory.getInstance().init0();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void loadModelFromConfig(final Config config,Environment environment,boolean removeOriginPlugs) {
		if(config == null)
			throw new IllegalArgumentException("config is null");
		config.allowKeyNull();
		loadEnvironmentProperties(environment,config);
		ConfigList configList = config.getList("plugins");
		if(configList != null) {
			configList.forEach(plugConfig->{
				if(removeOriginPlugs) {
					String className = null;
					if(plugConfig.valueType()==ConfigValueType.STRING) {
						className = (String) plugConfig.unwrapped();
					}else {
						Config conf = ((SimpleConfigObject) plugConfig).toConfig();
						className = conf.getString("class");
					}
					try {
						log.info("remove plugin class :"+className);
						ClassHelper clazz = ClassHelper.getClassHelper(className);
						PlugsFactory.getInstance().removeRegister(clazz.getCacheClass());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				PlugsFactory.getInstance().addPlugByConfig(plugConfig.valueType()==ConfigValueType.STRING?plugConfig.unwrapped():plugConfig);
			});
		}
		PlugsFactory.getInstance().init0();
		environment.getConfigure().merge(config);
	}
	@Override
	public void start(Environment environment) {
		environment.setVariable("-environment-boot-instance", this);
		long now = System.currentTimeMillis();
		loadEnvironmentProperties(environment);
		log.info("environment properties loaded at ["+(System.currentTimeMillis()-now)+" ms]");
		now = System.currentTimeMillis();
		loadModelPlugin(environment);
		log.info("environment model loaded at ["+(System.currentTimeMillis()-now)+" ms]");
	}

	@Override
	public void stop(Environment environment) {
		
	}


}
