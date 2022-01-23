package com.yanan.framework.fx.listener;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yanan.framework.fx.FxApplication;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;

public interface FxListener<T extends Annotation> {
	public void adapter(FxApplication fxApplication,Method method,T annotation);
	public static class DefaultMethodAdapter{
		public static <T> ChangeListener<T> onChangeListener(Method method,FxApplication fxApplication) {
			ChangeListener<T> changeListener = (observable,oldValue,newValue)->{
				try {
					if(method.getParameterCount() == 2) {
						method.invoke(fxApplication,newValue,oldValue);
						return;
					}
					if(method.getParameterCount() == 1) {
						method.invoke(fxApplication,newValue);
						return;
					}
					if(method.getParameterCount() == 0) {
						method.invoke(fxApplication);
						return;
					}
					method.invoke(fxApplication,observable,oldValue,newValue);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException("failed to execute listener ["+method+"]",e);
				}
			};
			return changeListener;
		}
		public static <T extends Number> ChangeListener<T> onChangeListener(Method method,FxApplication fxApplication,boolean negative) {
			ChangeListener<T> changeListener = (observable,oldValue,newValue)->{
				try {
					
					if(newValue.intValue() < 0 && !negative) {
						return;
					}
					if(method.getParameterCount() == 2) {
						method.invoke(fxApplication,newValue,oldValue);
						return;
					}
					if(method.getParameterCount() == 1) {
						method.invoke(fxApplication,newValue);
						return;
					}
					if(method.getParameterCount() == 0) {
						method.invoke(fxApplication);
						return;
					}
					method.invoke(fxApplication,observable,oldValue,newValue);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException("failed to execute listener ["+method+"]",e);
				}
			};
			return changeListener;
		}
		public static <T extends Event> EventHandler<T> onEventListener(Method method,FxApplication fxApplication) {
			EventHandler<T> eventHandler = (event)->{
				try {
					if(method.getParameterCount() == 0) {
						method.invoke(fxApplication);
						return;
					}
					method.invoke(fxApplication,event);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException("failed to execute listener ["+method+"]",e);
				}
			};
			return eventHandler;
		}
		
	}
 
}
