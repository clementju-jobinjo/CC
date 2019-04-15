package ch.unibe.scg.minijava.typechecker.visitors;

import java.util.List;
import java.util.Map;

import ch.unibe.scg.javacc.syntaxtree.AssignmentStatementArrayLeft;
import ch.unibe.scg.javacc.syntaxtree.AssignmentStatementIdentifierLeft;
import ch.unibe.scg.javacc.syntaxtree.Expression;
import ch.unibe.scg.javacc.syntaxtree.INode;
import ch.unibe.scg.javacc.syntaxtree.Identifier;
import ch.unibe.scg.javacc.syntaxtree.IfStatement;
import ch.unibe.scg.javacc.syntaxtree.MainClass;
import ch.unibe.scg.javacc.syntaxtree.MethodDeclaration;
import ch.unibe.scg.javacc.syntaxtree.NodeListOptional;
import ch.unibe.scg.javacc.syntaxtree.NodeOptional;
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

public class EvaluatorVisitor extends DepthFirstVoidVisitor {
	
	private List<Scope> scopes;
	private Scope currentScope;
	private Type typeOfLastVisitedExpression;
	private Type typeOfLastVisitedIdentifier;
	private Map<String, Scope> classOrMethodOrVariableToScope;
	
	
	public EvaluatorVisitor(List<Scope> scopes, Map<String, Scope> classOrMethodOrVariableToScope) {
		this.scopes = scopes;
		currentScope = scopes.get(0);
		this.classOrMethodOrVariableToScope = classOrMethodOrVariableToScope;
	}
	

	// Mainclass
	//"class" Identifier() "{" "public" "static" "void" "main" "(" "String" "[" "]" Identifier() ")" "{" ( Statement() )? "}" "}"
	@Override
	public void visit(MainClass mainClass) {
		
		// (Statement())?
		NodeOptional nodeOptional = mainClass.nodeOptional;
			if (nodeOptional.present()) {
				INode node = nodeOptional.node;
				node.accept(this);
		}
	}
	
	
	@Override
	public void visit(MethodDeclaration md) {
		
		// get parent scope of the method (the one containing its definition)
		Scope scope = classOrMethodOrVariableToScope.get(md.identifier.nodeToken.tokenImage);
		
		// get method object
		Method method = scope.getMethod(md.identifier.nodeToken.tokenImage);
		
		currentScope = scope;
		
		// check return type -> no void functions
		Type returnType = method.getReturnType();
		
		if (returnType == VoidType.VoidSingleton) {
			throw new RuntimeException("Void functions not allowed.");
		}
		
		// (Statement() )*
		NodeListOptional nodeListOptional2 = md.nodeListOptional1;
		if (nodeListOptional2.present()) {
			for (int i = 0; i < nodeListOptional2.size(); i++) {
				INode node = nodeListOptional2.elementAt(i);
				node.accept(this);
			}
		}
		
		// return Expression()
		md.expression.accept(this);

		if (typeOfLastVisitedExpression.getTypeName() != returnType.getTypeName()) {
			throw new RuntimeException("Return type of the function different from the type of the returned expression.");
		}
	}
	
	
	@Override
	public void visit(IfStatement st) {
		st.expression.accept(this);
		if (typeOfLastVisitedExpression != Boolean.BooleanSingleton) {
			throw new RuntimeException("Condition of an if statement has to be boolean.");
		}
		st.statement.accept(this);
		st.statement1.accept(this);
	}
	
	
	@Override
	public void visit(WhileStatement st) {
		st.expression.accept(this);
		if (typeOfLastVisitedExpression != Boolean.BooleanSingleton) {
			throw new RuntimeException("Condition of a while statement has to be boolean.");
		}
		st.statement.accept(this);
	}
	
	
	// Identifier() "=" Expression() ";"
	@Override
	public void visit(AssignmentStatementIdentifierLeft e){
		e.identifier.accept(this);
		Type leftType = typeOfLastVisitedIdentifier;
		
		e.expression.accept(this);
		Type rightType = typeOfLastVisitedExpression;
		
		if (!leftType.getTypeName().equals(rightType.getTypeName())) {
			throw new RuntimeException("Assigment error: types do not match.");
		}
	}
	

	// Identifier() ArrayAccess() "=" Expression() ";"
	@Override
	public void visit(AssignmentStatementArrayLeft e){
		// Identifier()
		e.identifier.accept(this);
		Type typeOfId = typeOfLastVisitedIdentifier;
		
		if (typeOfId != IntArray.IntArraySingleton) {
			throw new RuntimeException("Identifier is not an int array.");
		}
		
		// ArrayAccess()
		e.arrayAccess.accept(this);
		Type intraBracketType = typeOfLastVisitedExpression;
		
		
		if (intraBracketType != Int.IntSingleton) {
			throw new RuntimeException("Accessing an array requires a integer as index.");
		}
		
		// Expression
		e.expression.accept(this);
		
		Type rightType = typeOfLastVisitedExpression;
		
		if (rightType != Int.IntSingleton) {
			throw new RuntimeException("Can only assign an integer value to an int array field.");
		}
	}
	
	
	@Override
	public void visit(Identifier id) {
		
		Type identifierType = null;
		
		// get identifier type -> case of a variable
		for (Scope sc : scopes) {
			Variable t = sc.getVariableNonRecursive(id.nodeToken.tokenImage);

			if (t != null) {
				identifierType = t.getType();
				break;
			}
		}
		
		// get identifier type -> case of a class name
		if (identifierType == null) {
			for (Scope sc : scopes) {
				List<Type> types = sc.getClasses();
				for (Type t : types) {
					if (t.getTypeName().equals(id.nodeToken.tokenImage)) {
						identifierType = t;
						break;
					}
				}
			}
		}
		
		if (identifierType == null) {
			throw new RuntimeException("Type does not exist.");
		}
		
		typeOfLastVisitedIdentifier = identifierType;
	}
	
	
	@Override
	public void visit(Expression exp) {
		ExpressionTypeConstructor visitor = new ExpressionTypeConstructor(currentScope, scopes, classOrMethodOrVariableToScope);
		exp.accept(visitor);
		
		String infixExpression = visitor.getInfixExpression();

		PostfixExpressionConstructor pf = new PostfixExpressionConstructor();
		String postfixExpression = pf.postfix(infixExpression);
		
		String expTypeStr = pf.evaluatePostfix(postfixExpression);
		Type expType = null;
		
		for (Scope sc : scopes) {
			Type t = sc.getTypeFromString(expTypeStr);
			if (t != null) {
				expType = t;
				break;
			}
		}
		
		if (expType == null) {
			throw new RuntimeException("Type does not exist.");
		}
		
		typeOfLastVisitedExpression = expType;
		
	}
	
	
	public Type getTypeOfLastExpression() {
		return typeOfLastVisitedExpression;
	}
	
}
