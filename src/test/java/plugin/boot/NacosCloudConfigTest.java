package plugin.boot;

import java.util.Arrays;

import com.yanan.frame.plugin.Environment;
import com.yanan.frame.plugin.PluginEvent;
import com.yanan.frame.plugin.PlugsFactory;
import com.yanan.frame.plugin.annotations.Register;
import com.yanan.frame.plugin.definition.RegisterDefinition;
import com.yanan.frame.plugin.event.EventListener;
import com.yanan.framework.boot.BootArgs;
import com.yanan.framework.boot.PluginBoot;
import com.yanan.framework.boot.PluginBootServer;

@BootArgs(name="-boot-configure",value="boot-nacos.yc")
@BootArgs(name="-environment-boot",value="com.yanan.framework.boot.StandEnvironmentBoot")
@PluginBoot
@Register
public class NacosCloudConfigTest {
//	@Variable("includ")
//	private String boot;
//	@Variable("plugins")
//	private List<Config> config;
//	@Variable("jdb.id")
//	private String id;
	public static void main(String[] args) {
		Environment.getEnviroment().registEventListener(PlugsFactory.getInstance().getEventSource(), new EventListener<PluginEvent>() {

			@Override
			public void onEvent(PluginEvent abstractEvent) {
				if(abstractEvent.getEventType() == PluginEvent.EventType.register_init) {
					RegisterDefinition definition = (RegisterDefinition)abstractEvent.getEventContent();
//					System.out.println(definition.getId()+"=ad=>"+definition.getRegisterClass());
//					new RuntimeException().printStackTrace();
				}
			}
		});
		PluginBootServer.run(args);
		NacosCloudConfigTest nacosCloudConfigTest = PlugsFactory.getPluginsInstance(NacosCloudConfigTest.class);
		System.out.println("================");
//		System.out.println(nacosCloudConfigTest.boot);
//		System.out.println(nacosCloudConfigTest.config);
//		System.out.println(nacosCloudConfigTest.id);
//		System.out.println(nacosCloudConfigTest.FinalDiscountedPrice(new int[]{2,3,6,10,9,7,3,9,3,5}));
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