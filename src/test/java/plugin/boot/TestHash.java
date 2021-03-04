package plugin.boot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;

public class TestHash {
	public static void main(String[] args) throws IOException, InterruptedException {
//		Thread.sleep(10000);
//		System.out.println(System.identityHashCode("lllllsss"));
		HashFile hashFile = new HashFile();
//		Map<String,String> hashFile = new HashMap<String,String>();
		long t = System.currentTimeMillis();
//		hashFile.put("key", "hello world!");
//		hashFile.put("kes", "hello world!");
//		System.out.println(hashFile.get("key-1"));
		long len = 10000000000l;
		for(long i = 0;i<len;i++) {
//			System.out.println("key"+i);
			hashFile.put("key"+i, "hello world!"+i);
			System.out.println("key:"+i);
		}
		System.err.println("------------");
//		System.out.println(hashFile.get("key"));
//		System.out.println(hashFile.get("kes"));
		for(long i = len-1;i>=0;i--) {
			String value = hashFile.get("key"+i);
//			if(!StringUtils.equals(i+"", value.split("!")[1]))
			System.out.println("key"+i+"==>"+value);
		}
		long times = System.currentTimeMillis() - t;
		System.out.println(times+"ms");
//		synchronized (TestHash.class) {
//			TestHash.class.wait();
//		}
	}
	public static String valueMulti(int i) {
		StringBuffer sb = new StringBuffer();
		if(i >1024) 
			i=1024;
		while(i-->0) {
			sb.append(i);
		}
		return sb.toString();
	}
}
