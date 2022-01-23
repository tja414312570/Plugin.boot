package com.yanan.framework.fx.listener.stage;

import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.listener.FxListener;
import com.yanan.framework.plugin.annotations.Register;

@Register(attribute = "OnStageHeightChange")
public class OnStageHeightChangeAdapter implements FxListener<OnStageHeightChange>{

	@Override
	public void adapter(FxApplication fxApplication, Method method,OnStageHeightChange onStageHeightChange) {
			fxApplication.getPrimaryStage().heightProperty().addListener(
					FxListener.DefaultMethodAdapter.onChangeListener(method, fxApplication));
	}

}
