package com.yanan.framework.fx.provider.property;

import java.lang.reflect.InvocationTargetException;

import com.alibaba.nacos.api.utils.StringUtils;
import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.bind.BindContext;
import com.yanan.framework.fx.provider.FxPropertyProvider;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.asserts.Assert;
import com.yanan.utils.reflect.ReflectUtils;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.WritableValue;

@Register(priority = 1)
public class DefaultPropertyProvider implements FxPropertyProvider {

	@Override
	public <T extends Object & ReadOnlyProperty<?> & WritableValue<?>> T getProperty(FxApplication fxApplication, String name) {
		T targetProperty;
		char type = name.charAt(0);
		if(name.startsWith("#") || name.startsWith("$")) {
			name = name.substring(2,name.length()-1);
		}
		if(type == '#') {
			int index = name.lastIndexOf(".");
			String id = name.substring(0,index);
			Object view = fxApplication.findViewById(id);
			String property = name.substring(index+1);
			try {
				targetProperty =  ReflectUtils.invokeMethod(view,property+"Property");
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}else {
			targetProperty = BindContext.getContext().getProperty(name);
		}
		
//		if(StringUtils.isEmpty(propertyName)) {
//			fieldProperty = (Property<Object>) DefaultBindBuilder.getFieldProperty(fxApplication, field);
//		}else {
//			Object value = ReflectUtils.getFieldValue(field, fxApplication);
//			fieldProperty = ReflectUtils.invokeMethod(value, propertyName+"Property");
//		}
		
		Assert.isNotNull(targetProperty,"could not found property for "+name);
		return targetProperty;
	}

}
