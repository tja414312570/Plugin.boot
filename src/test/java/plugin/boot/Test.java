package plugin.boot;

import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.PushBuilder;

import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.webmvc.annotations.RequestMapping;
import com.yanan.framework.webmvc.session.Token;
import com.yanan.framework.webmvc.session.annotation.Authentication;

public class Test {
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
	@RequestMapping("/push")
	public String testPush(HttpServletRequest request,Token token) {
		System.out.println(Token.getToken().getTokenId());
		System.out.println(PlugsFactory.getPluginsInstanceList(ServletContextListener.class));
		PushBuilder pushBuilder  = request.newPushBuilder();
		System.out.println(request.getRequestURL());
		token.addRole("root");
		System.out.println(pushBuilder);
		if (pushBuilder != null) {
			   pushBuilder.path("images/hero-banner.jpg").push();
			   pushBuilder.path("css/menu.css").push();
			   pushBuilder.path("js/marquee.js").push();
			}
		
		return request.getRequestURL().toString()+"  "+token.getTokenId();
	}
}