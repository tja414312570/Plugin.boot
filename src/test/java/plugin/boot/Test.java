package plugin.boot;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.PushBuilder;

import com.YaNan.frame.servlets.annotations.RequestMapping;
import com.YaNan.frame.servlets.session.annotation.Authentication;

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
		return "hello world";
	}
	@RequestMapping("/push")
	public String testPush(HttpServletRequest request) {
		PushBuilder pushBuilder  = request.newPushBuilder();
		System.out.println(request.getRequestURL());
		System.out.println(pushBuilder);
		if (pushBuilder != null) {
			   pushBuilder.path("images/hero-banner.jpg").push();
			   pushBuilder.path("css/menu.css").push();
			   pushBuilder.path("js/marquee.js").push();
			}
		return "hello world";
	}
}
