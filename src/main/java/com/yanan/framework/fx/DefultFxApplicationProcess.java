package com.yanan.framework.fx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yanan.framework.fx.process.field.FxFieldProcess;
import com.yanan.framework.fx.process.method.FxMethodProcess;
import com.yanan.framework.fx.process.method.LazyLoader;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.reflect.TypeToken;
import com.yanan.utils.string.StringUtil;

import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;

public class DefultFxApplicationProcess implements FxApplicationProcess{

	@Override
	public void process(FxApplication fxApplication,Class<?> appClass) {
		try {
			bindView(appClass,fxApplication);
			bindListener(appClass,fxApplication);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchFieldException
				| SecurityException | NoSuchMethodException e) {
		e.printStackTrace();
		}
	}
	private void bindView(Class<?> appClass, FxApplication fxApplication) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Field[] fields = ReflectUtils.getAllFields(appClass,FxApplication.class);
		for(Field field: fields) {
				if(field.getAnnotation(Controller.class) != null) {
					setFieldValue(field,fxApplication,fxApplication.getController());
				}
				if(field.getAnnotation(RootView.class) != null) {
					setFieldValue(field, fxApplication, fxApplication.getRoot());
				}
				if(field.getAnnotation(PrimaryStage.class) != null) {
					setFieldValue(field, fxApplication, fxApplication.getPrimaryStage());
				}
				FindViewById findViewById = field.getAnnotation(FindViewById.class);
				if(findViewById != null) {
					Object view = fxApplication.findViewByField(field);
					setFieldValue(field, fxApplication, view);
				}
				FindView findView = field.getAnnotation(FindView.class);
				if(findView != null) {
					String name = field.getName();
					if(StringUtil.isNotEmpty(findView.value())) {
						name = findView.value();
					}
					Object view = fxApplication.getRoot().lookup(name);
					setFieldValue(field, fxApplication, view);
				}
				Annotation[] annotations = field.getAnnotations();
				LazyLoad lazyLoad = field.getAnnotation(LazyLoad.class);
				for(Annotation annotation : annotations) {
					FxFieldProcess<Annotation> listener = PlugsFactory.
							getPluginsInstanceByAttributeStrictAllowNull(new TypeToken<FxFieldProcess<Annotation>>() {}.getTypeClass(),
									annotation.annotationType().getSimpleName());
					if(listener != null) {
						bindListener(fxApplication, listener,lazyLoad,field,annotation,appClass);
					}
				}
			
		}
	}
	private void bindListener(Class<?> appClass, FxApplication fxApplication) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, SecurityException, NoSuchMethodException {
		Method[] methods = appClass.getMethods();
		for(Method method: methods) {
			LazyLoad lazyLoad = method.getAnnotation(LazyLoad.class);
			Annotation[] annotations = method.getAnnotations();
			for(Annotation annotation : annotations) {
				FxMethodProcess<Annotation> listener = PlugsFactory.
						getPluginsInstanceByAttributeStrictAllowNull(new TypeToken<FxMethodProcess<Annotation>>() {}.getTypeClass(),
								annotation.annotationType().getSimpleName());
				if(listener != null) {
					bindListener(fxApplication, listener,lazyLoad,method,annotation, appClass);
				}
			}
		}
	}
	private void setFieldValue(Field field, FxApplication fxApplication, Object value) {
		try {
			ReflectUtils.setFieldValue(field, fxApplication, value);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("failed to process field "+field+" value "+ value,e);
		}
		
	}
	private void bindListener(FxApplication fxApplication,FxFieldProcess<Annotation> listener, LazyLoad lazyLoad, Field field,
			Annotation annotation, Class<?> appClass) {
		try {
			if(lazyLoad != null && listener instanceof LazyLoader) {
				fxApplication.getFxmlLoader().getNamespace().addListener(new MapChangeListener<String, Object>(){
					@SuppressWarnings("unchecked")
					@Override
					public void onChanged(Change<? extends String, ? extends Object> change) {
						if(StringUtil.equals(change.getKey(), ((LazyLoader<Annotation>)listener).id(annotation,field))){
							try {
								listener.adapter(fxApplication, field,annotation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
			}else {
				listener.adapter(fxApplication, field,annotation);
			}
		} catch (Exception e) {
			throw new RuntimeException("exception occur at process field " +field+" at "+appClass.getName(),e);
		}
		
	}
	private void bindListener(FxApplication fxApplication,FxMethodProcess<Annotation> listener,
			LazyLoad lazyLoad, Method method,Annotation annotation, Class<?> appClass) {
 		try {
			if(lazyLoad != null && listener instanceof LazyLoader) {
				fxApplication.getFxmlLoader().getNamespace().addListener(new MapChangeListener<String, Object>(){
					@SuppressWarnings("unchecked")
					@Override
					public void onChanged(Change<? extends String, ? extends Object> change) {
						if(StringUtil.equals(change.getKey(), ((LazyLoader<Annotation>)listener).id(annotation,method))){
							try {
								listener.adapter(fxApplication, method,annotation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
			}else {
				listener.adapter(fxApplication, method,annotation);
			}
		} catch (Exception e) {
			throw new RuntimeException("exception occur at process method " +method+" at "+appClass.getName(),e);
		}
	}
	@Override
	public void before() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void after() {
		// TODO Auto-generated method stub
		
	}
	

}
