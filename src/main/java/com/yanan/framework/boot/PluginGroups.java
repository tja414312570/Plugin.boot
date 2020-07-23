package com.yanan.framework.boot;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ TYPE })
public @interface PluginGroups {
	/**
	 * 上下文类
	 * @return
	 */
	Plugin[] value();
}