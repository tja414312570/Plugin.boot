package com.yanan.framework.util;

import java.nio.ByteBuffer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.yanan.framework.ant.utils.SerialUtils;
import com.yanan.utils.HashFile;
import com.yanan.utils.HashNode;
import com.yanan.utils.IteratorHashNode;


public class AbstractHashMap<K,V> extends HashFile {
	private long size;
	private ByteBufferOutput byteBufferOutput;
	public AbstractHashMap() {
		this.inits();
	}
	public AbstractHashMap(String name) {
		super(name);
		this.inits();
	}
	public AbstractHashMap(String dir,String name) {
		super(dir,name);
		this.inits();
	}
	private void inits() {
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
		return  (T) kryo.readClassAndObject(input);
	}
	public void put(K key, V value) {
		super.put(serial(key),serial(value));
		size++;
	}
	public V get(String key) {
		byte[] values = super.get(serial(key));
		return values == null? null : deserial(values);
	}
	public long size() {
		return size;
	}
	
	public String toString() {
        HashNode i = entrySet();
        System.err.println(i.hasNext());
        if (i == null)
            return "{}";
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        while (i != null) {
            byte[] keyBytes = i.getKey();
            byte[] valueBytes = i.getValue();
            K key = deserial(keyBytes);
            V value = deserial(valueBytes);
            sb.append(key);
            sb.append('=');
            sb.append(value);
            if (! i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
            i = i.nextNode();
        }
        return sb.toString();
    }
}
