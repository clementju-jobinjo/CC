package ch.unibe.scg.minijava.typechecker.types;

public class Boolean extends Type {
	
	public static final Boolean BooleanSingleton = new Boolean();
	
	public Boolean() {
		super("boolean", null);
	}

}
