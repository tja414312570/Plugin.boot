package plugin.boot;

import java.io.IOException;
import java.util.HashMap;

import com.yanan.utils.ByteUtils;

public class TestHash {
	public static void main(String[] args) throws IOException, InterruptedException {
//		Thread.sleep(10000);
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
//		System.out.println(new String(hashFile.get("key".getBytes())));
//		hashFile.put("key".getBytes(), "hello world!s".getBytes());
//		hashFile.put("kes".getBytes(), "hello kes".getBytes());
//		System.out.println(node.hasNext());
//		System.out.println(new String(hashFile.get("key".getBytes())));
//		System.out.println(new String(hashFile.get("kes".getBytes())));
//		hashFile.remove("key".getBytes());
//		
//		byte[] result = hashFile.get("key".getBytes());
//		System.out.println(result);
//		System.out.println(new String(hashFile.get("kes".getBytes())));
//		hashFile.removeAll();
//		result = hashFile.get("kes".getBytes());
//		System.out.println(result);
//		result = hashFile.get("key".getBytes());
//		System.out.println(result);
//		hashFile.put("key".getBytes(), "hello world!ssssssss".getBytes());
//		result = hashFile.get("key".getBytes());
//		System.out.println(new String(result));
//		System.out.println(hashFile.get("key-1".getBytes()));
		long len = 200000000;
		byte[] bytes = new byte[8];
		for(long i = 0;i<len;i++) {
			if(i % 1000000 == 0)
			System.out.println(i);
			ByteUtils.longToBytes(i,bytes);
			hashFile.put(bytes, bytes);
//			System.out.println("key:"+i);
		}
		long times = System.currentTimeMillis() - t;
//		
		System.err.println("------------存储:" +(times+"ms"));
		t = System.currentTimeMillis();
////		System.out.println(hashFile.get("key"));
////		System.out.println(hashFile.get("kes"));
//		for(long i = len-1;i>=0;i--) {
//			byte[] value = hashFile.get(("key"+i).getBytes());
////			System.out.println(new String(value));
////			if(!StringUtils.equals(i+"", new String(value).split("!")[1]))
////				System.out.println("key"+i+"==>"+new String(value));
//		}
//		hashFile.remove(new String("key9879").getBytes());
		node = hashFile.entrySet();
//		while(node != null) {
//			System.out.println(new String(node.getKey())+"===>"+new String(node.getValue()));
//			node = node.nextNode();
//		}
//		hashFile.forEach((key,value)->{
//			System.out.println(new String(key)+"===>"+new String(value));
//		});
		hashFile.forEach((key,value)->{
			if(ByteUtils.bytesToLong(key) % 1000000 == 0)
			System.out.println(ByteUtils.bytesToLong(key)+"====>"+ByteUtils.bytesToLong(value));
		});
		times = System.currentTimeMillis() - t;
//		System.out.println(new String(hashFile.get("key999".getBytes()))+"======"+hashFile.getNode("key999".getBytes()).getNodePos());
		System.err.println("------------读取:" +(times+"ms"));
		
//		hashFile.forEach((key,value)->{
//			System.out.println(ByteUtils.bytesToLong(key)+"====>"+ByteUtils.bytesToLong(value));
//		});
		synchronized (TestHash.class) {
			TestHash.class.wait();
		}
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
