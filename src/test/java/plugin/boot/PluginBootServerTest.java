package plugin.boot;

import java.io.IOException;

import org.apache.catalina.LifecycleException;
import org.apache.tomcat.util.net.SSLHostConfigCertificate.Type;

import com.yanan.framework.boot.Certificate;
import com.yanan.framework.boot.HttpProtocol;
import com.yanan.framework.boot.PluginBoot;
import com.yanan.framework.boot.PluginBootServer;
import com.yanan.framework.boot.SSLHost;
import com.yanan.framework.boot.WebApp;

//@WebContext(contextPath = "/", docBase = "webapp")4
@WebApp(contextPath = "/", docBase = "webapp")
//@WebApp(contextPath = "/dcxt", docBase = "/Volumes/GENERAL/tomcat work groups/apache-tomcat-8.0.52/webapps/dcxt")
//@WebApp(contextPath = "/YaNanFrame", docBase = "/Volumes/GENERAL/tomcat work groups/apache-tomcat-8.0.53/webapps/YaNanFrame")
@PluginBoot(port=8081,httpProtocol=HttpProtocol.http11Nio)
@SSLHost(certificateKeyStoreFile="/Users/yanan/Desktop/归档/tomcat.keystore",certificateKeystorePassword="123123",upgradeProtocol=HttpProtocol.Http2Protocol,port=8443)
public class PluginBootServerTest {
	String contextPath; String docBase;
	public static void main(String[] args) throws LifecycleException, IOException {
		
//		System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));
//		System.out.println(ResourceManager.class.get.getSystemClassLoader().getResource("."));
		PluginBootServer.run(PluginBootServerTest.class);
//		org.apache.coyote.http2.Http2Protocol
	}
}
