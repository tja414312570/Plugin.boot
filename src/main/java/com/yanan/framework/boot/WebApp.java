package com.yanan.framework.boot;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ TYPE })
public @interface WebApp {
	/**
	 * WebApp请求上下文
	 * @return
	 */
	String contextPath();
	/**
	 * WebApp的资源路径
	 * @return
	 */
	String docBase();
}
