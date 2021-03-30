package plugin.boot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.resource.DefaultResourceLoader;
import com.yanan.framework.resource.ResourceLoader;
import com.yanan.utils.resource.FileUtils;
import com.yanan.utils.resource.ResourceManager;
import com.yanan.utils.string.PathMatcher;
import com.yanan.utils.string.StringUtil;

public class TestNlp {
	public static void main(String[] args) throws IllegalAccessException, IOException {
//		File file = new File("/Users/yanan/Desktop/C++资料.txt");
//		AbstractHashMap hashMap = new AbstractHashMap();
//		String content = FileUtils.read(file, "utf-8");
//		System.out.println(content);
		PlugsFactory.getInstance().addScanPath("classpath:**");
		PlugsFactory.init("classpath:plugin.yc");
		ResourceLoader resourceLoaer = new DefaultResourceLoader();
		resourceLoaer.getResource("https://ags.bgzchina.com:8999/proxy/v1/FTR/file/clientUpload");
		
	}
}