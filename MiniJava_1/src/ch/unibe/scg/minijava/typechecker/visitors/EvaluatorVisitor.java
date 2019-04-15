package ch.unibe.scg.minijava.typechecker.visitors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.unibe.scg.javacc.syntaxtree.AssignmentStatementArrayLeft;
import ch.unibe.scg.javacc.syntaxtree.AssignmentStatementIdentifierLeft;
import ch.unibe.scg.javacc.syntaxtree.BinaryOperator;
import ch.unibe.scg.javacc.syntaxtree.Expression;
import ch.unibe.scg.javacc.syntaxtree.FalseExpression;
import ch.unibe.scg.javacc.syntaxtree.INode;
import ch.unibe.scg.javacc.syntaxtree.Identifier;
import ch.unibe.scg.javacc.syntaxtree.IfStatement;
import ch.unibe.scg.javacc.syntaxtree.IntegerLiteral;
import ch.unibe.scg.javacc.syntaxtree.MainClass;
import ch.unibe.scg.javacc.syntaxtree.MethodDeclaration;
import ch.unibe.scg.javacc.syntaxtree.NodeListOptional;
import ch.unibe.scg.javacc.syntaxtree.NodeOptional;
import ch.unibe.scg.javacc.syntaxtree.TrueExpression;
import ch.unibe.scg.javacc.syntaxtree.UnaryOperator;
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
//		// Identifier()
//		Identifier identifier = mainClass.identifier;
//		identifier.accept(this);
		

		// (Statement())?
		NodeOptional nodeOptional = mainClass.nodeOptional;
			if (nodeOptional.present()) {
				INode node = nodeOptional.node;
				node.accept(this);
		}
	}
	
	@Override
	public void visit(MethodDeclaration md) {
		System.out.println("1");
		Scope scope = getScope(md);
		Method method = scope.getMethod(md.identifier.nodeToken.tokenImage);
		
//		for (Scope sc : scopes) {
//			if (sc.getScopeEnglobant() == scope) {
//				currentScope = sc;
//				break;
//			}
//		}
		currentScope = scope;
		System.out.println("CURRENT: " + scope);
		Type returnType = method.getReturnType();
		
		if (returnType == VoidType.VoidSingleton) {
			throw new RuntimeException();
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

		if (typeOfLastVisitedExpression != returnType) {
			throw new RuntimeException();
		}
	}
	
	@Override
	public void visit(IfStatement st) {
		System.out.println("2");
		st.expression.accept(this);
		if (typeOfLastVisitedExpression != Boolean.BooleanSingleton) {
			throw new RuntimeException();
		}
		st.statement.accept(this);
		st.statement1.accept(this);
	}
	
	@Override
	public void visit(WhileStatement st) {
		System.out.println("3");
		st.expression.accept(this);
		if (typeOfLastVisitedExpression != Boolean.BooleanSingleton) {
			throw new RuntimeException();
		}
		st.statement.accept(this);
	}
	
//	@Override
//	public void visit(AssignmentStatement st) {
//		System.out.println("4");
//		System.out.println("IOJFDIlfs: " + typeOfLastVisitedExpression);
//		// visit part before '='
//		st.assigned.accept(this);
//
//		
//		
//		// visit part after '='
//		st.expression.accept(this);
//
//		if (typeOfLastVisitedIdentifier != typeOfLastVisitedExpression) {
//			throw new RuntimeException();
//		}
//
//	}
	
	// Identifier() "=" Expression() ";"
	@Override
	public void visit(AssignmentStatementIdentifierLeft e){
		System.out.println("8908");
		e.identifier.accept(this);
		Type leftType = typeOfLastVisitedIdentifier;
		
		e.expression.accept(this);
		Type rightType = typeOfLastVisitedExpression;
		
		if (!leftType.getTypeName().equals(rightType.getTypeName())) {
			throw new RuntimeException();
		}
	}

	// Identifier() ArrayAccess() "=" Expression() ";"
	@Override
	public void visit(AssignmentStatementArrayLeft e){
		System.out.println("0007");
		e.identifier.accept(this);
		Type typeOfId = typeOfLastVisitedIdentifier;
		
		System.out.println("ID type: " + typeOfId.getTypeName());
		
		e.arrayAccess.accept(this);
		Type intraBracketType = typeOfLastVisitedExpression;
		
		System.out.println("intra type: " + intraBracketType.getTypeName());
		
		if (intraBracketType != Int.IntSingleton) {
			throw new RuntimeException();
		}
		
		e.expression.accept(this);
		
		Type rightType = typeOfLastVisitedExpression;
		System.out.println("Right type: " + rightType.getTypeName());
		
		if (rightType != Int.IntSingleton) {
			throw new RuntimeException();
		}
	}
	
	@Override
	public void visit(Identifier id) {
		System.out.println("5");
		System.out.println(id.nodeToken.tokenImage);
		
		Type identifierType = null;
		
		for (Scope sc : scopes) {
			Variable t = sc.getVariableNonRecursive(id.nodeToken.tokenImage);

			if (t != null) {
				identifierType = t.getType();
				break;
			}
		}
		
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
			throw new RuntimeException();
		}
		
		typeOfLastVisitedIdentifier = identifierType;
		System.out.println("YOLO");
	}
	
	@Override
	public void visit(Expression exp) {
		System.out.println("6");
		ExpressionTypeConstructor visitor = new ExpressionTypeConstructor(currentScope, scopes, classOrMethodOrVariableToScope);
		exp.accept(visitor);
		String infixExpression = visitor.getInfixExpression();
		System.out.println("Infix: " + infixExpression);
		PostfixExpressionConstructor pf = new PostfixExpressionConstructor();
		String postfixExpression = pf.postfix(infixExpression);
		System.out.println("Postfix: " + postfixExpression);
		String expTypeStr = pf.evaluatePostfix(postfixExpression);
		System.out.println("Exp Type: " + expTypeStr);
		Type expType = null;
		
		for (Scope sc : scopes) {
			Type t = sc.getTypeFromString(expTypeStr);
			if (t != null) {
				expType = t;
				break;
			}
		}
		
		if (expType == null) {
			throw new RuntimeException();
		}
		
		typeOfLastVisitedExpression = expType;
		
	}
	
//	@Override
//	public void visit(IntegerLiteral il) {
//		isCompatibleWithExpression(Int.IntSingleton);
//		expressionTypes.add(Int.IntSingleton);
//	}
//	
//	@Override
//	public void visit(TrueExpression tr) {
//		isCompatibleWithExpression(Boolean.BooleanSingleton);
//		expressionTypes.add(Boolean.BooleanSingleton);
//	}
//	
//	@Override
//	public void visit(FalseExpression il) {
//		isCompatibleWithExpression(Boolean.BooleanSingleton);
//		expressionTypes.add(Boolean.BooleanSingleton);
//	}
//	
//	@Override
//	public void visit(Identifier id) {
//		Variable var = currentScope.getVariable(id.nodeToken.tokenImage);
//		Type t = var.getType();
//		isCompatibleWithExpression(t);
//		expressionTypes.add(t);
//	}
//	
//	@Override
//	public void visit(BinaryOperator op) {
//		String operator = op.nodeToken.tokenImage;
//		
//		if (operator.equals("&&")) {
//			expressionTypes.add(Boolean.BooleanSingleton);
//		}
//		else {
//			expressionTypes.add(Int.IntSingleton);
//		}
//	}
//	
//	@Override
//	public void visit(UnaryOperator op) {
//		expressionTypes.add(Boolean.BooleanSingleton);
//	}
	
//	private void isCompatibleWithExpression(Type type) {
//		for (Type t : expressionTypes) {
//			if (!t.isCompatibleWith(type)) {
//				throw new RuntimeException();
//			};
//		}
//	}
	
	public Type getTypeOfLastExpression() {
		return typeOfLastVisitedExpression;
	}
	
	private Scope getScope(INode n) {
		for (Scope sc : scopes) {
			if (sc.getNodeRelatedTo() == n) {
				return sc;
			}
		}
		return null;
	}
}
