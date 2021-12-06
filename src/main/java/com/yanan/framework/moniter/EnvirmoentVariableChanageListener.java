package com.yanan.framework.moniter;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.autowired.enviroment.VariableProcesser;

@Register
public class EnvirmoentVariableChanageListener implements ConfigValueChanageListener{
	@Override
	public void onChange(String key) {
		VariableProcesser.updateVaiable(key);
	}

}
