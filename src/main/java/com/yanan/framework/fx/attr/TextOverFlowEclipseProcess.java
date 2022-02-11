package com.yanan.framework.fx.attr;

import javafx.scene.control.Control;
import javafx.scene.layout.Region;
import javafx.util.Callback;

public class TextOverFlowEclipseProcess<T,R> implements Callback<T, R>{
	private Callback<T,R> callBack;
	public TextOverFlowEclipseProcess(Callback<T,R> callback) {
		this.callBack = callback;
	}
	@Override
	public R call(T param) {
		R cell = callBack.call(param);
		((Region)cell).setPrefWidth(0);
		((Region)cell).setMaxWidth(Control.USE_PREF_SIZE);
		return cell;
	}

}
