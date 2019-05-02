package ch.unibe.scg.minijava.typechecker.types;

public class Variable {
	
	private String identifier;
	private Type type;
	private String value;
	
	public Variable(String identifier, Type type) {
		this.identifier = identifier;
		this.type = type;
		this.value = null;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String val) {
		value = val;
	}
}
