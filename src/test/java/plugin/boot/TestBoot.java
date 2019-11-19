package plugin.boot;

import java.io.File;
import java.util.Arrays;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.DefaultInstanceManager;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.SimpleInstanceManager;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.servlets.CoreDispatcher;
import com.YaNan.frame.servlets.ServletBuilder;
import com.YaNan.frame.servlets.ServletContextInit;
import com.YaNan.frame.servlets.session.TokenContextInit;
import com.YaNan.frame.servlets.session.filter.TokenFilter;
import com.YaNan.frame.utils.resource.AbstractResourceEntry;
import com.YaNan.frame.utils.resource.PackageScanner;
import com.YaNan.frame.utils.resource.PackageScanner.ClassInter;
import com.YaNan.frame.utils.resource.ResourceManager;
import com.YaNan.frame.utils.resource.ResourceScanner;
import com.YaNan.frame.utils.resource.ResourceScanner.ResourceInter;
import com.test.test;

public class TestBoot {
	public static void main(String[] args) throws LifecycleException {
		

	}
}
