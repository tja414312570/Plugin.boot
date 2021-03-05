package plugin.boot;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.UUID;

import com.yanan.utils.ByteUtils;

/**
 * 基于文件系统的hash map，用于存储文件数据
 * 支持大型数据
 * @author yanan
 *
 */
public class HashFile {
	private File indexFile;
	private File valueFile;
	private File nodeFile;
	private RandomAccessFile indexAccess;
	private RandomAccessFile valueAccess;
	private RandomAccessFile nodeAccess;
	//index表每个数据的宽度
	private static final int BYTES_SIZE_POS = 8;
	private static final int BYTES_SIZE_LEN = 4;
	
	private final static int BYTE_KEY_INDEX = 0;
	private final static int BYTE_KEY_NODE = 1;
	private static final int BYTE_KEY_NODE_VAL_LEN = 2;
	private static final int BYTE_KEY_NODE_KEY_LEN = 3;
	private static final byte[] NULL_POS_BYTES = {0,0,0,0,0,0,0,0};
	//index表的总大小
	private static final int MAX_INDEX_LEN = 1024*1024*1024-1;
	//list表的每个数据的宽度标志位==>valuePos==>keyLen==>valueLen==>nextNodePos
	private static final int NODE_BYTES_LEN = 1+8+4+4+8;
	private static final int INDEX_BYTES_LEN = 16;
	private static final byte[] NULL_POINTER_BYTES = {0,
													0,0,0,0,0,0,0,0,
													0,0,0,0,
													0,0,0,0,
													0,0,0,0,0,0,0,0};
	private static String tempDir = "/Users/yanan/Public";//ResourceManager.classPath();//
	private long valuePos = 1;
	private long nodePos = 1;
	private MappedByteBuffer indexByteBuffer;
	private BigMappedByteBuffer nodeByteBuffer;
	private BigMappedByteBuffer valueByteBuffer;
//	private static ThreadLocal<byte[][]> bytePools = new ThreadLocal<>();
//	public byte[] getThreadBytes(int hash,int width) {
//		hash= hash<<8 | width;
//		byte[][] bytes = bytePools.get();
//		if(bytes==null) {
//			bytes = new byte[1 << 16-1][];
//		}
//		byte[] bytes2 = bytes[hash];
//		if(bytes2==null) {
//			bytes2 = new byte[width];
//			bytes[hash] = bytes2;
//		}
//		return bytes2;
//	}
//	private static ThreadLocal<Map<Integer,byte[]>> bytePools = new ThreadLocal<>();
//	public byte[] getThreadBytes(int hash,int width) {
//		hash= hash<<16 | width;
//		Map<Integer,byte[]> byteMap = bytePools.get();
//		if(byteMap == null) {
//			byteMap = new HashMap<>();
//			bytePools.set(byteMap);
//		}
//		byte[] bytes = byteMap.get(hash);
//		if(bytes==null) {
//			bytes = new byte[width];
//			byteMap.put(hash, bytes);
//		}
//		return bytes;
//	}
	private final static ThreadLocal<byte[][]> bytePools = new ThreadLocal<byte[][]>() {
		protected byte[][] initialValue() {
			byte[][] init = new byte[128][];
			for(int i = 0;i<4;i++) {
				init[(4<<i)  ] = new byte[BYTES_SIZE_LEN];
				init[(4<<i) +1 ] = new byte[BYTES_SIZE_POS];
				init[(4<<i) +2 ] = new byte[INDEX_BYTES_LEN];
				init[(4<<i) +3 ] = new byte[NODE_BYTES_LEN];
			}
			return init;
		};
	};
	private final byte[] getThreadBytes(int hash,int width) {
		int pos = 4 << hash;
		byte[][] init = bytePools.get();
		switch (width) {
		case BYTES_SIZE_LEN:
			return init[pos];
		case BYTES_SIZE_POS:
			return init[pos+1];
		case INDEX_BYTES_LEN:
			return init[pos+2];
		case NODE_BYTES_LEN:
			return init[pos+3];
		default:
			break;
		}
		return null;
	}
	public HashFile() throws IOException {
		this(UUID.randomUUID().toString());
	}
	public HashFile(String dir,String name) throws IOException {
		this.indexFile = new File(dir , name + ".index");
		this.valueFile = new File(dir, name + ".value");
		this.nodeFile = new File(dir, name + ".node");
		init();
	}
	public HashFile(String name) throws IOException {
		this(tempDir,name);
	}
	public void init() throws IOException {
		this.indexAccess = new RandomAccessFile(indexFile, "rw");
		indexAccess.setLength(MAX_INDEX_LEN);
		this.valueAccess = new RandomAccessFile(valueFile, "rw");
		this.nodeAccess = new RandomAccessFile(nodeFile, "rw");
		
		this.indexByteBuffer = indexAccess.getChannel()
                .map(FileChannel.MapMode.READ_WRITE, 0, indexAccess.length());
		this.valueByteBuffer = new BigMappedByteBuffer(valueAccess);
		this.nodeByteBuffer = new BigMappedByteBuffer(nodeAccess);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			this.indexFile.delete();
			this.valueFile.delete();
			this.nodeFile.delete();
		}));
	}
	public HashNode iterator() {
		return null;
	}
	public void put(String key, byte[] valueBytes) throws IOException {
		long hash = hash(key);
		//获取节点
		HashNode node = getNode(hash,null);
		HashNode lastNode = null;
		byte[] keyBytes = key.getBytes();
		//节点不存在
		if(node != null) {
//			System.out.println(key+"-->"+node);
			lastNode = node.getLast();
			while(node != null) {
				if(Arrays.equals(keyBytes, getNodeKeyBytes(node))) {
					byte[] bodyBytes = new byte[valueBytes.length+keyBytes.length];
					System.arraycopy(keyBytes, 0, bodyBytes, 0, keyBytes.length);
					System.arraycopy(valueBytes, 0, bodyBytes, keyBytes.length, valueBytes.length);
					int bodyLen = bodyBytes.length;
					long posValue = writeValue(bodyBytes);
					byte[] keyLenBytes = getThreadBytes(BYTE_KEY_NODE_KEY_LEN,BYTES_SIZE_LEN);
					byte[] valueLenBytes = getThreadBytes(BYTE_KEY_NODE_VAL_LEN,BYTES_SIZE_LEN);
					byte[] nodeBytes = getThreadBytes(BYTE_KEY_NODE,NODE_BYTES_LEN);
					byte[] valuePosBytes = getThreadBytes(BYTE_KEY_NODE,BYTES_SIZE_POS);
					ByteUtils.longToBytes(posValue,valuePosBytes);
					ByteUtils.intToBytes(bodyLen,valueLenBytes);
					ByteUtils.intToBytes(keyBytes.length,keyLenBytes);
					getByteBuffer(nodeBytes,node.getNodePos(),nodeByteBuffer);
					System.arraycopy(valuePosBytes, 0, nodeBytes, 1, 8);
					System.arraycopy(valueLenBytes, 0, nodeBytes, 9, 4);
					System.arraycopy(keyLenBytes, 0, nodeBytes, 13, 4);
					this.nodeByteBuffer.position((int) node.getNodePos());
					this.nodeByteBuffer.put(nodeBytes);
					return ;
//					break;
				}
				node = node.nextNode();
			}
			byte[] bodyBytes = new byte[valueBytes.length+keyBytes.length];
			System.arraycopy(keyBytes, 0, bodyBytes, 0, keyBytes.length);
			System.arraycopy(valueBytes, 0, bodyBytes, keyBytes.length, valueBytes.length);
			int bodyLen = bodyBytes.length;
			long posValue = writeValue(bodyBytes);
			
			byte[] nodeBytes = getThreadBytes(BYTE_KEY_NODE,NODE_BYTES_LEN);
			byte[] valuePosBytes = getThreadBytes(BYTE_KEY_NODE,BYTES_SIZE_POS);
			ByteUtils.longToBytes(posValue,valuePosBytes);
			nodeBytes[0] = 1;
			System.arraycopy(valuePosBytes, 0, nodeBytes, 1, 8);
			byte[] valueLenBytes = getThreadBytes(BYTE_KEY_NODE_VAL_LEN,BYTES_SIZE_LEN);
			ByteUtils.intToBytes(bodyLen,valueLenBytes);
			byte[] keyLenBytes = getThreadBytes(BYTE_KEY_NODE_KEY_LEN,BYTES_SIZE_LEN);
			ByteUtils.intToBytes(keyBytes.length,keyLenBytes);
			System.arraycopy(valueLenBytes, 0, nodeBytes, 9, 4);
			System.arraycopy(keyLenBytes, 0, nodeBytes, 13, 4);
			System.arraycopy(NULL_POS_BYTES, 0, nodeBytes, 17, 8);
			long nodePos = writeNode(nodeBytes);
			if(nodePos==lastNode.getNodePos()) {
				System.out.println(key+"====>"+getNodeKey(lastNode));
				throw new RuntimeException();
			}
			
			getByteBuffer(nodeBytes,lastNode.getNodePos(),nodeByteBuffer);
			//重写上一节点
			ByteUtils.longToBytes(nodePos,valuePosBytes);
			System.arraycopy(valuePosBytes, 0, nodeBytes, 17, 8);
			this.nodeByteBuffer.position((int) lastNode.getNodePos());
			this.nodeByteBuffer.put(nodeBytes);
			return ;
		}
		byte[] bodyBytes = new byte[valueBytes.length+keyBytes.length];
		System.arraycopy(keyBytes, 0, bodyBytes, 0, keyBytes.length);
		System.arraycopy(valueBytes, 0, bodyBytes, keyBytes.length, valueBytes.length);
		int bodyLen = bodyBytes.length;
		long posValue = writeValue(bodyBytes);
		byte[] nodeBytes = getThreadBytes(BYTE_KEY_NODE,NODE_BYTES_LEN);
		byte[] valuePosBytes = getThreadBytes(BYTE_KEY_NODE,BYTES_SIZE_POS);
		ByteUtils.longToBytes(posValue,valuePosBytes);
		nodeBytes[0] = 1;
		System.arraycopy(valuePosBytes, 0, nodeBytes, 1, 8);
		byte[] valueLenBytes = getThreadBytes(BYTE_KEY_NODE_VAL_LEN,BYTES_SIZE_LEN);
		ByteUtils.intToBytes(bodyLen,valueLenBytes);
		byte[] keyLenBytes = getThreadBytes(BYTE_KEY_NODE_KEY_LEN,BYTES_SIZE_LEN);
		ByteUtils.intToBytes(keyBytes.length,keyLenBytes);
		System.arraycopy(valueLenBytes, 0, nodeBytes, 9, 4);
		System.arraycopy(keyLenBytes, 0, nodeBytes, 13, 4);
		System.arraycopy(NULL_POS_BYTES, 0, nodeBytes, 17, 8);
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
		return getNode(hash,key.getBytes());
	}
	public HashNode getNode(long hashCode,byte[] key) throws IOException {
		HashNode hashNode = null;
		long nodePos = getNodePos(hashCode);
//		byte[] bytes = new byte[INDEX_BYTES_LEN];
		byte[] bytes = getThreadBytes(BYTE_KEY_INDEX, NODE_BYTES_LEN);
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
			if(nodePos>this.nodePos-NODE_BYTES_LEN) {
				nodePos = -1;
			}
			node.setNextPos(nodePos);
			if(key != null ) {
				byte[] nodeKey = getNodeKeyBytes(node);
				if(Arrays.equals(key, nodeKey))
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
	private byte[] getNodeKeyBytes(HashNode node) throws IOException {
		return getValue(node.getValuePos(),node.getKeyLength());
	}
	private String getNodeKey(HashNode node) throws IOException {
		return new String(getValue(node.getValuePos(),node.getKeyLength()));
	}
	private long getNodePos(long hashCode) throws IOException {
		long posIndex = getPosIndex(hashCode);
		byte[] bytes = getThreadBytes(BYTE_KEY_INDEX, INDEX_BYTES_LEN);
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

	private byte[] getByteBuffer(byte[] bytes,long pos,BigMappedByteBuffer byteBuffer) throws IOException {
		int i= 0;
		int len = (int) (pos+bytes.length);
		for(;pos < len;) {
			bytes[i++] = byteBuffer.get((int) pos++);
		}
		return bytes;
		
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
		byte[] bytes = getThreadBytes(BYTE_KEY_INDEX, INDEX_BYTES_LEN);
		byte[] lens = getThreadBytes(BYTE_KEY_INDEX, BYTES_SIZE_LEN);
		ByteUtils.intToBytes(len,lens);
		byte[] poss = getThreadBytes(BYTE_KEY_INDEX, BYTES_SIZE_POS);
		ByteUtils.longToBytes(pos,poss);
		System.arraycopy(poss, 0, bytes, 0, poss.length);
		System.arraycopy(lens, 0, bytes, poss.length, lens.length);
		return bytes;
	}

	public byte[] get(String key) throws IOException {
		HashNode node = getNode(key);
		return node==null?null:getNodeValue(node);
	}
	public byte[] remove(String key) throws IOException {
		long hash = hash(key);
		byte[] keyBytes = key.getBytes();
		HashNode node = getNode(hash,null);
		HashNode before = null;
		HashNode current = null;
		while(node != null) {
			byte[] nodeKey = getNodeKeyBytes(node);
			if(Arrays.equals(keyBytes, nodeKey)) {
				current = node;
				break;
			}
			before = node;
			node = node.nextNode();
		}
		
		if(before != null) {
			byte[] nodeBytes = getThreadBytes(BYTE_KEY_NODE,NODE_BYTES_LEN);
			if(!current.hasNext()) {
				//删掉上个节点的下一个节点指针
				getByteBuffer(nodeBytes,before.getNodePos(),nodeByteBuffer);
				System.arraycopy(NULL_POS_BYTES, 0, nodeBytes, 17, 8);
			}else {
				//将上一节点的下一指针指向当前节点的下一个指针
				byte[] valueLenBytes = getThreadBytes(BYTE_KEY_NODE_VAL_LEN,BYTES_SIZE_LEN);
				ByteUtils.longToBytes(current.nextNode().getNodePos(),valueLenBytes);
				System.arraycopy(valueLenBytes, 0, nodeBytes, 17, 8);
			}
			this.nodeByteBuffer.position((int) before.getNodePos());
			this.nodeByteBuffer.put(nodeBytes);
		}
		this.nodeByteBuffer.position(current.getNodePos());
		this.nodeByteBuffer.put(NULL_POINTER_BYTES);
		return node==null?null:getNodeValue(node);
	}
	public void removeAll() throws IOException {
		this.indexFile.delete();
		this.nodeFile.delete();
		this.valueFile.delete();
		init();
	}
	private long getPosIndex(long hash) {
		long pos = ((hash & (MAX_INDEX_LEN/INDEX_BYTES_LEN)))*INDEX_BYTES_LEN;
		return pos > MAX_INDEX_LEN-INDEX_BYTES_LEN?0:pos;
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
