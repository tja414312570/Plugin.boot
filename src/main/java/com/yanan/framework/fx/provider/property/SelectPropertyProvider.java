package com.yanan.framework.fx.provider.property;

import java.lang.reflect.InvocationTargetException;

import com.alibaba.nacos.api.utils.StringUtils;
import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.bind.BindContext;
import com.yanan.framework.fx.process.method.FxMethodProcess;
import com.yanan.framework.fx.provider.FxPropertyProvider;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.asserts.Assert;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.string.StringUtil;

import javafx.beans.property.Property;
import javafx.scene.control.SelectionModel;
import javafx.beans.property.ReadOnlyProperty;

@Register(attribute = "*.select*")
public class SelectPropertyProvider implements FxPropertyProvider {

	@SuppressWarnings({ "unchecked", "restriction" })
	@Override
	public ReadOnlyProperty<?> getProperty(FxApplication fxApplication, String name) {
		int index = name.indexOf(".");
		String id = name.substring(0,index);
		Object view = fxApplication.findViewById(id);
		String property = name.substring(index+1);
		try {
			SelectionModel<?> selectModel = ReflectUtils.invokeMethod(view, "getSelectionModel");
			if(StringUtil.equals("select",property) || StringUtil.equals("selectItem",property)) {
				return selectModel.selectedItemProperty();
			}else {
				return selectModel.selectedIndexProperty();
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
		
//		selectModel.selectedItemProperty();
//		try {
//			targetProperty =  ReflectUtils.invokeMethod(view,property+"Property");
//		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
//				| NoSuchMethodException | SecurityException e) {
//			throw new RuntimeException(e);
//		}
		
//		if(StringUtils.isEmpty(propertyName)) {
//			fieldProperty = (Property<Object>) DefaultBindBuilder.getFieldProperty(fxApplication, field);
//		}else {
//			Object value = ReflectUtils.getFieldValue(field, fxApplication);
//			fieldProperty = ReflectUtils.invokeMethod(value, propertyName+"Property");
//		}
		
//		Assert.isNotNull(targetProperty,"could not found property for "+name);
//		return targetProperty;
	}

}
