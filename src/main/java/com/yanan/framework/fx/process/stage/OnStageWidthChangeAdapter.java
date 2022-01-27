package com.yanan.framework.fx.process.stage;

import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.process.method.FxMethodProcess;
import com.yanan.framework.plugin.annotations.Register;

@Register(attribute = "OnStageWidthChange")
public class OnStageWidthChangeAdapter implements FxMethodProcess<OnStageWidthChange>{

	@Override
	public void adapter(FxApplication fxApplication, Method method,OnStageWidthChange onStageHeightChange) {
			fxApplication.getPrimaryStage().widthProperty().addListener(
					FxMethodProcess.DefaultMethodAdapter.onChangeListener(method, fxApplication));
	}

}
