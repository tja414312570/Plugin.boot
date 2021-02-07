package com.yanan.framework.boot.cloud.nacos;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.nacos.api.exception.NacosException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.yanan.framework.boot.cloud.AbstractUpdateabledEnvironmentBoot;
import com.yanan.framework.plugin.Environment;
import com.yanan.framework.plugin.autowired.property.PropertyManager;
import com.yanan.utils.resource.ResourceNotFoundException;
import com.yanan.utils.string.StringUtil;

/**
 * 云环境
 * @author yanan
 */
public class CloudEnvironmentBoot extends AbstractUpdateabledEnvironmentBoot{
	private static final String NACOS_CLOUD_BIND_TOKEN= "clouds.nacos.bind";
	private static final String NACOS_CLOUD_TOKEN = "clouds.nacos";
	private static final String PROPERTIES_TOKEN = "properties";
	private static final String CONFIG_TOKEN = "config";
	private NacosConfigRuntime nacosConfigRuntime;
	private Set<String> loadProperties = new HashSet<>();
	Logger log = LoggerFactory.getLogger(CloudEnvironmentBoot.class);
	public CloudEnvironmentBoot(NacosConfigRuntime nacosConfigRuntime) {
		log.info("plugin boot nacos cloud server!");
		long now = System.currentTimeMillis();
		this.nacosConfigRuntime = nacosConfigRuntime;
		environment = Environment.getEnviroment();
		//获取clouds配置
		Config globalConfig = environment.getConfigure();
		globalConfig.allowKeyNull();
		Config nacosCloudConfig = globalConfig.getConfig(NACOS_CLOUD_TOKEN);
		if(nacosCloudConfig !=  null) {
			log.info("nacos cloud config:"+nacosCloudConfig);
			nacosCloudConfig.allowKeyNull();
			//获取属性配置
			Config propertiesConfig = nacosCloudConfig.getConfig(PROPERTIES_TOKEN);
			if(propertiesConfig != null) {
				loadFromCloud(propertiesConfig,(groupId,dataId,cloudContent)->{
					if(cloudContent == null) {
						throw new ResourceNotFoundException("nacos resource ["+groupId+"]-["+dataId+"] could not found");
					}
					log.debug("loaded properties ["+groupId+"]-["+dataId+"]:"+cloudContent.length());
					Reader reader = new StringReader(cloudContent);
					try {
						Properties properties = new Properties();
						properties.load(reader);
						Config cloudPropertiesConfig = ConfigFactory.parseProperties(properties);
						environment.getConfigure().merge(cloudPropertiesConfig);
//						environment.mergeConfig(cloudPropertiesConfig);
						PropertyManager.getInstance().put(properties);
						checkWhetherUpdateConfig(groupId, dataId);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
			propertiesConfig = nacosCloudConfig.getConfig(CONFIG_TOKEN);
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
	public void checkWhetherUpdateConfig(String groupId, String dataId) {
		String path = groupId+"/"+dataId;
		if(loadProperties.contains(path)) {
			Config bind = environment.getConfig(NACOS_CLOUD_BIND_TOKEN);
			if(bind == null)
				return;
			bind.allowKeyNull();
			String bindPath = bind.getString(path);
			if(StringUtil.isEmpty(bindPath))
				return;
			int pathIndex = bindPath.indexOf("/");
			groupId = bindPath.substring(0,pathIndex);
			dataId = bindPath.substring(pathIndex+1);
			try {
				Function executor = new Function() {
					@Override
					public void execute(String groupId, String dataId, String cloudContent) {
						log.debug("loaded config ["+groupId+"]-["+dataId+"]:"+cloudContent.length());
						Reader reader = new StringReader(cloudContent);
						Config cloudConfig = ConfigFactory.parseReader(reader);
						Environment.getEnviroment().mergeConfig(cloudConfig);
						loadModelPlugin(cloudConfig);
					}
				};
				String configResource = nacosConfigRuntime.getConfig(dataId, groupId, 1000);
				log.debug("cloud content ["+groupId+"]-["+dataId+"]:"+configResource);
				executor.execute(groupId,dataId,configResource);
			} catch (NacosException e) {
				log.error("failed to download ["+groupId+"]-["+dataId+"]",e);
			}
		}else
			loadProperties.add(path);
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