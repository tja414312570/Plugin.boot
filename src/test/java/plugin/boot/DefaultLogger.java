package plugin.boot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.autowired.plugin.CustomProxy;

@Register(register = Logger.class)
public class DefaultLogger implements CustomProxy<Logger>{
	public DefaultLogger() {
	}
	@Override
	public Logger getInstance() {
		System.out.println(this+"--->");
		new RuntimeException().printStackTrace();
		Logger logger = LoggerFactory.getLogger(DefaultLogger.class);
		return logger;
	}
}
