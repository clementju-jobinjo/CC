package ch.unibe.scg.minijava.bcel.generator;

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
import ch.unibe.scg.minijava.typechecker.visitors.PostfixExpressionConstructor;
import ch.unibe.scg.minijava.typechecker.scopes.Scope;
import ch.unibe.scg.minijava.typechecker.types.Boolean;

public class ExpressionConstructor extends DepthFirstVoidVisitor {
	
	private StringBuilder infixExpression;
	private Scope currentScope;
	private List<Scope> scopes;
	private Map<String, Scope> classOrMethodOrVariableToScope;
	
	public ExpressionConstructor(Scope currentScope, List<Scope> scopes, Map<String, Scope> classOrMethodOrVariableToScope) {
		infixExpression = new StringBuilder();
		this.currentScope = currentScope;
		this.scopes = scopes;
		this.classOrMethodOrVariableToScope = classOrMethodOrVariableToScope;
	}
	
	
	public String getInfixExpression() {
		return infixExpression.toString();
	}
	
	
	@Override
	public void visit(ObjectConstructionCall e) {
//		Type type = null;
//		
//		for (Scope s : scopes) {
//			Type t = s.getTypeFromString(e.identifier.nodeToken.tokenImage);
//			if (t != null) {
//				type = t;
//				break;
//			}
//		}
//		if (type == null) {
//			throw new RuntimeException("Unknown type.");
//		}
//		
//		infixExpression.append(type.getTypeName());
//		infixExpression.append(" ");
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
			System.out.println("bjlkfdjs");
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
		
//		Scope classScope = currentScope.getScopeEnglobant();
//
//		ClassDeclaration classDec = (ClassDeclaration)classScope.getNodeRelatedTo();
//		
//		String className = classDec.identifier.nodeToken.tokenImage;
//		
//		Type classType = scopes.get(0).getTypeFromString(className);
//		
//		infixExpression.append(classType.getTypeName());
//		infixExpression.append(" ");
		
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

		ExpressionConstructor vis = new ExpressionConstructor(currentScope, scopes, classOrMethodOrVariableToScope);
		e.expression.accept(vis);
		String intraBracket = vis.getInfixExpression();
		
		PostfixExpressionConstructor pf = new PostfixExpressionConstructor();
		String postfixExpression = pf.postfix(intraBracket);
		
		String intraBracketValue = pf.evaluatePostfixValue(postfixExpression);
//		
//		String expTypeStr = pf.evaluatePostfix(postfixExpression);
		
//		if (expTypeStr.equals(Int.IntSingleton.getTypeName())) {
		infixExpression.append("int["+intraBracketValue+"]");
		infixExpression.append(" ");
//		}
//		else {
//			throw new RuntimeException("The size of the created array must be an integer.");
//		}
		
	}
	
	
//	@Override
//	public void visit(ArrayCall e) {
////		String pred = infixExpression.substring(infixExpression.length() - 6);
////		if (pred.equals(IntArray.IntArraySingleton.getTypeName() + " ")) {
////			infixExpression = infixExpression.delete(infixExpression.length() - 6, infixExpression.length());
////			infixExpression.append(Int.IntSingleton.getTypeName());
////			infixExpression.append(" ");
////		}
//		
//	}
	
	
	@Override
	public void visit(DotArrayLength e) {
//		String expTypeStr = pf.evaluatePostfix(postfixExpression);
		
//		if (expTypeStr.equals(Int.IntSingleton.getTypeName())) {
		infixExpression.append(".length");
		infixExpression.append(" ");
//		}
	}
	
	
	@Override
	public void visit(DotFunctionCall e) {

		String[] tokens = infixExpression.toString().trim().split("\\s+");
		String beforeDot = tokens[tokens.length-1];
		
		String functionName = e.identifier.nodeToken.tokenImage;
		infixExpression.append("."+functionName+"/"+beforeDot);
		infixExpression.append(" ");
	}
		
//		System.out.println(infixExpression.toString());
//		
//		// scope of thisType class
//		Scope thisScope = classOrMethodOrVariableToScope.get(beforeDot);
//		String className;
//		
//		// case f.baz() -- Otherwise case new Foo().baz()
//		if(beforeDot.matches("new(.)+\\((.)?\\)\\s")) {
//			className = beforeDot.substring(3, beforeDot.length() - 3);
//			Variable var = currentScope.getVariableNonRecursive(className);
//			Type t = var.getType();
//			thisScope = classOrMethodOrVariableToScope.get(t.getTypeName());
//		}
		
//		Method method = thisScope.getMethod(functionName);
//		
//		// arguments
//		List<Variable> methodArguments = method.getArguments();
		
		// (Expression (xxx)*)?
		//List<Type> argsType = new ArrayList<Type>();
		
//		NodeOptional nodeOptional = e.nodeOptional;
//		if (nodeOptional.present()) {
//			NodeSequence nodeSequence = (NodeSequence) nodeOptional.node;
//			
//			ValueVisitor visitor = new ValueVisitor(scopes, classOrMethodOrVariableToScope);
//			nodeSequence.elementAt(0).accept(visitor);
//			Variable firstArg = methodArguments.get(0);
//			firstArg.setValue(visitor.getValueOfLastExpression());
//			//argsType.add(visitor.getTypeOfLastExpression());
//			
//
//			//  ("," Expression() )*
//			NodeListOptional nodeListOptional2 = (NodeListOptional) nodeSequence.elementAt(1);
//			if (nodeListOptional2.present()) {
//				for (int j = 0; j < nodeListOptional2.size(); j++) {
//					INode node2 = nodeListOptional2.elementAt(j);
//					NodeSequence nodeSequence2 = (NodeSequence) node2;
//					
//					ValueVisitor visitor2 = new ValueVisitor(scopes, classOrMethodOrVariableToScope);
//					nodeSequence2.elementAt(1).accept(visitor2);
//					Variable nextArg = methodArguments.get(j + 1);
//					nextArg.setValue(visitor2.getValueOfLastExpression());
//					//argsType.add(visitor2.getTypeOfLastExpression());
//
//				}
//			}
//		}
		
//		// same length -> args list
//		if (argsType.size() != methodArguments.size()) {
//			throw new RuntimeException("Function call does not have the right number of arguments.");
//		}
//		
//		// same argument types
//		for (int i = 0; i < argsType.size(); i++) {
//			if (argsType.get(i) != methodArguments.get(i).getType()) {
//				throw new RuntimeException("Wrong arguments.");
//			}
//		}
		
//		infixExpression.replace(infixExpression.length() - (thisType.length() + 1), infixExpression.length(), method.getReturnType().getTypeName());
//		infixExpression.append(" ");
		
		
//		String[] tokens = infixExpression.toString().trim().split("\\s+");
//		if (tokens[tokens.length - 1].startsWith("new")) {
//			infixExpression.append(tokens[tokens.length - 1].substring(0, tokens[tokens.length - 1].length() - 2));
//		}
//		else {
//			infixExpression.deleteCharAt(infixExpression.length() - 1);
//		}
//		
//		infixExpression.append(
//				"." // dot
//				+ functionName // method name
//				+ e.nodeToken1.tokenImage // parenthesis "("
//				);
//		
		
		
		
//		for (int i = 0; i < methodArguments.size(); i++) {
//			if (i != 0) {
//				infixExpression.append(",");
//			}
//			infixExpression.append(methodArguments.get(i).getIdentifier());
//		}

	}
