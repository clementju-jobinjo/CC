package ch.unibe.scg.minijava.typechecker.types;

public class Type {
	
	private String name;
	private Type parent;
	
	public Type(String name, Type parent) {
		this.name = name;
		this.parent = parent;
	}
	
	public String getTypeName() {
		return name;
	}
	
	public Type getParentType() {
		return parent;
	}
	
	public boolean isCompatibleWith(Type type) {
		
		return (type == this) ? true : this.parent.isCompatibleWith(type);
	}
}
