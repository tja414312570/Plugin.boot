package com.yanan.framework.boot;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 引导参数组设置
 * @author yanan
 *
 */
@Retention(RUNTIME)
@Target({ TYPE })
public @interface BootArgsGroups {
	BootArgs[] value();
}