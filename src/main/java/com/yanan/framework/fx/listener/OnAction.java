package com.yanan.framework.fx.listener;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 引导参数设置
 * @author yanan
 *
 */
@Retention(RUNTIME)
@Target({ ElementType.METHOD })
public @interface OnAction {
	String value();
}