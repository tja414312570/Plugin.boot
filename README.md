# plugin.boot
 plugin的基于embedded-tomcat引导项目
* 快速开发、简单配置、集成Plugin.mvc、无额为第三依赖（依赖于Plugin，embedded-tomcat等基础）
* WebApp 以原生WebApp的启动方式启动Web应用，支持Web.xml,原生Servlet3.0开发规范，支持Spring等
* WebContext 以基于Plugin.mvc的模式启动Web应用，不支持Web.xml等，原生Servlet3.0的注解开发方式的Filter、Servlet等需要添加@Register注册为Plugin的Bean，启动更快，支持静态资源，JSP
# 20200709 不只是简单，更要明了
* 理念说明：将配置文件独立出来，每个组件提供默认配置，同时配置可以很轻松的阅读，以及快速理解，同时易于更改
* 独立基础引导服务PluginBootService,用于初始化引导参数，boot级别的配置
* 抽象环境引导（EnvironmentBoot），将环境具体化，目前默认提供标准环境（StandEnvironmentBoot）和基于Tomcat的Web环境（WebEnvironmentBoot）
* 优化引导流程--》将组件加载逻辑等进行优化处理
* 基础引导注解 
* * @BootArgs 提供引导参数
* * @PluginBoot 标志引导类
* 基础引导参数
* * #-environment-boot:环境引导类，默认通过依赖判断为StandEnvironmentBoot或则WebEnvironmentBoot
* * #-environment-scan:环境扫描包位置，支持classpath
* * #-boot-configure:引导配置地址，支持classpath
* * #-boot-disabled:禁用配置，多个用,分开

# Future
* junit工具支持
* 云配置
* 优化其它组件以适应此引导中的开发方式
* 优化默认配置、优化架构和性能
# 配置案例
```java
====>boot.yc
##基础引导参数
#-environment-boot:环境引导类，默认通过依赖判断为StandEnvironmentBoot或则WebEnvironmentBoot
#-environment-scan:环境扫描包位置，支持classpath
#-boot-configure:引导配置地址，支持classpath
#-boot-disabled:禁用配置，多个用,分开
#环境启动前加载的属性，支持ant路径
properties:[
	"*.properties"
]
#模块配置，支持ant路径
includes:[
	"plugin.yc",
	"boot.jdb.yc",
	"boot.mvc.yc",
#	"Ant.yc" 此配置不采用
]

====>plugin.jdb.yc
plugins:[
	{
		id:dataSource,#标示为bean
		class:com.YaNan.frame.jdb.datasource.DefaultDataSource,#bean 类
		init:init #初始化后调用init方法
	},
	{
		id:jdbContext,
		class:com.YaNan.frame.jdb.JDBContext,
		args.ref:dataSource, #在构造器中传入id为xxx的bean的参数
		init:init
	},{
		id:sqlSession,
		class:com.YaNan.frame.jdb.mapper.DefaultSqlSessionExecuter,
		args.ref:jdbContext
	},{
		id:build,
		class:com.YaNan.frame.jdb.mapper.MapperInterfaceProxyBuilder,
		args.ref:sqlSession
	}
]

```
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
```java
[2020-07-09 19:02:34] [ INFO] [PluginBootServer:36] : Plugin Boot Snapshot Version!
[2020-07-09 19:02:34] [ INFO] [PluginBootServer:40] : Application main class path : /Volumes/GENERAL/git/plugin.boot/target/classes/
[2020-07-09 19:02:34] [ INFO] [PluginBootServer:43] : Application main class : class plugin.boot.PluginBootServerTest2
[2020-07-09 19:02:36] [ INFO] [PluginBootServer:182] : Plugin Application Context Path [/Volumes/GENERAL/git/plugin.boot/target/classes/]
[2020-07-09 19:02:36] [ INFO] [PluginBootServer:94] : Plugin enviroment boot:com.YaNan.framework.boot.StandEnvironmentBoot
[2020-07-09 19:02:39] [ INFO] [StandEnvironmentBoot:60] : loaded properties from /Volumes/GENERAL/git/plugin.boot/target/classes/jdb.properties
[2020-07-09 19:02:39] [ INFO] [StandEnvironmentBoot:60] : loaded properties from /Volumes/GENERAL/git/plugin.boot/target/classes/log4j.properties
[2020-07-09 19:02:39] [DEBUG] [StandEnvironmentBoot:98] : environment properties loaded at [2652 ms]
[2020-07-09 19:02:39] [ INFO] [StandEnvironmentBoot:79] : loaded plugin from /Volumes/GENERAL/git/plugin.boot/target/classes/plugin.yc
[2020-07-09 19:02:39] [ INFO] [StandEnvironmentBoot:79] : loaded plugin from /Volumes/GENERAL/git/plugin.boot/target/classes/boot.jdb.yc
[2020-07-09 19:02:40] [DEBUG] [DefaultDataSource:87] : init datasource [testJdb]
[2020-07-09 19:02:40] [DEBUG] [DefaultDataSource:94] : DefaultDataSource 
。。。
[2020-07-09 19:02:42] [DEBUG] [JDBContext:155] : build SELECT wrapper fragment , wrapper id : "testSql.query" ,ref : false
[2020-07-09 19:02:42] [DEBUG] [JDBContext:155] : build SELECT wrapper fragment , wrapper id : "testSql.query5" ,ref : false
[2020-07-09 19:02:42] [DEBUG] [JDBContext:155] : build SELECT wrapper fragment , wrapper id : "testSql.query1" ,ref : false
[2020-07-09 19:02:42] [DEBUG] [JDBContext:155] : build SELECT wrapper fragment , wrapper id : "testSql.queryCount" ,ref : false
[2020-07-09 19:02:42] [DEBUG] [JDBContext:155] : build INSERT wrapper fragment , wrapper id : "com.YaNan.billing.service.user.UserService.insertUser" ,ref : false
[2020-07-09 19:02:42] [DEBUG] [MapperInterfaceProxyBuilder:47] : prepared interface class com.YaNan.billing.service.billing.BillingService
[2020-07-09 19:02:42] [DEBUG] [MapperInterfaceProxyBuilder:47] : prepared interface class com.YaNan.billing.service.user.UserService
[2020-07-09 19:02:42] [ INFO] [StandEnvironmentBoot:79] : loaded plugin from /Volumes/GENERAL/git/plugin.boot/target/classes/boot.mvc.yc
[2020-07-09 19:02:42] [DEBUG] [StandEnvironmentBoot:101] : environment model loaded at [2772 ms]
[2020-07-09 19:02:42] [ INFO] [PluginBootServer:71] : Application started at 7649[ms]

```
