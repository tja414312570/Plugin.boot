package plugin.boot;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.yanan.framework.ant.utils.SerialUtils;

public class StringHashMap extends HashFile{
	private long size;
	private ByteBufferOutput byteBufferOutput;
	public StringHashMap() {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		byteBufferOutput = new ByteBufferOutput(buffer,2048000);
	}
	public byte[] serial(Object object) {
		try {
			byteBufferOutput.clear();
			Kryo kryo = SerialUtils.getKryo();
			kryo.writeClassAndObject(byteBufferOutput, object);
			byteBufferOutput.flush();
		} catch (KryoException e) {
			throw new RuntimeException("Message body serialization exception", e);
		} 
		return byteBufferOutput.toBytes();
	}
	@SuppressWarnings("unchecked")
	public <T> T deserial(byte[] bytes){
		Kryo kryo = SerialUtils.getKryo();
		ByteBufferInput input = new ByteBufferInput(bytes);
		input.setPosition(0);
		input.setLimit(bytes.length);
		return (T) kryo.readClassAndObject(input);
	}
	public void put(Object key, Object value) {
		super.put(serial(key),serial(value));
		size++;
	}
	public <T> T get(String key) {
		byte[] values = super.get(serial(key));
		return values == null? null : deserial(values);
	}
	public long size() {
		return size;
	}
}
