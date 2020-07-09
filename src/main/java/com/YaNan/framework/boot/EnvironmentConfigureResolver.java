package com.YaNan.framework.boot;

import com.YaNan.frame.plugin.annotations.Service;

@Service
@FunctionalInterface
public interface EnvironmentConfigureResolver {
	void resover(Environment environment);
}
