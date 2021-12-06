package com.yanan.framework.moniter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.alibaba.nacos.shaded.com.google.common.base.Objects;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.yanan.framework.plugin.Environment;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.plugin.autowired.enviroment.VariableProcesser;

@Register(attribute = "*.yc")
public class ConfigFileChanageListener implements PluginFileChanageListener{
	@Service
	private Logger logger;
	@Override
	public void onChange(File newFile,String scanPath) {
		logger.info("accept changed config file : "+newFile);
		Reader reader;
		try {
			reader = new FileReader(newFile);
			Config config = ConfigFactory.parseReader(reader);
			List<String> changeList = new ArrayList<>();
			foundChangeConfig(changeList,config);
			if(changeList.size()>0) {
				Environment.getEnviroment().mergeConfig(config);
				changeList.forEach(key->{
					logger.info("update key : "+key+",value:"+Environment.getEnviroment().getConfigValue(key).unwrapped());
					List<ConfigValueChanageListener> list = PlugsFactory.getPluginsInstanceListByAttribute(ConfigValueChanageListener.class, key);
					list.forEach(item->item.onChange(key));
				});
			}
		} catch (FileNotFoundException e) {
			logger.error("failed to process config "+newFile.getAbsolutePath(),e);
		}
		
	}
	private void foundChangeConfig(List<String> changeList, Config config) {
//		System.err.println(config);
		config.entrySet().forEach(entry->{
//			System.err.println(entry.getKey()+"==>"+entry.getValue());
			String key = entry.getKey();
			ConfigValue newValue = entry.getValue();
			ConfigValue oldValue = Environment.getEnviroment().getConfigValue(key);
			if(oldValue != null && Objects.equal(oldValue.unwrapped(),newValue.unwrapped())) 
				return;
			changeList.add(key);
		});
	}

}
