package com.yanan.framework.boot;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 引导参数设置
 * @author yanan
 *
 */
@Retention(RUNTIME)
@Target({ TYPE })
@Repeatable(BootArgsGroups.class)
public @interface BootArgs {
	/**
	 * 引导参数名
	 * @return
	 */
	String name();
	/**
	 * 引导参数值
	 * @return
	 */
	String value();

}