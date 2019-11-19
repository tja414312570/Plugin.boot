package plugin.boot;

import com.YaNan.frame.servlets.annotations.RequestMapping;
import com.YaNan.frame.servlets.session.annotation.Authentication;

public class Test {
	@Authentication(roles="root")
	@RequestMapping("/")
	public String sayHello() {
		return "hello world";
	}
}
