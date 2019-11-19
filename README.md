# plugin.boot
 plugin的基于embedded-tomcat引导项目
* 快速开发、简单配置、集成Plugin.mvc、无额为第三依赖（依赖于Plugin，embedded-tomcat等基础）
# Future
* 多WebApp支持
* 多WebContext支持
* 优化其它组件以适应此引导中的开发方式
```java
@WebContext(contextPath = "/", docBase = "webapp")
@WebApp(contextPath = "/dcxt", docBase = "/Volumes/GENERAL/tomcat work groups/apache-tomcat-8.0.52/webapps/dcxt")
@WebApp(contextPath = "/YaNanFrame", docBase = "/Volumes/GENERAL/tomcat work groups/apache-tomcat-8.0.53/webapps/YaNanFrame")
@PluginBoot(port=8081)
@PluginBoot(port=8081)
public class PluginBootServerTest {
	String contextPath; String docBase;
	public static void main(String[] args) throws LifecycleException, IOException {
		PluginBootServer.run(PluginBootServerTest.class);
	}
}

```
