package com.yanan.framework.boot;

import com.yanan.framework.plugin.Environment;
import com.yanan.framework.plugin.PlugsFactory;

import javafx.application.Application;

public interface PluginApplication {
	default void start() {
		if(Application.class.isAssignableFrom(this.getClass())) {
			LauncherImpl.launchApplication((Application)this, Environment.getEnviroment().getVariable(Environment.MAIN_ARGS));
		}
	};
	static <T> T start(Class<T> appClass) {
		T app = PlugsFactory.getPluginsInstance(appClass);
		if(Application.class.isAssignableFrom(appClass)) {
			LauncherImpl.launchApplication((Application)app, Environment.getEnviroment().getVariable(Environment.MAIN_ARGS));
		}
		return app;
	}
}
