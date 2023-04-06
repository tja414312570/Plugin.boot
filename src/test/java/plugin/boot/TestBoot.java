package plugin.boot;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

import org.apache.catalina.LifecycleException;

import com.alibaba.nacos.common.utils.Observable;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.decoder.StandScanResource;
import com.yanan.framework.resource.DefaultResourceLoader;
import com.yanan.utils.ArrayUtils;
import com.yanan.utils.beans.xml.Value;
import com.yanan.utils.resource.Resource;
import com.yanan.utils.resource.scanner.Path;

import javafx.beans.value.ObservableStringValue;
import javafx.collections.ObservableArray;

public class TestBoot {
	
	@Value
	private String name;
	
	public static void main(String[] args) throws LifecycleException, UnsupportedEncodingException {
		
		PlugsFactory.init(new StandScanResource("classpath*:**"));
		
		Resource resource = new DefaultResourceLoader().getResource("http://192.168.1.110:8091/scf/user/login");
		
		System.err.println(resource);
	}
}