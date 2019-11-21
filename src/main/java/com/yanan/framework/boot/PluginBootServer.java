package com.yanan.framework.boot;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
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
import org.apache.coyote.UpgradeProtocol;
import org.apache.tomcat.SimpleInstanceManager;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.servlets.CoreDispatcher;
import com.YaNan.frame.servlets.ServletContextInit;
import com.YaNan.frame.servlets.session.TokenContextInit;
import com.YaNan.frame.servlets.session.filter.TokenFilter;
import com.YaNan.frame.utils.StringUtil;

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
	private static boolean b = false;

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
		initTomcat(pluginBoot,configure);
		//设置基本路径
		setHostAppBase(pluginBoot);
		//配置ServletContext的监听
		WebContext webContext = configure.getAnnotation(WebContext.class);
		if(webContext == null) 
			webContext = getDefaultConfigure(WebContext.class);
		org.apache.catalina.Context ctx = tomcat.addContext(webContext.contextPath(), webContext.docBase());// 网络访问路径
		ctx.setInstanceManager(new SimpleInstanceManager());
		ctx.addLifecycleListener(new ContextConfig() {
			@Override
			public void lifecycleEvent(LifecycleEvent event) {
				StandardContext sc = (StandardContext) event.getSource();
				this.context = sc;
				if (event.getLifecycle().getState().equals(LifecycleState.STARTING_PREP) && !b) {
					ServletContextEvent sce = new ServletContextEvent(sc.getServletContext());
//					new PluginAppincationContext().contextInitialized(sce);
					new ServletContextInit().contextInitialized(sce);
					new TokenContextInit().contextInitialized(sce);
					List<ServletContextListener> lists = PlugsFactory.getPlugsInstanceList(ServletContextListener.class);
					for(ServletContextListener listener : lists) {
						listener.contextInitialized(sce);
					}
					b = true;
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
	public static void initTomcat(PluginBoot pluginBoot,Class<?> contextClass) {
		if (tomcat == null) {
			synchronized (PluginBootServer.class) {
				if (tomcat == null) {
					tomcat = new Tomcat();
					tomcat.setHostname(pluginBoot.host());
					tomcat.setPort(pluginBoot.port());
					tomcat.setBaseDir(pluginBoot.baseDir());
					//创建一个基础的连接 一般8080
					Connector connector = new Connector(pluginBoot.httpProtocol());
					//是否需要证书
					tryAddSslConnector(pluginBoot, contextClass, connector);
					connector.setPort(pluginBoot.port());
					tomcat.getService().addConnector(connector);
					addUpgradeProtocols(connector, pluginBoot.upgradeProtocol());
					StandardServer server = (StandardServer) tomcat.getServer();
					AprLifecycleListener listener = new AprLifecycleListener();
					server.addLifecycleListener(listener);
				}
			}
		}
	}


	public static void addUpgradeProtocols(Connector connector, String[] upgradeProtocols) {
		for(String upgradeProtocol : upgradeProtocols) {
			try {
		        Class<?> clazz = Class.forName(upgradeProtocol);
		        UpgradeProtocol protocol  = (UpgradeProtocol) clazz.getConstructor().newInstance();
				connector.addUpgradeProtocol(protocol);
		    } catch (Exception e) {
		        log.error(e.getMessage(), e);
		    } 
		}
	}

	/**
	 * 添加SSL的Connector
	 * @param pluginBoot
	 * @param contextClass
	 * @param connector
	 */
	public static void tryAddSslConnector(PluginBoot pluginBoot, Class<?> contextClass, Connector connector) {
		SSLHost sslHost = contextClass .getAnnotation(SSLHost.class);
		Certificate certificate = contextClass .getAnnotation(Certificate.class);
		if(certificate != null || sslHost != null) {
			SSLHostConfig sslHostConfig;
			if(sslHost != null) {
				sslHostConfig = new SSLHostConfig();
				sslHostConfig.setSslProtocol(sslHost.sslProtocol());
				sslHostConfig.setSslProtocol(sslHost.sslProtocol());
				if(StringUtil.isNotEmpty(sslHost.certificateKeyStoreFile()))
					sslHostConfig.setCertificateKeystoreFile(sslHost.certificateKeyStoreFile());
				if(StringUtil.isNotEmpty(sslHost.certificateKeystorePassword()))
					sslHostConfig.setCertificateKeystorePassword(sslHost.certificateKeystorePassword());
			}else {
				sslHost = getDefaultConfigure(SSLHost.class);
				sslHostConfig = new SSLHostConfig();
				sslHostConfig.setSslProtocol(sslHost.sslProtocol());
			}
			if(certificate == null) {
				for(Certificate childCertificate : sslHost.value()) {
					SSLHostConfigCertificate sslHostConfigCertificate = buildSslCertificate(childCertificate, sslHostConfig);
					sslHostConfig.addCertificate(sslHostConfigCertificate);
				}
			}else {
				SSLHostConfigCertificate sslHostConfigCertificate = buildSslCertificate(certificate, sslHostConfig);
				sslHostConfig.addCertificate(sslHostConfigCertificate);
			}
			Connector sslConnector = new Connector(HttpProtocol.Http11);
			sslConnector.setScheme(sslHost.scheme());
			sslConnector.setSecure(sslHost.secure());
			sslConnector.setURIEncoding(sslHost.URIEncoding());
			sslConnector.setProperty("SSLEnabled", "true");
			sslConnector.setPort(sslHost.port());
			sslConnector.addSslHostConfig(sslHostConfig);
			addUpgradeProtocols(sslConnector, sslHost.upgradeProtocol());
			connector.setRedirectPort(sslHost.port());
			tomcat.getService().addConnector(sslConnector);
		}
	}


	public static SSLHostConfigCertificate buildSslCertificate(Certificate certificate, SSLHostConfig sslHostConfig) {
		SSLHostConfigCertificate sslHostConfigCertificate =  
				new SSLHostConfigCertificate(sslHostConfig,certificate.type());
		sslHostConfig.addCertificate(sslHostConfigCertificate);
		sslHostConfigCertificate.setCertificateFile(certificate.certificateFile());
		sslHostConfigCertificate.setCertificateChainFile(certificate.certificateChainFile());
		sslHostConfigCertificate.setCertificateKeyFile(certificate.certificateKeyFile());
		sslHostConfigCertificate.setCertificateKeystoreType(certificate.certificateKeystoreType());
		return sslHostConfigCertificate;
	}
    /**
     * 添加Plugin的扫描上下文
     * @param configure
     * @param pluginBoot
     */
	public static void addPluginContext(Class<?> configure, PluginBoot pluginBoot) {
		if (pluginBoot == null || pluginBoot.contextClass().length == 0) {
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
