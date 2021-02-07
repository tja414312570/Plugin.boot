package com.yanan.framework.boot.cloud;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.SimpleConfigObject;
import com.yanan.framework.boot.PluginBootException;
import com.yanan.framework.boot.StandEnvironmentBoot;
import com.yanan.framework.plugin.Environment;
import com.yanan.framework.plugin.Plugin;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.builder.PluginDefinitionBuilderFactory;
import com.yanan.framework.plugin.definition.RegisterDefinition;

/**
 * 抽象可更新引导配置引导环境
 * 提供给云配置引导等场景等可能会更改配置的场景
 * @author yanan
 */
public class AbstractUpdateabledEnvironmentBoot extends StandEnvironmentBoot{
	protected Set<RegisterDefinition> loadedRegisterList ;
	protected Environment environment;
	public AbstractUpdateabledEnvironmentBoot() {
		environment = Environment.getEnviroment();
		loadedRegisterList = Collections.synchronizedSet(new HashSet<>());
	}
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
				RegisterDefinition registerDefinition = (RegisterDefinition) plugin;
				PlugsFactory.getInstance().addRegisterDefinition(registerDefinition);
				loadedRegisterList.add(registerDefinition);
			}
		} catch (Exception e) {
			throw new PluginBootException("failed to builder definition ! "+configValue,e);
		}
	}
	public void loadModelPlugin(final Config config) {
		if(config == null)
			throw new IllegalArgumentException("config is null");
		if(!loadedRegisterList.isEmpty()) {
			loadedRegisterList.removeIf(registerDefinition->{
				PlugsFactory.getInstance().removeRegister(registerDefinition);
				return true;
			});
		}
		config.allowKeyNull();
		loadEnvironmentProperties(environment,config);
		ConfigList configList = config.getList("plugins");
		if(configList != null) {
			configList.forEach(plugConfig->{
				tryDeducePluginDefinitionAndAddDefinition(plugConfig);
				PlugsFactory.getInstance().refresh();
			});
		}
		environment.mergeConfig(config);
	}
}