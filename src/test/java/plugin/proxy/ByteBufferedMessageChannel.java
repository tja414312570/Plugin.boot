package plugin.proxy;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.reflect.TypeToken;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.yanan.framework.a.core.AbstractMessage;
import com.yanan.framework.a.core.BufferReady;
import com.yanan.framework.a.core.ByteBufferChannel;
import com.yanan.framework.ant.AntContext;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.decoder.StandScanResource;
import com.yanan.utils.resource.ResourceManager;

public class ByteBufferedMessageChannel<T> {
	private Type type;

	public ByteBufferedMessageChannel(){
		
	}
	public static class Types<T> extends ByteBufferedMessageChannel{
		Types(ByteBufferedMessageChannel obj){
			obj.type = (Type) new TypeToken<T> (getClass()){
			}.getType();
		}
	}
	public Type getType(){
		new Types<T>(this);
		System.out.println(type);
		return type;
	}
	
	public static void main(String[] args) {
		new ByteBufferedMessageChannel<AbstractMessage>().getType();
		
		if(true)
			return;
		PlugsFactory.init(ResourceManager.getResource("classpath:plugin.yc"),
				new StandScanResource(ResourceManager.getClassPath(AntContext.class)[0]+"**")
//				new StandScanResource(ResourceManager.getClassPath(RPCTest.class)[0]+"**")
				);
		ByteOutputStream bos = new ByteOutputStream();
		AtomicInteger counts = new AtomicInteger();
		ByteBufferChannel<AbstractMessage> byteBufferChannel = PlugsFactory.getPluginsInstance(ByteBufferChannel.class);
		
		BufferReady<AbstractMessage> messageWriteHandler = new BufferReady<AbstractMessage> (){
			@Override
			public void write(ByteBuffer buffer){
				buffer.flip();
				System.err.println(buffer.position());
				System.err.println(buffer.remaining());
				counts.set(buffer.remaining());
				while(buffer.hasRemaining()) 
					bos.write(buffer.get());
				buffer.flip();
				System.out.println("读入后:"+bos.getBytes().length+"==="+new String(bos.getBytes()));
			}
			@Override
			public void handleRead(ByteBuffer buffer) {
				System.out.println("读取:"+counts.get()+":"+bos.getBytes());
				int i = 0;
				while(i<counts.get()) {
					buffer.put(bos.getBytes()[i++]);
				}
				System.err.println("===>"+buffer.remaining());
			}
			public void onMessage(AbstractMessage message) {
				System.out.println("得到新消息"+message);
			}
		};
		byteBufferChannel.setBufferReady(messageWriteHandler);
		System.out.println(byteBufferChannel);
//		bos.write("hello world，im put".getBytes());
//		byteBufferChannel.handleRead();
		AbstractMessage message = new AbstractMessage();
		message.setMessage("hello world");
		byteBufferChannel.write(message);
		byteBufferChannel.handleRead();
	}
}
