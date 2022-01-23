package com.yanan.framework.fx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.alibaba.nacos.api.utils.StringUtils;
import com.yanan.framework.fx.listener.FxListener;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.handler.PlugsHandler;
import com.yanan.utils.asserts.Assert;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.reflect.TypeToken;
import com.yanan.utils.resource.ResourceManager;
import com.yanan.utils.string.StringUtil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class FxApplication extends Application{
	protected abstract void startApp(Stage stage) throws Exception;

	protected Parent root;
	protected Object controller;
	protected Stage primaryStage;
	private FXMLLoader fxmlLoader;
	private Class<?> appClass;

	@SuppressWarnings("unchecked")
	public <T extends Parent> T getRootView() {
		return (T) root;
	}
	public Parent getRoot() {
		return root;
	}

	public FXMLLoader getFxmlLoader() {
		return fxmlLoader;
	}

	public Class<?> getAppClass() {
		return appClass;
	}
	@SuppressWarnings("unchecked")
	public <T> T getController() {
		return (T) controller;
	}
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	private void bind() throws Exception {
		appClass = getClass();
		if(PlugsFactory.isProxy(this)) {
			PlugsHandler plugsHandler = PlugsFactory.getPluginsHandler(this);
			appClass = plugsHandler.getRegisterDefinition().getRegisterClass();
		}
		ContextView contextView = appClass.getAnnotation(ContextView.class);
		String res ;
		fxmlLoader = new FXMLLoader();
		if (contextView != null) {
			res = contextView.value();
			if(StringUtils.isEmpty(res))
				res = "classpath*:**"+appClass.getSimpleName()+".fxml";
		}else {
			res = "classpath*:**"+appClass.getSimpleName()+".fxml";
		}
		fxmlLoader.setLocation(ResourceManager.getResource(res).getURI().toURL());
		fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
		root = fxmlLoader.load();
		this.controller = fxmlLoader.getController();
		bindView();
		bindListener();
	}
	private void bindView() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Field[] fields = appClass.getDeclaredFields();
		for(Field field: fields) {
			if(field.getAnnotation(Bind.class) != null) {
				ReflectUtils.setFieldValue(field, this, controller);
			}
			if(field.getAnnotation(View.class) != null) {
				ReflectUtils.setFieldValue(field, this, root);
			}
			if(field.getAnnotation(BindStage.class) != null) {
				ReflectUtils.setFieldValue(field, this, primaryStage);
			}
			FindViewById findViewById = field.getAnnotation(FindViewById.class);
			if(findViewById != null) {
				String name = field.getName();
				if(StringUtil.isNotEmpty(findViewById.value())) {
					name = findViewById.value();
				}
				Object view = findViewById(name);
				ReflectUtils.setFieldValue(field, this, view);
			}
			FindView findView = field.getAnnotation(FindView.class);
			if(findView != null) {
				String name = field.getName();
				if(StringUtil.isNotEmpty(findView.value())) {
					name = findView.value();
				}
				Object view = root.lookup(name);
				ReflectUtils.setFieldValue(field, this, view);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T findViewById(String id) {
		T t = (T) root.lookup("#"+id);
//		if(t == null) {
//			t = (T) root.lookupAll("#"+id);
//		}
		if(t == null) {
			t = (T) fxmlLoader.getNamespace().get(id);
		}
		if(t == null)
			throw new RuntimeException("could not found view by id ["+id+"]");
		return t;
	}
 	private void bindListener() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, SecurityException, NoSuchMethodException {
		Method[] methods = appClass.getMethods();
		for(Method method: methods) {
			//stage部分
			Annotation[] annotations = method.getAnnotations();
			for(Annotation annotation : annotations) {
				FxListener<Annotation> listener = PlugsFactory.
						getPluginsInstanceByAttributeStrictAllowNull(new TypeToken<FxListener<Annotation>>() {}.getTypeClass(),
								annotation.annotationType().getSimpleName());
				if(listener != null) {
					listener.adapter(this, method,annotation);
				}
			}
		}
	}

 	public void callDrawUi(Runnable execute) {
 		 Platform.runLater(execute);
 	}
 	
	public void start(Stage stage) throws Exception {
		this.primaryStage = stage;
		bind();
		Scene scence = new Scene(root);
		this.startApp(stage);
		Assert.isNotNull(scence);
		stage.setScene(scence);
		stage.show();
	}
}
