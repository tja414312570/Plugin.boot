package com.yanan.framework.boot.jdb;

import javax.annotation.PostConstruct;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.autowired.enviroment.Variable;

@Variable("plugin.jdb")
@Register(layInit = false)
public class PluginBootJdbRegister {
	private String url;
	
	private String name;
	
	private String password;
	@PostConstruct
	public void start() {
		System.err.println(url);
		System.err.println(name);
		System.err.println(password);
		throw new RuntimeException("");
	}
}
