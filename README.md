# plugin.boot
 plugin的基于embedded-tomcat引导项目
* 快速开发、简单配置、集成Plugin.mvc、无额为第三依赖（依赖于Plugin，embedded-tomcat等基础）
* WebApp 以原生WebApp的启动方式启动Web应用，支持Web.xml,原生Servlet3.0开发规范，支持Spring等
* WebContext 以基于Plugin.mvc的模式启动Web应用，不支持Web.xml等，原生Servlet3.0的注解开发方式的Filter、Servlet等需要添加@Register注册为Plugin的Bean，启动更快，支持静态资源，JSP
# Future
* 多WebApp支持
* 多WebContext支持
* 优化其它组件以适应此引导中的开发方式
* 优化默认配置、优化架构和性能
```java
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

```
