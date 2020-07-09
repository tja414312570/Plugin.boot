# plugin.boot
 plugin的基于embedded-tomcat引导项目
* 快速开发、简单配置、集成Plugin.mvc、无额为第三依赖（依赖于Plugin，embedded-tomcat等基础）
* WebApp 以原生WebApp的启动方式启动Web应用，支持Web.xml,原生Servlet3.0开发规范，支持Spring等
* WebContext 以基于Plugin.mvc的模式启动Web应用，不支持Web.xml等，原生Servlet3.0的注解开发方式的Filter、Servlet等需要添加@Register注册为Plugin的Bean，启动更快，支持静态资源，JSP
# 20200709
* 独立基础引导服务PluginBootService,用于初始化引导参数，boot级别的配置
* 抽象环境引导（EnvironmentBoot），将环境具体化，目前默认提供标准环境（StandEnvironmentBoot）和基于Tomcat的Web环境（WebEnvironmentBoot）
* 优化引导流程--》将组件加载逻辑等进行优化处理
* 基础引导注解 
** @BootArgs 提供引导参数
** @PluginBoot 标志引导类
* 基础引导参数
** #-environment-boot:环境引导类，默认通过依赖判断为StandEnvironmentBoot或则WebEnvironmentBoot
** #-environment-scan:环境扫描包位置，支持classpath
** #-boot-configure:引导配置地址，支持classpath
** #-boot-disabled:禁用配置，多个用,分开

# Future
* 多WebApp支持
* 多WebContext支持
* 优化其它组件以适应此引导中的开发方式
* 优化默认配置、优化架构和性能
```java
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
```
