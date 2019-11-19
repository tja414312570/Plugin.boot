package com.yanan.framework.boot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WebContextGroups {
	/**
	 * WebApp的集合
	 * @return
	 */
	WebContext[] value();
}
