package plugin.boot;

import java.io.IOException;

public class TestHash {
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println(System.identityHashCode("lllllsss"));
		HashFile hashFile = new HashFile();
		long t = System.currentTimeMillis();
		hashFile.put("key", "hello world!");
//		hashFile.put("kes", "hello world!");
//		int len = 19;
//		for(int i = 0;i<len;i++) {
//			hashFile.put("key"+i, "hello world!"+i);
//		}
		System.err.println("------------");
//		System.out.println(hashFile.get("key"));
//		System.out.println(hashFile.get("kes"));
//		for(int i = 0;i<len;i++) {
//			String value = hashFile.get("key"+i);
//			System.out.println(value);
//		}
		long times = System.currentTimeMillis() - t;
		System.out.println(times+"ms");
//		synchronized (TestHash.class) {
//			TestHash.class.wait();
//		}
		
	}
}
