package plugin.boot;

public class HashNode {
	private HashFile hashFile;
	private HashNode next;
	private HashNode before;
	private long mark;
	// 值的指针
	private long valuePos;
	// 值的总长度
	private int valueLength;
	// 值的key的长度
	private int keyLength;
	// 下一个hash值相同的指针
	private long nodePos;
	// 下一个节点指针
	private long nextPos;
	
	private long hashCode;

	public long getMark() {
		return mark;
	}

	public void setMark(long mark) {
		this.mark = mark;
	}

	public long getValuePos() {
		return valuePos;
	}

	public void setValuePos(long valuePos) {
		this.valuePos = valuePos;
	}

	public int getValueLength() {
		return valueLength;
	}

	public void setValueLength(int valueLength) {
		this.valueLength = valueLength;
	}

	public int getKeyLength() {
		return keyLength;
	}

	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}

	public long getNodePos() {
		return nodePos;
	}

	public void setNodePos(long nodePos) {
		this.nodePos = nodePos;
	}

	public HashNode(HashFile hashFile) {
		super();
		this.hashFile = hashFile;
	}

	public boolean hasNext() {
		return this.next != null;
	}

	public HashNode nextNode() {
		return next;
	}

	public HashNode getNext() {
		return next;
	}

	public void setNext(HashNode next) {
		this.next = next;
	}

	public HashNode getLast() {
		return this.hasNext() ? this.nextNode().getLast() : this;
	}

	@Override
	public String toString() {
		return "HashNode [hashFile=" + hashFile + ", next=" + next + ", before=" + before + ", mark=" + mark
				+ ", valuePos=" + valuePos + ", valueLength=" + valueLength + ", keyLength=" + keyLength + ", nodePos="
				+ nodePos + ", nextPos=" + nextPos + ", hashCode=" + hashCode + "]";
	}

	public HashNode getBefore() {
		return before;
	}

	public void setBefore(HashNode before) {
		this.before = before;
	}

	public HashFile getHashFile() {
		return hashFile;
	}

	public long getNextPos() {
		return nextPos;
	}

	public void setNextPos(long nextPos) {
		this.nextPos = nextPos;
	}

	public long getHashCode() {
		return hashCode;
	}

	public void setHashCode(long hashCode) {
		this.hashCode = hashCode;
	}

}
