package com.yanan.framework.fx.attr;

import java.lang.reflect.InvocationTargetException;

import com.yanan.framework.fx.BeanAdapter;
import com.yanan.framework.fx.FXMLLoader;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.asserts.Assert;
import com.yanan.utils.reflect.ReflectUtils;

import javafx.beans.binding.NumberExpression;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

@Register(attribute = { "width", "height", "size" ,"prefWidth","prefHeight" })
public class SizeProcess implements FxmlAttrProcess{

	public boolean process(FXMLLoader fxmlLoader, Class<?> sourceType, String attrName, String attrValue, Object node,
			Object parent) {
		
		if (attrValue.equals("match_parent")) {
			if(attrName == "size") {
				processSize(sourceType, "width", attrValue, node, parent);
				processSize(sourceType, "height", attrValue, node, parent);
			}else {
				processSize(sourceType, attrName, attrValue, node, parent);
			}
			return true;
		}
		return false;
	}
	public void processSize(Class<?> sourceType, String attrName, String attrValue, Object node,
			Object parent) {
		try {
			BeanAdapter targetAdapter = new BeanAdapter(node);
			String propertyName;
			if (attrName.toLowerCase().indexOf("width") != -1) {
				propertyName = "prefWidthProperty";
			} else {
				propertyName = "prefHeightProperty";
			}
			ObservableValue<Object> propertyModel = targetAdapter.getPropertyModel(propertyName);
			if (propertyModel == null) {
				propertyModel = ReflectUtils.invokeMethod(node, propertyName);
				if (propertyModel == null) {
					throw new RuntimeException(
							"Cannot found property " + node.getClass().getName() + "." + attrName);
				}
			}
			Assert.isNotNull(parent,"could not found parent!");
			NumberExpression parentModel = ReflectUtils.invokeMethod(parent, attrName + "Property");
			double pos = 0;
			if (attrName.toLowerCase().indexOf("width") != -1) {
				pos = ((Node) node).getLayoutX();
			} else {
				pos = ((Node) node).getLayoutY();
			}
			parentModel = parentModel.subtract(pos);
			if (parent instanceof ScrollPane) {
				((Property<Object>) propertyModel).bind(new ScrollPanelWidthProperty((ScrollPane) parent,
						attrName.toLowerCase().indexOf("width") != -1 ? ScrollPanelWidthProperty.Type.WIDTH
								: ScrollPanelWidthProperty.Type.HEIGHT));
			} else {
				((Property<Object>) propertyModel).bind(parentModel.subtract(2));
			}
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

}
