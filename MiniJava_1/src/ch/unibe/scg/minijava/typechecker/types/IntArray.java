package ch.unibe.scg.minijava.typechecker.types;

public class IntArray extends Type {
	
	public static final IntArray IntArraySingleton = new IntArray();
	
	public IntArray() {
		super("int[]", null);
	}
}
