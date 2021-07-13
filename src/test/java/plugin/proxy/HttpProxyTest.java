package plugin.proxy;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;

import com.yanan.framework.boot.BootArgs;
import com.yanan.framework.boot.PluginBootServer;
import com.yanan.framework.resource.DefaultResourceLoader;
import com.yanan.framework.resource.ResourceLoader;
import com.yanan.utils.resource.Resource;

@BootArgs(name="-environment-boot",value="com.yanan.framework.boot.StandEnvironmentBoot")
@BootArgs(name="-boot-configure",value="boot.yc")
public class HttpProxyTest {
	public static void main(String[] args) throws InterruptedException {
		PluginBootServer.run();
		Executor executor =  Executors.newFixedThreadPool(5);
		for(int i = 0;i<400;i++) {
			System.err.println("发起调用："+i+"次");
			ResourceLoader resourceLoader = new DefaultResourceLoader();
			Resource resource = resourceLoader.getResource("http://http.tiqu.letecs.com/getip3?num=2&type=1&pro=0&city=0&yys=0&port=1&pack=157202&ts=0&ys=0&cs=0&lb=1&sb=0&pb=4&mr=1&regions=110000,320000,410000,520000&gm=4");
			try {
				String content = IOUtils.toString(resource.getInputStream());
				String[] proxyList = content.split("\r\n");
				List<ClientProxyHttpClientHttp> proxyClientList= new ArrayList<>();
				for(String proxyAddr : proxyList) {
					int index = proxyAddr.indexOf(":");
					String host = proxyAddr.substring(0,index);
					int port = Integer.valueOf(proxyAddr.substring(index+1));
					System.err.println("ip:"+host+",port:"+port);
					ClientProxyHttpClientHttp clientHttp = new ClientProxyHttpClientHttp(host, port);
					proxyClientList.add(clientHttp);
					executor.execute(()->{
						clientHttp.doGetRequest();
					});
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			Thread.sleep(5000);
		}
		
	}
}
