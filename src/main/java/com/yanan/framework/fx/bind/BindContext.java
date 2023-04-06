package com.yanan.framework.fx.bind;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class BindContext {
	final static class BindContextHolder {
		private static final BindContext CONTEXT = new BindContext();
	}
	public static BindContext getContext() {
		return BindContextHolder.CONTEXT;
	}
	private ObservableMap<String,Property<?>> propertySpace = FXCollections.observableHashMap();
	public void addProperty(String namespace,Property<?> property) {
		this.propertySpace.put(namespace, property);
	}
	@SuppressWarnings("unchecked")
	public <X,T extends Property<X>> T getProperty(String namespace) {
		return (T) this.propertySpace.get(namespace);
	}
}
