package com.yanan.framework.fx.bind;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.handler.InvokeHandler;
import com.yanan.framework.plugin.handler.MethodHandler;

import javafx.application.Platform;
import javafx.beans.value.WritableObjectValue;

@Register(attribute = "null")
public class DataSourceHandler implements InvokeHandler{
	
	@Override
	public void after(MethodHandler methodHandler) {
		InvokeHandler.super.after(methodHandler);
		WritableObjectValue<Object> property = BindContext.getContext().getProperty(methodHandler.getMethod());
		if(property == null )
			return;
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
