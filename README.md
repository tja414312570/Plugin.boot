# plugin.boot
 plugin的基于embedded-tomcat引导项目
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
