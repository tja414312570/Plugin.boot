package com.yanan.framework.fx.bind.conver;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

public interface PropertyAdapter<T extends ObservableValue,V extends Property> {
	V adapter(T property);
}
