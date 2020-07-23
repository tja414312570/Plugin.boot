package com.yanan.framework.boot;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ TYPE })
public @interface PluginBoot {
	/**
	 * 上下文类
	 * @return
	 */
	Class<?>[] contextClass() default {};
	/**
	 * 扫描位置
	 */
	String scnner() default "";
	/**
	 * 项目目录
	 * @return
	 */
	String appBase() default "user.dir";
	
}