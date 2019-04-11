package ch.unibe.scg.minijava.typechecker.types;

public class Variable {
	
	private String identifier;
	private Type type;
	
	public Variable(String identifier, Type type) {
		this.identifier = identifier;
		this.type = type;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public Type getType() {
		return type;
	}
}
