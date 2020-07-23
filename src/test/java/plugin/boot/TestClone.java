package plugin.boot;

import java.util.Properties;

import com.yanan.framework.boot.cloud.nacos.NacosConfigureFactory;
import com.yanan.utils.reflect.AppClassLoader;

public class TestClone {
	public static void main(String[] args) {
		Properties properties = NacosConfigureFactory.build("classpath:boot.cloud.yc");
		System.out.println(properties);
		Properties properties2 = new Properties();
		AppClassLoader.DisClone(properties2, properties);
//		properties2 = (Properties) properties.clone();
		System.out.println(properties2);
	}
}
