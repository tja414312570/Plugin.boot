package com.yanan.framework.fx.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.reflect.ReflectUtils;

import javafx.beans.property.StringProperty;

@Register(attribute = "OnTextChange")
public class OnTextChangeAdapter implements FxListener<OnTextChange>{

	@Override
	public void adapter(FxApplication fxApplication, Method method,OnTextChange onSelected) {
		
		try {
			Object view = fxApplication.findViewById(onSelected.value());
			StringProperty stringProperty = ReflectUtils.invokeMethod(view,"textProperty");
			stringProperty.addListener(FxListener.DefaultMethodAdapter.onChangeListener(method,fxApplication));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		
	}

}
