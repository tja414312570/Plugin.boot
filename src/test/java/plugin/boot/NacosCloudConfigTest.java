package plugin.boot;

import java.io.Reader;
import java.io.StringReader;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.framework.boot.BootArgs;
import com.YaNan.framework.boot.Environment;
import com.YaNan.framework.boot.PluginBoot;
import com.YaNan.framework.boot.PluginBootServer;
import com.YaNan.framework.boot.StandEnvironmentBoot;
import com.YaNan.framework.boot.cloud.nacos.CloudConfigServer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@BootArgs(name="-boot-configure",value="boot-nacos.yc")
@BootArgs(name="-environment-boot",value="com.YaNan.framework.boot.StandEnvironmentBoot")
@PluginBoot
public class NacosCloudConfigTest {
	public static void main(String[] args) {
		PluginBootServer.run(args);
	}
}
