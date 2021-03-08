package com.yanan.framework.boot;

import com.yanan.framework.plugin.Environment;
import com.yanan.framework.plugin.annotations.Service;

@Service
@FunctionalInterface
public interface EnvironmentConfigureResolver {
	void resover(Environment environment);
}