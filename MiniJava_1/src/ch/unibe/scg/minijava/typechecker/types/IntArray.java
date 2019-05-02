package ch.unibe.scg.minijava.typechecker.types;

public class IntArray extends Type {
	
	public static final IntArray IntArraySingleton = new IntArray();
	private int length;
	
	public IntArray() {
		super("int[]", null);
	}
	
	@Override
	public org.apache.bcel.generic.Type getBcelType() {
		return new org.apache.bcel.generic.ArrayType(org.apache.bcel.generic.Type.INT, 1);
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}
}
