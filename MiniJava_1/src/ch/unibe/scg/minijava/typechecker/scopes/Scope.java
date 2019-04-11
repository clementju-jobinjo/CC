package ch.unibe.scg.minijava.typechecker.scopes;

import java.util.ArrayList;
import java.util.List;
import ch.unibe.scg.minijava.typechecker.types.*;

public class Scope {
	
	Scope scopeEnglobant;
	List<Type> classes;
	List<Method> methods;
	List<Variable> variables;
	
	public Scope(Scope scopeEnglobant) {
		this.scopeEnglobant = scopeEnglobant;
		classes = new ArrayList<Type>();
		methods = new ArrayList<Method>();
		variables =  new ArrayList<Variable>();
	}
	
	public Scope getScopeEnglobant() {
		return scopeEnglobant;
	}
	
	public List<Type> getClasses() {
		return classes;
	}
	
	public List<Method> getMethods() {
		return methods;
	}
	
	public List<Variable> getVariables() {
		return variables;
	}
	
	public void setScopeEnglobant(Scope sc) {
		this.scopeEnglobant = sc;
	}
	
	public void setClasses(Type cl) throws Exception {
		for (Type cla : classes) {
			if (cla.getTypeName().equals(cl.getTypeName())) {
				throw new Exception();
			}
		}
		
		classes.add(cl);
	}
	
	public void setMethods(Method met) throws Exception {
		
		String metIdentifier = met.getIdentifier();
		List<Variable> metArguments = met.getArguments();
		
		for (Method m : methods) {
			String mIdentifier = m.getIdentifier();
			List<Variable> mArguments = m.getArguments();
			boolean sameArguments = true;
			
			
			// check if the list of arguments is exactly the same
			for (int i = 0; i < metArguments.size(); i++) {
				if (! metArguments.get(i).getIdentifier().equals(mArguments.get(i).getIdentifier())) {
					sameArguments = false;
					break;
				}
			}
			
			// exact same definition same parameters, different return types
			if (sameArguments && metIdentifier.equals(mIdentifier)) {
				throw new Exception();
			}
		
			methods.add(met);
			
		}
		
		
		
	}
	
	public void setVariables(Variable var) throws Exception {
		for (Variable variable : variables) {
			if (variable.getIdentifier().equals(var.getIdentifier())) {
				throw new Exception();
			}
		}
		
		variables.add(var);
	}
	
}
