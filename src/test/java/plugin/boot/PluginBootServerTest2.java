package plugin.boot;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import org.apache.catalina.LifecycleException;

import com.yanan.frame.plugin.autowired.plugin.PluginWiredHandler;
import com.yanan.frame.plugin.builder.PluginInstanceFactory;
import com.yanan.frame.plugin.decoder.ResourceDecoder;
import com.yanan.frame.plugin.definition.RegisterDefinition;
import com.yanan.frame.plugin.event.EventListener;
import com.yanan.framework.boot.BootArgs;
import com.yanan.framework.boot.Plugin;
import com.yanan.framework.boot.PluginBoot;
import com.yanan.framework.boot.PluginBootServer;
import com.yanan.framework.boot.cloud.nacos.CloudConfigServer;
import com.yanan.utils.resource.AbstractResourceEntry;
import com.yanan.utils.resource.Resource;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.yanan.frame.ant.service.Function;
import com.yanan.frame.plugin.Environment;
import com.yanan.frame.plugin.PluginEvent;
import com.yanan.frame.plugin.PlugsFactory;

//@EnableClassHotUpdater(contextClass=PluginBootServerTest.class);
//@WebContext(contextPath = "/", docBase = "webapp")4
//@WebApp(contextPath = "/", docBase = "webapp")
//@WebApp(contextPath = "/dcxt", docBase = "/Volumes/GENERAL/tomcat work groups/apache-tomcat-8.0.52/webapps/dcxt")
//@WebApp(contextPath = "/yananFrame", docBase = "/Volumes/GENERAL/tomcat work groups/apache-tomcat-8.0.53/webapps/yananFrame")
@PluginBoot()
@BootArgs(name="-environment-boot",value="com.yanan.framework.boot.StandEnvironmentBoot")
@BootArgs(name="-boot-configure",value="boot-nacos.yc")
//@BootArgs(name="-boot-disabled",value="-boot-configure,-environment-boot")
@Plugin(PluginWiredHandler.class)
public class PluginBootServerTest2 {
	String contextPath; String docBase;
	public static void main(String[] args) throws LifecycleException, IOException {
		Environment.getEnviroment().registEventListener(PlugsFactory.getInstance().getEventSource(), new EventListener<PluginEvent>() {

			@Override
			public void onEvent(PluginEvent abstractEvent) {
				if(abstractEvent.getEventType() == PluginEvent.EventType.register_init) {
					RegisterDefinition definition = (RegisterDefinition)abstractEvent.getEventContent();
					System.out.println(definition.getId()+"==>"+definition.getRegisterClass());
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