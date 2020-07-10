package com.YaNan.framework.boot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.YaNan.frame.utils.reflect.ClassLoaderShared;
import com.typesafe.config.Config;

/**
 * 环境类，一个引导仅支持一个环境
 * 环境类提供全局上下文配置，引导类等数据
 * 采用holder的单例模式
 * @author yanan
 *
 */
@ClassLoaderShared
public class Environment {
	public static final String MAIN_CLASS = "-MAIN-CLASS";
	public static final String MAIN_CLASS_PATH = "-MAIN-CLASS-PATH";
	private static InheritableThreadLocal<Environment> enInheritableThreadLocal = new InheritableThreadLocal<>();
	public final static class EnviromentHolder{
		private static final Environment ENVIRONMENT = new Environment();
	}
	//全局配置
	private Config globalConfig;
	//全局变量
	private Map<String,Object> globalVariable = new ConcurrentHashMap<String,Object>();
	private Environment() {
		enInheritableThreadLocal.set(this);
	};
	/**
	 * instance a Environment 
	 * @return Environment
	 */
	public static Environment getEnviroment() {
		return EnviromentHolder.ENVIRONMENT;
	}
	/**
	 * get the global environment configure
	 * @return global configure
	 */
	public Config getConfigure() {
		return this.globalConfig;
	}
	public synchronized void mergeConfig(Config config){
		if(globalConfig == null) {
			globalConfig = config;
		}else {
			globalConfig.merge(config);
		}
	}
	/**
	 * set the variable to environment
	 * @param key name
	 * @param value value
	 */
	public void setVariable(String key,Object value) {
		this.globalVariable.put(key, value);
	}
	/**
	 * get a variable
	 * @param <T> variable type
	 * @param key variable name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getVariable(String key) {
		return (T) this.globalVariable.get(key);
	}
	/**
	 * judge the environment whether contains a variable
	 * @param key variable name
	 * @return boolean true or false
	 */
	public boolean hasVariable(String key) {
		return this.globalVariable.containsKey(key);
	}
	/**
	 * remove variable
	 * @param keys names
	 */
	public void removeVariable(String... keys) {
		for(String key : keys)
			this.globalVariable.remove(key);
	}
}
