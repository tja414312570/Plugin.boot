package com.yanan.framework.boot;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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
import org.apache.catalina.startup.Constants;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.SimpleInstanceManager;
import org.apache.tomcat.util.buf.UriUtil;
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

		Tomcat.addServlet(ctx, "coreServlet", new CoreDispatcher()); // 配置servlet
		ctx.addServletMappingDecoded("/*", "coreServlet");// 配置servlet映射路径

		addFilter(ctx,configure);
		StandardServer server = (StandardServer) tomcat.getServer();// 添加监听器，不知何用
		AprLifecycleListener listener = new AprLifecycleListener();
		server.addLifecycleListener(listener);
		// 设置appBase为项目所在目录
		setHostAppBase(pluginBoot);
		// 设置WEB-INF文件夹所在目录
		// 该文件夹下包含web.xml
		if (pluginBoot.enableWebApp()) {
			addWebApp(configure);
		}
		tomcat.init();
		tomcat.start();// 启动tomcat
		tomcat.getServer().await();// 维持tomcat服务，否则tomcat一启动就会关闭
	}

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
				}
			}
		}
	}
	public static String noDefaultWebXmlPath() {
        return Constants.NoDefaultWebXml;
    }
    protected static URL getWebappConfigFile(String path, String contextName) {
        File docBase = new File(path);
        if (docBase.isDirectory()) {
            return getWebappConfigFileFromDirectory(docBase, contextName);
        } else {
            return getWebappConfigFileFromWar(docBase, contextName);
        }
    }

    private static URL getWebappConfigFileFromDirectory(File docBase, String contextName) {
        URL result = null;
        File webAppContextXml = new File(docBase, Constants.ApplicationContextXml);
        if (webAppContextXml.exists()) {
            try {
                result = webAppContextXml.toURI().toURL();
            } catch (MalformedURLException e) {
            	e.printStackTrace();
//                Logger.getLogger(getLoggerName(getHost(), contextName)).log(Level.WARNING,
//                        sm.getString("tomcat.noContextXml", docBase), e);
            }
        }
        return result;
    }

    private static URL getWebappConfigFileFromWar(File docBase, String contextName) {
        URL result = null;
        try (JarFile jar = new JarFile(docBase)) {
            JarEntry entry = jar.getJarEntry(Constants.ApplicationContextXml);
            if (entry != null) {
                result = UriUtil.buildJarUrl(docBase, Constants.ApplicationContextXml);
            }
        } catch (IOException e) {
        	e.printStackTrace();
//            Logger.getLogger(getLoggerName(getHost(), contextName)).log(Level.WARNING,
//                    sm.getString("tomcat.noContextXml", docBase), e);
        }
        return result;
    }
	public static void addPluginContext(Class<?> configure, PluginBoot pluginBoot) {
		if (pluginBoot == null || pluginBoot.contextClass().equals(PluginBoot.class)) {
			// 将class文件上下文添加到Plug中
			PlugsFactory.getInstance().addScanPath(configure);
		} else {
			PlugsFactory.getInstance().addScanPath(pluginBoot.contextClass());
		}
		PlugsFactory.getInstance().init0();
	}

	public static void setHostAppBase(PluginBoot pluginBoot) {
		String userDir = System.getProperty(pluginBoot.appBase()) + File.separator;
		tomcat.getHost().setAppBase(userDir);
	}

	public static void addWebApp(Class<?> configure) {
		WebApp webApp;
		if (configure == null) {
			webApp = getDefaultConfigure(WebApp.class);
		} else {
			webApp = configure.getAnnotation(WebApp.class);
			// 当访问localhost:端口号时，会默认访问该目录下的index.html/jsp页面
			if (webApp == null) {
				webApp = getDefaultConfigure(WebApp.class);
			}
		}
		tomcat.addWebapp(webApp.contextPath(), webApp.docBase());
	}

	@SuppressWarnings("unchecked")
	private static <T> T getDefaultConfigure(Class<? extends Annotation> annotationClass) {
		return (T) DefaultBootConfigure.class.getAnnotation(annotationClass);
	}

	public static void stop() throws LifecycleException {
		if (tomcat != null)
			tomcat.stop();
	}
}
