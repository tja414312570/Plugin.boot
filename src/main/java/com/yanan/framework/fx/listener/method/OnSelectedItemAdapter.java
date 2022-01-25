package com.yanan.framework.fx.listener.method;

import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.reflect.ReflectUtils;

import javafx.scene.control.SelectionModel;

@Register(attribute = "OnSelectedItem")
public class OnSelectedItemAdapter implements FxMethodListener<OnSelectedItem> {

	@Override
	public void adapter(FxApplication fxApplication, Method method, OnSelectedItem onSelected) throws Exception {
		Object view = fxApplication.findViewById(onSelected.value());
		SelectionModel<?> selectModel = ReflectUtils.invokeMethod(view, "getSelectionModel");
		selectModel.selectedItemProperty()
				.addListener(FxMethodListener.DefaultMethodAdapter.onChangeListener(method, fxApplication));
	}

}
