package ch.unibe.scg.minijava.bcel.generator;

import java.util.List;
import java.util.Map;

import ch.unibe.scg.javacc.syntaxtree.ArrayCall;
import ch.unibe.scg.javacc.syntaxtree.BinaryOperator;
import ch.unibe.scg.javacc.syntaxtree.DotArrayLength;
import ch.unibe.scg.javacc.syntaxtree.DotFunctionCall;
import ch.unibe.scg.javacc.syntaxtree.FalseExpression;
import ch.unibe.scg.javacc.syntaxtree.INode;
import ch.unibe.scg.javacc.syntaxtree.Identifier;
import ch.unibe.scg.javacc.syntaxtree.IntArrayConstructionCall;
import ch.unibe.scg.javacc.syntaxtree.IntegerLiteral;
import ch.unibe.scg.javacc.syntaxtree.NodeListOptional;
import ch.unibe.scg.javacc.syntaxtree.NodeOptional;
import ch.unibe.scg.javacc.syntaxtree.NodeSequence;
import ch.unibe.scg.javacc.syntaxtree.ObjectConstructionCall;
import ch.unibe.scg.javacc.syntaxtree.ParenthesisExpression;
import ch.unibe.scg.javacc.syntaxtree.ThisExpression;
import ch.unibe.scg.javacc.syntaxtree.TrueExpression;
import ch.unibe.scg.javacc.syntaxtree.UnaryOperator;
import ch.unibe.scg.javacc.visitor.DepthFirstVoidVisitor;
import ch.unibe.scg.minijava.typechecker.types.Variable;
import ch.unibe.scg.minijava.typechecker.visitors.PostfixExpressionConstructor;
import ch.unibe.scg.minijava.typechecker.scopes.Scope;

public class ExpressionConstructor extends DepthFirstVoidVisitor {
	
	private StringBuilder infixExpression;
	private Scope currentScope;
	private List<Scope> scopes;
	private Map<String, Scope> classOrMethodOrVariableToScope;
	private Map<String, Scope> methodToScope;
	
	public ExpressionConstructor(Scope currentScope, List<Scope> scopes, Map<String, Scope> classOrMethodOrVariableToScope, Map<String, Scope> methodToScope) {
		infixExpression = new StringBuilder();
		this.currentScope = currentScope;
		this.scopes = scopes;
		this.methodToScope=methodToScope;
		this.classOrMethodOrVariableToScope = classOrMethodOrVariableToScope;
	}
	
	
	public String getInfixExpression() {
		return infixExpression.toString();
	}
	
	
	@Override
	public void visit(ObjectConstructionCall e) {
		infixExpression.append("new" + e.identifier.nodeToken.tokenImage + "()");
		infixExpression.append(" ");
	}
	
	
	@Override
	public void visit(Identifier id) {	
		Variable var = currentScope.getVariable(id.nodeToken.tokenImage);
		String value = var.getValue();
	
		
		if(value != null) {
			infixExpression.append(value);
			infixExpression.append(" ");
		}
		else {
			infixExpression.append(id.nodeToken.tokenImage);
			infixExpression.append(" ");
		}
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
		infixExpression.append(e.nodeToken.tokenImage);
		infixExpression.append(" ");
	}
	
	
	@Override
	public void visit(ThisExpression e) {
		infixExpression.append("This");
		infixExpression.append("  ");
	}
	
	
	@Override
	public void visit(TrueExpression e) {
		infixExpression.append("true");
		infixExpression.append(" ");
	}
	
	
	@Override
	public void visit(FalseExpression e) {
		infixExpression.append("false");
		infixExpression.append(" ");
	}
	
	
	@Override
	public void visit(BinaryOperator e) {
		infixExpression.append(e.nodeToken.tokenImage);
		infixExpression.append(" ");
	}

	
	@Override
	public void visit(IntArrayConstructionCall e) {

		ExpressionConstructor vis = new ExpressionConstructor(currentScope, scopes, classOrMethodOrVariableToScope, methodToScope);
		e.expression.accept(vis);
		String intraBracket = vis.getInfixExpression();
		
		PostfixExpressionConstructor pf = new PostfixExpressionConstructor();
		String postfixExpression = pf.postfix(intraBracket);
		
		String intraBracketValue = pf.evaluatePostfixValue(postfixExpression);

		infixExpression.append("newint["+intraBracketValue+"]");
		infixExpression.append(" ");
		
	}
	
	
	@Override
	public void visit(ArrayCall e) {
		
		ExpressionConstructor vis = new ExpressionConstructor(currentScope, scopes, classOrMethodOrVariableToScope, methodToScope);
		e.expression.accept(vis);
		String intraBracket = vis.getInfixExpression();
		
		PostfixExpressionConstructor pf = new PostfixExpressionConstructor();
		String postfixExpression = pf.postfix(intraBracket);
		
		infixExpression = infixExpression.deleteCharAt(infixExpression.length() - 1);
		infixExpression.append("[" + postfixExpression + "]");
		infixExpression.append(" ");
	}
	
	
	@Override
	public void visit(DotArrayLength e) {
		infixExpression.append(".length");
		infixExpression.append(" ");
	}
	
	
	@Override
	public void visit(DotFunctionCall e) {

		String[] tokens = infixExpression.toString().trim().split("\\s+");
		String beforeDot = tokens[tokens.length-1];
		StringBuilder arguments = new StringBuilder();
		
		NodeOptional nodeOptional = e.nodeOptional;
		
		// (Expression (xxx)*)?
		if (nodeOptional.present()) {
			NodeSequence nodeSequence = (NodeSequence) nodeOptional.node;
			
			ExpressionConstructor vis = new ExpressionConstructor(currentScope, scopes, classOrMethodOrVariableToScope, methodToScope);
			nodeSequence.elementAt(0).accept(vis);
			arguments.append(vis.getInfixExpression().replace(" ", "%"));
			arguments.deleteCharAt(arguments.length() - 1);
			

			//  ("," Expression() )*
			NodeListOptional nodeListOptional2 = (NodeListOptional) nodeSequence.elementAt(1);
			if (nodeListOptional2.present()) {
				for (int j = 0; j < nodeListOptional2.size(); j++) {
					INode node2 = nodeListOptional2.elementAt(j);
					NodeSequence nodeSequence2 = (NodeSequence) node2;
					
					ExpressionConstructor vis2 = new ExpressionConstructor(currentScope, scopes, classOrMethodOrVariableToScope, methodToScope);
					nodeSequence2.elementAt(1).accept(vis2);
					arguments.append(",");
					arguments.append(vis2.getInfixExpression().replace(" ", "%"));

				}
			}
		}
		
		String functionName = e.identifier.nodeToken.tokenImage;
		infixExpression.append("."+functionName + "(" + arguments.toString() + ")/"+beforeDot);
		infixExpression.append(" ");
	}
	
}
