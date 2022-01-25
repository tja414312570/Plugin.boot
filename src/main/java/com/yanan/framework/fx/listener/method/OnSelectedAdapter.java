package com.yanan.framework.fx.listener.method;

import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.reflect.ReflectUtils;

import javafx.scene.control.SelectionModel;

@Register(attribute = "OnSelected")
public class OnSelectedAdapter implements FxMethodListener<OnSelected> {

	@Override
	public void adapter(FxApplication fxApplication, Method method, OnSelected onSelected) throws Exception {
		Object view = fxApplication.findViewById(onSelected.value());
		SelectionModel<?> selectModel = ReflectUtils.invokeMethod(view, "getSelectionModel");
		selectModel.selectedIndexProperty().addListener(
				FxMethodListener.DefaultMethodAdapter.onChangeListener(method, fxApplication, onSelected.negative()));

	}

}
