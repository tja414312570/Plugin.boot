package com.yanan.framework.boot.cloud.nacos;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.nacos.api.exception.NacosException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.yanan.frame.plugin.Environment;
import com.yanan.frame.plugin.autowired.property.PropertyManager;
import com.yanan.framework.boot.cloud.AbstractUpdateabledEnvironmentBoot;

/**
 * 云环境
 * @author yanan
 */
public class CloudEnvironmentBoot extends AbstractUpdateabledEnvironmentBoot{
	private NacosConfigRuntime nacosConfigRuntime;
	Logger log = LoggerFactory.getLogger(CloudEnvironmentBoot.class);
	public CloudEnvironmentBoot(NacosConfigRuntime nacosConfigRuntime) {
		log.info("plugin boot nacos cloud server!");
		long now = System.currentTimeMillis();
		this.nacosConfigRuntime = nacosConfigRuntime;
		environment = Environment.getEnviroment();
		//获取clouds配置
		Config globalConfig = environment.getConfigure();
		globalConfig.allowKeyNull();
		Config nacosCloudConfig = globalConfig.getConfig("clouds.nacos");
		if(nacosCloudConfig !=  null) {
			log.info("nacos cloud config:"+nacosCloudConfig);
			nacosCloudConfig.allowKeyNull();
			//获取属性配置
			Config propertiesConfig = nacosCloudConfig.getConfig("properties");
			if(propertiesConfig != null) {
				loadFromCloud(propertiesConfig,(groupId,dataId,cloudContent)->{
					log.debug("loaded properties ["+groupId+"]-["+dataId+"]:"+cloudContent.length());
					Reader reader = new StringReader(cloudContent);
					try {
						Properties properties = new Properties();
						properties.load(reader);
						Config cloudPropertiesConfig = ConfigFactory.parseProperties(properties);
						environment.getConfigure().merge(cloudPropertiesConfig);
						PropertyManager.getInstance().put(properties);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
			propertiesConfig = nacosCloudConfig.getConfig("config");
			if(propertiesConfig != null) {
				loadFromCloud(propertiesConfig,(groupId,dataId,cloudContent)->{
					log.debug("loaded config ["+groupId+"]-["+dataId+"]:"+cloudContent.length());
					Reader reader = new StringReader(cloudContent);
					Config cloudConfig = ConfigFactory.parseReader(reader);
					Environment.getEnviroment().mergeConfig(cloudConfig);
					loadModelPlugin(cloudConfig);
				});
			}
		}
		log.info("nacos cloud started at["+(System.currentTimeMillis()-now)+"ms]");
	}
	private void loadFromCloud(Config nacosCloudConfig, Function executor) {
		nacosCloudConfig.entrySet().forEach(entry->{
			String groupId = entry.getKey();
			List<String> dataIdList = nacosCloudConfig.getStringList(groupId);
			dataIdList.forEach(dataId->{
				nacosConfigRuntime.subscribeConfig(dataId, groupId, executor);
				try {
					String configResource = nacosConfigRuntime.getConfig(dataId, groupId, 1000);
					log.debug("cloud content ["+groupId+"]-["+dataId+"]:"+configResource);
					executor.execute(groupId,dataId,configResource);
				} catch (NacosException e) {
					log.error("failed to download ["+groupId+"]-["+dataId+"]",e);
				}
			});
		});
	}
	
}