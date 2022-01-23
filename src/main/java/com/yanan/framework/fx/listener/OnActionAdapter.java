package com.yanan.framework.fx.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.reflect.ReflectUtils;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

@Register(attribute = "OnAction")
public class OnActionAdapter implements FxListener<OnAction>{

	@Override
	public void adapter(FxApplication fxApplication, Method method,OnAction onSelected) {
		
		try {
			Object view = fxApplication.findViewById(onSelected.value());
			ObjectProperty<EventHandler<ActionEvent>> objectProperty = ReflectUtils.invokeMethod(view,"onActionProperty");
			objectProperty.set(FxListener.DefaultMethodAdapter.onEventListener(method,fxApplication));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		
	}

}
