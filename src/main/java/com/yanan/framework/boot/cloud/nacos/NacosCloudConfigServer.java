package com.yanan.framework.boot.cloud.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.yanan.framework.plugin.ProxyModel;
import com.yanan.framework.plugin.annotations.Register;

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