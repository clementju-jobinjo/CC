package ch.unibe.scg.minijava.typechecker.types;

public class RootObject extends Type {
	
	public static final RootObject RootObjectSingleton = new RootObject();
	
	public RootObject() {
		super("Object", null);
	}
}
