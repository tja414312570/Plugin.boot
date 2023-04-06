package com.yanan.framework.fx;

public interface FxApplicationProcess{
	void before();
	void after();
//	void process(Class<?> appClass, FxApplication fxApplication);
	void process(FxApplication fxApplication, Class<?> appClass);
}
