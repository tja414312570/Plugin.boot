package com.yanan.framework.fx.attr;

import java.util.Arrays;

import com.yanan.framework.fx.FXMLLoader;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.string.StringUtil;

import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination.Modifier;

@Register(attribute = { "keys"})
public class KeysProcess implements FxmlAttrProcess{

	public boolean process(FXMLLoader fxmlLoader, Class<?> sourceType, String attrName, String attrValue, Object node,
			Object parent) {
		if(StringUtil.isEmpty(attrValue))
			return true;
		MenuItem menu = (MenuItem) node;
		String[] keys = attrValue.split(",");
		Modifier[] modifiers = new Modifier[keys.length-1];
//		 final @NamedArg("shift") ModifierValue shift,
//         final @NamedArg("control") ModifierValue control,
//         final @NamedArg("alt") ModifierValue alt,
//         final @NamedArg("meta") ModifierValue meta,
//         final @NamedArg("shortcut") ModifierValue shortcut
		for(int i = 0;i<keys.length-1;i++) {
			switch(keys[i].toLowerCase()) {
			case "control":
				modifiers[i] = KeyCodeCombination.CONTROL_DOWN;
				break;
			case "ctrl":
				modifiers[i] = KeyCodeCombination.CONTROL_DOWN;
				break;
			case "shift":
				modifiers[i] = KeyCodeCombination.SHIFT_DOWN;
				break;
			case "alt":
				modifiers[i] = KeyCodeCombination.ALT_DOWN;
				break;
			case "meta":
				modifiers[i] = KeyCodeCombination.META_DOWN;
				break;
			case "shortcut":
				modifiers[i] = KeyCodeCombination.SHORTCUT_DOWN;
				break;
			default:
				throw new RuntimeException("modifier \""+keys[i]+"\" not support!");
			}
		}
		String keyCode  = keys[keys.length-1];
		if(keyCode != null) {
			keyCode = keyCode.toUpperCase();
		}
		KeyCodeCombination keyCodeCombination = new KeyCodeCombination(KeyCode.valueOf(keyCode), 
				modifiers);
		menu.setAccelerator(keyCodeCombination);
		return true;
	}
	public void processSize(Class<?> sourceType, String attrName, String attrValue, Object node,
			Object parent) {
		
	}

}
