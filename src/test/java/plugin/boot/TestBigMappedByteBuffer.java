package plugin.boot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.yanan.utils.ByteUtils;
import com.yanan.utils.resource.ResourceManager;

public class TestBigMappedByteBuffer {
	public static void main(String[] args) throws IOException {
		File file = new File(ResourceManager.classPath(),"test.file");
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
		BigMappedByteBuffer bigMappedByteBuffer = new BigMappedByteBuffer(randomAccessFile);
		byte[] bytes = new byte[8];
		long len = 10;
		for(long i = 0;i<len;i++) {
			ByteUtils.longToBytes(i,bytes);
			bigMappedByteBuffer.put(bytes);
		}
		bigMappedByteBuffer.position(1);
		System.err.println(bigMappedByteBuffer.position());
		for(long i = 0;i<len;i++) {
			bigMappedByteBuffer.get(bytes);
			System.err.println(ByteUtils.bytesToLong(bytes));
		}
				
	}
}
