# plugin.boot
 plugin的基于embedded-tomcat引导项目
## 快速开发、简单配置、集成Plugin.mvc、无额为第三依赖（依赖于Plugin，embedded-tomcat等基础）
```java
@WebContext(contextPath = "/", docBase = "webapp")
@WebApp(contextPath = "/", docBase = "webapp")
@PluginBoot(port=8081)
public class PluginBootServerTest {
	String contextPath; String docBase;
	public static void main(String[] args) throws LifecycleException, IOException {
		PluginBootServer.run(PluginBootServerTest.class);
	}
}

```
