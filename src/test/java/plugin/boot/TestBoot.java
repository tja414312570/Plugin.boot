package plugin.boot;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

import org.apache.catalina.LifecycleException;

import com.yanan.utils.ArrayUtils;
import com.yanan.utils.resource.scanner.Path;

public class TestBoot {
	public static void main(String[] args) throws LifecycleException, UnsupportedEncodingException {
		File file = new File("/Users/yanan/Desktop/C++资料.txt");///Volumes/GENERAL
//		StringHashMap map = new StringHashMap();
		Path path = new Path(file);
		path.scanner((files)->{
			if(files.getName().endsWith(".download") || files.isDirectory() && ArrayUtils.isEmpty(files.list())
					|| files.length() == 0l) {
				new Thread(()->{
					System.out.println(files);
					files.delete();
				}).start();
			}
//			File child ;
//			if(map.get(files.getName()) != null) {
//				child = map.get(files.getName());
//				
//				System.err.println(files+"===>"+child+"----"+child.lastModified()+"----"+child.length());
//			}else {
//				map.put(files.getName(), files);
//				child = map.get(files.getName());
////				System.out.println(files.getName()+"-->"+child+"----"+child.lastModified()+"----"+child.length());
//			}
		});
	}
}