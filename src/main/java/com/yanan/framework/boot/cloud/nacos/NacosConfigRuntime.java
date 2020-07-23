package com.yanan.framework.boot.cloud.nacos;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.yanan.frame.plugin.PlugsFactory;

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
			PlugsFactory.getInstance().addDefinition(NacosCloudConfigServer.class);
			nacosCloudConfigServer = PlugsFactory.getPluginsInstance(CloudConfigServer.class);
			((NacosCloudConfigServer)nacosCloudConfigServer).setRuntime(this);
		} catch (NacosException | IllegalArgumentException | SecurityException e) {
			throw new RuntimeException("failed to init nacos server!",e);
		}
	}
	public NacosConfigRuntime(Properties properties) {
		this.init(properties);
	}
	public String getConfig(String dataId, String groupId, long timeoutMs) throws NacosException {
		logger.debug("download config ["+groupId+"]-["+dataId+"]");
		return this.configService.getConfig(dataId, groupId, timeoutMs);
	}
	public void subscribeConfig(String dataId,String groupId,Function function) {
		try {
			logger.debug("subscribe config ["+groupId+"]-["+dataId+"]");
			configService.addListener(dataId, groupId, new Listener() {
				@Override
				public void receiveConfigInfo(String configInfo) {
					logger.debug("config ["+groupId+"]-["+dataId+"] chanaged");
					long now = System.currentTimeMillis();
					function.execute(groupId,dataId,configInfo);
					logger.debug("config ["+groupId+"]-["+dataId+"] loaded at ["+(System.currentTimeMillis()-now)+"ms]");
				}
				@Override
				public Executor getExecutor() {
					return null;
				}
			});
		} catch (NacosException e) {
			throw new RuntimeException("failed to subscribe config ["+groupId+"]-["+dataId+"]",e);
		}
	}
	public void avaiable() throws Exception {
		String result = configService.getServerStatus();
		if(!"UP".equals(result))
			throw new RuntimeException("nacos server result :"+result);
	}
}