package com.yanan.framework.fx;

import java.lang.reflect.Field;

import com.alibaba.nacos.api.utils.StringUtils;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.handler.PlugsHandler;
import com.yanan.utils.asserts.Assert;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.resource.ResourceManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class FxWindow {
	protected abstract Scene start(Stage stage);

	protected Parent root;
	protected Object controller;

	@SuppressWarnings("unchecked")
	public <T extends Parent> T getRootView() {
		return (T) root;
	}

	@SuppressWarnings("unchecked")
	public <T> T getController() {
		return (T) controller;
	}

	public void start(Application application) throws Exception {
		PlugsHandler plugsHandler = PlugsFactory.getPluginsHandler(this);
		Class<?> appClass = getClass();
		if(plugsHandler != null) {
			appClass = plugsHandler.getRegisterDefinition().getRegisterClass();
		}
		ContextView contextView = appClass.getAnnotation(ContextView.class);
		Scene scence = null;
		Stage stage = new Stage();
		if (contextView != null) {
			FXMLLoader fxmlLoader = new FXMLLoader();
			String res = contextView.value();
			if(StringUtils.isEmpty(res)) {
				res = "classpath*:**"+appClass.getSimpleName()+".fxml";
			}
			fxmlLoader.setLocation(ResourceManager.getResource(res).getURI().toURL());
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
			root = fxmlLoader.load();
			this.controller = fxmlLoader.getController();
			scence = new Scene(root);
			Field[] fields = appClass.getDeclaredFields();
			for(Field field: fields) {
				if(field.getAnnotation(Bind.class) != null) {
					ReflectUtils.setFieldValue(field, this, controller);
				}
				if(field.getAnnotation(View.class) != null) {
					ReflectUtils.setFieldValue(field, this, root);
				}
			}
		}
		Scene newScence = this.start(stage);
		if(contextView == null) {
			scence = newScence;
		}
		Assert.isNotNull(scence);
		stage.setScene(scence);
		stage.show();
	}
}
