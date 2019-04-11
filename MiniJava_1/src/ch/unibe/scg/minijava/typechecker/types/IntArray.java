package ch.unibe.scg.minijava.typechecker.types;

public class IntArray extends Type {
	
	public Type array;
	
	public IntArray(Type array) {
		super("int[]", null);
		this.array = array;
	}
}
