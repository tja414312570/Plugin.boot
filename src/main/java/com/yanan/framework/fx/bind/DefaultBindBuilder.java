package com.yanan.framework.fx.bind;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.definition.RegisterDefinition;
import com.yanan.utils.asserts.Assert;
import com.yanan.utils.reflect.ReflectUtils;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

@Register
public class DefaultBindBuilder implements BindBuilder{
	
	public Map<String,Property<?>> builder(Object instance){
		Map<String,Property<? extends Object>> map;
		if(instance instanceof PropertySupport) {
			map = ((PropertySupport)instance).getProperty();
		}else {
			Class<?> endClass = Object.class;
			if(instance instanceof FxApplication) {
				endClass = FxApplication.class;
			}
			Assert.isTrue(PlugsFactory.isProxy(instance),"instance "+instance+" is not proxy!");
			RegisterDefinition registerDefinition = PlugsFactory.getPluginsHandler(instance)
					.getRegisterDefinition();
			map = buildMapping(registerDefinition, endClass, instance);
		}
		
		return map;
	}
//	public static void main(String[] args) {
////		Map map = new DefaultBindBuilder().builder(new DeviceEditWindow());
////		System.err.println("-----------");
////		System.err.println(map);
//		QemuCommand qemu = new QemuCommand();
//		PlugsFactory.init(new StandScanResource("classpath*:**"));
//		ComCommand device = PlugsFactory.getPluginsInstance(ComCommand.class);
//		Map<String,Property<?>> map = new DefaultBindBuilder().builder(device);
//		device.setName("xxx");
//		System.err.println("-----------");
//		System.err.println(map);
//		map.values().forEach(item->{
//			((Property<String>)item).setValue(((int)(Math.random()*100))+"");
//		});;
//		System.err.println(device);
//	}
	private Map<String,Property<?>> buildMapping(RegisterDefinition currentRegisterDefinition,Class<?> endClass,Object instance) {
		Map<String,Property<?>> map = new HashMap<>();
		Field[] fields = ReflectUtils.getAllFields(currentRegisterDefinition.getRegisterClass(), endClass);
		DataSource parentDataSource = currentRegisterDefinition.getRegisterClass().getAnnotation(DataSource.class);
		for(Field field : fields) {
			DataSource dataSource = field.getAnnotation(DataSource.class);
			String namespace = null;
			if(parentDataSource != null) {
				namespace = parentDataSource.value();
				if(dataSource != null) {
					namespace += "."+dataSource.value();
				}else {
					namespace += "."+field.getName();
				}
			}else {
				if(dataSource != null) {
					namespace = dataSource.value();
				}
			}
			
			if(namespace != null) {
				String fieldSetMethod = ReflectUtils.createFieldSetMethod(field.getName());
				Method method;
				try {
					method = currentRegisterDefinition.getRegisterClass().getMethod(fieldSetMethod,field.getType());
				} catch (NoSuchMethodException | SecurityException e) {
					throw new RuntimeException("could not found field set method "+fieldSetMethod
							+" at "+currentRegisterDefinition.getRegisterClass().getName(),e);
				}
				Property<?> property =  getFieldProperty(instance, field);
//				property.addListener((o,od,ne)->{
//					try {
//						ReflectUtils.setFieldValue(field, instance, ne);
//					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
//						throw new RuntimeException("failed to set field value "+field.getDeclaringClass().getName()+"."+field.getName()+" ["+ne+"]",e);
//					}
//				});
//				BindContext.getContext().addProperty(namespace, method, property);
				currentRegisterDefinition.addMethodHandler(method,
						PlugsFactory.getPluginsInstance(DataSourceHandler.class,property,field));
				map.put(namespace, property);
			}
			
		}
		return map;
	}
	public static Property<?> getFieldProperty(Object instance, Field field) {
        Class<?> type = field.getType();
        Property<?> property;
		if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            property = new SimpleBooleanProperty();
        } else if (Double.class.equals(type) || double.class.equals(type)) {
            property = new SimpleDoubleProperty();
        } else if (Float.class.equals(type) || float.class.equals(type)) {
            property = new SimpleFloatProperty();
        } else if (Integer.class.equals(type) || int.class.equals(type)) {
            property = new SimpleIntegerProperty();
        } else if (Long.class.equals(type) || long.class.equals(type)) {
            property = new SimpleLongProperty();
        } else if (String.class.equals(type)) {
            property = new SimpleStringProperty();
        } else if (List.class.isAssignableFrom(type)) {
            property = new SimpleListProperty<Object>();
        } else {
        	property = new SimpleObjectProperty<Object>();
        }
		property.addListener((ob,old,newv)->{
			try {
				String fieldSetMethod = ReflectUtils.createFieldSetMethod(field.getName());
				ReflectUtils.invokeMethod(instance, fieldSetMethod, newv);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		});
        return property;
    }

}
