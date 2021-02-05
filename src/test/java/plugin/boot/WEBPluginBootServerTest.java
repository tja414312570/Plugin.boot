package plugin.boot;

import java.io.IOException;

import org.apache.catalina.LifecycleException;

import com.typesafe.config.Config;
import com.yanan.framework.boot.Plugin;
import com.yanan.framework.boot.PluginBoot;
import com.yanan.framework.boot.PluginBootServer;
import com.yanan.framework.boot.web.WebPluginBoot;
import com.yanan.framework.plugin.Environment;
import com.yanan.framework.plugin.PluginEvent;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.autowired.plugin.PluginWiredHandler;
import com.yanan.framework.plugin.autowired.property.PropertyManager;
import com.yanan.framework.plugin.event.EventListener;
import com.yanan.utils.resource.ResourceManager;
import com.yanan.utils.resource.scanner.PackageScanner;
import com.yanan.utils.resource.scanner.PackageScanner.ClassInter;

//@WebApp(contextPath = "/yananFrame", docBase = "webapps/yananFrame")
@PluginBoot()
@WebPluginBoot(port = 8888)
//@BootArgs(name="-environment-boot",value="com.yanan.framework.boot.StandEnvironmentBoot")
//@BootArgs(name="-boot-configure",value="boot.yc")
@Plugin(PluginWiredHandler.class)
public class WEBPluginBootServerTest {
	public static void main(String[] args) throws LifecycleException, IOException {
		
//		System.out.println(ResourceManager.getResourceList("classpath:**"));
		Environment.getEnviroment().registEventListener(PlugsFactory.getInstance().getEventSource(), new EventListener<PluginEvent>() {
			@Override
			public void onEvent(PluginEvent abstractEvent) {
//				System.out.println(abstractEvent);
//				if(abstractEvent.getEventType() == PluginEvent.EventType.add_registerDefinition) {
//					RegisterDefinition definition = (RegisterDefinition)abstractEvent.getEventContent();
////					System.out.println(definition.getId()+"==>"+definition.getRegisterClass()+":"+definition.isSignlton());
////					new RuntimeException().printStackTrace();
//				}
			}
		});
//		ResourceManager.classPath();
//		new Thread(()->{
//			while(true) {
//				try {
//					System.out.println();
//					System.out.println(Environment.getEnviroment().getConfigValue("jdb.password"));
//					System.out.println(PropertyManager.getInstance().getProperty("jdb.password"));
//					System.out.println(Environment.getEnviroment().getConfigValue("clouds.nacos.config"));
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
		PluginBootServer.run();
	}
}