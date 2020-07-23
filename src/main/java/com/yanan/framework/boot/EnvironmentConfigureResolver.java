package com.yanan.framework.boot;

import com.yanan.frame.plugin.Environment;
import com.yanan.frame.plugin.annotations.Service;

@Service
@FunctionalInterface
public interface EnvironmentConfigureResolver {
	void resover(Environment environment);
}