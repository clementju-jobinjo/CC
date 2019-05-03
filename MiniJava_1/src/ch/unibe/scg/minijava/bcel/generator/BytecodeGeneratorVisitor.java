package ch.unibe.scg.minijava.bcel.generator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ARETURN;
import org.apache.bcel.generic.ARRAYLENGTH;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.IRETURN;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.RETURN;

import ch.unibe.scg.javacc.syntaxtree.ClassDeclaration;
import ch.unibe.scg.javacc.syntaxtree.DotArrayLength;
import ch.unibe.scg.javacc.syntaxtree.Expression;
import ch.unibe.scg.javacc.syntaxtree.INode;
import ch.unibe.scg.javacc.syntaxtree.IntArrayConstructionCall;
import ch.unibe.scg.javacc.syntaxtree.IntegerLiteral;
import ch.unibe.scg.javacc.syntaxtree.MainClass;
import ch.unibe.scg.javacc.syntaxtree.MethodDeclaration;
import ch.unibe.scg.javacc.syntaxtree.NodeListOptional;
import ch.unibe.scg.javacc.syntaxtree.NodeOptional;
import ch.unibe.scg.javacc.syntaxtree.Statement;
import ch.unibe.scg.javacc.syntaxtree.VarDeclaration;
import ch.unibe.scg.javacc.visitor.DepthFirstVoidVisitor;
import ch.unibe.scg.minijava.typechecker.scopes.Scope;
import ch.unibe.scg.minijava.typechecker.types.Method;
import ch.unibe.scg.minijava.typechecker.types.RootObject;
import ch.unibe.scg.minijava.typechecker.types.Type;
import ch.unibe.scg.minijava.typechecker.types.Variable;
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
	private boolean varDeclarationInClass;
	private Map<String, Integer> variableToLocation;
	
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
		varDeclarationInClass = false;
		variableToLocation = new HashMap<String, Integer>();
	}
	
	// ClassDeclaration
	// "class" Identifier() ( "extends" Identifier() )? "{" ( VarDeclaration() )* ( MethodDeclaration() )* "}"
	@Override
	public void visit(ClassDeclaration classDeclaration) {
		
		String className = classDeclaration.nodeToken.tokenImage;
		String superClassName = classOrMethodOrVariableToScope.get(className).getScopeEnglobant().getTypeFromString(className).getBcelType().toString();
		
		// update class objects
		classGen = new ClassGen(className, superClassName, className + ".java", Const.ACC_PUBLIC | Const.ACC_SUPER, new String[]{});
		classGen.addEmptyConstructor(Const.ACC_PUBLIC);
		
		constantPool = classGen.getConstantPool();
		
		instructionFactory = new InstructionFactory(classGen);
		
		// change current scope
		currentScope = classOrMethodOrVariableToScope.get(className);
		
		// ( VarDeclaration() )*
		varDeclarationInClass = true;
		
		NodeListOptional nodeListOptional = classDeclaration.nodeListOptional;
		if (nodeListOptional.present()) {
			for (int i = 0; i < nodeListOptional.size(); i++) {
				INode node = nodeListOptional.elementAt(i);
				node.accept(this);
			}
		}
		
		varDeclarationInClass = false;
		
		// ( MethodDeclaration() )*
		NodeListOptional nodeListOptional1 = classDeclaration.nodeListOptional1;
		if (nodeListOptional1.present()) {
			for (int i = 0; i < nodeListOptional1.size(); i++) {
				INode node = nodeListOptional1.elementAt(i);
				node.accept(this);
			}
		}
		
		// generate class
		JavaClass javaClass = classGen.getJavaClass();
		
		try {
			javaClass.dump("bin/" + classGen.getClassName().replaceAll("\\.", "/") + ".class");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void visit(MainClass mainClass) {
		String className = mainClass.identifier.nodeToken.tokenImage;
		
		currentScope = classOrMethodOrVariableToScope.get(className);
		
		classGen = new ClassGen(className, RootObject.RootObjectSingleton.getBcelType().toString(), className + ".java", Const.ACC_PUBLIC | Const.ACC_SUPER, new String[]{});
		
		classGen.addEmptyConstructor(Const.ACC_PUBLIC);
		
		constantPool = classGen.getConstantPool();
		
		instructionFactory = new InstructionFactory(classGen);
		
		
		// main method
		instructionList = new InstructionList();
		
		Method m = currentScope.getMethod("main");
		
		// main method: arguments
		List<Variable> args = m.getArguments();
		int argsSize = args.size();
		
		org.apache.bcel.generic.Type[] argTypes = new org.apache.bcel.generic.Type[argsSize];
		String[] argNames = new String[argsSize];
		
		for (int i = 0; i < argsSize; i++) {
			Variable arg = args.get(i);
			argTypes[i] = arg.getType().getBcelType();
			argNames[i] = arg.getIdentifier();
		}
		
		methodGen = new MethodGen(Const.ACC_PUBLIC | Const.ACC_STATIC, m.getReturnType().getBcelType(), argTypes, argNames, "main", classGen.getClassName(), instructionList, constantPool);
		
		LocalVariableGen[] methodLocalVariables = methodGen.getLocalVariables();
		for (int i = 0; i < methodLocalVariables.length; i++) {
			variableToLocation.put(methodLocalVariables[i].getName(), methodLocalVariables[i].getIndex());
		}
		
		// main method: ( Statement() )?
		NodeOptional nodeOptional = mainClass.nodeOptional;
		if (nodeOptional.present()) {
			nodeOptional.accept(this);
		}
		
		instructionList.append(new RETURN());

		methodGen.setMaxStack();
		methodGen.setMaxLocals();

		bytecodeGenerator.addMethod(classGen, methodGen);
		
		instructionList.dispose();
	}
	
	
	@Override
	public void visit(MethodDeclaration methodDeclaration) {
		
		String methodName = methodDeclaration.identifier.nodeToken.tokenImage;
		
		currentScope = classOrMethodOrVariableToScope.get(methodName);
		
		Method method = currentScope.getScopeEnglobant().getMethodNonRecursive(methodName);
		
		instructionList = new InstructionList();
		
		//arguments
		List<Variable> args = method.getArguments();
		int argsSize = args.size();
		
		org.apache.bcel.generic.Type[] argTypes = new org.apache.bcel.generic.Type[argsSize];
		String[] argNames = new String[argsSize];
		
		for (int i = 0; i < argsSize; i++) {
			Variable arg = args.get(i);
			argTypes[i] = arg.getType().getBcelType();
			argNames[i] = arg.getIdentifier();
		}
		
		methodGen = new MethodGen(Const.ACC_PUBLIC | Const.ACC_STATIC, method.getReturnType().getBcelType(), argTypes, argNames, "main", classGen.getClassName(), instructionList, constantPool);
		
		LocalVariableGen[] methodLocalVariables = methodGen.getLocalVariables();
		for (int i = 0; i < methodLocalVariables.length; i++) {
			variableToLocation.put(methodLocalVariables[i].getName(), methodLocalVariables[i].getIndex());
		}
		
		// ( LOOKAHEAD(2) VarDeclaration() )*
		NodeListOptional nodeListOptional = methodDeclaration.nodeListOptional;
		if (nodeListOptional.present()) {
			for (int i = 0; i < nodeListOptional.size(); i++) {
				VarDeclaration node = (VarDeclaration)nodeListOptional.elementAt(i);
				node.accept(this);
			}
		}
		
		// ( Statement() )*
		NodeListOptional nodeListOptional1 = methodDeclaration.nodeListOptional1;
		if (nodeListOptional1.present()) {
			for (int i = 0; i < nodeListOptional1.size(); i++) {
				Statement node = (Statement)nodeListOptional1.elementAt(i);
				node.accept(this);
			}
		}
		
		
		// return
		String returnTypeStr = method.getReturnType().getTypeName();
		
		if (returnTypeStr.equals("int") || returnTypeStr.equals("boolean")) {
			instructionList.append(new IRETURN());
		}
		else {
			instructionList.append(new ARETURN());
		}
		
		methodGen.setMaxStack();
		methodGen.setMaxLocals();

		bytecodeGenerator.addMethod(classGen, methodGen);
		instructionList.dispose();
		
	}
	
	
	@Override
	public void visit(VarDeclaration varDeclaration) {
		String varName = varDeclaration.nodeToken.tokenImage;
		Variable var = currentScope.getVariable(varName);
		
		// in class 
		if (varDeclarationInClass) {
			classGen.addField(new FieldGen(Const.ACC_PUBLIC, var.getType().getBcelType(), varName, constantPool).getField());
		}
		else {
			LocalVariableGen localVariableGen = methodGen.addLocalVariable(varName + "_0", var.getType().getBcelType(), null, null);
			variableToLocation.put(varName, localVariableGen.getIndex());
		}
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
