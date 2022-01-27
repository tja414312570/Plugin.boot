package com.yanan.framework.fx.process.method;

import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.reflect.ReflectUtils;

import javafx.beans.property.StringProperty;

@Register(attribute = "OnTextChange")
public class OnTextChangeAdapter implements FxMethodProcess<OnTextChange> {

	@Override
	public void adapter(FxApplication fxApplication, Method method, OnTextChange onSelected) throws Exception {
		Object view = fxApplication.findViewById(onSelected.value());
		StringProperty stringProperty = ReflectUtils.invokeMethod(view, "textProperty");
		stringProperty.addListener(FxMethodProcess.DefaultMethodAdapter.onChangeListener(method, fxApplication));
	}

}
