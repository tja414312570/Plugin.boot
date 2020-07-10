package com.YaNan.framework.boot.cloud.nacos;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.plugin.PlugsFactory;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

public class NacosConfigRuntime {
	private ConfigService configService;
	private Properties properties;
	private CloudConfigServer nacosCloudConfigServer;
	private static Logger logger = LoggerFactory.getLogger(NacosConfigRuntime.class);
	List<String> eventList = new ArrayList<String>(16);
	public NacosConfigRuntime(String path) {
		logger.debug("Nacos Cloud Configure server!");
		properties = NacosConfigureFactory.build(path);
		logger.debug("Ant Nacos servcie config "+properties);
		this.init(properties);
	}
	
	public void init(Properties properties) {
		try {
			this.properties = properties;
			logger.debug("Ant Nacos servcie config "+properties);
			configService = ConfigFactory.createConfigService(properties);
			PlugsFactory.getInstance().addPlugs(NacosCloudConfigServer.class);
			nacosCloudConfigServer = PlugsFactory.getPlugsInstance(CloudConfigServer.class);
			((NacosCloudConfigServer)nacosCloudConfigServer).setRuntime(this);
			//获取服务
			
		} catch (NacosException | IllegalArgumentException | SecurityException e) {
			throw new RuntimeException("failed to init nacos server!",e);
		}
	}
	public NacosConfigRuntime(Properties properties) {
		this.init(properties);
	}
	public String getConfig(String dataId, String group, long timeoutMs) throws NacosException {
		return this.configService.getConfig(dataId, group, timeoutMs);
	}
	public void avaiable() throws Exception {
		String result = configService.getServerStatus();
		if(!"UP".equals(result))
			throw new RuntimeException("nacos server result :"+result);
	}
}
