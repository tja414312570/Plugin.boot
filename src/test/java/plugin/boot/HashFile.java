package plugin.boot;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;

import org.apache.commons.codec.binary.StringUtils;

import com.yanan.utils.ByteUtils;

public class HashFile {
	private String tabName;
	private File indexFile;
	private File valueFile;
	private File nodeFile;
	private RandomAccessFile indexAccess;
	private RandomAccessFile valueAccess;
	private RandomAccessFile nodeAccess;
	//index表每个数据的宽度
	private int index_bytes_len = 16;
	//index表的总大小
	private int max_index_len = 1024*1024*1024-1;
	//list表的每个数据的宽度valuePos==>keyLen==>valueLen==>nextNodePos==>标志位
	private int node_bytes_len = 1+8+4+4+8;
	private String tempDir = "/Users/yanan/Public";//ResourceManager.classPath();//
	private long valuePos = 1;
	private long nodePos = 1;
	private MappedByteBuffer indexByteBuffer;
	private MappedByteBuffer nodeByteBuffer;
	private BigMappedByteBuffer valueByteBuffer;
	public HashFile() throws IOException {
		System.out.println(Integer.toBinaryString(max_index_len/index_bytes_len));
		this.tabName = UUID.randomUUID().toString();
		this.indexFile = new File(this.tempDir , this.tabName + ".index");
		this.valueFile = new File(tempDir, this.tabName + ".value");
		this.nodeFile = new File(tempDir, this.tabName + ".node");
		this.indexAccess = new RandomAccessFile(indexFile, "rw");
		indexAccess.setLength(max_index_len);
		this.valueAccess = new RandomAccessFile(valueFile, "rw");
		this.nodeAccess = new RandomAccessFile(nodeFile, "rw");
		
		this.indexByteBuffer = indexAccess.getChannel()
                .map(FileChannel.MapMode.READ_WRITE, 0, indexAccess.length());
//		this.valueByteBuffer = valueAccess.getChannel()
//                .map(FileChannel.MapMode.READ_WRITE, 0, indexAccess.length());
		this.valueByteBuffer = new BigMappedByteBuffer(valueAccess);
		this.nodeByteBuffer = nodeAccess.getChannel()
                .map(FileChannel.MapMode.READ_WRITE, 0, Integer.MAX_VALUE>>1);
		
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
		long hash = hash(key);
		//获取节点
		HashNode node = getNode(hash,null);
		HashNode lastNode = null;
		//节点不存在
		if(node != null) {
//			System.out.println(key+"-->"+node);
			lastNode = node.getLast();
			while(node.hasNext()) {
				if(StringUtils.equals(key, getNodeKey(node))) {
					System.out.println("找到node==>替换");
					throw new RuntimeException("提换");
//					break;
				}
				node = node.nextNode();
			}
//			System.out.println("hash冲突："+key+"-->"+getNodeKey(lastNode));
			byte[] valueBytes = getValueBytes(value);
			byte[] keyBytes = key.getBytes();
			byte[] bodyBytes = new byte[valueBytes.length+keyBytes.length];
			System.arraycopy(keyBytes, 0, bodyBytes, 0, keyBytes.length);
			System.arraycopy(valueBytes, 0, bodyBytes, keyBytes.length, valueBytes.length);
			int bodyLen = bodyBytes.length;
			long posValue = writeValue(bodyBytes);
			byte[] nodeBytes = new byte[node_bytes_len];
			byte[] valuePosBytes = ByteUtils.longToBytes(posValue);
			nodeBytes[0] = 1;
			System.arraycopy(valuePosBytes, 0, nodeBytes, 1, 8);
			byte[] valueLenBytes = ByteUtils.intToBytes(bodyLen);
			byte[] keyLenBytes = ByteUtils.intToBytes(keyBytes.length);
			System.arraycopy(valueLenBytes, 0, nodeBytes, 9, 4);
			System.arraycopy(keyLenBytes, 0, nodeBytes, 13, 4);
			System.arraycopy(new byte[] {0,0,0,0,0,0,0,0}, 0, nodeBytes, 17, 8);
			long nodePos = writeNode(nodeBytes);
			if(nodePos==lastNode.getNodePos()) {
				System.out.println(key+"====>"+getNodeKey(lastNode));
				throw new RuntimeException();
			}
			//重写上一节点
			getByteBuffer(nodeBytes,lastNode.getNodePos(),nodeByteBuffer);
			System.arraycopy(ByteUtils.longToBytes(nodePos), 0, nodeBytes, 17, 8);
			this.nodeByteBuffer.position((int) lastNode.getNodePos());
			this.nodeByteBuffer.put(nodeBytes);
			return ;
		}
		byte[] valueBytes = getValueBytes(value);
		byte[] keyBytes = key.getBytes();
		byte[] bodyBytes = new byte[valueBytes.length+keyBytes.length];
		System.arraycopy(keyBytes, 0, bodyBytes, 0, keyBytes.length);
		System.arraycopy(valueBytes, 0, bodyBytes, keyBytes.length, valueBytes.length);
		int bodyLen = bodyBytes.length;
		long posValue = writeValue(bodyBytes);
		
		byte[] nodeBytes = new byte[node_bytes_len];
		byte[] valuePosBytes = ByteUtils.longToBytes(posValue);
		nodeBytes[0] = 1;
		System.arraycopy(valuePosBytes, 0, nodeBytes, 1, 8);
		byte[] valueLenBytes = ByteUtils.intToBytes(bodyLen);
		byte[] keyLenBytes = ByteUtils.intToBytes(keyBytes.length);
		System.arraycopy(valueLenBytes, 0, nodeBytes, 9, 4);
		System.arraycopy(keyLenBytes, 0, nodeBytes, 13, 4);
		System.arraycopy(new byte[] {0,0,0,0,0,0,0,0}, 0, nodeBytes, 17, 8);
		
		long nodePos = writeNode(nodeBytes);
		
		long posIndex = getPosIndex(hash);
		byte[] indexBytes = getIndexBytes(nodeBytes.length, nodePos);
		indexByteBuffer.position((int) posIndex);
		indexByteBuffer.put(indexBytes);
	}
	private long writeNode(byte[] nodeBytes) throws IOException {
		long oldPos = this.nodePos;
		this.nodeByteBuffer.position((int) nodePos);
		nodeByteBuffer.put(nodeBytes);
		this.nodePos+=nodeBytes.length;
		return oldPos;
	}
	public long writeValue(byte[] bodyBytes) throws IOException {
		long oldPos = this.valuePos;
		valueByteBuffer.position((int) oldPos);
		valueByteBuffer.put(bodyBytes);
		this.valuePos+=bodyBytes.length;
		return oldPos;
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
		while(nodePos >= 1) {
			//拿到第一个数据的位置
			getByteBuffer(bytes,nodePos,nodeByteBuffer);
			//节点标志
			int mark = bytes[0] & 0xff;
			//值的指针
			long valuePos = ByteUtils.bytesToLong(bytes,1);
			//值的总长度
			int valueLength = ByteUtils.bytesToInt(bytes,9);
			//值的key的长度
			int keyLength = ByteUtils.bytesToInt(bytes,13);
			//下一个hash值相同的指针
			HashNode node = new HashNode(this);
			node.setKeyLength(keyLength);
			node.setNodePos(nodePos);
			node.setHashCode(hashCode);
			node.setMark(mark);
			node.setValueLength(valueLength);
			node.setValuePos(valuePos);
			nodePos = ByteUtils.bytesToLong(bytes,17);
			if(nodePos>this.nodeFile.length()-node_bytes_len) {
				nodePos = -1;
			}
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
	private byte[] getValue(long pos,int length) throws IOException {
		byte[] bytes = new byte[length];
		getByteBuffer(bytes,pos,valueByteBuffer);
		return bytes;
	}
	private byte[] getNodeValue(HashNode node) throws IOException {
		return getValue(node.getValuePos()+node.getKeyLength(),node.getValueLength()-node.getKeyLength());
	}
	private String getNodeKey(HashNode node) throws IOException {
		return new String(getValue(node.getValuePos(),node.getKeyLength()));
	}
	private long getNodePos(long hashCode) throws IOException {
		long posIndex = getPosIndex(hashCode);
		byte[] bytes = new byte[index_bytes_len];
		int i= 0;
		int len = (int) (posIndex+bytes.length);
		for(;posIndex < len;) {
			try {
				bytes[i++] = this.indexByteBuffer.get((int) posIndex++);
			}catch (Exception e) {
				System.err.println(posIndex+"===>"+hashCode+"===>"+getPosIndex(hashCode));
				throw new RuntimeException(e);
			}
		}
		return ByteUtils.bytesToLong(bytes, 0);
		
	}

	private long getByteBuffer(byte[] bytes,long pos,BigMappedByteBuffer byteBuffer) throws IOException {
		int i= 0;
		int len = (int) (pos+bytes.length);
		for(;pos < len;) {
			bytes[i++] = byteBuffer.get((int) pos++);
		}
		return ByteUtils.bytesToInt(bytes, 0);
		
	}
	private long getByteBuffer(byte[] bytes,long pos,MappedByteBuffer byteBuffer) throws IOException {
		int i= 0;
		int len = (int) (pos+bytes.length);
		for(;pos < len;) {
			bytes[i++] = byteBuffer.get((int) pos++);
		}
		return ByteUtils.bytesToInt(bytes, 0);
		
	}
	
	private byte[] getIndexBytes(int len, long pos) {
		byte[] bytes = new byte[index_bytes_len];
		byte[] lens = ByteUtils.intToBytes(len);
		byte[] poss = ByteUtils.longToBytes(pos);
		System.arraycopy(poss, 0, bytes, 0, poss.length);
		System.arraycopy(lens, 0, bytes, poss.length, lens.length);
		return bytes;
	}

	private byte[] getValueBytes(String value) {
		return value.getBytes();
	}

	public String get(String key) throws IOException {
		HashNode node = getNode(key);
		return node==null?null:getValue(getNodeValue(node));
	}

	private long getPosIndex(long hash) {
		long pos = ((hash & (max_index_len/index_bytes_len)))*index_bytes_len;
		return pos > max_index_len-index_bytes_len?0:pos;
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
