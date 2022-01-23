package com.yanan.framework.fx.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.reflect.ReflectUtils;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.Toggle;

@Register(attribute = "OnSelectedToggle")
public class OnSelectedToggleAdapter implements FxListener<OnSelectedToggle>{

	@Override
	public void adapter(FxApplication fxApplication, Method method,OnSelectedToggle onSelected) {
		
		try {
			Object view = fxApplication.findViewById(onSelected.value());
			ReadOnlyObjectProperty<Toggle> objectProperty = ReflectUtils.invokeMethod(view,"selectedToggleProperty");
			objectProperty.addListener(FxListener.DefaultMethodAdapter.onChangeListener(method, fxApplication));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		
	}

}
