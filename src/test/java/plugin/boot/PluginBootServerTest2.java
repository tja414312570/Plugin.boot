package plugin.boot;

import java.io.IOException;

import org.apache.catalina.LifecycleException;

import com.yanan.framework.boot.BootArgs;
import com.yanan.framework.boot.Plugin;
import com.yanan.framework.boot.PluginBoot;
import com.yanan.framework.boot.PluginBootServer;
import com.yanan.framework.plugin.Environment;
import com.yanan.framework.plugin.PluginEvent;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.autowired.plugin.PluginWiredHandler;
import com.yanan.framework.plugin.definition.RegisterDefinition;
import com.yanan.framework.plugin.event.EventListener;

//@EnableClassHotUpdater(contextClass=PluginBootServerTest.class);
//@WebContext(contextPath = "/", docBase = "webapp")4
//@WebApp(contextPath = "/", docBase = "webapp")
//@WebApp(contextPath = "/dcxt", docBase = "/Volumes/GENERAL/tomcat work groups/apache-tomcat-8.0.52/webapps/dcxt")
//@WebApp(contextPath = "/yananFrame", docBase = "/Volumes/GENERAL/tomcat work groups/apache-tomcat-8.0.53/webapps/yananFrame")
@PluginBoot()
@BootArgs(name="-environment-boot",value="com.yanan.framework.boot.StandEnvironmentBoot")
@BootArgs(name="-boot-configure",value="boot.yc")
//@BootArgs(name="-boot-disabled",value="-boot-configure,-environment-boot")
@Plugin(PluginWiredHandler.class)
public class PluginBootServerTest2 {
	String contextPath; String docBase;
	public static void main(String[] args) throws LifecycleException, IOException {
		Environment.getEnviroment().registEventListener(PlugsFactory.getInstance().getEventSource(), new EventListener<PluginEvent>() {

			@Override
			public void onEvent(PluginEvent abstractEvent) {
				if(abstractEvent.getEventType() == PluginEvent.EventType.add_registerDefinition) {
					RegisterDefinition definition = (RegisterDefinition)abstractEvent.getEventContent();
					System.out.println(definition.getId()+"==>"+definition.getRegisterClass()+":"+definition.isSignlton());
//					new RuntimeException().printStackTrace();
				}
			}
		});
//		System.out.println(ResourceManager.getClassPath(PluginBootServerTest.class)[0]);
//		System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));
//		System.out.println(ResourceManager.class.get.getSystemClassLoader().getResource("."));
		PluginBootServer.run();
		System.out.println(PlugsFactory.getPluginsInstance("nacosConfigureFactory")+"");
//		System.out.println(PlugsFactory.getPluginsInstance("sqlSession")+" ");
//		org.apache.coyote.http2.Http2Protocol
	}
}