package com.yanan.framework.boot.utils;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.annotations.Support;
import com.yanan.framework.plugin.definition.RegisterDefinition;
import com.yanan.framework.plugin.exception.PluginInitException;
import com.yanan.framework.plugin.handler.FieldHandler;
import com.yanan.framework.plugin.handler.HandlerSet;

@Support(Log.class)
@Register
public class LoggerParameter implements FieldHandler
{

	@Override
	public void preparedField(RegisterDefinition registerDefinition, Object proxy, Object target,
			HandlerSet handlerSet, Field field) {
		Logger logger = LoggerFactory.getLogger(registerDefinition.getRegisterClass());
		try {
			field.setAccessible(true);
			field.set(proxy, logger);
			field.setAccessible(false);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new PluginInitException("logger inject failed",e);
		}
		System.out.println(field);
	}

}