package ch.unibe.scg.minijava.typechecker.types;

import java.util.List;

public class Method {
	
	private String identifier;
	private Type returnType;
	private List<Variable> arguments;
	
	public Method(String identifier, Type returnType, List<Variable> arguments) {
		this.identifier = identifier;
		this.returnType = returnType;
		this.arguments = arguments;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public Type getReturnType() {
		return returnType;
	}
	
	public List<Variable> getArguments(){
		return arguments;
	}
}
