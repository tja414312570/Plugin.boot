package plugin.boot;

import java.io.IOException;
import org.apache.catalina.LifecycleException;

import com.YaNan.frame.plugin.autowired.plugin.PluginWiredHandler;
import com.YaNan.framework.boot.BootArgs;
import com.YaNan.framework.boot.Plugin;
import com.YaNan.framework.boot.PluginBoot;
import com.YaNan.framework.boot.PluginBootServer;

//@EnableClassHotUpdater(contextClass=PluginBootServerTest.class);
//@WebContext(contextPath = "/", docBase = "webapp")4
//@WebApp(contextPath = "/", docBase = "webapp")
//@WebApp(contextPath = "/dcxt", docBase = "/Volumes/GENERAL/tomcat work groups/apache-tomcat-8.0.52/webapps/dcxt")
//@WebApp(contextPath = "/YaNanFrame", docBase = "/Volumes/GENERAL/tomcat work groups/apache-tomcat-8.0.53/webapps/YaNanFrame")
@PluginBoot()
@BootArgs(name="-environment-boot",value="com.YaNan.framework.boot.StandEnvironmentBoot")
//@BootArgs(name="-boot-configure",value="bootc.yc")
//@BootArgs(name="-boot-disabled",value="-boot-configure,-environment-boot")
@Plugin(PluginWiredHandler.class)
public class PluginBootServerTest2 {
	String contextPath; String docBase;
	public static void main(String[] args) throws LifecycleException, IOException {
//		System.out.println(ResourceManager.getClassPath(PluginBootServerTest.class)[0]);
//		System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));
//		System.out.println(ResourceManager.class.get.getSystemClassLoader().getResource("."));
		PluginBootServer.run();
		
//		org.apache.coyote.http2.Http2Protocol
	}
}
