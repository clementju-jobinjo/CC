package ch.unibe.scg.minijava.typechecker.types;

public class Int extends Type {
	
	public static final Int IntSingleton = new Int();
	
	public Int() {
		super("int", null);
	}
}
