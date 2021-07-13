package plugin.boot;

import java.io.IOException;

import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.resource.DefaultResourceLoader;
import com.yanan.framework.resource.ResourceLoader;
import com.yanan.utils.resource.Resource;

public class TestNlp {
	public static void main(String[] args) throws IllegalAccessException, IOException {
//		File file = new File("/Users/yanan/Desktop/C++资料.txt");
//		AbstractHashMap hashMap = new AbstractHashMap();
//		String content = FileUtils.read(file, "utf-8");
//		System.out.println(content);
		PlugsFactory.getInstance().addScanPath("classpath:**");
		PlugsFactory.init("classpath:plugin.yc");
		ResourceLoader resourceLoaer = new DefaultResourceLoader();
		Resource resource = resourceLoaer.getResource("https://www.linuxprobe.com/block-string-device.html");
		
		int len = (int) resource.size();
		byte[] bytes = new byte[len];
		resource.getInputStream().read(bytes);
		System.out.println(new String(bytes));
		
	}
}