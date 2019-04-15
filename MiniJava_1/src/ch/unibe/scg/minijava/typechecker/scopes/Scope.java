package ch.unibe.scg.minijava.typechecker.scopes;

import java.util.ArrayList;
import java.util.List;

import ch.unibe.scg.minijava.typechecker.types.Method;
import ch.unibe.scg.minijava.typechecker.types.Variable;
import ch.unibe.scg.javacc.syntaxtree.INode;
import ch.unibe.scg.minijava.typechecker.types.*;

/**
 * 
 * Represents a global scope, method internal scope or class internal scope
 *
 */
public class Scope {
	
	private Scope scopeEnglobant;
	private List<Type> classes;
	private List<Method> methods;
	private List<Variable> variables;
	private INode nodeRelatedTo;
	
	
	public Scope(Scope scopeEnglobant, INode nodeRelatedTo) {
		this.scopeEnglobant = scopeEnglobant;
		classes = new ArrayList<Type>();
		methods = new ArrayList<Method>();
		variables =  new ArrayList<Variable>();
		this.nodeRelatedTo = nodeRelatedTo;
	}
	
	
	// get methods
	public Scope getScopeEnglobant() {
		return scopeEnglobant;
	}
	
	
	public List<Type> getClasses() {
		return classes;
	}
	
	
	public List<Method> getMethods() {
		return methods;
	}
	
	
	public Method getMethod(String methodName) {
		for (Method m : methods) {
			if (m.getIdentifier().equals(methodName)) {
				return m;
			}
		}
		if (scopeEnglobant == null) {
			throw new RuntimeException("Method does not exist in the current scope or in any parent/child scope");
		}
		else {
			return scopeEnglobant.getMethod(methodName);
		}
	}
	
	
	public Method getMethodNonRecursive(String methodName) {
		for (Method m : methods) {
			if (m.getIdentifier().equals(methodName)) {
				return m;
			}
		}
		return null;
	}
	
	
	public List<Variable> getVariables() {
		return variables;
	}
	
	
	public Variable getVariable(String varName) {
		if(variables != null) {
			for (Variable var : variables){
				if (var.getIdentifier().equals(varName)) {
					return var;
				}
			}	
		}
		if (scopeEnglobant != null) {
			return this.scopeEnglobant.getVariable(varName);
		}
		else {
			throw new RuntimeException("Variable does not exist in that scope or in any parent/child scope");
		}
	}
	
	
	public Variable getVariableNonRecursive(String varName) {
		for (Variable var : variables) {
			if (var.getIdentifier().equals(varName)) {
				return var;
			}
		}
		return null;
	}
	
	
	public INode getNodeRelatedTo() {
		return nodeRelatedTo;
	}
	
	
	public Type getTypeFromString(String str) {
		for (Type t : classes) {
			if (str.equals(t.getTypeName())) {
				return t;
			}
		}
		
		return null;
	}
	
	
	// set and add methods
	public void setScopeEnglobant(Scope sc) {
		this.scopeEnglobant = sc;
	}
	
	
	public void addClass(Type cl) {
		for (Type cla : classes) {
			if (cla.getTypeName().equals(cl.getTypeName())) {
				throw new RuntimeException("The scope already contains a class called " + cl.getTypeName());
			}
		}
		
		classes.add(cl);
	}
	
	
	public void addMethod(Method met) {
		
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
				throw new RuntimeException("Cannot add two methods with the same name and parameters to the same scope. Method: " + metIdentifier);
			}
			
		}
		methods.add(met);
		
		
		
	}
	
	public void addVariable(Variable var) {
		int index = -1;

		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getIdentifier().equals(var.getIdentifier())) {
				index = i;
				break;
			}
		}
		
		if (index == -1) {
			variables.add(var);
		}
		else {
			variables.remove(index);
			variables.add(var);
		}
		
	}
	
	// redefinition of toString
	@Override
	public String toString() {
		List<Variable> vars = this.getVariables();
		List<Method> mets = this.getMethods();
		List<Type> clas = this.getClasses();
		
		String str = "";
		
		str += "#########\nClasses\n";
		
		for (Type cla : clas) {
			str += "> " + cla.getTypeName() + "\n"; 
		}
		
		str += "\nMethods\n"; 
		for (Method met : mets) {
			str += "> " + met.getIdentifier() + "\n";
			
			for (Variable arg : met.getArguments()) {
				str += ">> " + arg.getType().getTypeName() + " " + arg.getIdentifier() + "\n";
			}
		}
		
		str += "\nVariables\n"; 
		for (Variable var : vars) {
			str += "> " + var.getType().getTypeName() + " " + var.getIdentifier() + "\n";
		}
		
		return str;
	}
	
}
