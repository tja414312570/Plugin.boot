package com.yanan.framework.fx.listener.stage;

import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.listener.method.FxMethodListener;
import com.yanan.framework.plugin.annotations.Register;

@Register(attribute = "OnStageWidthChange")
public class OnStageWidthChangeAdapter implements FxMethodListener<OnStageWidthChange>{

	@Override
	public void adapter(FxApplication fxApplication, Method method,OnStageWidthChange onStageHeightChange) {
			fxApplication.getPrimaryStage().widthProperty().addListener(
					FxMethodListener.DefaultMethodAdapter.onChangeListener(method, fxApplication));
	}

}
