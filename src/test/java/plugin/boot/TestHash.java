package plugin.boot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;

public class TestHash {
	public static void main(String[] args) throws IOException, InterruptedException {
		int hash = 0;
		int pos = hash << 6;
//		System.out.println(pos);
//		Thread.sleep(10000);
//		System.out.println(System.identityHashCode("lllllsss"));
		HashFile hashFile = new HashFile();
//		Map<String,String> hashFile = new HashMap<String,String>();
		long t = System.currentTimeMillis();
		hashFile.put("key", "hello world!".getBytes());
		System.out.println(new String(hashFile.get("key")));
		hashFile.put("key", "hello world!s".getBytes());
		hashFile.put("kes", "hello kes".getBytes());
		System.out.println(new String(hashFile.get("key")));
		System.out.println(new String(hashFile.get("kes")));
		hashFile.remove("key");
		
		byte[] result = hashFile.get("key");
		System.out.println(result);
		System.out.println(new String(hashFile.get("kes")));
		hashFile.removeAll();
		result = hashFile.get("kes");
		System.out.println(result);
		result = hashFile.get("key");
		System.out.println(result);
		hashFile.put("key", "hello world!ssssssss".getBytes());
		result = hashFile.get("key");
		System.out.println(new String(result));
//		System.out.println(hashFile.get("key-1"));
//		int len = 100000000;
//		for(long i = 0;i<len;i++) {
////			System.out.println("key"+i);
//			hashFile.put("key"+i, "hello world!"+i);
////			System.out.println("key:"+i);
//		}
//		long times = System.currentTimeMillis() - t;
//		
//		System.err.println("------------存储:" +(times+"ms"));
//		t = System.currentTimeMillis();
////		System.out.println(hashFile.get("key"));
////		System.out.println(hashFile.get("kes"));
//		for(long i = len-1;i>=0;i--) {
//			hashFile.get("key"+i);
////			System.out.println(new String(value));
////			if(!StringUtils.equals(i+"", new String(value).split("!")[1]))
////				System.out.println("key"+i+"==>"+new String(value));
//		}
//		times = System.currentTimeMillis() - t;
//		System.err.println("------------读取:" +(times+"ms"));
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
