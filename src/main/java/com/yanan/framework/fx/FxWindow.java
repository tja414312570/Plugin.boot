package com.yanan.framework.fx;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;

public abstract class FxWindow extends FxApplication{

	@SuppressWarnings("unchecked")
	public <T extends Parent> T getRootView() {
		return (T) root;
	}

	@SuppressWarnings("unchecked")
	public <T> T getController() {
		return (T) controller;
	}

	public void start(Application application) throws Exception {
		Stage stage = new Stage();
		start(stage);
	}
	
}
