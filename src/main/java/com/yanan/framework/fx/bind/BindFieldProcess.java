package com.yanan.framework.fx.bind;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import com.alibaba.nacos.api.utils.StringUtils;
import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.FxApplicationPostProcess;
import com.yanan.framework.fx.bind.conver.PropertyAdapter;
import com.yanan.framework.fx.process.field.FxFieldPostProcess;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.reflect.TypeToken;

import javafx.beans.property.Property;

@Register(attribute = "Bind")
public class BindFieldProcess implements FxFieldPostProcess<Bind>{

	@SuppressWarnings("unchecked")
	@Override
	public void adapter(FxApplication fxApplication, Field field, Bind annotation) throws Exception {
		String target = annotation.target();
		String propertyName = annotation.property();
		Property<Object> fieldProperty;
		if(StringUtils.isEmpty(propertyName)) {
			fieldProperty = (Property<Object>) FxApplicationPostProcess.getFieldProperty(fxApplication, field);
		}else {
			Object instance = ReflectUtils.getFieldValue(field, fxApplication);
			fieldProperty = ReflectUtils.invokeMethod(instance, propertyName+"Property");
		}
		char type = target.charAt(0);
		if(target.startsWith("#") || target.startsWith("$")) {
			target = target.substring(2,target.length()-1);
		}
		Property<Object> targetProperty;
		if(type == '#') {
			int index = target.lastIndexOf(".");
			String id = target.substring(0,index);
			Object view = fxApplication.findViewById(id);
			String property = target.substring(index+1);
			targetProperty =  ReflectUtils.invokeMethod(view,property+"Property");
		}else {
			targetProperty = BindContext.getContext().getProperty(target);
		}
		String adpater = annotation.adapter();
		if(StringUtils.isEmpty(adpater)) {
			String s = getPropertyAttr(fieldProperty);
			String t = getPropertyAttr(targetProperty);
			adpater = t+"_"+s;
		}
		PropertyAdapter<Property<Object>, Property<Object>> propertyAdapter = PlugsFactory.
				getPluginsInstanceByAttributeStrictAllowNull(new TypeToken<PropertyAdapter<Property<Object>, Property<Object>>>() 
				{}.getTypeClass(), adpater);
		if(propertyAdapter != null) {
			Property<Object> adp = propertyAdapter.adapter(targetProperty);
			targetProperty = adp;
		}
		fieldProperty.bindBidirectional(targetProperty);
	}

	private String getPropertyAttr(Property<Object> fieldProperty) {
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
