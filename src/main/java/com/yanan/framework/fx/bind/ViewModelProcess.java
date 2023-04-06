package com.yanan.framework.fx.bind;

import java.lang.reflect.Field;
import java.net.MalformedURLException;

import com.yanan.framework.fx.FxApplication;
import com.yanan.framework.fx.process.field.FxFieldPostProcess;
import com.yanan.framework.fx.process.method.AbstractLazyListener;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.resource.ResourceManager;

import javafx.stage.Stage;

@Register(attribute = "ViewModel")
public class ViewModelProcess extends AbstractLazyListener implements FxFieldPostProcess<ViewModel>{

	@Override
	protected String getId(String id) {
		try {
			return ResourceManager.getResource(id).getURI().toURL().toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void adapter(FxApplication fxApplication, Field field, ViewModel annotation) throws Exception {
		Object instance = ReflectUtils.getFieldValue(field, fxApplication);
		if(instance == null) {
			instance = (FxApplication) PlugsFactory.getPluginsInstance(field.getType());
		}
		if(instance instanceof FxApplication) {
			((FxApplication)instance).start(fxApplication.getPrimaryStage());
		}else {
			FxApplication fxApplication2 = new FxApplication() {
				
				@Override
				protected void startApp(Stage stage) throws Exception {
					
				}
			};
//			fxApplication2.setAppClass(field.getType());
			fxApplication2.start(fxApplication.getPrimaryStage());
		}
		
	}
}
