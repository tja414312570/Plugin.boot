package com.yanan.framework.fx.bind;

import java.lang.reflect.Field;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.process.field.FxFieldPostProcess;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.annotations.Service;

@Register(attribute = "BindGroup")
public class BindFieldGuoupProcess implements FxFieldPostProcess<BindGroup>{
	
	@Service(attribute = "Bind")
	private FxFieldPostProcess<Bind> bindFieldProcess;

	@Override
	public void adapter(FxApplication fxApplication, Field field, BindGroup annotation) throws Exception {
		for(Bind bind : annotation.value()) {
			bindFieldProcess.adapter(fxApplication, field, bind);
		}
	}
}
