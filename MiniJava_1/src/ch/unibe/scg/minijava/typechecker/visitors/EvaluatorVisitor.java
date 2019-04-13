package ch.unibe.scg.minijava.typechecker.visitors;

import java.util.ArrayList;
import java.util.List;

import ch.unibe.scg.javacc.syntaxtree.BinaryOperator;
import ch.unibe.scg.javacc.syntaxtree.Expression;
import ch.unibe.scg.javacc.syntaxtree.FalseExpression;
import ch.unibe.scg.javacc.syntaxtree.INode;
import ch.unibe.scg.javacc.syntaxtree.Identifier;
import ch.unibe.scg.javacc.syntaxtree.IntegerLiteral;
import ch.unibe.scg.javacc.syntaxtree.MethodDeclaration;
import ch.unibe.scg.javacc.syntaxtree.TrueExpression;
import ch.unibe.scg.javacc.syntaxtree.UnaryOperator;
import ch.unibe.scg.javacc.visitor.DepthFirstVoidVisitor;
import ch.unibe.scg.minijava.typechecker.scopes.Scope;
import ch.unibe.scg.minijava.typechecker.types.Int;
import ch.unibe.scg.minijava.typechecker.types.Method;
import ch.unibe.scg.minijava.typechecker.types.Type;
import ch.unibe.scg.minijava.typechecker.types.Variable;
import ch.unibe.scg.minijava.typechecker.types.VoidType;
import ch.unibe.scg.minijava.typechecker.types.Boolean;

public class EvaluatorVisitor extends DepthFirstVoidVisitor {
	
	private List<Scope> scopes;
	private List<Type> expressionTypes;
	private Scope currentScope;
	
	public EvaluatorVisitor(List<Scope> scopes) {
		this.scopes = scopes;
		expressionTypes = new ArrayList<Type>();
		currentScope = scopes.get(0);
	}
	
	@Override
	public void visit(MethodDeclaration md) {
		Scope scope = getScope(md);
		Scope parentScope = scope.getScopeEnglobant();	
		Method method = parentScope.getMethod(md.identifier.nodeToken.tokenImage);
		
		currentScope = scope;
		
		Type returnType = method.getReturnType();
		
		if (returnType == VoidType.VoidSingleton) {
			throw new RuntimeException();
		}
		else {
			md.expression.accept(this);
		}
	}
	
	@Override
	public void visit(Expression exp) {
		ExpressionTypeConstructor visitor = new ExpressionTypeConstructor(currentScope);
		exp.accept(visitor);
		String infixExpression = visitor.getInfixExpression();
		System.out.println(infixExpression);
		PostfixExpressionConstructor pf = new PostfixExpressionConstructor();
		String postfixExpression = pf.postfix(infixExpression);
		System.out.println(postfixExpression);
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
	
	private void isCompatibleWithExpression(Type type) {
		for (Type t : expressionTypes) {
			if (!t.isCompatibleWith(type)) {
				throw new RuntimeException();
			};
		}
	}
	
	private Scope getScope(INode n) {
		for (Scope sc : scopes) {
			if (sc.getScopeRelatedTo() == n) {
				return sc;
			}
		}
		return null;
	}
}
