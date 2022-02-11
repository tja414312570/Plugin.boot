package com.yanan.framework.fx.attr;

import com.yanan.framework.fx.FXMLLoader;

public interface FxmlAttrProcess {
	boolean process(FXMLLoader fxmlLoade,Class<?> sourceType, String attrName, String attrValue,Object node,Object parent);
}
