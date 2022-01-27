package com.yanan.framework.fx;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.yanan.framework.fx.bind.BindContext;
import com.yanan.framework.fx.bind.DataSource;
import com.yanan.framework.fx.bind.DataSourceHandler;
import com.yanan.framework.fx.process.field.FxFieldBeforeProcess;
import com.yanan.framework.fx.process.field.FxFieldPostProcess;
import com.yanan.framework.fx.process.field.FxFieldProcess;
import com.yanan.framework.fx.process.method.FxMethodBeforeProcess;
import com.yanan.framework.fx.process.method.FxMethodPostProcess;
import com.yanan.framework.fx.process.method.FxMethodProcess;
import com.yanan.framework.plugin.FactoryRefreshProcess;
import com.yanan.framework.plugin.InstanceBeforeProcesser;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.ProxyModel;
import com.yanan.framework.plugin.RegisterRefreshProcess;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.definition.RegisterDefinition;
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

/**
 * 将fx应用强制设置为cglib代理
 * @author tja41
 *
 */
@Register
public class FxApplicationPostProcess implements RegisterRefreshProcess,InstanceBeforeProcesser,FactoryRefreshProcess{

	@Override
	public void process(PlugsFactory plugsFactory, RegisterDefinition currentRegisterDefinition) {
		if(FxApplication.class.isAssignableFrom(currentRegisterDefinition.getRegisterClass())) {
			currentRegisterDefinition.setProxyModel(ProxyModel.CGLIB);
		}
	}

	private void buildMethodMapping(RegisterDefinition currentRegisterDefinition,Class<?> endClass,Object instance) {
		Field[] fields = ReflectUtils.getAllFields(currentRegisterDefinition.getRegisterClass(), endClass);
		for(Field field : fields) {
			DataSource dataSource = field.getAnnotation(DataSource.class);
			if(dataSource != null) {
				String fieldSetMethod = ReflectUtils.createFieldSetMethod(field.getName());
				Method method;
				try {
					method = currentRegisterDefinition.getRegisterClass().getMethod(fieldSetMethod,field.getType());
				} catch (NoSuchMethodException | SecurityException e) {
					throw new RuntimeException("could not found field set method "+fieldSetMethod
							+" at "+currentRegisterDefinition.getRegisterClass().getName(),e);
				}
				String namespace = dataSource.value();
				Property<?> property =  getFieldProperty(instance, field);
				BindContext.getContext().addProperty(namespace,method,property);
				currentRegisterDefinition.addMethodHandler(method,
						PlugsFactory.getPluginsInstance(DataSourceHandler.class));
			}
			
		}
		
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

	@Override
	public Object before(RegisterDefinition registerDefinition, Class<?> serviceClasss, Object instance) {
		if(FxApplication.class.isAssignableFrom(registerDefinition.getRegisterClass())) {
			buildMethodMapping(registerDefinition,FxApplication.class,instance);
		}
		return instance;
	}

	@Override
	public void process(PlugsFactory plugsFactory) {
		plugsFactory.addPlugininDefinition(FxFieldPostProcess.class);
		plugsFactory.addPlugininDefinition(FxFieldProcess.class);
		plugsFactory.addPlugininDefinition(FxFieldBeforeProcess.class);
		plugsFactory.addPlugininDefinition(FxMethodPostProcess.class);
		plugsFactory.addPlugininDefinition(FxMethodProcess.class);
		plugsFactory.addPlugininDefinition(FxMethodBeforeProcess.class);
	}

}
