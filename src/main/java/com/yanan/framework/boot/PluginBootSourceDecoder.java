package com.yanan.framework.boot;

import com.yanan.framework.plugin.Plugin;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.plugin.builder.PluginDefinitionBuilderFactory;
import com.yanan.framework.plugin.decoder.ResourceDecoder;
import com.yanan.framework.plugin.definition.RegisterDefinition;
import com.yanan.utils.resource.scanner.PackageScanner;

@Register(attribute="PluginBootSource",id="pluginBootSourceDecoder")
public class PluginBootSourceDecoder implements ResourceDecoder<PluginBootSource>{
	@Override
	public void decodeResource(PlugsFactory factory,PluginBootSource resource) {
		String scanExpress = resource.getPath();
		PackageScanner scanner = new PackageScanner();
		scanner.setScanPath(scanExpress);
		scanner.setIgnoreLoadingException(true);
		scanner.doScanner((cls) -> tryDecodeDefinition(cls));
//		scanner = new PackageScanner();
//		scanner.setScanPath(env);
//		scanner.setIgnoreLoadingException(true);
//		scanner.doScanner((cls) -> tryDecodeDefinition(cls));
	}
	private void tryDecodeDefinition(Class<?> cls) {
		if(cls.getAnnotation(Service.class)!= null) {
			try {
				Plugin plugin = PluginDefinitionBuilderFactory.builderPluginDefinition(cls);
				PlugsFactory.getInstance().addPlugininDefinition(plugin);
			}catch(Throwable t) {
//				t.printStackTrace();
			}
		}
		if(cls.getAnnotation(Register.class)!= null) {
			try {
				RegisterDefinition registerDefinition = PluginDefinitionBuilderFactory.builderRegisterDefinition(cls);
				PlugsFactory.getInstance().addRegisterDefinition(registerDefinition);
			}catch(Throwable t) {
//				t.printStackTrace();
			}
			
		}
	}
}
