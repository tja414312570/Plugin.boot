package com.yanan.framework.fx.bind.conver;

import javafx.beans.property.Property;

public interface PropertyAdapter<T extends Property,V extends Property> {
	V adapter(T property);
}
