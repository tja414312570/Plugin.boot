package com.yanan.framework.fx.bind.conver;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;

public interface PropertyAdapter<I extends ReadOnlyProperty,V  extends ReadOnlyProperty> {
	V adapter(I property);
}
