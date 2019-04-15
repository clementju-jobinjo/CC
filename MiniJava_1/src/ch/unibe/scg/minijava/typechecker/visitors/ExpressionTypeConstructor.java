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
import ch.unibe.scg.javacc.syntaxtree.MethodDeclaration;
import ch.unibe.scg.javacc.syntaxtree.NewExpression;
import ch.unibe.scg.javacc.syntaxtree.NodeListOptional;
import ch.unibe.scg.javacc.syntaxtree.NodeOptional;
import ch.unibe.scg.javacc.syntaxtree.NodeSequence;
import ch.unibe.scg.javacc.syntaxtree.ObjectConstructionCall;
import ch.unibe.scg.javacc.syntaxtree.ParenthesisExpression;
import ch.unibe.scg.javacc.syntaxtree.ThisExpression;
import ch.unibe.scg.javacc.syntaxtree.TrueExpression;
import ch.unibe.scg.javacc.syntaxtree.UnaryExpression;
import ch.unibe.scg.javacc.syntaxtree.UnaryOperator;
import ch.unibe.scg.javacc.syntaxtree.VarDeclaration;
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
	private Map<String, Scope> classOrMethodOrVariableToScope;
	
	public ExpressionTypeConstructor(Scope currentScope, List<Scope> scopes, Map<String, Scope> classOrMethodOrVariableToScope) {
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
		System.out.println("55");
		Type type = null;
//		Type t = currentScope.getVariable(e.identifier.nodeToken.tokenImage).getType();
		for (Scope s : scopes) {
			Type t = s.getTypeFromString(e.identifier.nodeToken.tokenImage);
			if (t != null) {
				type = t;
				break;
			}
		}
		if (type == null) {
			throw new RuntimeException();
		}
		
		infixExpression.append(type.getTypeName());
		infixExpression.append(" ");
	}
	
	@Override
	public void visit(Identifier id) {
		System.out.println("66");
		Type t = null;
	
		for (Scope s : scopes) {
			Variable var = s.getVariableNonRecursive(id.nodeToken.tokenImage);
			if (var != null) {
				t =  var.getType();
				break;
			}
		}
		if(t != null) {
		System.out.println(t.getTypeName());
		System.out.println("67");
		infixExpression.append(t.getTypeName());
		infixExpression.append(" ");
		}
		else {
			throw new RuntimeException();
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
		System.out.println("Boo");
		System.out.println(scopes.size());
//		System.out.println(scopes.get(0).toString());
//		System.out.println(scopes.get(1).toString());
//		System.out.println(scopes.get(2).toString());
		Scope classScope = currentScope.getScopeEnglobant().getScopeEnglobant();
		System.out.println(classScope.toString());
		ClassDeclaration classDec = (ClassDeclaration)classScope.getNodeRelatedTo();
		System.out.println("Boo2");
		String className = classDec.identifier.nodeToken.tokenImage;
		System.out.println("Boo3");
		Type classType = classScope.getTypeFromString(className);
		System.out.println("Boo4" + classType.getTypeName());
		
		infixExpression.append(classType.getTypeName());
		infixExpression.append(" ");
		System.out.println("Boo5");
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
		System.out.println("INTARRAY");
		ExpressionTypeConstructor vis = new ExpressionTypeConstructor(currentScope, scopes, classOrMethodOrVariableToScope);
		e.expression.accept(vis);
		String intraBracketType = vis.getInfixExpression();
		
		PostfixExpressionConstructor pf = new PostfixExpressionConstructor();
		String postfixExpression = pf.postfix(intraBracketType);
		System.out.println(postfixExpression);
		String expTypeStr = pf.evaluatePostfix(postfixExpression);
		
		
		if (expTypeStr.equals(Int.IntSingleton.getTypeName())) {
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
		System.out.println("DOT FUNCTION CALL");
		String[] tokens = infixExpression.toString().trim().split("\\s+");
		String thisType = tokens[tokens.length-1];
		
//		System.out.println("TYPE " + thisType);
		
		String functionName = e.identifier.nodeToken.tokenImage;
		
		// scope of thisType class
		Scope thisScope = classOrMethodOrVariableToScope.get(thisType);
		System.out.println(thisScope);
		
		Method method = thisScope.getMethod(functionName);
		
		if (method == null) {
			throw new RuntimeException();
		}
		
		
//		// get method object
//		Method method = null;
//		for (Scope s : scopes) {
//			Method m = s.getMethodNonRecursive(functionName);
//			if (m != null) {
//				method = m;
//				break;
//			}
//		}
		
		System.out.println("METHOD" + method.getIdentifier());
		
		
		// get internal class scope of this
		
		
		
		List<Variable> methodArguments = method.getArguments();
		
		// arguments
		// (Expression (xxx)*)?
		List<Type> argsType = new ArrayList<Type>();
		
		NodeOptional nodeOptional = e.nodeOptional;
		if (nodeOptional.present()) {
			NodeSequence nodeSequence = (NodeSequence) nodeOptional.node;
			
			EvaluatorVisitor visitor = new EvaluatorVisitor(scopes, classOrMethodOrVariableToScope);
			nodeSequence.elementAt(0).accept(visitor);
			argsType.add(visitor.getTypeOfLastExpression());
			

			//  ("," Expression() )*
			NodeListOptional nodeListOptional2 = (NodeListOptional) nodeSequence.elementAt(1);
			if (nodeListOptional2.present()) {
				for (int j = 0; j < nodeListOptional2.size(); j++) {
					INode node2 = nodeListOptional2.elementAt(j);
					NodeSequence nodeSequence2 = (NodeSequence) node2;
					
					EvaluatorVisitor visitor2 = new EvaluatorVisitor(scopes, classOrMethodOrVariableToScope);
					nodeSequence2.elementAt(1).accept(visitor2);
					argsType.add(visitor2.getTypeOfLastExpression());

				}
			}
		}
		
		// same length -> args list
		if (argsType.size() != methodArguments.size()) {
			throw new RuntimeException();
		}
		
		// same argument types
		for (int i = 0; i < argsType.size(); i++) {
			if (argsType.get(i) != methodArguments.get(i).getType()) {
				throw new RuntimeException();
			}
		}
		
		infixExpression.replace(infixExpression.length() - (thisType.length() + 1), infixExpression.length(), method.getReturnType().getTypeName());
		infixExpression.append(" ");
	}
}
