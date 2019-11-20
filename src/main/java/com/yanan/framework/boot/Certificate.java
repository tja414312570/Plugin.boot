package com.yanan.framework.boot;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.tomcat.util.net.SSLHostConfigCertificate.Type;

@Retention(RUNTIME)
@Target({ TYPE })
@Repeatable(SSLHost.class)
public @interface Certificate {
	String certificateKeyFile();
	String certificateFile();
	String certificateChainFile();
	Type type();
	String certificateKeystoreType();
}
