package com.yanan.framework.fx.bind;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.alibaba.nacos.api.utils.StringUtils;
import com.alibaba.nacos.shaded.com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.Adapter;
import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.bind.conver.PropertyAdapter;
import com.yanan.framework.fx.process.field.FxFieldPostProcess;
import com.yanan.framework.fx.provider.FxPropertyProvider;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.reflect.TypeToken;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;

@Register(attribute = "Bind")
public class BindFieldProcess implements FxFieldPostProcess<Bind>{
	
	@SuppressWarnings("unchecked")
	@Override
	public void adapter(FxApplication fxApplication,Object instance, Field field, Bind annotation) throws Exception {
		String target = annotation.target();
		String propertyName = annotation.property();
		Property<Object> fieldProperty;
		if(StringUtils.isEmpty(propertyName)) {
			fieldProperty = (Property<Object>) DefaultBindBuilder.getFieldProperty(instance, field);
		}else {
			Object value = ReflectUtils.getFieldValue(field, fxApplication);
			fieldProperty = ReflectUtils.invokeMethod(value, propertyName+"Property");
		}
		FxPropertyProvider fxPropertyProvider = PlugsFactory
				.getPluginsInstanceByAttributeStrict(FxPropertyProvider.class, target);
		ReadOnlyProperty targetProperty =  fxPropertyProvider.getProperty(fxApplication, target);
		String adpater = annotation.adapter();
		if(StringUtils.isEmpty(adpater)) {
			String s = getPropertyAttr(fieldProperty);
			String t = getPropertyAttr(targetProperty);
			adpater = t+"_"+s;
		}
		System.err.println(adpater);
		PropertyAdapter propertyAdapter = PlugsFactory.
				getPluginsInstanceByAttributeStrictAllowNull(PropertyAdapter.class, adpater);
		if(propertyAdapter != null) {
			Object adp = propertyAdapter.adapter(targetProperty);
			targetProperty = (ReadOnlyProperty) adp;
		}
		if(targetProperty instanceof Property) {
			fieldProperty.bindBidirectional((Property<Object>) targetProperty);
		}
		if(targetProperty instanceof ReadOnlyProperty) {
			fieldProperty.bind(targetProperty);
		}
		
	}

	private String getPropertyAttr(ReadOnlyProperty<Object> fieldProperty) {
		String name = fieldProperty.getName();
		if(StringUtils.isEmpty(name)) {
			Class<?> clzz = fieldProperty.getClass();
			
			while(!clzz.equals(Object.class)) {
				Type[] types = clzz.getGenericInterfaces();
				for(Type type : types) {
					Class<?>[] realType = ReflectUtils.getActualType(type);
					if(realType.length > 0) {
						name = realType[0].getSimpleName();
						break;
					}
				}
				if(types.length>0)
					break;
				clzz = clzz.getSuperclass();
			}
			if(StringUtils.isEmpty(name)) {
				name = fieldProperty.getClass().getSimpleName();
			}
		}
		return name;
	}
}
