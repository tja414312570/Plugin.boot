package plugin.boot;

import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.PushBuilder;

import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.webmvc.ServletMapping;
import com.yanan.framework.webmvc.annotations.RequestMapping;
import com.yanan.framework.webmvc.session.annotation.Authentication;
import com.yanan.test.ant.AntService1;

public class Test2 {
	@Service
	private AntService1 service;
	@Authentication(roles="root")
	@RequestMapping("/")
	public String sayHello(HttpServletRequest request) {
		PushBuilder pushBuilder  = request.newPushBuilder();
		if (pushBuilder != null) {
			   pushBuilder.path("images/hero-banner.jpg").push();
			   pushBuilder.path("css/menu.css").push();
			   pushBuilder.path("js/marquee.js").push();
			}
		return "hello worlds";
	}
	@RequestMapping("/pushs")
	public String testPush(HttpServletRequest request) {
		System.out.println(ServletMapping.getInstance());
		System.out.println(PlugsFactory.getPluginsInstanceList(ServletContextListener.class));
		PushBuilder pushBuilder  = request.newPushBuilder();
		System.out.println(request.getRequestURL());
		System.out.println(pushBuilder);
		if (pushBuilder != null) {
			   pushBuilder.path("images/hero-banner.jpg").push();
			   pushBuilder.path("css/menu.css").push();
			   pushBuilder.path("js/marquee.js").push();
			}
		
		return "hello world this is hot class" +service.add(3, 2);
	}
}