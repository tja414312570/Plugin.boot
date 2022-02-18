package com.yanan.framework.fx.provider;

import com.yanan.framework.fx.FxApplication;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.WritableValue;

public interface FxPropertyProvider {
	@SuppressWarnings("restriction")
	public <T extends Object & ReadOnlyProperty<?> & WritableValue<?>> T getProperty(FxApplication fxApplication,String property);
}
