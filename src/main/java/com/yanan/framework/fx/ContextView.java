package com.yanan.framework.fx;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 布局文件
 * @author yanan
 *
 */
@Retention(RUNTIME)
@Target({ TYPE })
public @interface ContextView {
	String value() default "";
}