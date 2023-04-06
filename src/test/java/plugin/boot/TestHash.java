package plugin.boot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import com.yanan.utils.ByteUtils;

public class TestHash {
	public static void main(String[] args) throws IOException, InterruptedException {
//		System.out.println(System.identityHashCode("lllllsss"));
		HashFile hashFile = new HashFile();
		hashFile.setHashCode((keys)->{
			return ByteUtils.bytesToLong(keys);
		});
		
//		Map<String,String> hashFile = new HashMap<String,String>();
		long t = System.currentTimeMillis();
//		hashFile.put("key".getBytes(), "hello world!".getBytes());
		
		new HashMap<>().entrySet();
		HashNode node = hashFile.entrySet();
		System.out.println(node.hasNext());
		long len = 10000000;
		byte[] bytes = new byte[8];
		Random r = new Random();
		for(long i = 0;i<len;i++) {
			if(i % 10000 == 0)
			System.out.println(i);
			long num = r.nextInt( Integer.MAX_VALUE);
			ByteUtils.longToBytes(num,bytes);
			hashFile.put(bytes, bytes);
		}
		long times = System.currentTimeMillis() - t;
		System.err.println("------------存储:" +(times+"ms"));
		t = System.currentTimeMillis();
		node = hashFile.entrySet();
		hashFile.forEach((key,value)->{
			if(ByteUtils.bytesToLong(key) % 1000000 == 0)
			System.out.println(ByteUtils.bytesToLong(key)+"====>"+ByteUtils.bytesToLong(value));
		});
		times = System.currentTimeMillis() - t;
		System.err.println("------------读取:" +(times+"ms"));
		hashFile.destory();
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
