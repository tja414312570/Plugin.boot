package com.yanan.framework.boot;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ TYPE })
public @interface SSLHost {
	/**
	 * 证书
	 * @return
	 */
	Certificate[] value() default {};
	/**
	 * SSL端口
	 * @return
	 */
	int port() default 443;
	/**
	 * 证书存储位置
	 * @return
	 */
	String certificateKeyStoreFile() default "";
	/**
	 * SSL协议
	 * @return
	 */
	String sslProtocol() default "TLS";
	/**
	 * 证书密码
	 * @return
	 */
	String certificateKeystorePassword() default "";
	/**
	 * SSL 请求协议
	 * @return
	 */
	String scheme() default "https";
	/**
	 * 启用安全
	 * @return
	 */
	boolean secure() default true;
	/**
	 * 
	 * @return
	 */
	String URIEncoding() default "utf-8";
	/**
	 * SSL升级协议
	 * @return
	 */
	String[] upgradeProtocol() default {};
}
