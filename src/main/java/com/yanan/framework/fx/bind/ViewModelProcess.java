package com.yanan.framework.fx.bind;

import java.lang.reflect.Field;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.process.field.FxFieldPostProcess;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;

@Register(attribute = "ViewModel")
public class ViewModelProcess implements FxFieldPostProcess<ViewModel>{

	@Override
	public void adapter(FxApplication fxApplication, Field field, ViewModel annotation) throws Exception {
		FxApplication instance = (FxApplication) PlugsFactory.getPluginsInstance(field.getType());
		instance.start(fxApplication.getPrimaryStage());
	}
}
