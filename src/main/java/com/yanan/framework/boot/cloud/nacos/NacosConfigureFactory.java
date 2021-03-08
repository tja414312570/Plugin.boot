package com.yanan.framework.boot.cloud.nacos;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.yanan.framework.ant.exception.AntInitException;
import com.yanan.framework.plugin.exception.PluginInitException;
import com.yanan.utils.asserts.Assert;
import com.yanan.utils.resource.Resource;
import com.yanan.utils.resource.ResourceManager;

public class NacosConfigureFactory {
	private static final int CONF = 0;
	private static final int PROPERTIES = 1;
	public static Properties build(Config config) {
		Assert.isNull(config, "nacos config is null");
		Assert.isFalse(config.hasPath("nacos"), "could not found nacos config at this config "+config);
		Properties properties = new Properties();
		config = config.getConfig("nacos");
		config.allowKeyNull();
		properties.setProperty("serverAddr", config.getString("host"));
		properties.setProperty("namespace", config.getString("namespace"));
		properties.setProperty("port",config.getString("port","8848"));
		System.setProperty("nacos.server.port",config.getString("port","8848"));
		properties.setProperty("appKey", config.getString("appKey",""));
		System.setProperty("nacos.client.appKey",config.getString("appKey",""));
		System.setProperty("NACOS.CONNECT.TIMEOUT",config.getString("timeout","1000"));
		properties.setProperty("timeout", config.getString("timeout","1000"));
		
		return properties;
	}
	public static Properties build(String filePath) throws IOException {
		Assert.isNull(filePath, "nacos config name is null");
		Resource resourceManager =ResourceManager.getResource(filePath);
		Assert.isNull(resourceManager,"the ant config file ["+filePath+"] is not exists!");
		if(filePath.endsWith(".yc")) {
			return build(resourceManager.getInputStream(),CONF);
		}
		if(filePath.endsWith(".properties")) {
			return build(resourceManager.getInputStream(),PROPERTIES);
		}
		throw new AntInitException("the type of this file is not be support!");
	}
	public static Properties build(InputStream inputStream,int type) {
		Assert.isNull(inputStream, "nacos config is null");
		try {
			switch(type) {
				case CONF:
					InputStreamReader reader = new InputStreamReader(inputStream);
					Config config = ConfigFactory.parseReader(reader);
					return build(config);
//				case XML:
//					return buildFromXml(inputStream);
				case PROPERTIES:
					try {
						Properties properties = new Properties();
						properties.load(inputStream);
						return build(properties);
					} catch (IOException e) {
						new PluginInitException(e);
					}
				default :
					throw new AntInitException("the stream type is not support for "+type);
			}
		}finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
//	public static Properties build(XMLHelper xmlHelper,String resourceMark) {
//		Assert.isNull(xmlHelper, "nacos config is null");
//		List<nacosContextConfigure> contextConfigures = xmlHelper.read();
//		if(contextConfigures != null && contextConfigures.size()==0)
//			throw new PluginInitException("could not pase this xml resource from "+resourceMark);
//		nacosContext antContext = new nacosContext(contextConfigures.get(0));
//		return antContext;
//	}
//	public static Properties build(XMLHelper xmlHelper) {
//		return build(xmlHelper, xmlHelper.toString());
//	}
//	public static Properties buildFromXml(AbstractResourceEntry resource) {
//		Assert.isNull(resource, "nacos config is null");
//		XMLHelper xmlHelper = new XMLHelper(resource,nacosContextConfigure.class);
//		return build(xmlHelper, resource.getPath());
//	}
//	public static Properties buildFromXml(InputStream inputStream) {
//		Assert.isNull(inputStream, "nacos config is null");
//		XMLHelper xmlHelper = new XMLHelper(inputStream,nacosContextConfigure.class);
//		return build(xmlHelper, inputStream.toString());
//	}
	public static Properties build(Properties proerties) {
		return proerties;
	}
}