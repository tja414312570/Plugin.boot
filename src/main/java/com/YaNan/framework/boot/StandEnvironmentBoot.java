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
import com.YaNan.frame.utils.resource.AbstractResourceEntry;
import com.YaNan.frame.utils.resource.ResourceManager;
import com.YaNan.frame.utils.resource.ResourceNotFoundException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValueType;

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
	private void loadModelFromResource(AbstractResourceEntry are,Environment environment) {
		if(are == null)
			throw new IllegalArgumentException("resource is not null");
		log.info("loaded plugin from "+are.getPath());
		InputStream inputStream = are.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		Config modelConfig = ConfigFactory.parseReader(inputStreamReader);
		modelConfig.allowKeyNull();
		loadEnvironmentProperties(environment,modelConfig);
		ConfigList configList = modelConfig.getList("plugins");
		if(configList != null) {
			configList.forEach(config->{
				PlugsFactory.getInstance().addPlugByConfig(config.valueType()==ConfigValueType.STRING?config.unwrapped():config);
			});
		}
		PlugsFactory.getInstance().init0();
		environment.getConfigure().merge(modelConfig);
	}
	@Override
	public void start(Environment environment) {
		long now = System.currentTimeMillis();
		loadEnvironmentProperties(environment);
		log.debug("environment properties loaded at ["+(System.currentTimeMillis()-now)+" ms]");
		now = System.currentTimeMillis();
		loadModelPlugin(environment);
		log.debug("environment model loaded at ["+(System.currentTimeMillis()-now)+" ms]");
	}

	@Override
	public void stop(Environment environment) {
		
	}


}
