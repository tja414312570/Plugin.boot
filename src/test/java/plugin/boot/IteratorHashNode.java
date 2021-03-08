package plugin.boot;

import java.util.concurrent.atomic.AtomicBoolean;

public class IteratorHashNode extends HashNode{
	private BigMappedByteBuffer nodeByteBuffered;
	public IteratorHashNode(HashFile hashFile) {
		super(hashFile);
		this.nodeByteBuffered = hashFile.getNodeByteBuffer();
	}
	public boolean hasNext() {
		long pos = this.nodePos;
		AtomicBoolean loop = new AtomicBoolean(true);
		while(loop.get() && (pos+=HashFile.NODE_BYTES_LEN) <= this.nodeByteBuffered.position()) {
			HashFile.try_catch((poss)->{
				HashNode node = this.hashFile.readNode(poss, new IteratorHashNode(hashFile));
				if(node.getMark() > 0) {
					loop.set(false);
				}
			},pos);
		}
		return !loop.get();
	}
	public HashNode nextNode() {
		long pos = this.nodePos;
		this.next = null;
		AtomicBoolean loop = new AtomicBoolean(true);
		System.out.println(this.nodePos);
		while(loop.get() && (pos+=HashFile.NODE_BYTES_LEN) <= this.nodeByteBuffered.position()+1) {
			HashFile.try_catch((poss)->{
				HashNode node = this.hashFile.readNode(poss, new IteratorHashNode(hashFile));
				System.out.println(new String(this.getKey())+"::"+this.nodeByteBuffered.position()+"==>"+poss+":"+this.nodePos+"==>"+new String(node.getKey()));
				if(node.getMark() > 0) {
					this.next = node;
					loop.set(false);
				}
			},pos);
		}
		return this.next;
	}

}
