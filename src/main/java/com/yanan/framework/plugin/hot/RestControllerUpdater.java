package com.yanan.framework.plugin.hot;

import java.io.File;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.webmvc.RestfulDispatcher;
import com.yanan.framework.plugin.PlugsFactory;

@Register
public class RestControllerUpdater implements ClassUpdateListener{

	@Override
	public void updateClass(Class<?> originClass, Class<?> updateClass, Class<?> updateOrigin, File updateFile) {
		try {
			RestfulDispatcher restfulDispatcher = PlugsFactory.getPluginsInstance(RestfulDispatcher.class);
			restfulDispatcher.addServletByScanBean(updateClass);
			restfulDispatcher.rebuildServlet(originClass, updateClass);
		}catch(Exception e) {
			
		}
		
	}

}