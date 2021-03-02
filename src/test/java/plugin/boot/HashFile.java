package plugin.boot;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import org.apache.commons.codec.binary.StringUtils;

import com.yanan.utils.ByteUtils;
import com.yanan.utils.UnsafeUtils;
import com.yanan.utils.resource.ResourceManager;

import sun.misc.Unsafe;

public class HashFile {
	private HashIndex hashIndex;
	private String tabName;
	private HashValue hashValue;
	private File indexFile;
	private File valueFile;
	private File nodeFile;
	private RandomAccessFile indexAccess;
	private RandomAccessFile valueAccess;
	private RandomAccessFile nodeAccess;
	//index表每个数据的宽度
	private int index_bytes_len = 8;
	//index表的总大小
	private int max_index_len = 1024*1024*1024-1;
	//list表的每个数据的宽度valuePos==>keyLen==>valueLen==>nextNodePos==>标志位
	private int node_bytes_len = 1+8+8+4+8;
	public HashFile() throws IOException {
		System.out.println(Integer.toBinaryString(max_index_len));
		this.tabName = UUID.randomUUID().toString();
		this.indexFile = new File(ResourceManager.classPath(), this.tabName + ".index");
		this.valueFile = new File(ResourceManager.classPath(), this.tabName + ".value");
		this.nodeFile = new File(ResourceManager.classPath(), this.tabName + ".node");
		this.indexAccess = new RandomAccessFile(indexFile, "rw");
		indexAccess.setLength(max_index_len);
		this.valueAccess = new RandomAccessFile(valueFile, "rw");
		this.nodeAccess = new RandomAccessFile(nodeFile, "rw");
		System.out.println(ResourceManager.classPath());
		System.out.println(this.indexFile.length());
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			this.indexFile.delete();
			this.valueFile.delete();
			this.nodeFile.delete();
		}));
	}
	public HashNode iterator() {
		return null;
	}
	public void put(String key, String value) throws IOException {
//		System.out.println("---------------------");
//		long t  = System.currentTimeMillis();
//		System.out.println(key + "===》" + value);
//		//写入value
//		byte[] valueBytes = getValueBytes(value);
//		byte[] keyBytes = key.getBytes();
//		byte[] bodyBytes = new byte[valueBytes.length+keyBytes.length];
//		System.arraycopy(keyBytes, 0, bodyBytes, 0, keyBytes.length);
//		System.arraycopy(valueBytes, 0, bodyBytes, keyBytes.length, valueBytes.length);
//		int valueLen = valueBytes.length;
//		long posValue = this.valueFile.length();
//		this.valueAccess.seek(posValue);
//		this.valueAccess.write(valueBytes);
		//获取节点
		HashNode node = getNode(key);
		//节点不存在
		if(node == null) {
			
		}else {
			
		}
//		long posIndex = getPosIndex(hash);
//		System.out.println("pos:" + posIndex+"===>"+(posIndex>max_index_len));
//		
//		byte[] indexBytes = getIndexBytes(len, posValue);
//		this.indexAccess.seek(posIndex);
//		System.out.println(indexBytes.length);
//		this.indexAccess.write(indexBytes);
//		System.out.println("耗时:"+(System.currentTimeMillis()-t));
		
	
	}

	public HashNode getNode(String key) throws IOException {
		long hash = hash(key);
		//获取key的值
		return getNode(hash,key);
	}
	public HashNode getNode(long hashCode,String key) throws IOException {
		HashNode hashNode = null;
		long nodePos = getNodePos(hashCode);
		byte[] bytes = new byte[node_bytes_len];
		while(nodePos > 0) {
			nodePos = nodePos + 1;
			//拿到第一个数据的位置
			this.nodeAccess.seek(nodePos);
			this.nodeAccess.read(bytes);
			//节点标志
			int mark = bytes[0] & 0xff;
			//值的指针
			long valuePos = ByteUtils.bytesToLong(bytes,1);
			//值的总长度
			long valueLength = ByteUtils.bytesToLong(bytes,9);
			//值的key的长度
			int keyLength = ByteUtils.bytesToInt(bytes,17);
			//下一个hash值相同的指针
			HashNode node = new HashNode(this);
			node.setKeyLength(keyLength);
			node.setNodePos(nodePos);
			node.setMark(mark);
			node.setValueLength(valueLength);
			node.setValuePos(valuePos);
			nodePos = ByteUtils.bytesToLong(bytes,21) - 1;
			node.setNextPos(nodePos);
			if(key != null ) {
				String nodeKey = getNodeKey(node);
				if(StringUtils.equals(key, nodeKey))
					return node;
			} else {
				if(hashNode == null) {
					hashNode = node;
				}else {
					hashNode.getLast().setNext(node);
					node.setBefore(hashNode.getLast());
				}
			}
		}
		return hashNode;
	}

	private HashNode getNodeValue(HashNode current) {
		return null;
	}
	private String getNodeKey(HashNode current) {
		// TODO Auto-generated method stub
		return null;
	}
	private long getNodePos(long hashCode) throws IOException {
		long posIndex = getPosIndex(hashCode);
		byte[] bytes = new byte[index_bytes_len];
		this.indexAccess.seek(posIndex);
		this.indexAccess.read(bytes);
		return ByteUtils.bytesToLong(bytes, 0)-1;
	}

	private byte[] getIndexBytes(int len, long pos) {
		byte[] bytes = new byte[index_bytes_len];
		byte[] lens = ByteUtils.intToBytes(len);
		byte[] poss = ByteUtils.longToBytes(pos);
		System.arraycopy(lens, 0, bytes, 0, lens.length);
		System.arraycopy(poss, 0, bytes, lens.length, poss.length);
		return bytes;
	}

	private byte[] getValueBytes(String value) {
		return value.getBytes();
	}

	public String get(String key) throws IOException {
		System.out.println("===========get==" + key);
		byte[] bytes = new byte[index_bytes_len];
		long hash = hash(key);
		long posIndex = getPosIndex(hash);
		System.out.println(posIndex);
		this.indexAccess.seek(posIndex);
		this.indexAccess.read(bytes);
		int len = ByteUtils.bytesToInt(bytes, 0);
		long posValue = ByteUtils.bytesToLong(bytes, 4);
		System.out.println(ByteUtils.bytesToInt(bytes));
		System.out.println(ByteUtils.bytesToLong(bytes, 4));
		byte[] valueBytes = new byte[len];
		this.valueAccess.seek(posValue);
		this.valueAccess.read(valueBytes);
		return getValue(valueBytes);
	}

	private long getPosIndex(long hash) {
		return hash & (max_index_len/8);
	}

	private String getValue(byte[] valueBytes) {
		return new String(valueBytes);
	}

	public long hash(String key) {
		long h = 0;
		if (h == 0) {
			int off = 0;
			char val[] = key.toCharArray();
			long len = key.length();
			for (long i = 0; i < len; i++) 
				h = 31 * h + val[off++];
		}
		return h;
	}
}
