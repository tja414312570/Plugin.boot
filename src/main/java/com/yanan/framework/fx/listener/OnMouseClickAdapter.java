package com.yanan.framework.fx.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.reflect.ReflectUtils;

import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

@Register(attribute = "OnMouseClick")
public class OnMouseClickAdapter implements FxListener<OnMouseClick>{

	@Override
	public void adapter(FxApplication fxApplication, Method method,OnMouseClick onSelected) {
		
		try {
			Object view = fxApplication.findViewById(onSelected.value());
			ObjectProperty<EventHandler<? super MouseEvent>> objectProperty = ReflectUtils.invokeMethod(view,"onMouseClickedProperty");
			objectProperty.set(FxListener.DefaultMethodAdapter.onEventListener(method,fxApplication));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		
	}

}
