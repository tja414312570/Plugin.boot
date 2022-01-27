package com.yanan.framework.fx.process.stage;

import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.process.method.FxMethodProcess;
import com.yanan.framework.plugin.annotations.Register;

@Register(attribute = "OnStageHeightChange")
public class OnStageHeightChangeAdapter implements FxMethodProcess<OnStageHeightChange>{

	@Override
	public void adapter(FxApplication fxApplication, Method method,OnStageHeightChange onStageHeightChange) {
			fxApplication.getPrimaryStage().heightProperty().addListener(
					FxMethodProcess.DefaultMethodAdapter.onChangeListener(method, fxApplication));
	}

}
