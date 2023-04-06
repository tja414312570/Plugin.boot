package com.yanan.framework.fx.bind.conver;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.autowired.enviroment.Adapter;
import com.yanan.framework.plugin.autowired.enviroment.ResourceAdapter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

@Register(attribute = "selectItem_text")
public class SelectItemPropertyAdapter implements PropertyAdapter< ReadOnlyObjectProperty,Property>{

	@Override
	public Property adapter(ReadOnlyObjectProperty input) {
		Property result =  new SimpleStringProperty();
		input.addListener((obs,old,nev)->{
			result.setValue(nev.toString());
		});
		return result;
	}


}
