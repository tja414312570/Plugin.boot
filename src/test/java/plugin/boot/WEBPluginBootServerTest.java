package plugin.boot;

import java.io.IOException;
import java.util.Arrays;

import org.apache.catalina.LifecycleException;

import com.typesafe.config.Config;
import com.yanan.framework.boot.Plugin;
import com.yanan.framework.boot.PluginBoot;
import com.yanan.framework.boot.PluginBootServer;
import com.yanan.framework.boot.web.WebPluginBoot;
import com.yanan.framework.debug.SnapshotHandler;
import com.yanan.framework.plugin.Environment;
import com.yanan.framework.plugin.PluginEvent;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.autowired.enviroment.VariableWiredHandler;
import com.yanan.framework.plugin.autowired.plugin.PluginWiredHandler;
import com.yanan.framework.plugin.autowired.property.PropertyManager;
import com.yanan.framework.plugin.event.EventListener;
import com.yanan.framework.resource.DefaultResourceLoader;
import com.yanan.utils.resource.Resource;
import com.yanan.utils.resource.ResourceManager;
import com.yanan.utils.resource.scanner.PackageScanner;
import com.yanan.utils.resource.scanner.PackageScanner.ClassInter;
import com.yanan.utils.string.PathMatcher;
import com.yanan.utils.string.PathMatcher.Matcher;

//@WebApp(contextPath = "/yananFrame", docBase = "webapps/yananFrame")
@PluginBoot()
@WebPluginBoot(port = 8888)
//@BootArgs(name="-environment-boot",value="com.yanan.framework.boot.StandEnvironmentBoot")
//@BootArgs(name="-boot-configure",value="boot.yc")
@Plugin(PluginWiredHandler.class)
public class WEBPluginBootServerTest {
//	? * **
	public static void main(String[] args) throws LifecycleException, IOException {
//		Matcher matcher = PathMatcher.match("token/{p1**}/l{p2*}", "token/lll/lll/ls");
//		System.out.println(matcher.getTokens());
//		System.out.println(ResourceManager.getResourceList("classpath:**"));
//		Environment.getEnviroment().registEventListener(PlugsFactory.getInstance().getEventSource(), new EventListener<PluginEvent>() {
//			@Override
//			public void onEvent(PluginEvent abstractEvent) {
////				System.out.println(abstractEvent);
////				if(abstractEvent.getEventType() == PluginEvent.EventType.add_registerDefinition) {
////					RegisterDefinition definition = (RegisterDefinition)abstractEvent.getEventContent();
//////					System.out.println(definition.getId()+"==>"+definition.getRegisterClass()+":"+definition.isSignlton());
//////					new RuntimeException().printStackTrace();
////				}
//			}
//		});
//		System.out.println(PathMatcher.match("/Volumes/GENERAL/git/plugin.boot/target/test-classes/*.properties", "/Volumes/GENERAL/git/plugin.boot/target/test-classes/jdb.properties.bk").getTokens());
//		ResourceManager.getResourceList("classpath:*.properties").forEach(re->{
//			System.out.println(re.getPath());
//		});
		
//		ResourceManager.classPaths();
//		new Thread(()->{
//			while(true) {
//				try {
//					System.out.println();
//					System.out.println(Environment.getEnviroment().getConfigValue("jdb.password"));
//					System.out.println(PropertyManager.getInstance().getProperty("jdb.password"));
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
		PlugsFactory.getInstance().addDefinition(VariableWiredHandler.class);
		PluginBootServer.run();
		
	}
}