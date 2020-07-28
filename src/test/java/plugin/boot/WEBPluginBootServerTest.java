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
import com.yanan.framework.boot.web.WebApp;
import com.yanan.utils.resource.AbstractResourceEntry;
import com.yanan.utils.resource.Resource;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.yanan.frame.ant.service.Function;
import com.yanan.frame.plugin.Environment;
import com.yanan.frame.plugin.PluginEvent;
import com.yanan.frame.plugin.PlugsFactory;

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