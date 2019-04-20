package ch.unibe.scg.minijava.typechecker.types;

public class Boolean extends Type {
	
	public static final Boolean BooleanSingleton = new Boolean();
	
	public Boolean() {
		super("boolean", null);
	}
	
	@Override
	public org.apache.bcel.generic.Type getBcelType() {
		return org.apache.bcel.generic.Type.BOOLEAN;
	}

}
