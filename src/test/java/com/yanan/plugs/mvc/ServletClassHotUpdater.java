package com.yanan.plugs.mvc;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.hot.ClassUpdateListener;
import com.yanan.framework.webmvc.RestfulDispatcher;
import com.yanan.framework.plugin.PlugsFactory;

@Register(attribute = "com.yanan.billing.controller.*")
public class ServletClassHotUpdater implements ClassUpdateListener{
	static Logger log = LoggerFactory.getLogger(ServletClassHotUpdater.class);
	@Override
	public void updateClass(Class<?> originClass, Class<?> updateClass, Class<?> updateOrigin, File updateFile) {
		synchronized (this) {
			RestfulDispatcher rstDispatcher = PlugsFactory.getPluginsInstance(RestfulDispatcher.class);
			if(originClass!=null){
				if(updateClass!=null){//更新类
					log.debug("try update servlet bean class : " + originClass.getName()+ " as new servlet bean class : " + updateClass.getName());
					rstDispatcher.addServletByScanBean(updateClass);
					rstDispatcher.rebuildServlet(originClass,updateClass);
				}else{//删除类时删除对应映射
					log.debug("try remove servlet bean class : " + originClass.getName());
					rstDispatcher.rebuildServlet(originClass,null);
				}
			}else{//新添加类
				log.debug("try add servlet bean class : " + updateClass.getName());
				rstDispatcher.addServletByScanBean(updateClass);
				rstDispatcher.rebuildServlet(null,updateClass);
			}
			
		}
	}

}