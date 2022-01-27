package com.yanan.framework.fx.process.method;

import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.reflect.ReflectUtils;

import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

@Register(attribute = "OnMouseClick")
public class OnMouseClickAdapter implements FxMethodProcess<OnMouseClick> {

	@Override
	public void adapter(FxApplication fxApplication, Method method, OnMouseClick onSelected) throws Exception {
		Object view = fxApplication.findViewById(onSelected.value());
		ObjectProperty<EventHandler<? super MouseEvent>> objectProperty = ReflectUtils.invokeMethod(view,
				"onMouseClickedProperty");
		objectProperty.set(FxMethodProcess.DefaultMethodAdapter.onEventListener(method, fxApplication));

	}

}
