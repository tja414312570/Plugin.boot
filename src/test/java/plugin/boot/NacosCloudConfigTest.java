package plugin.boot;

import java.io.IOException;

import com.yanan.frame.plugin.PlugsFactory;
import com.yanan.frame.plugin.annotations.Register;
import com.yanan.framework.boot.BootArgs;
import com.yanan.framework.boot.PluginBoot;
import com.yanan.framework.boot.PluginBootServer;
import com.yanan.framework.boot.resource.DefaultResourceLoader;
import com.yanan.framework.boot.resource.ResourceLoader;
import com.yanan.utils.resource.Resource;

@BootArgs(name="-boot-configure",value="boot-nacos.yc")
@BootArgs(name="-environment-boot",value="com.yanan.framework.boot.StandEnvironmentBoot")
@PluginBoot
@Register
public class NacosCloudConfigTest {
	public static void main(String[] args) {
		PluginBootServer.run(args);
		System.out.println("================");
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("nacos:DEFAULT_GROUP/boot-jdb");
		Resource resource2 = resourceLoader.getResource("classpath:boot.cloud.yc");
		Resource resource3 = resourceLoader.getResource("/Volumes/GENERAL/fusionaccess_mac.dmg");
		try {
			System.out.println("资源名称:"+resource.getName()+",大小:"+resource.size());
			System.out.println("资源名称:"+resource2.getName()+",大小:"+resource2.size());
			System.out.println("资源名称:"+resource3.getName()+",大小:"+resource3.size());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			synchronized (NacosCloudConfigTest.class) {
				NacosCloudConfigTest.class.wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}