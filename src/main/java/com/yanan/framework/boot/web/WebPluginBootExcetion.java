package com.yanan.framework.boot.web;


import com.yanan.framework.boot.PluginBootException;

public class WebPluginBootExcetion extends PluginBootException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8377924973332124186L;

	public WebPluginBootExcetion(String msg) {
		super(msg);
	}

	public WebPluginBootExcetion(String msg, Throwable exc) {
		super(msg,exc);
	}

}