package com.yanan.framework.fx;

public interface FxApplicationProcess{
	void process(FxApplication fxApplication, Object instance);
	void after(FxApplication fxApplication, Object instance);
	void before(FxApplication fxApplication, Object instance);
}
