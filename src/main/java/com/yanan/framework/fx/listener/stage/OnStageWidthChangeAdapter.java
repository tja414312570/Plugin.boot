package com.yanan.framework.fx.listener.stage;

import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.listener.FxListener;
import com.yanan.framework.plugin.annotations.Register;

@Register(attribute = "OnStageWidthChange")
public class OnStageWidthChangeAdapter implements FxListener<OnStageWidthChange>{

	@Override
	public void adapter(FxApplication fxApplication, Method method,OnStageWidthChange onStageHeightChange) {
			fxApplication.getPrimaryStage().widthProperty().addListener(
					FxListener.DefaultMethodAdapter.onChangeListener(method, fxApplication));
	}

}
