package ch.unibe.scg.minijava.typechecker.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.unibe.scg.javacc.syntaxtree.ArrayAccess;
import ch.unibe.scg.javacc.syntaxtree.ArrayCall;
import ch.unibe.scg.javacc.syntaxtree.BinaryOperator;
import ch.unibe.scg.javacc.syntaxtree.BlockStatement;
import ch.unibe.scg.javacc.syntaxtree.BooleanType;
import ch.unibe.scg.javacc.syntaxtree.ClassDeclaration;
import ch.unibe.scg.javacc.syntaxtree.DotArrayLength;
import ch.unibe.scg.javacc.syntaxtree.DotFunctionCall;
import ch.unibe.scg.javacc.syntaxtree.Goal;
import ch.unibe.scg.javacc.syntaxtree.INode;
import ch.unibe.scg.javacc.syntaxtree.Identifier;
import ch.unibe.scg.javacc.syntaxtree.IfStatement;
import ch.unibe.scg.javacc.syntaxtree.IntArrayConstructionCall;
import ch.unibe.scg.javacc.syntaxtree.IntArrayDeclaration;
import ch.unibe.scg.javacc.syntaxtree.IntegerLiteral;
import ch.unibe.scg.javacc.syntaxtree.MainClass;
import ch.unibe.scg.javacc.syntaxtree.MethodDeclaration;
import ch.unibe.scg.javacc.syntaxtree.NewExpression;
import ch.unibe.scg.javacc.syntaxtree.NodeListOptional;
import ch.unibe.scg.javacc.syntaxtree.NodeOptional;
import ch.unibe.scg.javacc.syntaxtree.NodeSequence;
import ch.unibe.scg.javacc.syntaxtree.NodeToken;
import ch.unibe.scg.javacc.syntaxtree.ObjectConstructionCall;
import ch.unibe.scg.javacc.syntaxtree.ParenthesisExpression;
import ch.unibe.scg.javacc.syntaxtree.PrintStatement;
import ch.unibe.scg.javacc.syntaxtree.UnaryExpression;
import ch.unibe.scg.javacc.syntaxtree.UnaryOperator;
import ch.unibe.scg.javacc.syntaxtree.VarDeclaration;
import ch.unibe.scg.javacc.syntaxtree.WhileStatement;
import ch.unibe.scg.javacc.visitor.DepthFirstVoidVisitor;
import ch.unibe.scg.minijava.typechecker.scopes.Scope;
import ch.unibe.scg.minijava.typechecker.types.Int;
import ch.unibe.scg.minijava.typechecker.types.IntArray;
import ch.unibe.scg.minijava.typechecker.types.Method;
import ch.unibe.scg.minijava.typechecker.types.Type;
import ch.unibe.scg.minijava.typechecker.types.Variable;
import ch.unibe.scg.minijava.typechecker.types.VoidType;
import ch.unibe.scg.minijava.typechecker.types.Boolean;

public class AllScopesBuilder extends DepthFirstVoidVisitor {

	private List<Scope> scopes;
	private Map<String, Type> stringToType;
	private Map<String, Scope> classOrMethodOrVariableToScope;
	private Type currentType;

	public AllScopesBuilder(Scope globalScope, Map<String, Type> stringToType) {
		super();
		scopes = new ArrayList<Scope>();
		scopes.add(globalScope);
		this.stringToType = stringToType;
		classOrMethodOrVariableToScope = new HashMap<String, Scope>();
	}
	
	public List<Scope> getAllScopes(){
		return scopes;
	}

	// Goal
	// MainClass() ( ClassDeclaration() )* < EOF >
	@Override
	public void visit(Goal goal) {

		// MainClass
		MainClass mainClass = goal.mainClass;
		mainClass.accept(this);

		// ClassDeclaration
		NodeListOptional nodeList = goal.nodeListOptional;
		if (nodeList.present()) {
			for (int i = 0; i < nodeList.size(); i++) {
				INode node = nodeList.elementAt(i);
				node.accept(this);
			}
		}
	}


	// Mainclass
	//"class" Identifier() "{" "public" "static" "void" "main" "(" "String" "[" "]" Identifier() ")" "{" ( Statement() )? "}" "}"
	@Override
	public void visit(MainClass mainClass) {
		
		Scope currentScope = new Scope(scopes.get(0), mainClass);
		scopes.add(currentScope);
		
		// Identifier()
		String className = mainClass.identifier.nodeToken.tokenImage;
		classOrMethodOrVariableToScope.put(className, currentScope);
		scopes.get(0).addClass(stringToType.get(className));
	}


	// ClassDeclaration
	// "class" Identifier() ( "extends" Identifier() )? "{" ( VarDeclaration() )* ( MethodDeclaration() )* "}"
	@Override
	public void visit(ClassDeclaration classDeclaration) {
		
		// create scope for the current class
		Scope currentScope = new Scope(scopes.get(0), classDeclaration);
		scopes.add(currentScope);
		
		
		// link class name to scope
		// Identifier()
		String className = classDeclaration.identifier.nodeToken.tokenImage;
		classOrMethodOrVariableToScope.put(className, currentScope);
		
		// add class to (parent) scope
		// Identifier -> class name
		scopes.get(0).addClass(stringToType.get(className));

		

		//( VarDeclaration() )*
		NodeListOptional nodeListOptional = classDeclaration.nodeListOptional;
		if (nodeListOptional.present()) {

			for (int i = 0; i < nodeListOptional.size(); i++) {
				VarDeclaration node = (VarDeclaration)nodeListOptional.elementAt(i);
				
				String varName = node.identifier.nodeToken.tokenImage;
				
				node.type.accept(this);
				
				currentScope.addVariable(new Variable(varName, currentType));
				//classOrMethodOrVariableToScope.put(varName, currentScope);			
				
			}


		}

		// ( MethodDeclaration() )*
		NodeListOptional nodeListOptional1 = classDeclaration.nodeListOptional1;
		if (nodeListOptional1.present()) {

			for (int i = 0; i < nodeListOptional1.size(); i++) {
				MethodDeclaration node = (MethodDeclaration)nodeListOptional1.elementAt(i);
				
				// name
				String methodName = node.identifier.nodeToken.tokenImage;
				
				// return type
				node.type.accept(this);
				Type returnType = currentType;
				
				// arguments
				List<Variable> args = new ArrayList<Variable>();
				
				NodeOptional nodeOptional = node.nodeOptional;
				if (nodeOptional.present()) {
					NodeSequence nodeSequence = (NodeSequence) nodeOptional.node;
					
					// First argument
					// "Type" first arg
					INode nodeType = nodeSequence.elementAt(0);
					nodeType.accept(this);
					
					// Identifier first arg
					Identifier nodeIdentifier = (Identifier)nodeSequence.elementAt(1);
					
					args.add(new Variable(nodeIdentifier.nodeToken.tokenImage, currentType));
					
					
					
					//  ("," Type() Identifier() )*
					NodeListOptional nodeListOptional2 = (NodeListOptional) nodeSequence.elementAt(2);
					if (nodeListOptional2.present()) {
						for (int j = 0; j < nodeListOptional2.size(); j++) {
							INode node2 = nodeListOptional2.elementAt(j);
							NodeSequence nodeSequence2 = (NodeSequence) node2;
							
							// Type arg i+1
							INode type2 = nodeSequence2.elementAt(1);
							type2.accept(this);

							// Identifier arg i+1
							Identifier identifier2 = (Identifier)nodeSequence2.elementAt(2);
							
							args.add(new Variable(identifier2.nodeToken.tokenImage, currentType));

						}
					}
				}
				
				currentScope.addMethod(new Method(methodName, returnType, args));
				classOrMethodOrVariableToScope.put(methodName, currentScope);
				
				node.accept(this);
			}

		}
	}


	// VarDeclaration -> in global scope (not in a function or class)
	// Type() Identifier() ";"
	@Override
	public void visit(VarDeclaration varDeclaration) {
		varDeclaration.type.accept(this);
		
		scopes.get(0).addVariable(new Variable(varDeclaration.identifier.nodeToken.tokenImage, currentType));
	}


	// MethodDeclaration
	// "public" Type() Identifier() "(" (Type() Identifier() ("," Type() Identifier() )* )? ")"
	// "{" ( LOOKAHEAD(2) VarDeclaration() )* (Statement() )* "return" Expression() ";" "}"
	@Override
	public void visit(MethodDeclaration methodDeclaration) {
		
		
		// get return type
		methodDeclaration.type.accept(this);
		Type returnType = currentType;


		// Identifier()
		String methodName = methodDeclaration.identifier.nodeToken.tokenImage;
		
		// find scope of method
		Scope parentScope = classOrMethodOrVariableToScope.get(methodName);
		
		if (parentScope == null) {
			
			// arguments
			List<Variable> args = new ArrayList<Variable>();
			
			NodeOptional nodeOptional = methodDeclaration.nodeOptional;
			if (nodeOptional.present()) {
				NodeSequence nodeSequence = (NodeSequence) nodeOptional.node;
				
				// First argument
				// "Type" first arg
				INode nodeType = nodeSequence.elementAt(0);
				nodeType.accept(this);
				
				// Identifier first arg
				Identifier nodeIdentifier = (Identifier)nodeSequence.elementAt(1);
				
				args.add(new Variable(nodeIdentifier.nodeToken.tokenImage, currentType));
				
				
				
				//  ("," Type() Identifier() )*
				NodeListOptional nodeListOptional2 = (NodeListOptional) nodeSequence.elementAt(2);
				if (nodeListOptional2.present()) {
					for (int j = 0; j < nodeListOptional2.size(); j++) {
						INode node2 = nodeListOptional2.elementAt(j);
						NodeSequence nodeSequence2 = (NodeSequence) node2;
						
						// Type arg i+1
						INode type2 = nodeSequence2.elementAt(1);
						type2.accept(this);

						// Identifier arg i+1
						Identifier identifier2 = (Identifier)nodeSequence2.elementAt(2);
						
						args.add(new Variable(identifier2.nodeToken.tokenImage, currentType));

					}
				}
			}
			
			// add method to global scope
			scopes.get(0).addMethod(new Method(methodName, returnType, args));
			classOrMethodOrVariableToScope.put(methodName, scopes.get(0));
			
		} 

		
		// create internal method scope
		Scope currentScope = new Scope(classOrMethodOrVariableToScope.get(methodName), methodDeclaration);
		scopes.add(currentScope);
		
		List<Variable> arguments = classOrMethodOrVariableToScope.get(methodName).getMethod(methodName).getArguments();
		
		
		for (Variable arg : arguments) {
			currentScope.addVariable(arg);
		}

		
		// ( LOOKAHEAD(2) VarDeclaration() )*
		NodeListOptional nodeListOptional = methodDeclaration.nodeListOptional;
		if (nodeListOptional.present()) {
			for (int i = 0; i < nodeListOptional.size(); i++) {
				VarDeclaration node = (VarDeclaration)nodeListOptional.elementAt(i);
				
				String varName = node.identifier.nodeToken.tokenImage;
				
				node.type.accept(this);
				
				currentScope.addVariable(new Variable(varName, currentType));
			}
		}
	}

	
	// Type
	public void visit(ch.unibe.scg.javacc.syntaxtree.Type type) {
		
		switch (type.nodeChoice.which) {
			case 0:
				currentType = IntArray.IntArraySingleton;
				break;
			case 1:
				currentType = Int.IntSingleton;
				break;
			case 2:
				currentType = Boolean.BooleanSingleton;
				break;
			case 3: 
				currentType = VoidType.VoidSingleton;
				break;
			case 4:
				Identifier id = (Identifier) type.nodeChoice.choice;
				currentType = stringToType.get(id.nodeToken.tokenImage);
				
				// if type was not declared
				if (currentType == null) {
					throw new RuntimeException();
				}
				break;
			default:
				throw new RuntimeException();
		}
	}

}
