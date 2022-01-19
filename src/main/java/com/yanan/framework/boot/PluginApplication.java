package com.yanan.framework.boot;

import com.yanan.framework.plugin.Environment;

import javafx.application.Application;

public interface PluginApplication {
	default void start() {
		if(Application.class.isAssignableFrom(this.getClass())) {
			LauncherImpl.launchApplication((Application)this, Environment.getEnviroment().getVariable(Environment.MAIN_ARGS));
		}
	};
}
