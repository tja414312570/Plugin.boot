package ant.test;

import java.io.IOException;
import java.util.Properties;

import com.yanan.framework.a.core.MessageChannel;
import com.yanan.framework.a.core.cluster.ChannelManager;
import com.yanan.framework.a.dispatcher.ChannelDispatcher;
import com.yanan.framework.a.proxy.Callback;
import com.yanan.framework.a.proxy.ProxyInvokerMapper;
import com.yanan.framework.ant.nacos.AntNacosConfigureFactory;
import com.yanan.framework.boot.cloud.nacos.NacosConfigureFactory;
import com.yanan.framework.plugin.Environment;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.decoder.StandScanResource;
import com.yanan.utils.resource.ResourceManager;

public class DiscoveryServiceTest {
	public static void main(final String[] args) throws InterruptedException, IOException {

		Environment.getEnviroment().registEventListener(PlugsFactory.getInstance().getEventSource(),event->{
//    		if( ((PluginEvent)event).getEventType() == EventType.add_registerDefinition)
//			System.err.println(((RegisterDefinition)((PluginEvent)event).getEventContent()).getRegisterClass());
		});
		
		PlugsFactory.init(ResourceManager.getResource("classpath:plugin.yc"),
				new StandScanResource(ResourceManager.getClassPath(MessageChannel.class)[0] + "**"),
				new StandScanResource(ResourceManager.getClassPath(AntNacosConfigureFactory.class)[0] + "**"),
				new StandScanResource(ResourceManager.getClassPath(DiscoveryServiceTest.class)[0] + "**"));

		Properties properties = NacosConfigureFactory.build("classpath:ant.yc");

		ChannelManager<?> server = PlugsFactory.getPluginsInstance(ChannelManager.class, properties);

		server.start();

		ChannelDispatcher<Object> channelDispatcher = PlugsFactory.getPluginsInstance(ChannelDispatcher.class);
		System.err.println(channelDispatcher);
		channelDispatcher.bind(server);
		
		ProxyInvokerMapper proxyInvokerMapper = PlugsFactory.getPluginsInstance(ProxyInvokerMapper.class);
		
//		channelDispatcher.bind(invoker);
		proxyInvokerMapper.bind(channelDispatcher);
		
//		channelDispatcher.request("defaultName","hello world");
		
		Request request = PlugsFactory.getPluginsInstance(Request.class);
		System.err.println(request);
		long now = System.currentTimeMillis();
		Object result = request.add(10, 20);
		for(int i = 0;i<1000;i++) {
			Callback.newCallback(request).wrapper(request.multi(10, i),(message,context)->{
				System.err.println("异步结果:"+message);
			}, (exception,context)->{
				System.err.println("异步错误:"+exception);
			});
		}
//		Callback<Integer> callback = ;
//		callback.on((message,context)->{
//			
//		}, (exception,context)->{
//			
//		});
		System.err.println("执行结果:"+result+",耗时:"+(System.currentTimeMillis()-now));
//		now = System.currentTimeMillis();
//		for(int i = 0;i<1000;i++) {
//			result = request.add(i, 20);
//		}
//		System.err.println("执行结果:"+result+",耗时:"+(System.currentTimeMillis()-now));
//		MessageChannel<String> messageChannel =server.getChannel("defaultName");
//		System.err.println(messageChannel);
//		messageChannel.transport("Hello");
	}
}
