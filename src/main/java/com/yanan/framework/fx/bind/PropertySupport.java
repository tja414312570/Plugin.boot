package com.yanan.framework.fx.bind;

import java.util.Map;

import javafx.beans.property.Property;

public interface PropertySupport {
	Map<String,Property<?>> getProperty();
}
