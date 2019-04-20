package ch.unibe.scg.minijava.typechecker.types;

public class Int extends Type {
	
	public static final Int IntSingleton = new Int();
	
	public Int() {
		super("int", null);
	}
	
	@Override
	public org.apache.bcel.generic.Type getBcelType() {
		return org.apache.bcel.generic.Type.INT;
	}
}
