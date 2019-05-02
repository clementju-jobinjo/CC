package ch.unibe.scg.minijava.bcel.generator;

import java.util.List;
import java.util.Map;

import org.apache.bcel.Const;
import org.apache.bcel.generic.ARRAYLENGTH;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.PUSH;

import ch.unibe.scg.javacc.syntaxtree.ClassDeclaration;
import ch.unibe.scg.javacc.syntaxtree.DotArrayLength;
import ch.unibe.scg.javacc.syntaxtree.Expression;
import ch.unibe.scg.javacc.syntaxtree.IntArrayConstructionCall;
import ch.unibe.scg.javacc.syntaxtree.IntegerLiteral;
import ch.unibe.scg.javacc.visitor.DepthFirstVoidVisitor;
import ch.unibe.scg.minijava.typechecker.scopes.Scope;
import ch.unibe.scg.minijava.typechecker.types.Type;
import ch.unibe.scg.minijava.typechecker.visitors.ExpressionTypeConstructor;
import ch.unibe.scg.minijava.typechecker.visitors.PostfixExpressionConstructor;

public class BytecodeGeneratorVisitor extends DepthFirstVoidVisitor {
	
	private JavaBytecodeGenerator bytecodeGenerator;
	private ClassGen classGen;
	private MethodGen methodGen;
	private InstructionList instructionList;
	private InstructionFactory instructionFactory;
	private ConstantPoolGen constantPool;
	private Map<String, Scope> classOrMethodOrVariableToScope;
	private List<Scope> scopes;
	private Scope currentScope;
	//private Stack<> expressionStack;
	
	public BytecodeGeneratorVisitor(JavaBytecodeGenerator bytecodeGenerator, ClassGen classGen, MethodGen methodGen, InstructionList instructionList, InstructionFactory instructionFactory, Map<String, Scope> classOrMethodOrVariableToScope, List<Scope> scopes) {
		super();
		this.bytecodeGenerator = bytecodeGenerator;
		this.classGen = classGen;
		this.methodGen = methodGen;
		this.instructionList = instructionList;
		this.instructionFactory = instructionFactory;
		this.classOrMethodOrVariableToScope = classOrMethodOrVariableToScope;
		this.constantPool = classGen.getConstantPool();
		this.scopes = scopes;
		currentScope = scopes.get(0);
	}
	
	// ClassDeclaration
	// "class" Identifier() ( "extends" Identifier() )? "{" ( VarDeclaration() )* ( MethodDeclaration() )* "}"
	@Override
	public void visit(ClassDeclaration classDeclaration) {
		
		String className = classDeclaration.nodeToken.tokenImage;
		String superClassName = classOrMethodOrVariableToScope.get(className).getScopeEnglobant().getTypeFromString(className).getBcelType().toString();
		
		classGen = new ClassGen(className, superClassName, className + ".java", Const.ACC_PUBLIC | Const.ACC_SUPER, new String[]{});
	}
	
	
	@Override
	public void visit(Expression exp) {
		ExpressionConstructor visitor = new ExpressionConstructor(currentScope, scopes, classOrMethodOrVariableToScope);
		exp.accept(visitor);
		
		String infixExpression = visitor.getInfixExpression();
		
		System.out.println("INFIX: " + infixExpression);

		PostfixExpressionConstructor pf = new PostfixExpressionConstructor();
		String postfixExpression = pf.postfix(infixExpression);
		
		System.out.println("POSTFIX: " + postfixExpression);
		
		String val = pf.evaluatePostfixValue(postfixExpression);
		
		System.out.println("POSTFIX EVALUATED: " + val);
		
		if (val.equals("true")) {
			instructionList.append(new PUSH(constantPool, true));
		}
		else if (val.equals("false")) {
			instructionList.append(new PUSH(constantPool, false));
		}
		else {
			instructionList.append(new PUSH(constantPool, Integer.parseInt(val)));
		}
		
//		String expTypeStr = pf.evaluatePostfix(postfixExpression);
//		Type expType = null;
	}
	
	@Override
	public void visit(IntegerLiteral i) {
		instructionList.append(new PUSH(constantPool, Integer.parseInt(i.nodeToken.tokenImage)));
	}
	
	@Override
	public void visit(IntArrayConstructionCall iACC) {
		iACC.expression.accept(this);
		instructionList.append(new NEWARRAY(BasicType.INT));
	}
	
	@Override
	public void visit(DotArrayLength l) {
		instructionList.append(new ARRAYLENGTH());
	}
}
