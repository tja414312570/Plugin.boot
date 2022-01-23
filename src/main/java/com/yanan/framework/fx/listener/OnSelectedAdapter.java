package com.yanan.framework.fx.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.reflect.ReflectUtils;

import javafx.scene.control.SingleSelectionModel;

@Register(attribute = "OnSelected")
public class OnSelectedAdapter implements FxListener<OnSelected>{

	@Override
	public void adapter(FxApplication fxApplication, Method method,OnSelected onSelected) {
		
		try {
			Object view = fxApplication.findViewById(onSelected.value());	
			SingleSelectionModel<?> selectModel = ReflectUtils.invokeMethod(view,"getSelectionModel");
			selectModel.selectedIndexProperty().addListener(FxListener.DefaultMethodAdapter.onChangeListener(method, 
					fxApplication,onSelected.negative()));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		
	}

}
