package com.yanan.framework.fx.bind;

import java.lang.reflect.Field;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.handler.InvokeHandler;
import com.yanan.framework.plugin.handler.MethodHandler;

import javafx.application.Platform;
import javafx.beans.value.WritableObjectValue;

@Register(attribute = "null",signlTon = false)
public class DataSourceHandler implements InvokeHandler{
	private WritableObjectValue<Object> property;
	private Field field;
	
	public DataSourceHandler(WritableObjectValue<Object> property, Field field) {
		super();
		this.property = property;
		this.field = field;
	}

	public WritableObjectValue<Object> getProperty() {
		return property;
	}

	public Field getField() {
		return field;
	}

	@Override
	public void after(MethodHandler methodHandler) {
		InvokeHandler.super.after(methodHandler);
		FxApplication fxApplication = FxApplication.getCurrentFxApplication();
		if(fxApplication != null) {
			Platform.runLater(()->{
				property.set(methodHandler.getParameters()[0]);
			});
			return;
		}
		property.set(methodHandler.getParameters()[0]);
	}
}
