package com.yanan.framework.fx.attr;

import java.lang.ref.SoftReference;

import javafx.beans.property.DoublePropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;

public class ScrollPanelWidthProperty extends DoublePropertyBase implements ChangeListener<Number>{
	public static enum Type{
		WIDTH,HEIGHT;
	}
	SoftReference<ScrollPane> scrollReference ;
	private Type type;
	private ScrollBar hBar;
	private ScrollBar vBar;
	public ScrollPanelWidthProperty(ScrollPane pane,Type type) {
		scrollReference = new SoftReference<ScrollPane>(pane);
		this.type = type;
		if(type.equals(Type.WIDTH)) {
			pane.prefWidthProperty().addListener(this);
//			pane.setFitToWidth(true);
		}else {
			pane.prefHeightProperty().addListener(this);
//			pane.setFitToHeight(true);
		}
		initBar();
	}
	@Override
	public Object getBean() {
		return scrollReference.get();
	}
	private void initBar(){
		for (final Node node : scrollReference.get().lookupAll(".scroll-bar")) {
	        if (node instanceof ScrollBar) {
	            ScrollBar sb = (ScrollBar) node;
	            if (sb.getOrientation() == Orientation.HORIZONTAL) {
	                this.hBar = sb;
	            }
	            if (sb.getOrientation() == Orientation.VERTICAL) {
	                this.vBar = sb;
	            }
	        }
	    }
	}
	@Override
	public String getName() {
		return "scroll size "+this.scrollReference.get()+" type "+this.type;
	}
	
	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		  fireValueChangedEvent();
	}
	@Override
	public double get() {
		return getValue();
		
	}
	 @Override
    public Double getValue() {
		 ScrollPane scrollPane = scrollReference.get();
		 double value;
		 if(type == Type.WIDTH) {
			 if(vBar == null) {
				 initBar();
			 }
			 value = scrollPane.getWidth();
			 if(vBar != null && vBar.isVisible()) {
				 value = value - vBar.getWidth()-3;
			 }
		 }else {
			 value = scrollPane.getHeight();
			 if(hBar == null) {
				 initBar();
			 }
			 if(hBar != null && hBar.isVisible()) {
				 value = value - hBar.getHeight()-3;
			 }
		 }
        return value;
    }

	

}
