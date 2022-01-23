package com.yanan.framework.fx.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.reflect.ReflectUtils;

import javafx.scene.control.SingleSelectionModel;

@Register(attribute = "OnSelectedItem")
public class OnSelectedItemAdapter implements FxListener<OnSelectedItem>{

	@Override
	public void adapter(FxApplication fxApplication, Method method,OnSelectedItem onSelected) {
		
		try {
			Object view = fxApplication.findViewById(onSelected.value());	
			SingleSelectionModel<?> selectModel = ReflectUtils.invokeMethod(view,"getSelectionModel");
			selectModel.selectedItemProperty().addListener(FxListener.DefaultMethodAdapter.onChangeListener(method, 
					fxApplication));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		
	}

}
