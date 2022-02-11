package com.yanan.framework.fx.process.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public interface LazyLoader<T extends Annotation> {
	public String id(T annotations,AnnotatedElement annotatedElement);
}

