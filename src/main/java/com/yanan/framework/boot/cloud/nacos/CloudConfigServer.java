package com.yanan.framework.boot.cloud.nacos;

/**
 * 云配置服务接口
 * @author yanan
 */
public interface CloudConfigServer {
	String getConfig(String dataId, String group,int timeout);
}