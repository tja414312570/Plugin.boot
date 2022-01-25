package com.yanan.framework.fx.layout;

import java.lang.reflect.Field;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.listener.field.FxFieldListener;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.asserts.Assert;

import javafx.scene.layout.Region;

@Register(attribute = "MatchParent")
public class MatchParentAdapter implements FxFieldListener<MatchParent>{

	@Override
	public void adapter(FxApplication fxApplication, Field field, MatchParent annotation) throws Exception {
		Region view = fxApplication.findViewByField(field);
		Region parent = (Region) view.getParent();
		if(parent == null) {
			parent = fxApplication.getRootView();
		}
		Assert.isNotNull(parent,"could not found parent for field "+field.getName()+" at "+fxApplication.getAppClass().getName());
		if(annotation.width()) {
			view.prefWidthProperty().bind(parent.widthProperty());
		}
		if(annotation.height()) {
			view.prefHeightProperty().bind(parent.heightProperty());
		}
	}

}
