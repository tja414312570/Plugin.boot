package com.yanan.framework.fx.bind.conver;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.autowired.enviroment.Adapter;
import com.yanan.framework.plugin.autowired.enviroment.ResourceAdapter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

@Register(attribute = "text_background")
public class BackgroundPropertyAdapter implements PropertyAdapter< StringProperty,ObjectProperty<Background>>{

	@Override
	public ObjectProperty<Background> adapter(StringProperty input) {
		ObjectProperty<Background> result =  new SimpleObjectProperty<>();
		input.addListener((obs,old,nev)->{
			result.set(new Background(new BackgroundFill(Color.valueOf(nev),null,null)));
		});
		return result;
	}


}
