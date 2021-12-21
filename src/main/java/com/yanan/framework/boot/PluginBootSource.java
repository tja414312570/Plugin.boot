package com.yanan.framework.boot;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.decoder.StandScanResource;

@Register
public class PluginBootSource extends StandScanResource{
	public PluginBootSource() {
		super("classpath*:com.yanan.framework.**");
	}
}
