package com.yanan.framework.fx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yanan.framework.fx.process.field.FxFieldPostProcess;
import com.yanan.framework.fx.process.field.FxFieldProcess;
import com.yanan.framework.fx.process.method.FxMethodPostProcess;
import com.yanan.framework.fx.process.method.FxMethodProcess;
import com.yanan.framework.fx.process.method.LazyLoader;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.asserts.Assert;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.reflect.TypeToken;
import com.yanan.utils.string.StringUtil;

import javafx.collections.MapChangeListener;

@Register
public class DefultFxApplicationProcess implements FxApplicationProcess{

	@Override
	public void process(FxApplication fxApplication,Object instance) {
		before(fxApplication, instance);
		after(fxApplication, instance);
	}
	@Override
	public void before(FxApplication fxApplication,Object instance) {
		Assert.isTrue(PlugsFactory.isProxy(instance),"instance "+instance+" is not proxy!");
		Class<?> clazz = PlugsFactory.getPluginsHandler(instance).getRegisterDefinition()
				.getRegisterClass();
		bindView(fxApplication,clazz,instance);
		methodProcess(fxApplication,new TypeToken<FxMethodProcess<Annotation>>() {
		}.getTypeClass(),clazz,instance);
	}

	@Override
	public void after(FxApplication fxApplication,Object instance) {
		Assert.isTrue(PlugsFactory.isProxy(instance),"instance "+instance+" is not proxy!");
		Class<?> clazz = PlugsFactory.getPluginsHandler(instance).getRegisterDefinition()
				.getRegisterClass();
		fieldProcess(fxApplication,new TypeToken<FxFieldPostProcess<Annotation>>() {
		}.getTypeClass(),clazz,instance);
		methodProcess(fxApplication,new TypeToken<FxMethodPostProcess<Annotation>>() {
		}.getTypeClass(),clazz,instance);
	}
	@SuppressWarnings("restriction")
	private void bindView( FxApplication fxApplication,Class<?> appClass,Object instance) {
		Field[] fields = ReflectUtils.getAllFields(appClass,FxApplication.class);
		for(Field field: fields) {
				if(field.getAnnotation(Controller.class) != null) {
					setFieldValue(field,instance,fxApplication.getController());
				}
				if(field.getAnnotation(RootView.class) != null) {
					setFieldValue(field, instance, fxApplication.getRoot());
				}
				if(field.getAnnotation(PrimaryStage.class) != null) {
					setFieldValue(field, instance, fxApplication.getPrimaryStage());
				}
				FindViewById findViewById = field.getAnnotation(FindViewById.class);
				if(findViewById != null) {
					Object view = fxApplication.findViewByField(field);
					setFieldValue(field, instance, view);
				}
				FindView findView = field.getAnnotation(FindView.class);
				if(findView != null) {
					String name = field.getName();
					if(StringUtil.isNotEmpty(findView.value())) {
						name = findView.value();
					}
					Object view = fxApplication.getRoot().lookup(name);
					setFieldValue(field, instance, view);
				}
				Annotation[] annotations = field.getAnnotations();
				LazyLoad lazyLoad = field.getAnnotation(LazyLoad.class);
				for(Annotation annotation : annotations) {
					FxFieldProcess<Annotation> listener = PlugsFactory.
							getPluginsInstanceByAttributeStrictAllowNull(new TypeToken<FxFieldProcess<Annotation>>() {}.getTypeClass(),
									annotation.annotationType().getSimpleName());
					if(listener != null) {
						bindListener(fxApplication,listener,appClass,instance,field,annotation,lazyLoad);
					}
				}
			
		}
	}
	private void bindListener(FxApplication fxApplication,Class<?> appClass,Object instance)  {
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
	private void setFieldValue(Field field, Object instance, Object value) {
		try {
			ReflectUtils.setFieldValue(field, instance, value);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("failed to process field "+field+" value "+ value,e);
		}
		
	}
	private void bindListener(FxApplication fxApplication,FxFieldProcess<Annotation> listener,Class<?> appClass,Object instance,  Field field,
			Annotation annotation, LazyLoad lazyLoad ) {
		try {
			if(lazyLoad != null && listener instanceof LazyLoader) {
				fxApplication.getFxmlLoader().getNamespace().addListener(new MapChangeListener<String, Object>(){
					@SuppressWarnings("unchecked")
					@Override
					public void onChanged(Change<? extends String, ? extends Object> change) {
						if(StringUtil.equals(change.getKey(), ((LazyLoader<Annotation>)listener).id(annotation,field))){
							try {
								listener.adapter(fxApplication,instance, field,annotation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
			}else {
				listener.adapter(fxApplication,instance, field,annotation);
			}
		} catch (Exception e) {
			throw new RuntimeException("exception occur at process field " +field.getName()+" at "+appClass.getName(),e);
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
			throw new RuntimeException("exception occur at process method " +method.getName()+" at "+appClass.getName(),e);
		}
	}
	
	private <T extends FxFieldProcess<Annotation>> void fieldProcess(FxApplication fxApplication,Class<T> processClass, Class<?> clazz, Object instance)  {
		Field[] fields = ReflectUtils.getAllFields(clazz,FxApplication.class);
		for(Field field: fields) {
			Annotation[] annotations = field.getAnnotations();
			LazyLoad lazyLoad = field.getAnnotation(LazyLoad.class);
			for(Annotation annotation : annotations) {
				T listener = PlugsFactory.
						getPluginsInstanceByAttributeStrictAllowNull(processClass,
								annotation.annotationType().getSimpleName());
				if(listener != null) {
					bindListener(fxApplication,listener,clazz,instance,field,annotation,lazyLoad);
				}
			}
		}
		
	}
	private <T extends FxMethodProcess<Annotation>> void methodProcess(FxApplication fxApplication, Class<T> processClass,Class<?> clazz, Object instance) {
		Method[] methods = clazz.getMethods();
		for(Method method: methods) {
			//stage部分
			Annotation[] annotations = method.getAnnotations();
			LazyLoad lazyLoad = method.getAnnotation(LazyLoad.class);
			for(Annotation annotation : annotations) {
				T listener = PlugsFactory.
						getPluginsInstanceByAttributeStrictAllowNull(processClass,
								annotation.annotationType().getSimpleName());
				if(listener != null) {
					bindListener(fxApplication,listener,lazyLoad,method,annotation,clazz);
				}
			}
		}
	}
}
