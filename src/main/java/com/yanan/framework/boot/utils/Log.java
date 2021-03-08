package com.yanan.framework.boot.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 实例化之后执行某方法
 * 仅作用同一实例
 * @author yanan
 */
@Target(ElementType.FIELD )
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
}