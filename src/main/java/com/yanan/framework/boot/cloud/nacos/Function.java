package com.yanan.framework.boot.cloud.nacos;

@FunctionalInterface
public interface Function{
	void execute(String groupId,String dataId,String confStr);
}