package com.YaNan.framework.boot.cloud.nacos;

import com.YaNan.frame.plugin.ProxyModel;
import com.YaNan.frame.plugin.annotations.Register;
import com.alibaba.nacos.api.exception.NacosException;

@Register(model=ProxyModel.CGLIB)
public class NacosCloudConfigServer implements CloudConfigServer{
	
	private NacosConfigRuntime nacosConfigRuntime;

	@Override
	public String getConfig(String dataId, String group, int timeout) {
		try {
			return nacosConfigRuntime.getConfig(dataId, group, timeout);
		} catch (NacosException e) {
			throw new RuntimeException(e);
		}
	}

	void setRuntime(NacosConfigRuntime runtime) {
		this.nacosConfigRuntime = runtime;
	}
	
}
