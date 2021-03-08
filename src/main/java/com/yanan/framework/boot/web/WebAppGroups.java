package com.yanan.framework.boot.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WebAppGroups {
	/**
	 * WebApp的集合
	 * @return
	 */
	WebApp[] value() default {};
	/**
	 * 是否启用WebApp支持
	 * @return
	 */
	boolean enable() default true;
}