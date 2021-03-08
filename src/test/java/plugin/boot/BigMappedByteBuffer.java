package plugin.boot;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class BigMappedByteBuffer{
	private RandomAccessFile randomAccessFile;
	private long pos = 1;
	private static long MAX_FRAGMENT_LEN = Integer.MAX_VALUE>>1;
	private MappedByteBuffer[] mapperedByteBuffers;
	public BigMappedByteBuffer(RandomAccessFile randomAccessFile) throws IOException {
		this.randomAccessFile = randomAccessFile;
		this.init();
	}
	private void init() throws IOException {
		this.pos = 1;
		long len = randomAccessFile.length();
		extract(len);
	}
	private void extract(long len) throws IOException {
		int fragments = (int)((len / MAX_FRAGMENT_LEN )+ 1);
		int index = 0;
		if(mapperedByteBuffers == null) {
			mapperedByteBuffers = new MappedByteBuffer[fragments];
			index = 0;
		}else if(fragments > mapperedByteBuffers.length){
			MappedByteBuffer[] old = mapperedByteBuffers;
			mapperedByteBuffers = new MappedByteBuffer[fragments];
			for(int i = 0;i<old.length;i++) {
				mapperedByteBuffers[i] = old[i];
			}
			index = fragments - old.length;
		}else {
			index = fragments;
		}
		for(int i = index;i<fragments;i++) {
			long lpos = i*MAX_FRAGMENT_LEN;
			mapperedByteBuffers[i] = this.randomAccessFile.getChannel()
	                .map(FileChannel.MapMode.READ_WRITE,lpos,MAX_FRAGMENT_LEN);
			mapperedByteBuffers[i].limit((int) MAX_FRAGMENT_LEN);
		}
	}
//	public static void main(String[] args) throws IOException {
//		File file = new File("/Users/yanan/Public","test.text");
//		RandomAccessFile ras = new RandomAccessFile(file, "rw");
//		BigMappedByteBuffer mmb = new BigMappedByteBuffer(ras);
//		mmb.position(10737400000l);
//		for(long l = 0;l<Long.MAX_VALUE;l++) {
//			byte[] bytes = ("test:"+l+";").getBytes();
//			mmb.put(bytes);
//			System.out.println(mmb.pos+"["+MAX_FRAGMENT_LEN+"("+(mmb.pos>MAX_FRAGMENT_LEN)+")]"+"--->"+ (mmb.pos / MAX_FRAGMENT_LEN)+"==>"+(mmb.pos % MAX_FRAGMENT_LEN));
////			if((mmb.pos / MAX_FRAGMENT_LEN)>0) {
////				return;
////			}
//		}
//		System.out.println("结束");
//	}
	public static String valueMulti(int i) {
		StringBuffer sb = new StringBuffer();
		if(i >1024) 
			i=1024;
		while(i-->0) {
			sb.append(i);
		}
		return sb.toString();
	}
//	@Override
//	public ByteBuffer slice() {
//		return null;
//	}
//	@Override
//	public ByteBuffer duplicate() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public ByteBuffer asReadOnlyBuffer() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	public byte get() {
		return get(pos++);
	}
	public ByteBuffer put(byte b) throws IOException {
		return put(pos++,b);
	}
	public void put(byte[] bs) throws IOException {
		for(byte b : bs)
			put(b);
	}
	public byte get(long index) {
		return mapperedByteBuffers[(int) (index / MAX_FRAGMENT_LEN) ].get((int) (index % MAX_FRAGMENT_LEN));
	}
	public ByteBuffer put(long index, byte b) throws IOException {
//		System.out.println("put；"+index+"-->"+b);
//		System.out.println((int) (index % MAX_FRAGMENT_LEN));
//		System.out.println((index % MAX_FRAGMENT_LEN));
		extract(index);
//		System.out.println(mapperedByteBuffers.length);
//		System.out.println( (index / MAX_FRAGMENT_LEN) );
		return mapperedByteBuffers[(int) (index / MAX_FRAGMENT_LEN) ].put((int) (index % MAX_FRAGMENT_LEN),b);
	}
	public long position(long newPosition) {
		long old = pos;
		this.pos = newPosition;
		return old;
	}
//	@Override
//	public ByteBuffer compact() {
//		return null;
//	}
	public boolean isDirect() {
		return mapperedByteBuffers[0].isDirect();
	}
//	public char getChar() {
//		return getChar(pos);
//	}
//	public char getChar(long index) {
//		return mapperedByteBuffers[(int) (index % MAX_FRAGMENT_LEN) ].getChar((int) (index % MAX_FRAGMENT_LEN));
//	}
//	@Override
//	public ByteBuffer putChar(char value) {
//		return null;
//	}
//	
//	@Override
//	public ByteBuffer putChar(int index, char value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public CharBuffer asCharBuffer() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public short getShort() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	@Override
//	public ByteBuffer putShort(short value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public short getShort(int index) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	@Override
//	public ByteBuffer putShort(int index, short value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public ShortBuffer asShortBuffer() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public int getInt() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	@Override
//	public ByteBuffer putInt(int value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public int getInt(int index) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	@Override
//	public ByteBuffer putInt(int index, int value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public IntBuffer asIntBuffer() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public long getLong() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	@Override
//	public ByteBuffer putLong(long value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public long getLong(int index) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	@Override
//	public ByteBuffer putLong(int index, long value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public LongBuffer asLongBuffer() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public float getFloat() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	@Override
//	public ByteBuffer putFloat(float value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public float getFloat(int index) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	@Override
//	public ByteBuffer putFloat(int index, float value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public FloatBuffer asFloatBuffer() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public double getDouble() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	@Override
//	public ByteBuffer putDouble(double value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public double getDouble(int index) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	@Override
//	public ByteBuffer putDouble(int index, double value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public DoubleBuffer asDoubleBuffer() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public boolean isReadOnly() {
//		// TODO Auto-generated method stub
//		return false;
//	}
	public long position() {
		return pos;
	}
}
