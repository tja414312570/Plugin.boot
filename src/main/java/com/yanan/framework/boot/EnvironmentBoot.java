package com.yanan.framework.boot;

import com.yanan.frame.plugin.Environment;

public interface EnvironmentBoot {
	void start(Environment environment);
	void stop(Environment environment);
}