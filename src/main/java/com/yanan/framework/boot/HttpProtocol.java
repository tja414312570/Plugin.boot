package com.yanan.framework.boot;

public interface HttpProtocol {
	/**
	 * HTTP/1.1
	 */
	static String Http11 = "HTTP/1.1";
	/**
	 * AJP/1.3
	 */
	static String Ajp13 = "AJP/1.3";
	/**
	 * AJP/1.3
	 */
	static String Http2 = "AJP/1.3";
	 /**
	  * org.apache.coyote.http11.Http11NioProtocol
	  */
	 static String http11Nio = "org.apache.coyote.http11.Http11NioProtocol";
	 /**
	  *  org.apache.coyote.http11.Http11AprProtocol
	  */
	 static String Http11AprProtocol = "org.apache.coyote.http11.Http11AprProtocol";
	 /**
	  * org.apache.coyote.http2.Http2Protocol
	  */
	 static String Http2Protocol = "org.apache.coyote.http2.Http2Protocol";
	 
}
