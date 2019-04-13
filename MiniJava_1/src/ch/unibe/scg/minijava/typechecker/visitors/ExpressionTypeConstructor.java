package ch.unibe.scg.minijava.typechecker.visitors;

import ch.unibe.scg.javacc.syntaxtree.ArrayCall;
import ch.unibe.scg.javacc.syntaxtree.BinaryOperator;
import ch.unibe.scg.javacc.syntaxtree.DotArrayLength;
import ch.unibe.scg.javacc.syntaxtree.DotFunctionCall;
import ch.unibe.scg.javacc.syntaxtree.FalseExpression;
import ch.unibe.scg.javacc.syntaxtree.Identifier;
import ch.unibe.scg.javacc.syntaxtree.IntArrayConstructionCall;
import ch.unibe.scg.javacc.syntaxtree.IntegerLiteral;
import ch.unibe.scg.javacc.syntaxtree.ParenthesisExpression;
import ch.unibe.scg.javacc.syntaxtree.ThisExpression;
import ch.unibe.scg.javacc.syntaxtree.TrueExpression;
import ch.unibe.scg.javacc.syntaxtree.UnaryExpression;
import ch.unibe.scg.javacc.syntaxtree.UnaryOperator;
import ch.unibe.scg.javacc.visitor.DepthFirstVoidVisitor;
import ch.unibe.scg.minijava.typechecker.types.Int;
import ch.unibe.scg.minijava.typechecker.types.IntArray;
import ch.unibe.scg.minijava.typechecker.types.Type;
import ch.unibe.scg.minijava.typechecker.types.Variable;
import ch.unibe.scg.minijava.typechecker.scopes.Scope;
import ch.unibe.scg.minijava.typechecker.types.Boolean;

public class ExpressionTypeConstructor extends DepthFirstVoidVisitor {
	
	private StringBuilder infixExpression;
	private Scope currentScope;
	
	public ExpressionTypeConstructor(Scope currentScope) {
		infixExpression = new StringBuilder();
		this.currentScope = currentScope;
	}
	
	public String getInfixExpression() {
		return infixExpression.toString();
	}
	
	@Override
	public void visit(Identifier id) {
		Variable var = currentScope.getVariable(id.nodeToken.tokenImage);
		Type t = var.getType();
		infixExpression.append(t.getTypeName());
		infixExpression.append(" ");
	}
	
	@Override
	public void visit(UnaryOperator e) {
		infixExpression.append("! ");
	}
	
	@Override
	public void visit(ParenthesisExpression e) {
		infixExpression.append("( ");
		e.expression.accept(this);
		infixExpression.append(") ");
	}
	
	@Override
	public void visit(IntegerLiteral e) {
		infixExpression.append(Int.IntSingleton.getTypeName());
		infixExpression.append(" ");
	}
	
	@Override
	public void visit(ThisExpression e) {
		
	}
	
	@Override
	public void visit(TrueExpression e) {
		infixExpression.append(Boolean.BooleanSingleton.getTypeName());
		infixExpression.append(" ");
	}
	
	@Override
	public void visit(FalseExpression e) {
		infixExpression.append(Boolean.BooleanSingleton.getTypeName());
		infixExpression.append(" ");
	}
	
	@Override
	public void visit(BinaryOperator e) {
		infixExpression.append(e.nodeToken.tokenImage);
		infixExpression.append(" ");
	}
	
	@Override
	public void visit(IntArrayConstructionCall e) {
		ExpressionTypeConstructor vis = new ExpressionTypeConstructor(currentScope);
		e.expression.accept(vis);
		String intraBracketType = vis.getInfixExpression();
		
		if (intraBracketType.equals(Int.IntSingleton.getTypeName() + " ")) {
			infixExpression.append(IntArray.IntArraySingleton.getTypeName());
			infixExpression.append(" ");
		}
		else {
			throw new RuntimeException();
		}
		
	}
	
	@Override
	public void visit(ArrayCall e) {
		String pred = infixExpression.substring(infixExpression.length() - 6);
		if (pred.equals(IntArray.IntArraySingleton.getTypeName() + " ")) {
			infixExpression = infixExpression.delete(infixExpression.length() - 6, infixExpression.length());
			infixExpression.append(Int.IntSingleton.getTypeName());
			infixExpression.append(" ");
		}
		else {
			throw new RuntimeException();
		}
	}
	
	@Override
	public void visit(DotArrayLength e) {
		String pred = infixExpression.substring(infixExpression.length() - 6);
		if (pred.equals(IntArray.IntArraySingleton.getTypeName() + " ")) {
			infixExpression = infixExpression.delete(infixExpression.length() - 6, infixExpression.length());
			infixExpression.append(Int.IntSingleton.getTypeName());
			infixExpression.append(" ");
		}
		else {
			throw new RuntimeException();
		}
	}
	
	@Override
	public void visit(DotFunctionCall e) {
		
	}
}
