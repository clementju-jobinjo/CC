package ch.unibe.scg.minijava.bcel.generator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ARETURN;
import org.apache.bcel.generic.ARRAYLENGTH;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DUP;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IADD;
import org.apache.bcel.generic.IAND;
import org.apache.bcel.generic.IDIV;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.IFGT;
import org.apache.bcel.generic.IFNE;
import org.apache.bcel.generic.IF_ICMPGE;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.IMUL;
import org.apache.bcel.generic.IRETURN;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.ISUB;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.NOP;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.RETURN;
import org.apache.bcel.generic.SWAP;

import ch.unibe.scg.javacc.syntaxtree.AssignmentStatementIdentifierLeft;
import ch.unibe.scg.javacc.syntaxtree.ClassDeclaration;
import ch.unibe.scg.javacc.syntaxtree.DotArrayLength;
import ch.unibe.scg.javacc.syntaxtree.Expression;
import ch.unibe.scg.javacc.syntaxtree.INode;
import ch.unibe.scg.javacc.syntaxtree.Identifier;
import ch.unibe.scg.javacc.syntaxtree.IfStatement;
import ch.unibe.scg.javacc.syntaxtree.IntArrayConstructionCall;
import ch.unibe.scg.javacc.syntaxtree.IntegerLiteral;
import ch.unibe.scg.javacc.syntaxtree.MainClass;
import ch.unibe.scg.javacc.syntaxtree.MethodDeclaration;
import ch.unibe.scg.javacc.syntaxtree.NodeListOptional;
import ch.unibe.scg.javacc.syntaxtree.NodeOptional;
import ch.unibe.scg.javacc.syntaxtree.PrintStatement;
import ch.unibe.scg.javacc.syntaxtree.Statement;
import ch.unibe.scg.javacc.syntaxtree.VarDeclaration;
import ch.unibe.scg.javacc.syntaxtree.WhileStatement;
import ch.unibe.scg.javacc.visitor.DepthFirstVoidVisitor;
import ch.unibe.scg.minijava.typechecker.scopes.Scope;
import ch.unibe.scg.minijava.typechecker.types.Method;
import ch.unibe.scg.minijava.typechecker.types.RootObject;
import ch.unibe.scg.minijava.typechecker.types.Type;
import ch.unibe.scg.minijava.typechecker.types.Variable;
import ch.unibe.scg.minijava.typechecker.visitors.ExpressionTypeConstructor;
import ch.unibe.scg.minijava.typechecker.visitors.PostfixExpressionConstructor;

public class BytecodeGeneratorVisitor2 extends DepthFirstVoidVisitor {
	
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
	private String valueOfLastVisitedExpression;
	private boolean isInWhile;
	private boolean isInAssignment;
	
	public BytecodeGeneratorVisitor2(JavaBytecodeGenerator bytecodeGenerator, ClassGen classGen, MethodGen methodGen, InstructionList instructionList, InstructionFactory instructionFactory, Map<String, Scope> classOrMethodOrVariableToScope, List<Scope> scopes) {
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
		valueOfLastVisitedExpression = new String();
		isInWhile = false;
		isInAssignment = false;
	}
	
	// ClassDeclaration
	// "class" Identifier() ( "extends" Identifier() )? "{" ( VarDeclaration() )* ( MethodDeclaration() )* "}"
	@Override
	public void visit(ClassDeclaration classDeclaration) {
		
		String className = classDeclaration.identifier.nodeToken.tokenImage;
		
		// change current scope
		currentScope = classOrMethodOrVariableToScope.get(className);
		
		String superClassName = currentScope.getScopeEnglobant().getTypeFromString(className).getParentType().getBcelType().toString();

		// update class objects
		classGen = new ClassGen(className, superClassName, className + ".java", Const.ACC_PUBLIC | Const.ACC_SUPER, new String[]{});
		classGen.addEmptyConstructor(Const.ACC_PUBLIC);
		
		constantPool = classGen.getConstantPool();
		
		instructionFactory = new InstructionFactory(classGen);
		
		
		
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
		
		//instructionList.dispose();
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
		
		methodGen = new MethodGen(Const.ACC_PUBLIC, method.getReturnType().getBcelType(), argTypes, argNames, methodName, classGen.getClassName(), instructionList, constantPool);
		
		LocalVariableGen[] methodLocalVariables = methodGen.getLocalVariables();
		for (int i = 0; i < methodLocalVariables.length; i++) {
			variableToLocation.put(methodLocalVariables[i].getName(), methodLocalVariables[i].getIndex());
		}
		
		for (Map.Entry<String, Integer> entry : variableToLocation.entrySet()) {
		    System.out.println(entry.getKey() + ": " + entry.getValue());
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
		
		
		// return Expression()
		methodDeclaration.expression.accept(this);
		
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
		//instructionList.dispose();
		
	}
	
	
	@Override
	public void visit(VarDeclaration varDeclaration) {
		String varName = varDeclaration.identifier.nodeToken.tokenImage;
		Variable var = currentScope.getVariable(varName);
		
		
		// in class 
		if (varDeclarationInClass) {
			classGen.addField(new FieldGen(Const.ACC_PUBLIC, var.getType().getBcelType(), varName, constantPool).getField());
		}
		else {
			LocalVariableGen localVariableGen = methodGen.addLocalVariable(varName + "_0", var.getType().getBcelType(), null, null);
			variableToLocation.put(varName, localVariableGen.getIndex());
			System.out.println("VARNAME: " + varName + ", INDEX: " + localVariableGen.getIndex());
		}
	}
	
	
	// "if" < PARENTHESIS_LEFT > Expression() < PARENTHESIS_RIGHT > Statement() "else" Statement()
	@Override
	public void visit(IfStatement ifStatement) {
		
		// Expression
		ifStatement.expression.accept(this);
		InstructionHandle beginIf = instructionList.getEnd();
		
		// If then statements
		ifStatement.statement.accept(this);
		InstructionHandle endIf = instructionList.getEnd();
		
		// Else statements
		ifStatement.statement1.accept(this);
		InstructionHandle endElse = instructionList.append(new NOP());
		
		instructionList.append(beginIf, new IFEQ(endIf.getNext()));
		instructionList.append(endIf, new GOTO(endElse));
		
	}
	
	
	// "while" < PARENTHESIS_LEFT > Expression() < PARENTHESIS_RIGHT > Statement()
	@Override
	public void visit(WhileStatement whileStatement) {
		InstructionHandle startExpression = instructionList.getEnd();
		
		// Expression()
		whileStatement.expression.accept(this);
		
		startExpression = startExpression.getNext();
		
		InstructionHandle endExpression = instructionList.getEnd();
		
		// Statement
		whileStatement.statement.accept(this);

		instructionList.append(new GOTO(startExpression));
		
		InstructionHandle endWhile = instructionList.append(new NOP());

		instructionList.append(endExpression, new IFEQ(endWhile));
	}
	
	
	// Identifier() "=" Expression() ";"
	@Override
	public void visit(AssignmentStatementIdentifierLeft st) {
		isInAssignment = true;
		System.out.println("Assignment");
		// Identifier()
//		String varName = st.identifier.nodeToken.tokenImage;
//		int index = variableToLocation.get(varName);
		
		// Expression()
		st.expression.accept(this);
		
		// Identifier()
		st.identifier.accept(this);
		
//		instructionList.insert(new ALOAD(0));
//		int i = this.constantPool.addFieldref(className, varName, "I");
//		instructionList.append(new PUTFIELD(i));
		
		isInAssignment = false;
		
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
		
		String[] tokens = postfixExpression.split(" ");
		
		//System.out.println(isEvaluable(postfixExpression));
		
		// array
		if (postfixExpression.length() > 4 && postfixExpression.substring(0, 4).equals("int[")) {
			int firstBracket = postfixExpression.indexOf("[");
			int secondBracket = postfixExpression.indexOf("]");
			String arraySize = postfixExpression.substring(firstBracket + 1, secondBracket);
			instructionList.append(new PUSH(constantPool, Integer.parseInt(arraySize)));
			instructionList.append(new NEWARRAY(BasicType.INT));
			
			if (postfixExpression.contains(".length")) {
				instructionList.append(new ARRAYLENGTH());
			}
			else if ( postfixExpression.matches("int[(.)+][(.)+]")) {
				System.out.println("HELLO ITS ME");
			}
		}
		// object constructor
		else if (postfixExpression.matches("new(.)+\\((.)?\\)\\s") && !postfixExpression.contains(".")) {
			int indexOfSecondBracket = postfixExpression.indexOf(")");
			String className = postfixExpression.substring(3, indexOfSecondBracket - 1);
			
			instructionList.append(instructionFactory.createNew(className));
			instructionList.append(new DUP());
			instructionList.append(instructionFactory.createInvoke(className, "<init>", org.apache.bcel.generic.Type.VOID, new org.apache.bcel.generic.Type[0], Const.INVOKESPECIAL));
		}
		else if (isEvaluable(postfixExpression)) {
			String val = pf.evaluatePostfixValue(postfixExpression);
			if (val.equals("true")) {
				instructionList.append(new PUSH(constantPool, true));
			}
			else if (val.equals("false")) {
				instructionList.append(new PUSH(constantPool, false));
			}
			else {
				instructionList.append(new PUSH(constantPool, Integer.parseInt(val)));
			}
		}
		else {
			for (String s : tokens) {
				if (s.equals("+")) {
					instructionList.append(new IADD());
				}
				else if (s.equals("-")) {
					instructionList.append(new ISUB());
				}
				else if (s.equals("*")) {
					instructionList.append(new IMUL());
				}
				else if (s.equals("/")) {
					instructionList.append(new IDIV());
				}
				else if (s.equals("<")) {
					instructionList.append(new SWAP());
					instructionList.append(new ISUB());
					InstructionHandle isFalse = instructionList.append(new PUSH(constantPool, false));
					InstructionHandle isTrue = instructionList.append(new PUSH(constantPool, true));
					InstructionHandle jump = instructionList.append(new NOP());
					instructionList.insert(isFalse, new IFGT(isTrue));
					instructionList.insert(isTrue, new GOTO(jump));
				}
				else if (s.equals(">")) {
					instructionList.append(new ISUB());
					InstructionHandle isFalse = instructionList.append(new PUSH(constantPool, false));
					InstructionHandle isTrue = instructionList.append(new PUSH(constantPool, true));
					InstructionHandle jump = instructionList.append(new NOP());
					instructionList.insert(isFalse, new IFGT(isTrue));
					instructionList.insert(isTrue, new GOTO(jump));
				}
				else if (s.equals("==")) {
					instructionList.append(new ISUB());
					InstructionHandle isFalse = instructionList.append(new PUSH(constantPool, false));
					InstructionHandle isTrue = instructionList.append(new PUSH(constantPool, true));
					InstructionHandle jump = instructionList.append(new NOP());
					instructionList.insert(isFalse, new IFEQ(isTrue));
					instructionList.insert(isTrue, new GOTO(jump));
				}
				else if (s.equals("&&")) {
					instructionList.append(new IAND());
				}
				else if (s.equals("!")) {
					InstructionHandle ifNe = instructionList.append(new PUSH(constantPool, true));
					InstructionHandle ifNotNe = instructionList.append(new PUSH(constantPool, false));
					InstructionHandle nop = instructionList.append(new NOP());
					instructionList.insert(ifNotNe, new GOTO(nop));
					instructionList.insert(ifNe, new IFNE(ifNotNe));
				}
				else if (s.equals("true")){
					instructionList.append(new PUSH(constantPool, true));
				}
				else if (s.equals("false")){
					instructionList.append(new PUSH(constantPool, false));
				}
				else if (s.matches("new(.)+\\((.)?\\)") && !s.contains(".")) {
					int indexOfSecondBracket = postfixExpression.indexOf(")");
					String className = postfixExpression.substring(3, indexOfSecondBracket - 1);
					
					instructionList.append(instructionFactory.createNew(className));
					instructionList.append(new DUP());
					instructionList.append(instructionFactory.createInvoke(className, "<init>", org.apache.bcel.generic.Type.VOID, new org.apache.bcel.generic.Type[0], Const.INVOKESPECIAL));
				}
				else if (s.contains(".")) {
					// newFoo.baz() or f.baz()
					if (s.contains("new")) {
						System.out.println("SUILA");
						// newFoo.baz()
						int indexDot = s.indexOf(".");
						
						// newFoo part -> keep Foo
						String correspondingClass = s.substring(3, indexDot);
						
						// baz() part
						String methodName = s.substring(indexDot + 1, s.length() - 2);
						
						
						Method method = classOrMethodOrVariableToScope.get(correspondingClass).getMethod(methodName);
						List<Variable> methodArgs = method.getArguments();
						org.apache.bcel.generic.Type[] argTypes = new org.apache.bcel.generic.Type[methodArgs.size()];
						
						for (int i = 0; i < methodArgs.size(); i++) {
							argTypes[methodArgs.size() - i - 1] = methodArgs.get(i).getType().getBcelType();
						}
						
						System.out.println(correspondingClass);
						System.out.println(methodName);
						System.out.println(method.getReturnType().getBcelType().toString());
						instructionList.append(instructionFactory.createInvoke(correspondingClass, methodName, method.getReturnType().getBcelType(), argTypes, Const.INVOKEVIRTUAL));
					}
					else {
						// foo.baz()
						int indexDot = s.indexOf(".");
						
						// foo part
						String varName = s.substring(0, indexDot);
						Variable var = currentScope.getVariableNonRecursive(varName);
						String correspondingClass = var.getType().getTypeName();
						
						instructionList.append(new ALOAD(variableToLocation.get(varName)));
						
						// baz() part
						String methodName = s.substring(indexDot + 1, s.length() - 2);
						
						Method method = currentScope.getMethod(methodName);
						List<Variable> methodArgs = method.getArguments();
						org.apache.bcel.generic.Type[] argTypes = new org.apache.bcel.generic.Type[methodArgs.size()];
						
						for (int i = 0; i < methodArgs.size(); i++) {
							argTypes[methodArgs.size() - i - 1] = methodArgs.get(i).getType().getBcelType();
						}
						
						instructionList.append(instructionFactory.createInvoke(correspondingClass, methodName, method.getReturnType().getBcelType(), argTypes, Const.INVOKEVIRTUAL));
					}
					
				}
				else if (!s.matches("[0-9]+")) {
					
					String typeName = currentScope.getVariableNonRecursive(s).getType().getTypeName();
					
//					if(isInAssignment) {
//						switch(typeName) {
//							case "int":
//								instructionList.append(new ISTORE(variableToLocation.get(s)));
//								break;
//							
//							case "boolean":
//								instructionList.append(new ISTORE(variableToLocation.get(s)));
//								break;
//							
//							case "int[]":
//								instructionList.append(new ASTORE(variableToLocation.get(s)));
//								break;
//							
//							default:
//								instructionList.append(new ASTORE(variableToLocation.get(s)));
//						}
//					}
//					else {
					switch(typeName) {
						case "int":
							instructionList.append(new ILOAD(variableToLocation.get(s)));
							break;
						
						case "boolean":
							instructionList.append(new ILOAD(variableToLocation.get(s)));
							break;
						
						case "int[]":
							instructionList.append(new ALOAD(variableToLocation.get(s)));
							break;
						
						default:
							instructionList.append(new ALOAD(variableToLocation.get(s)));
					}
//					}
					
				} 
				else {
						instructionList.append(new PUSH(constantPool, Integer.parseInt(s)));
				}
			}
		}
		
//		if (val.equals("true")) {
//			instructionList.append(new PUSH(constantPool, true));
//			valueOfLastVisitedExpression = "true";
//		}
//		else if (val.equals("false")) {
//			instructionList.append(new PUSH(constantPool, false));
//			valueOfLastVisitedExpression = "false";
//		}
//		else {
//			instructionList.append(new PUSH(constantPool, Integer.parseInt(val)));
//			valueOfLastVisitedExpression = val;
//		}
		
//		String expTypeStr = pf.evaluatePostfix(postfixExpression);
//		Type expType = null;
	}
	
	private boolean isEvaluable(String exp) {
		boolean isEvaluable = true;
		String[] tokens = exp.split(" ");
		for (String s : tokens) {
			if (!s.matches("true|false|[0-9]+|\\+|-|==|&&|<|>|!")) {
				isEvaluable = false;
				break;
			}
		}
		return isEvaluable;
	}
	
	@Override
	public void visit(Identifier id) {
		
		if (isInAssignment) {
			String varName = id.nodeToken.tokenImage;
			String typeName = currentScope.getVariableNonRecursive(varName).getType().getTypeName();
			
			switch(typeName) {
				case "int":
					instructionList.append(new ISTORE(variableToLocation.get(varName)));
					break;
				
				case "boolean":
					instructionList.append(new ISTORE(variableToLocation.get(varName)));
					break;
				
				case "int[]":
					instructionList.append(new ASTORE(variableToLocation.get(varName)));
					break;
				
				default:
					instructionList.append(new ASTORE(variableToLocation.get(varName)));
			}
		}
	}
	
	@Override
	public void visit(IntegerLiteral i) {
		instructionList.append(new PUSH(constantPool, Integer.parseInt(i.nodeToken.tokenImage)));
	}
	
//	@Override
//	public void visit(IntArrayConstructionCall iACC) {
//		iACC.expression.accept(this);
//		instructionList.append(new NEWARRAY(BasicType.INT));
//	}
//	
//	@Override
//	public void visit(DotArrayLength l) {
//		instructionList.append(new ARRAYLENGTH());
//	}
	
	@Override
	public void visit(PrintStatement st) {
		ObjectType javaPrintStream = new ObjectType("java.io.PrintStream");
		//InstructionFactory factory = new InstructionFactory(classGen);
		instructionList.append(instructionFactory.createFieldAccess("java.lang.System", "out", javaPrintStream, Const.GETSTATIC));
		
		st.expression.accept(this);

		instructionList.append(instructionFactory.createInvoke("java.io.PrintStream", "println", org.apache.bcel.generic.Type.VOID, new org.apache.bcel.generic.Type[] { org.apache.bcel.generic.Type.INT }, Const.INVOKEVIRTUAL));
	}
	
	
	public MethodGen getMethodGen() {
		return methodGen;
	}
	
	public ClassGen getClassGen() {
		return classGen;
	}
}
