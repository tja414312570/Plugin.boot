package com.yanan.framework.boot;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.SimpleInstanceManager;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.servlets.CoreDispatcher;
import com.YaNan.frame.servlets.ServletContextInit;
import com.YaNan.frame.servlets.session.TokenContextInit;
import com.YaNan.frame.servlets.session.filter.TokenFilter;

/**
 * Plugin 引导服务
 * 
 * @author yanan
 *
 */
public class PluginBootServer {
	// tomcat 实例
	private static Tomcat tomcat;
	static org.slf4j.Logger log = LoggerFactory.getLogger(PluginBootServer.class);

	/**
	 * 运行服务
	 * 
	 * @param configure
	 * @throws LifecycleException
	 */
	public static void run(Class<?> configure) throws LifecycleException {
		log.info("Plugin Boot Snapshot Version!");
		PluginBoot pluginBoot = configure.getAnnotation(PluginBoot.class);
		if (pluginBoot == null)
			pluginBoot = getDefaultConfigure(PluginBoot.class);
		addPluginContext(configure, pluginBoot);
		//初始化Tomcat
		initTomcat(pluginBoot);
		//设置基本路径
		setHostAppBase(pluginBoot);
		//配置ServletContext的监听
		org.apache.catalina.Context ctx = tomcat.addContext("/", "webapp");// 网络访问路径
		ctx.setInstanceManager(new SimpleInstanceManager());
		ctx.addLifecycleListener(new ContextConfig() {
			@Override
			public void lifecycleEvent(LifecycleEvent event) {
				StandardContext sc = (StandardContext) event.getSource();
				this.context = sc;
				if (event.getLifecycle().getState().equals(LifecycleState.STARTING_PREP)) {
					ServletContextEvent sce = new ServletContextEvent(sc.getServletContext());
//					new PluginAppincationContext().contextInitialized(sce);
					new ServletContextInit().contextInitialized(sce);
					new TokenContextInit().contextInitialized(sce);
				}
				if (event.getLifecycle().getState().equals(LifecycleState.STARTED)) {
				}
//				super.lifecycleEvent(event);
			}
		});
		//添加核心Servlet
		Tomcat.addServlet(ctx, "coreServlet", new CoreDispatcher()); 
		ctx.addServletMappingDecoded("/*", "coreServlet");
		//添加过滤器
		addFilter(ctx,configure);
		//增加WebApp
		addWebApp(configure);
		tomcat.init();
		tomcat.start();
		log.info("Plugin Boot Started Success!");
		tomcat.getServer().await();
	}

	
	/**
	 * WebContext添加过滤器
	 * @param ctx
	 * @param configure
	 */
	public static void addFilter(org.apache.catalina.Context ctx, Class<?> configure) {
//		FilterDef filterDef = new FilterDef();
//		filterDef.setFilterName(TokenFilter.class.getSimpleName());
//		filterDef.setFilterClass(TokenFilter.class.getName());
//		ctx.addFilterDef(filterDef);
//
//		FilterMap filterMap = new FilterMap();
//		filterMap.setFilterName(TokenFilter.class.getSimpleName());
//		filterMap.addURLPattern("/*");
//		ctx.addFilterMap(filterMap);
		addFilter(ctx, new TokenFilter(),TokenFilter.class);
		try {
			List<Filter> filters = PlugsFactory.getPlugsInstanceList(Filter.class);
			for(Filter filter : filters) {
				addFilter(ctx, filter,PlugsFactory.getPlugsHandler(filter).getRegisterDescription().getRegisterClass());
			}
		}catch(Exception e) {
			
		}
		
	}
	/**
	 * WebContext时添加过滤器
	 * @param ctx
	 * @param filter
	 * @param filterClass
	 */
	public static void addFilter(org.apache.catalina.Context ctx, Filter filter, Class<?> filterClass) {
		WebFilter webFilter = filterClass.getAnnotation(WebFilter.class);
		if(webFilter != null ) {
			FilterDef filterDef = new FilterDef();
			filterDef.setFilterName(webFilter.filterName());
//					filterDef.setFilterClass(filter.getClass().getName());
			filterDef.setFilter(filter);
			filterDef.setSmallIcon(webFilter.smallIcon());
			filterDef.setLargeIcon(webFilter.largeIcon());
			filterDef.setDescription(webFilter.description());
			filterDef.setDisplayName(webFilter.displayName());
			filterDef.setAsyncSupported(webFilter.asyncSupported()+"");
			WebInitParam webInitParam = filterClass.getAnnotation(WebInitParam.class);
			if(webInitParam != null)
				filterDef.addInitParameter(webInitParam.name(), webInitParam.value());
			ctx.addFilterDef(filterDef);
			String[] urlPatterns = webFilter.urlPatterns();
			for(String urlPattern : urlPatterns) {
				FilterMap filterMap = new FilterMap();
				filterMap.setFilterName(webFilter.filterName());
				filterMap.addURLPattern(urlPattern);
				ctx.addFilterMap(filterMap);
			}
		}
	}
	/**
	 * 初始化Tomcat
	 * @param pluginBoot
	 */
	public static void initTomcat(PluginBoot pluginBoot) {
		if (tomcat == null) {
			synchronized (PluginBootServer.class) {
				if (tomcat == null) {
					tomcat = new Tomcat();
					tomcat.setHostname(pluginBoot.host());
					tomcat.setPort(pluginBoot.port());
					tomcat.setBaseDir(pluginBoot.baseDir());
					String DEFAULT_PROTOCOL = pluginBoot.DEFAULT_PROTOCOL();
					Connector connector = new Connector(DEFAULT_PROTOCOL);
					connector.setPort(pluginBoot.port());
					tomcat.getService().addConnector(connector);
					StandardServer server = (StandardServer) tomcat.getServer();
					AprLifecycleListener listener = new AprLifecycleListener();
					server.addLifecycleListener(listener);
				}
			}
		}
	}
    /**
     * 添加Plugin的扫描上下文
     * @param configure
     * @param pluginBoot
     */
	public static void addPluginContext(Class<?> configure, PluginBoot pluginBoot) {
		if (pluginBoot == null || pluginBoot.contextClass().equals(PluginBoot.class)) {
			// 将class文件上下文添加到Plug中
			PlugsFactory.getInstance().addScanPath(configure);
		} else {
			PlugsFactory.getInstance().addScanPath(pluginBoot.contextClass());
		}
		PlugsFactory.getInstance().init0();
	}
	/**
	 * 设置App的基本路径
	 * @param pluginBoot
	 */
	public static void setHostAppBase(PluginBoot pluginBoot) {
		String userDir = System.getProperty(pluginBoot.appBase()) + File.separator;
		tomcat.getHost().setAppBase(userDir);
	}
	/**
	 * 添加WebApp
	 * @param configure
	 */
	public static void addWebApp(Class<?> configure) {
		WebAppGroups webAppGroups = configure.getAnnotation(WebAppGroups.class);
		if(webAppGroups != null && webAppGroups.enable()) {
			if(webAppGroups.value().length != 0 ) {
				for(WebApp webApp : webAppGroups.value()) {
					tomcat.addWebapp(webApp.contextPath(), webApp.docBase());
				}
			}else {
				WebApp webApp = getDefaultConfigure(WebApp.class);
				tomcat.addWebapp(webApp.contextPath(), webApp.docBase());
			}
		}else {
			WebApp webApp = configure.getAnnotation(WebApp.class);
			if(webApp != null)
				tomcat.addWebapp(webApp.contextPath(), webApp.docBase());
		}
	}
	/**
	 * 获取默认配置
	 * @param annotationClass
	 * @return
	 */
	private static <T extends Annotation> T getDefaultConfigure(Class<T> annotationClass) {
		return DefaultBootConfigure.class.getAnnotation(annotationClass);
	}
	/**
	 * 停止运行
	 * @throws LifecycleException
	 */
	public static void stop() throws LifecycleException {
		if (tomcat != null)
			tomcat.stop();
	}
}
