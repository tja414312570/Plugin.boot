package plugin.boot;

import java.io.IOException;

import org.apache.catalina.LifecycleException;

import com.yanan.framework.boot.BootArgs;
import com.yanan.framework.boot.Plugin;
import com.yanan.framework.boot.PluginBoot;
import com.yanan.framework.boot.PluginBootServer;
import com.yanan.framework.boot.web.WebPluginBoot;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.autowired.enviroment.VariableWiredHandler;
import com.yanan.framework.plugin.autowired.plugin.PluginWiredHandler;
import com.yanan.framework.plugin.handler.InvokeHandler;
import com.yanan.framework.plugin.handler.MethodHandler;

//@WebApp(contextPath = "/yananFrame", docBase = "webapps/yananFrame")
@PluginBoot()
@WebPluginBoot(port = 8888)
//@BootArgs(name="-environment-boot",value="com.yanan.framework.boot.StandEnvironmentBoot")
@Plugin(PluginWiredHandler.class)
@Register
public class WEBPluginBootServerTest implements InvokeHandler{
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
		PlugsFactory.getInstance().addDefinition(Test.class);
		PlugsFactory.getInstance().addDefinition(DefaultLogger.class);
		PlugsFactory.getInstance().addDefinition(VariableWiredHandler.class);
		PluginBootServer.run();
		
	}
	
}