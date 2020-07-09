package com.YaNan.framework.boot;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ TYPE })
public @interface Plugin {
	/**
	 * 上下文类
	 * @return
	 */
	Class<?> value();
	/**
	 * 项目目录
	 * @return
	 */
	String[] properties() default {};
	
}
