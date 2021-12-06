package com.yanan.framework.moniter;

import java.io.File;

/**
 * 文件修改监听
 * @author tja41
 *
 */
public interface PluginFileChanageListener {
	default void onAdded(File file,String scanPath) {};
	default void onChange(File file,String scanPath) {};
	default void onDelete(String fileName) {};
}
