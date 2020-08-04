package plugin.boot;

import java.io.IOException;

import org.apache.catalina.LifecycleException;

import com.yanan.framework.boot.BootArgs;
import com.yanan.framework.boot.Plugin;
import com.yanan.framework.boot.PluginBoot;
import com.yanan.framework.boot.PluginBootServer;
import com.yanan.framework.boot.web.WebApp;
import com.yanan.framework.plugin.autowired.plugin.PluginWiredHandler;

@WebApp(contextPath = "/yananFrame", docBase = "webapps/yananFrame")
@PluginBoot()
//@BootArgs(name="-environment-boot",value="com.yanan.framework.boot.StandEnvironmentBoot")
@BootArgs(name="-boot-configure",value="boot.yc")
@Plugin(PluginWiredHandler.class)
public class WEBPluginBootServerTest {
	public static void main(String[] args) throws LifecycleException, IOException {
		
//		Environment.getEnviroment().registEventListener(PlugsFactory.getInstance().getEventSource(), new EventListener<PluginEvent>() {
//			@Override
//			public void onEvent(PluginEvent abstractEvent) {
//				if(abstractEvent.getEventType() == PluginEvent.EventType.add_registerDefinition) {
//					RegisterDefinition definition = (RegisterDefinition)abstractEvent.getEventContent();
////					System.out.println(definition.getId()+"==>"+definition.getRegisterClass()+":"+definition.isSignlton());
////					new RuntimeException().printStackTrace();
//				}
//			}
//		});
		PluginBootServer.run();
	}
}