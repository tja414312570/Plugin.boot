package com.yanan.framework.fx.process.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Map;

import com.yanan.framework.fx.FindViewById;
import com.yanan.framework.fx.FxApplication;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.reflect.cache.ClassInfoCache;

public abstract class AbstractLazyListener implements LazyLoader<Annotation> {

	@Override
	public String id(Annotation annotation,AnnotatedElement annotatedElement) {
		try {
			Field field = ClassInfoCache.getClassHelper(annotation.getClass()).getAnyField("h");
			Object annotationInvocationHandler = ReflectUtils.getFieldValue(field, annotation);
			Map<String,String> memberMap = ReflectUtils.getDeclaredFieldValue("memberValues", annotationInvocationHandler);
			FindViewById findView = annotatedElement.getAnnotation(FindViewById.class);
			if(findView != null) {
				return FxApplication.getViewIdByField((Field) annotatedElement);
			}
			if(memberMap.isEmpty()) {
				throw new RuntimeException("could not found id for anno "+annotation);
			}
			String id = memberMap.get("id");
			if(id == null) {
				id = memberMap.get("value");
			}
			return id;
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("could not found id for anno "+annotation,e);
		}
	}

}
