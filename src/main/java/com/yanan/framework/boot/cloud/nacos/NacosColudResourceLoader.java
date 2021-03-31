package com.yanan.framework.boot.cloud.nacos;

import org.slf4j.Logger;

import com.alibaba.nacos.api.exception.NacosException;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.resource.ResourceLoader;
import com.yanan.framework.resource.ResourceLoaderException;
import com.yanan.utils.asserts.Assert;
import com.yanan.utils.resource.Resource;

/**
 * Nacos 云资源加载器
 * @author yanan
 */
@Register(attribute="nacos")
public class NacosColudResourceLoader implements ResourceLoader{
	@Service
	private Logger logger;
	@Service
	private NacosConfigRuntime nacosConfigRuntime;

	@Override
	public Resource getResource(String path) {
		Assert.isNull(path,"resource path is null!");
		logger.info("download resource from "+path);
		int pathTokenIndex = path.indexOf(':');
		if(pathTokenIndex != -1)
			path = path.substring(pathTokenIndex+1);
		int pathIndex = path.indexOf('/');
		Assert.isTrue(pathIndex == -1,"resource path ["+path+"] incorrect!");
		String groupId = path.substring(0,pathIndex);
		String dataId = path.substring(pathIndex+1);
		try {
			String content = nacosConfigRuntime.getConfig(dataId, groupId, 3000);
			return new NacosCloudConfigResource(groupId, dataId, content,nacosConfigRuntime);
		} catch (NacosException e) {
			throw new ResourceLoaderException("failed to get resource:"+path, e);
		}
	}

}