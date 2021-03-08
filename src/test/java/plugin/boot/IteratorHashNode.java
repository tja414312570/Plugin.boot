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
					this.next = node;
					loop.set(false);
				}
			},pos);
		}
		return this.next != null;
	}

}
