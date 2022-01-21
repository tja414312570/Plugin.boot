package com.yanan.framework.fx;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.alibaba.nacos.api.utils.StringUtils;
import com.yanan.framework.fx.listener.OnAction;
import com.yanan.framework.fx.listener.OnMouseClick;
import com.yanan.framework.fx.listener.OnSelected;
import com.yanan.framework.fx.listener.OnSelectedItem;
import com.yanan.framework.fx.listener.OnSelectedToggle;
import com.yanan.framework.fx.listener.OnTextChange;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.handler.PlugsHandler;
import com.yanan.utils.asserts.Assert;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.resource.ResourceManager;
import com.yanan.utils.string.StringUtil;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Toggle;
import javafx.scene.input.MouseEvent;
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
			OnSelected onSelected = method.getAnnotation(OnSelected.class);
			if(onSelected != null) {
				Object view = findViewById(onSelected.value());
				SingleSelectionModel<?> selectModel = (SingleSelectionModel<?>) ReflectUtils.invokeMethod(view,"getSelectionModel");
				selectModel.selectedIndexProperty().addListener((observable,oldValue,newValue)->{
					try {
						if(newValue.intValue() < 0 && !onSelected.negative()) {
							return;
						}
						if(method.getParameterCount() == 2) {
							method.invoke(this,newValue,oldValue);
							return;
						}
						if(method.getParameterCount() == 1) {
							method.invoke(this,newValue);
							return;
						}
						if(method.getParameterCount() == 0) {
							method.invoke(this);
							return;
						}
						method.invoke(this,observable,oldValue,newValue);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new RuntimeException("failed to execute listener ["+method+"]",e);
					}
				});
			}
			OnSelectedItem onSelectedItem = method.getAnnotation(OnSelectedItem.class);
			if(onSelectedItem != null) {
				Object view = findViewById(onSelectedItem.value());
				SingleSelectionModel<?> selectModel = ReflectUtils.invokeMethod(view,"getSelectionModel");
				selectModel.selectedItemProperty().addListener((observable,oldValue,newValue)->{
					try {
						if(method.getParameterCount() == 2) {
							method.invoke(this,newValue,oldValue);
							return;
						}
						if(method.getParameterCount() == 1) {
							method.invoke(this,newValue);
							return;
						}
						if(method.getParameterCount() == 0) {
							method.invoke(this);
							return;
						}
						method.invoke(this,observable,oldValue,newValue);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new RuntimeException("failed to execute listener ["+method+"]",e);
					}
				});
			}
			OnAction onAction = method.getAnnotation(OnAction.class);
			if(onAction != null) {
				Object view = findViewById(onAction.value());
				ObjectProperty<EventHandler<ActionEvent>> objectProperty = ReflectUtils.invokeMethod(view,"onActionProperty");
				objectProperty.set((event)->{
					try {
						if(method.getParameterCount() == 0) {
							method.invoke(this);
							return;
						}
						method.invoke(this,event);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new RuntimeException("failed to execute listener ["+method+"]",e);
					}
				});
			}
			OnTextChange OnTextChange = method.getAnnotation(OnTextChange.class);
			if(OnTextChange != null) {
				Object view = findViewById(OnTextChange.value());
				StringProperty stringProperty = ReflectUtils.invokeMethod(view,"textProperty");
				stringProperty.addListener((value)->{
					try {
						if(method.getParameterCount() == 0) {
							method.invoke(this);
							return;
						}
						method.invoke(this,value);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new RuntimeException("failed to execute listener ["+method+"]",e);
					}
				});
			}
			OnMouseClick onMouseClick = method.getAnnotation(OnMouseClick.class);
			if(onMouseClick != null) {
				Object view = findViewById(onMouseClick.value());
				ObjectProperty<EventHandler<? super MouseEvent>> objectProperty = ReflectUtils.invokeMethod(view,"onMouseClickedProperty");
				objectProperty.set((value)->{
					try {
						if(method.getParameterCount() == 0) {
							method.invoke(this);
							return;
						}
						method.invoke(this,value);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new RuntimeException("failed to execute listener ["+method+"]",e);
					}
				});
			}
			OnSelectedToggle onSelectedToggle = method.getAnnotation(OnSelectedToggle.class);
			if(onSelectedToggle != null) {
				Object view = findViewById(onSelectedToggle.value());
				ReadOnlyObjectProperty<Toggle> objectProperty = ReflectUtils.invokeMethod(view,"selectedToggleProperty");
				objectProperty.addListener((observable,oldValue,newValue)->{
					try {
						if(method.getParameterCount() == 2) {
							method.invoke(this,newValue,oldValue);
							return;
						}
						if(method.getParameterCount() == 1) {
							method.invoke(this,newValue);
							return;
						}
						if(method.getParameterCount() == 0) {
							method.invoke(this);
							return;
						}
						method.invoke(this,observable,oldValue,newValue);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new RuntimeException("failed to execute listener ["+method+"]",e);
					}
				});
			}
		}
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
