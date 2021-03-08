package com.yanan.framework.boot.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yanan.framework.boot.EnvironmentBoot;
import com.yanan.framework.boot.StandEnvironmentBoot;
import com.yanan.framework.plugin.Environment;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.token.web.TokenContextInit;
import com.yanan.framework.token.web.TokenFilter;
import com.yanan.framework.webmvc.CoreDispatcher;
import com.yanan.framework.webmvc.ServletContextInit;
import com.yanan.utils.reflect.AppClassLoader;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.resource.ResourceManager;
import com.yanan.utils.string.StringUtil;

/**
 * Plugin Web 引导服务
 * 
 * @author yanan
 *
 */
@Register
public class WebEnvironmentBoot extends StandEnvironmentBoot implements EnvironmentBoot{
	static final String WEB_ENVIROMNET_BOOT_PATH = "com.yanan.framework.boot.web.WebEnvironmentBoot";
	private static final String WEB_ENVIROMNET_BOOT_CLASS = "-boot-web-environment-class";
	private static final String WEB_ENVIROMNET_BOOT_LOADER = "-boot-web-environment-loader";
	// tomcat 实例
	private static Tomcat tomcat;
	static Logger log = LoggerFactory.getLogger(WebEnvironmentBoot.class);
	private static boolean b = false;

	/**
	 * WebContext添加过滤器
	 * @param ctx
	 * @param configure
	 */
	public static void addFilter(org.apache.catalina.Context ctx, Class<?> configure) {
		addFilter(ctx, new TokenFilter(),TokenFilter.class);
		if(PlugsFactory.getPlugin(Filter.class) != null) {
			List<Filter> filters = PlugsFactory.getPluginsInstanceList(Filter.class);
			for(Filter filter : filters) {
				addFilter(ctx, filter,PlugsFactory.getPluginsHandler(filter).getRegisterDefinition().getRegisterClass());
			}
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
	public static void initTomcat(WebPluginBoot pluginBoot,Class<?> contextClass) {
		if (tomcat == null) {
			synchronized (WebEnvironmentBoot.class) {
				if (tomcat == null) {
					tomcat = new Tomcat();
					tomcat.setHostname(pluginBoot.host());
					tomcat.setPort(pluginBoot.port());
					tomcat.setBaseDir(pluginBoot.baseDir());
					tomcat.setAddDefaultWebXmlToWebapp(true);
					if(System.getProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE") == null)
						System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", true+"");
					//创建一个基础的连接 一般8080
					Connector connector = new Connector(pluginBoot.httpProtocol());
					//是否需要证书
					tryAddSslConnector(pluginBoot, contextClass, connector);
					connector.setPort(pluginBoot.port());
					//如果需要重定向
					if(pluginBoot.redirectPort() >-1 )
						connector.setRedirectPort(pluginBoot.redirectPort());
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
				log.info("Upgrade Protocol:"+protocol.getClass().getName());
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
	public static void tryAddSslConnector(WebPluginBoot pluginBoot, Class<?> contextClass, Connector connector) {
		SSLHost sslHost = contextClass .getAnnotation(SSLHost.class);
		Certificate certificate = contextClass .getAnnotation(Certificate.class);
		if(certificate != null || sslHost != null) {
			log.info("Try Add SSl Connector !");
			log.info("SSL host Info:"+sslHost);
			log.info("Certificate Info:"+certificate);
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
	public static void addPluginContext(Class<?> configure, WebPluginBoot pluginBoot) {
		if (pluginBoot == null || pluginBoot.contextClass().length == 0) {
			// 将class文件上下文添加到Plug中
			PlugsFactory.getInstance().addScanPath(configure);
			log.info("Plugin Application Context Path "+Arrays.toString(ResourceManager.getClassPath(configure)));
		} else {
			PlugsFactory.getInstance().addScanPath(pluginBoot.contextClass());
			log.info("Plugin Application Context Path "+Arrays.toString(ResourceManager.getClassPath(pluginBoot.contextClass())));
		}
		PlugsFactory.init();
	}
	/**
	 * 设置App的基本路径
	 * @param pluginBoot
	 */
	public static void setHostAppBase(WebPluginBoot pluginBoot) {
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
					log.info("Web Application, context path:"+webApp.contextPath()+", doc base:"+webApp.docBase());
				}
			}else { 
				WebApp webApp = getDefaultConfigure(WebApp.class);
				tomcat.addWebapp(webApp.contextPath(), webApp.docBase());
				log.info("Web Application, context path:"+webApp.contextPath()+", doc base:"+webApp.docBase());
			}
		}else {
			WebApp webApp = configure.getAnnotation(WebApp.class);
			if(webApp != null) {
				tomcat.addWebapp(webApp.contextPath(), webApp.docBase());
				log.info("Web Application, context path:"+webApp.contextPath()+", doc base:"+webApp.docBase());
			}
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

	public void start(Environment environment) {
		log.info("plugin boot environment in web [tomcat]");
		super.start(environment);
		Class<?> mainClass = environment.getVariable(Environment.MAIN_CLASS);
		WebPluginBoot pluginBoot = mainClass.getAnnotation(WebPluginBoot.class);
		if (pluginBoot == null)
			pluginBoot = getDefaultConfigure(WebPluginBoot.class);
		addPluginContext(mainClass, pluginBoot);
		//初始化Tomcat
		initTomcat(pluginBoot,mainClass);
		log.info("Web Application run with :"+tomcat.getConnector().getDomain()+" "+tomcat.getHost().getName()+":"+tomcat.getConnector().getPort());
		//设置基本路径
		setHostAppBase(pluginBoot);
		//配置ServletContext的监听
		WebContext webContext = mainClass.getAnnotation(WebContext.class);
		String docBase = "";
		if(webContext == null) {
			webContext = getDefaultConfigure(WebContext.class);
			if(new File(pluginBoot.baseDir(),webContext.docBase()).exists())
				docBase = webContext.docBase();
		}else {
			docBase = webContext.docBase();
		}
		log.info("Web Application Context Info, Context Path:"+webContext.contextPath()+", Doc Base:"+docBase);
		org.apache.catalina.Context ctx = tomcat.addContext(webContext.contextPath(), docBase);// 网络访问路径
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
					try {
						List<ServletContextListener> lists = PlugsFactory.getPluginsInstanceList(ServletContextListener.class);
						for(ServletContextListener listener : lists) {
							listener.contextInitialized(sce);
						}
					} catch (Exception e) {
						log.debug("none any servlet context listener register found ! {}",e.getMessage());
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
		addFilter(ctx,mainClass);
		//增加WebApp
		addWebApp(mainClass);
		try {
			tomcat.init();
			tomcat.start();
		} catch (LifecycleException e) {
			e.printStackTrace();
			System.exit(0);
			new WebPluginBootExcetion("failed to init tomcat;",e);
		}
		tomcat.getServer().await();
		
	}
	public void started(Environment environment) {
		//隔离环境，使用新的加载器
		AppClassLoader appClassLoader = new AppClassLoader();
		appClassLoader.addShardClass(Environment.class.getName());
		appClassLoader.addShardClass("org.apache.*");
		appClassLoader.addShardClass("java*");
		//使用环境隔离模式
		appClassLoader.enableExclusive();
		environment.setVariable(WebEnvironmentBoot.WEB_ENVIROMNET_BOOT_LOADER, appClassLoader);
		//获取环境引导
		InputStream is = WebEnvironmentBoot.class.getClassLoader().getResourceAsStream(WEB_ENVIROMNET_BOOT_PATH.replace(".", "/")+".class");
		byte[] bytes;
		try {
			bytes = new byte[is.available()];
			is.read(bytes);
			Class<?> WebEnvironmentBootClass = AppClassLoader.loadClass(WEB_ENVIROMNET_BOOT_PATH, bytes, appClassLoader);
			environment.setVariable(WebEnvironmentBoot.WEB_ENVIROMNET_BOOT_CLASS, WebEnvironmentBootClass);
			ReflectUtils.invokeStaticMethod(WebEnvironmentBootClass, "started",environment);
		} catch (IOException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop(Environment environment) {
		super.stop(environment);
		try {
			tomcat.stop();
		} catch (LifecycleException e) {
			new WebPluginBootExcetion("failed to stop tomcat;",e);
		}
	}
	
}