package com.yanan.framework.boot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.SimpleConfigObject;
import com.yanan.framework.plugin.Environment;
import com.yanan.framework.plugin.Plugin;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.autowired.property.PropertyManager;
import com.yanan.framework.plugin.builder.PluginDefinitionBuilderFactory;
import com.yanan.framework.plugin.decoder.ResourceDecoder;
import com.yanan.framework.plugin.definition.RegisterDefinition;
import com.yanan.utils.reflect.TypeToken;
import com.yanan.utils.reflect.cache.ClassHelper;
import com.yanan.utils.resource.Resource;
import com.yanan.utils.resource.ResourceManager;
import com.yanan.utils.resource.ResourceNotFoundException;

/**
 * 标准环境引导
 * 用于支持非Web服务器
 * @author yanan
 *
 */
public class StandEnvironmentBoot implements EnvironmentBoot{
	Logger log = LoggerFactory.getLogger(StandEnvironmentBoot.class);
	public void tryDeducePluginDefinitionAndAddDefinition(ConfigValue configValue) {
		try {
			if (configValue == null)
				throw new PluginBootException("conf is null");
			Object plugin = null;
			if (configValue.valueType() == ConfigValueType.STRING) {
				Class<?> clzz = Class.forName((String) configValue.unwrapped());
				plugin = PluginDefinitionBuilderFactory.builderPluginDefinitionAuto(clzz);
			} else if (configValue.valueType() == ConfigValueType.OBJECT) {
				plugin = PluginDefinitionBuilderFactory.buildRegisterDefinitionByConfig(((SimpleConfigObject) configValue).toConfig());
			}
			if(Objects.equals(plugin.getClass(), Plugin.class)) {
				PlugsFactory.getInstance().addPlugininDefinition((Plugin) plugin);
			}else {
				PlugsFactory.getInstance().addRegisterDefinition((RegisterDefinition) plugin);
			}
		} catch (Exception e) {
			throw new PluginBootException("failed to builder definition ! "+configValue,e);
		}
	}
	public void loadModelPlugin(Environment environment) {
		Config config = environment.getConfigure();
		config.allowKeyNull();
		ConfigList configList = config.getList("plugins");
		if(configList != null) {
			configList.forEach(pConfig->{
				tryDeducePluginDefinitionAndAddDefinition(pConfig);
			});
		}
		PlugsFactory.getInstance().refresh();
		List<String> includeList = config.getStringList("includes");
		for(String includePath : includeList) {
			List<Resource> ares = ResourceManager.getResourceList(includePath);
			if(ares == null || ares.isEmpty()) {
				ares = ResourceManager.getResourceList("classpath:"+includePath);
			}
			if(ares == null)
				throw new ResourceNotFoundException("colud not found resource at "+includePath);
			for(Resource resourceEntry : ares) {
				ResourceDecoder<Resource> decoder = PlugsFactory.getPluginsInstanceByAttributeStrict(new TypeToken<ResourceDecoder<Resource>>() {}.getTypeClass()
						, resourceEntry.getClass().getSimpleName());
				decoder.decodeResource(PlugsFactory.getInstance(),resourceEntry);
				PlugsFactory.getInstance().refresh();
			}
		}
	}
	public void loadEnvironmentProperties(Environment environment,Config config) {
		List<String> includeList = config.getStringList("properties");
		for(String includePath : includeList) {
			List<Resource> ares = ResourceManager.getResourceList(includePath);
			if(ares == null || ares.isEmpty()) {
				ares = ResourceManager.getResourceList("classpath:"+includePath);
			}
			if(ares == null)
				throw new ResourceNotFoundException("colud not found resource at "+includePath);
//			loadModelFromResource(are,environment);
			for(Resource resourceEntry : ares) {
				Properties properties = new Properties();
				try {
					log.info("loaded properties from "+resourceEntry.getPath());
					properties.load(resourceEntry.getInputStream());
					Config propertiesConfig = ConfigFactory.parseProperties(properties);
					environment.mergeConfig(propertiesConfig);
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
					tryDeducePluginDefinitionAndAddDefinition(config);
				});
			}
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
				tryDeducePluginDefinitionAndAddDefinition(plugConfig);
				PlugsFactory.getInstance().refresh();
			});
		}
		environment.mergeConfig(config);
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