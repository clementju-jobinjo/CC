package ch.unibe.scg.minijava.typechecker.types;

public class VoidType extends Type{
	
	public static final VoidType VoidSingleton = new VoidType();
	
	public VoidType() {
		super("void", null);
	}
}
