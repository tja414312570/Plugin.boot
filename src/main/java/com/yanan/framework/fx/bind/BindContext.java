package com.yanan.framework.fx.bind;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.Property;

public class BindContext {
	final static class BindContextHolder {
		private static final BindContext CONTEXT = new BindContext();
	}
	public static BindContext getContext() {
		return BindContextHolder.CONTEXT;
	}
	private Map<String,Property<?>> propertySpace = new HashMap<>();
	private Map<String,String> nameSpace = new HashMap<>();
	public <T> void addProperty(String namespace,String method,Property<T> property) {
		this.propertySpace.put(namespace, property);
		this.nameSpace.put(namespace, method);
	}
	public <T> void addProperty(String namespace,Method method,Property<T> property) {
		this.propertySpace.put(namespace, property);
		this.nameSpace.put(method.toString(), namespace);
	}
	@SuppressWarnings("unchecked")
	public <X,T extends Property<X>> T getProperty(String namespace) {
		return (T) this.propertySpace.get(namespace);
	}
	public <X,T extends Property<X>> T getProperty(Method methodName) {
		String namespace = getNameSpace(methodName);
		return getProperty(namespace);
	}
	public String getNameSpace(String methodName) {
		return this.nameSpace.get(methodName);
	}
	public String getNameSpace(Method method) {
		return this.nameSpace.get(method.toString());
	}
}
