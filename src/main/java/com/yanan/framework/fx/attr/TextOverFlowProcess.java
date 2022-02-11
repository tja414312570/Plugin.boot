package com.yanan.framework.fx.attr;

import com.yanan.framework.fx.FXMLLoader;
import com.yanan.framework.plugin.annotations.Register;

import javafx.scene.control.ListView;
import javafx.util.Callback;

@Register(attribute = { "textOverflow" })
public class TextOverFlowProcess implements FxmlAttrProcess{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean process(FXMLLoader fxmlLoader, Class<?> sourceType, String attrName, String attrValue, Object node,
			Object parent) {
		if(node instanceof ListView) {
			ListView<Object> listView = (ListView<Object>) node;
			listView.cellFactoryProperty().addListener((n1,e1,v1)->{
				if(v1 instanceof TextOverFlowEclipseProcess)
					return;
				Callback newCall = new TextOverFlowEclipseProcess<>(v1);
				listView.setCellFactory(newCall);
			});
		}
		return true;
	}

}
