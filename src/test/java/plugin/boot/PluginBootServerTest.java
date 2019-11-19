package plugin.boot;

import java.io.IOException;

import org.apache.catalina.LifecycleException;

import com.yanan.framework.boot.PluginBoot;
import com.yanan.framework.boot.PluginBootServer;
import com.yanan.framework.boot.WebApp;
import com.yanan.framework.boot.WebContext;

@WebContext(contextPath = "/", docBase = "webapp")
@WebApp(contextPath = "/dcxt", docBase = "/Volumes/GENERAL/tomcat work groups/apache-tomcat-8.0.52/webapps/dcxt")
@WebApp(contextPath = "/YaNanFrame", docBase = "/Volumes/GENERAL/tomcat work groups/apache-tomcat-8.0.53/webapps/YaNanFrame")
@PluginBoot(port=8081)
public class PluginBootServerTest {
	String contextPath; String docBase;
	public static void main(String[] args) throws LifecycleException, IOException {
		PluginBootServer.run(PluginBootServerTest.class);
	}
}
