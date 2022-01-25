package com.yanan.framework.fx.listener.stage;

import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.listener.method.FxMethodListener;
import com.yanan.framework.plugin.annotations.Register;

@Register(attribute = "OnStageHeightChange")
public class OnStageHeightChangeAdapter implements FxMethodListener<OnStageHeightChange>{

	@Override
	public void adapter(FxApplication fxApplication, Method method,OnStageHeightChange onStageHeightChange) {
			fxApplication.getPrimaryStage().heightProperty().addListener(
					FxMethodListener.DefaultMethodAdapter.onChangeListener(method, fxApplication));
	}

}
