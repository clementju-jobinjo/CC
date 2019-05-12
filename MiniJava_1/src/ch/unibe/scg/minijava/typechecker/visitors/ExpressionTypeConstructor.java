package ch.unibe.scg.minijava.typechecker.visitors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.unibe.scg.javacc.syntaxtree.ArrayCall;
import ch.unibe.scg.javacc.syntaxtree.BinaryOperator;
import ch.unibe.scg.javacc.syntaxtree.ClassDeclaration;
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
import ch.unibe.scg.minijava.typechecker.types.Int;
import ch.unibe.scg.minijava.typechecker.types.IntArray;
import ch.unibe.scg.minijava.typechecker.types.Method;
import ch.unibe.scg.minijava.typechecker.types.Type;
import ch.unibe.scg.minijava.typechecker.types.Variable;
import ch.unibe.scg.minijava.typechecker.scopes.Scope;
import ch.unibe.scg.minijava.typechecker.types.Boolean;

public class ExpressionTypeConstructor extends DepthFirstVoidVisitor {
	
	private StringBuilder infixExpression;
	private Scope currentScope;
	private List<Scope> scopes;
	private Map<String, Scope> classToScope;
	private Map<String, Scope> methodToScope;
	
	public ExpressionTypeConstructor(Scope currentScope, List<Scope> scopes, Map<String, Scope> classToScope, Map<String, Scope> methodToScope) {
		infixExpression = new StringBuilder();
		this.currentScope = currentScope;
		this.scopes = scopes;
		this.classToScope = classToScope;
	}
	
	
	public String getInfixExpression() {
		return infixExpression.toString();
	}
	
	
	@Override
	public void visit(ObjectConstructionCall e) {
		Type type = null;
		
		for (Scope s : scopes) {
			Type t = s.getTypeFromString(e.identifier.nodeToken.tokenImage);
			if (t != null) {
				type = t;
				break;
			}
		}
		if (type == null) {
			throw new RuntimeException("Unknown type.");
		}
		
		infixExpression.append(type.getTypeName());
		infixExpression.append(" ");
	}
	
	
	@Override
	public void visit(Identifier id) {
		
		Variable var = currentScope.getVariable(id.nodeToken.tokenImage);
		Type t = var.getType();
		
		if(t != null) {
			infixExpression.append(t.getTypeName());
			infixExpression.append(" ");
		}
		else {
			throw new RuntimeException("Unknown identifier type.");
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
		infixExpression.append(Int.IntSingleton.getTypeName());
		infixExpression.append(" ");
	}
	
	
	@Override
	public void visit(ThisExpression e) {
		
		Scope classScope = currentScope.getScopeEnglobant();

		ClassDeclaration classDec = (ClassDeclaration)classScope.getNodeRelatedTo();
		
		String className = classDec.identifier.nodeToken.tokenImage;
		
		Type classType = scopes.get(0).getTypeFromString(className);
		
		infixExpression.append(classType.getTypeName());
		infixExpression.append(" ");
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

		ExpressionTypeConstructor vis = new ExpressionTypeConstructor(currentScope, scopes, classToScope, methodToScope);
		e.expression.accept(vis);
		String intraBracketType = vis.getInfixExpression();
		
		PostfixExpressionConstructor pf = new PostfixExpressionConstructor();
		String postfixExpression = pf.postfix(intraBracketType);

		String expTypeStr = pf.evaluatePostfix(postfixExpression);
		
		if (expTypeStr.equals(Int.IntSingleton.getTypeName())) {
			infixExpression.append(IntArray.IntArraySingleton.getTypeName());
			infixExpression.append(" ");
		}
		else {
			throw new RuntimeException("The size of the created array must be an integer.");
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
			throw new RuntimeException("Trying to access a field of a non int array object.");
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
			throw new RuntimeException("Trying to get the length of an non int array object.");
		}
	}
	
	
	@Override
	public void visit(DotFunctionCall e) {

		String[] tokens = infixExpression.toString().trim().split("\\s+");
		String thisType = tokens[tokens.length-1];
		String functionName = e.identifier.nodeToken.tokenImage;
		
		// scope of thisType class
		Scope thisScope = classToScope.get(thisType);
		
		Method method = thisScope.getMethod(functionName);
		
		if (method == null) {
			throw new RuntimeException("Trying to call a non-existing method.");
		}
		
		
		// arguments
		List<Variable> methodArguments = method.getArguments();
		
		// (Expression (xxx)*)?
		List<Type> argsType = new ArrayList<Type>();
		
		NodeOptional nodeOptional = e.nodeOptional;
		if (nodeOptional.present()) {
			NodeSequence nodeSequence = (NodeSequence) nodeOptional.node;
			
			EvaluatorVisitor visitor = new EvaluatorVisitor(scopes, classToScope, methodToScope);
			nodeSequence.elementAt(0).accept(visitor);
			argsType.add(visitor.getTypeOfLastExpression());
			

			//  ("," Expression() )*
			NodeListOptional nodeListOptional2 = (NodeListOptional) nodeSequence.elementAt(1);
			if (nodeListOptional2.present()) {
				for (int j = 0; j < nodeListOptional2.size(); j++) {
					INode node2 = nodeListOptional2.elementAt(j);
					NodeSequence nodeSequence2 = (NodeSequence) node2;
					
					EvaluatorVisitor visitor2 = new EvaluatorVisitor(scopes, classToScope, methodToScope);
					nodeSequence2.elementAt(1).accept(visitor2);
					argsType.add(visitor2.getTypeOfLastExpression());

				}
			}
		}
		
		// same length -> args list
		if (argsType.size() != methodArguments.size()) {
			throw new RuntimeException("Function call does not have the right number of arguments.");
		}
		
		// same argument types
		for (int i = 0; i < argsType.size(); i++) {
			if (argsType.get(i) != methodArguments.get(i).getType()) {
				throw new RuntimeException("Wrong arguments.");
			}
		}
		
		infixExpression.replace(infixExpression.length() - (thisType.length() + 1), infixExpression.length(), method.getReturnType().getTypeName());
		infixExpression.append(" ");
	}
}
