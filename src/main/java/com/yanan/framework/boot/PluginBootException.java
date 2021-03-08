package com.yanan.framework.boot;

/**
 * 环境引导异常
 * @author yanan
 *
 */
public class PluginBootException extends RuntimeException {

	public PluginBootException(String msg, Throwable exc) {
		super(msg,exc);
	}

	public PluginBootException(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3360055883821567015L;

}