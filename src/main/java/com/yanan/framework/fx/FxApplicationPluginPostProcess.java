package com.yanan.framework.fx;

import java.util.Map;

import com.yanan.framework.fx.bind.BindBuilder;
import com.yanan.framework.fx.bind.BindContext;
import com.yanan.framework.fx.process.field.FxFieldBeforeProcess;
import com.yanan.framework.fx.process.field.FxFieldPostProcess;
import com.yanan.framework.fx.process.field.FxFieldProcess;
import com.yanan.framework.fx.process.method.FxMethodBeforeProcess;
import com.yanan.framework.fx.process.method.FxMethodPostProcess;
import com.yanan.framework.fx.process.method.FxMethodProcess;
import com.yanan.framework.fx.process.method.LazyLoader;
import com.yanan.framework.plugin.FactoryRefreshProcess;
import com.yanan.framework.plugin.InstanceBeforeProcesser;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.ProxyModel;
import com.yanan.framework.plugin.RegisterRefreshProcess;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.definition.RegisterDefinition;

import javafx.beans.property.Property;

/**
 * 将fx应用强制设置为cglib代理
 * @author tja41
 *
 */
@Register
public class FxApplicationPluginPostProcess implements RegisterRefreshProcess,InstanceBeforeProcesser,FactoryRefreshProcess{

	@Override
	public void process(PlugsFactory plugsFactory, RegisterDefinition currentRegisterDefinition) {
//		System.err.println(currentRegisterDefinition.getRegisterClass());
		if(FxApplication.class.isAssignableFrom(currentRegisterDefinition.getRegisterClass())
				||LazyLoader.class.isAssignableFrom(currentRegisterDefinition.getRegisterClass())) {
			currentRegisterDefinition.setProxyModel(ProxyModel.CGLIB);
		}
	}

	private void buildMethodMapping(RegisterDefinition currentRegisterDefinition,Class<?> endClass,Object instance) {
		Map<String,Property<?>> map = PlugsFactory.getPluginsInstance(BindBuilder.class).builder(instance);
		map.forEach((key,value)->{
			BindContext.getContext().addProperty(key,value);
		});
	}

	@Override
	public Object before(RegisterDefinition registerDefinition, Class<?> serviceClasss, Object instance) {
		if(FxApplication.class.isAssignableFrom(registerDefinition.getRegisterClass())) {
			buildMethodMapping(registerDefinition,FxApplication.class,instance);
		}
		return instance;
	}

	@Override
	public void process(PlugsFactory plugsFactory) {
		plugsFactory.addPlugininDefinition(FxFieldPostProcess.class);
		plugsFactory.addPlugininDefinition(FxFieldProcess.class);
		plugsFactory.addPlugininDefinition(FxFieldBeforeProcess.class);
		plugsFactory.addPlugininDefinition(FxMethodPostProcess.class);
		plugsFactory.addPlugininDefinition(FxMethodProcess.class);
		plugsFactory.addPlugininDefinition(FxMethodBeforeProcess.class);
	}

}
