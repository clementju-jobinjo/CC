package ch.unibe.scg.minijava.typechecker.types;

public class Type {
	
	private String name;
	private Type parent;
	
	public Type(String name, Type parent) {
		this.name = name;
		this.parent = parent;
	}
	
	public String getTypeName() {
		return (name == null) ? "null" : name;
	}
	
	public Type getParentType() {
		return parent;
	}
	
	public boolean isCompatibleWith(Type type) {
		
		if (type == this) {
			return true;
		}
		else {
			if (this.parent == null) {
				return false;
			}
			else {
				return this.parent.isCompatibleWith(type);
			}
		}
	}
	
	public org.apache.bcel.generic.Type getBcelType() {
		return new org.apache.bcel.generic.ObjectType(name);
	};
}
