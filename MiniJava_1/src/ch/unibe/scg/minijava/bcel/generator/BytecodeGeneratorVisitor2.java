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
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IADD;
import org.apache.bcel.generic.IALOAD;
import org.apache.bcel.generic.IAND;
import org.apache.bcel.generic.IASTORE;
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

import ch.unibe.scg.javacc.syntaxtree.ArrayAccess;
import ch.unibe.scg.javacc.syntaxtree.AssignmentStatementArrayLeft;
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
	private String lastVisitedClass;
	private String currentClassFunctionCall;
	
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
		currentClassFunctionCall = new String();
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
		System.out.println(className);
		System.out.println(classOrMethodOrVariableToScope.get(className));
		
		currentScope = classOrMethodOrVariableToScope.get(className);
		
		classGen = new ClassGen(className, RootObject.RootObjectSingleton.getBcelType().toString(), className + ".java", Const.ACC_PUBLIC | Const.ACC_SUPER, new String[]{});
		
		classGen.addEmptyConstructor(Const.ACC_PUBLIC);
		
		constantPool = classGen.getConstantPool();
		
		instructionFactory = new InstructionFactory(classGen);
		
		
		// main method
		instructionList = new InstructionList();
		
		Method m = currentScope.getMethod("main");
		currentScope = classOrMethodOrVariableToScope.get(m.getIdentifier());
		
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
		
		if (!variableToLocation.containsKey(st.identifier.nodeToken.tokenImage)) {
			instructionList.append(new ALOAD(0));
		}
		
		// Expression()
		st.expression.accept(this);
		
		// Identifier()
		st.identifier.accept(this);
		
//		instructionList.insert(new ALOAD(0));
//		int i = this.constantPool.addFieldref(className, varName, "I");
//		instructionList.append(new PUTFIELD(i));
		
		isInAssignment = false;
		
	}
	
	
	// Identifier() ArrayAccess() "=" Expression() ";"
	@Override
	public void visit(AssignmentStatementArrayLeft st) {
		// Visit Identifier
		String arrayIdentifier = st.identifier.nodeToken.tokenImage;
		
		if (!variableToLocation.containsKey(st.identifier.nodeToken.tokenImage)) {
			String typeName = currentScope.getVariable(arrayIdentifier).getType().getBcelType().getSignature();
			instructionList.append(new ALOAD(0));
			instructionList.append(new GETFIELD(constantPool.addFieldref(classGen.getClassName(), arrayIdentifier, typeName)));
		}
		else {
			instructionList.append(new ALOAD(variableToLocation.get(arrayIdentifier)));
		}
		
		// Visit array index
		st.arrayAccess.accept(this);
		
		// Visit Expression
		st.expression.accept(this);
		
		instructionList.append(new IASTORE());
	}
	
	
//	// [ Expression() ]
//	@Override
//	public void visit (ArrayAccess arrayAccess) {
//		arrayAccess.expression.accept(this);
//	}
	
	
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
		
		
		
		// Array
		if (infixExpression.length() > 4 && infixExpression.contains("[")) {
			System.out.println("02934i");
			// Expression -> new int[1]
			int firstBracket = infixExpression.indexOf("[");
			if (infixExpression.startsWith("newint")) {
				
				generateIntrabracketBytecode(infixExpression, 0);
			
				//instructionList.append(new PUSH(constantPool, Integer.parseInt(intraBracket)));
				instructionList.append(new NEWARRAY(BasicType.INT));
			}
			// Expression -> identifier[index] (ex. return array[0])
			else {
				// load array
				String arrayIdentifier = infixExpression.substring(0, firstBracket);
				
				if(variableToLocation.containsKey(arrayIdentifier)){
					instructionList.append(new ALOAD(variableToLocation.get(arrayIdentifier)));
				}
				else {
					String typeName = currentScope.getVariable(arrayIdentifier).getType().getBcelType().getSignature();
					instructionList.append(new ALOAD(0));
					instructionList.append(new GETFIELD(constantPool.addFieldref(classGen.getClassName(), arrayIdentifier, typeName)));
				}
				
				// push index
				generateIntrabracketBytecode(infixExpression, 0);
				
				// load value in array at index
				instructionList.append(new IALOAD());
			}
			
			if (infixExpression.contains(".length")) {
				instructionList.append(new ARRAYLENGTH());
			}
			else if ( infixExpression.matches("newint\\[(.)+\\]\\[(.)+\\]\\s")) {
				int indexOfSecondOpeningBracket = infixExpression.lastIndexOf("[");
				String secondIndexExpression = infixExpression.substring(indexOfSecondOpeningBracket);
				
				// push index
				generateIntrabracketBytecode(secondIndexExpression, 0);
				
				// load value in array at index
				instructionList.append(new IALOAD());
			}
		}
		// Object constructor alone
		else if (postfixExpression.matches("new(.)+\\((.)?\\)\\s") && !postfixExpression.contains(".")) {
			System.out.println("2");
			int indexOfSecondBracket = postfixExpression.indexOf(")");
			String className = postfixExpression.substring(3, indexOfSecondBracket - 1);
			
			instructionList.append(instructionFactory.createNew(className));
			instructionList.append(new DUP());
			instructionList.append(instructionFactory.createInvoke(className, "<init>", org.apache.bcel.generic.Type.VOID, new org.apache.bcel.generic.Type[0], Const.INVOKESPECIAL));
			currentClassFunctionCall = className;
		}
		// If the postfix expression is a constant
		else if (isEvaluable(postfixExpression)) {
			System.out.println("3");
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
			System.out.println("4");
			for (String s : tokens) {
				System.out.println("----------TOKEN"+ s );
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
				else if (s.contentEquals("This")) {
					instructionList.append(new ALOAD(0));
					Scope classScope = currentScope.getScopeEnglobant();
					ClassDeclaration classDec = (ClassDeclaration)classScope.getNodeRelatedTo();
					String className = classDec.identifier.nodeToken.tokenImage;
					currentClassFunctionCall = className;
				}
				// In the case of the pattern .function()
				else if (s.contains(".")) {
					System.out.println("5");
						System.out.println("7");
						
						// new alone
						if (s.matches("new(.)+\\((.)?\\)\\s")) {
							System.out.println("2");
							int indexOfSecondBracket = postfixExpression.indexOf(")");
							String className = postfixExpression.substring(3, indexOfSecondBracket - 1);
							
							instructionList.append(instructionFactory.createNew(className));
							instructionList.append(new DUP());
							instructionList.append(instructionFactory.createInvoke(className, "<init>", org.apache.bcel.generic.Type.VOID, new org.apache.bcel.generic.Type[0], Const.INVOKESPECIAL));
							currentClassFunctionCall = className;
						}

						// case .function/[(dot function)+|newObject()|This]
						else {
							String[] slashTokens = s.split("/");
							String functionName = slashTokens[0].substring(1, slashTokens[0].indexOf("("));

							Method m = classOrMethodOrVariableToScope.get(currentClassFunctionCall).getMethod(functionName);
							org.apache.bcel.generic.Type returnType = m.getReturnType().getBcelType();
							
							// ARGUMENTS
							String argsString = s.substring(s.indexOf("(") + 1, s.indexOf(")")).replace("%", " ");
							String[] argsTokens = argsString.split(",");
							
							for (String argExp : argsTokens) {
								if (argExp.length() > 0) {
									generateIntrabracketBytecode(argExp, 1);
								}
							}
							
							List<Variable> methodArgs = m.getArguments();
							org.apache.bcel.generic.Type[] argTypes = new org.apache.bcel.generic.Type[methodArgs.size()];
							
							for (int i = 0; i < methodArgs.size(); i++) {
								argTypes[methodArgs.size() - i - 1] = methodArgs.get(i).getType().getBcelType();
							}
							
							// END ARGUMENTS
							
							instructionList.append(instructionFactory.createInvoke(currentClassFunctionCall, functionName, returnType, argTypes, Const.INVOKEVIRTUAL));
							
							currentClassFunctionCall = classOrMethodOrVariableToScope.get(functionName).getScopeEnglobant().getMethod(functionName).getReturnType().getTypeName();
						}
				}
				// new a la volee
				else if(s.contains("new")) {
					System.out.println("*Salut");
					int indexOfSecondBracket = postfixExpression.indexOf(")");
					String className = postfixExpression.substring(3, indexOfSecondBracket - 1);
					lastVisitedClass = className;
					instructionList.append(instructionFactory.createNew(className));
					instructionList.append(new DUP());
					instructionList.append(instructionFactory.createInvoke(className, "<init>", org.apache.bcel.generic.Type.VOID, new org.apache.bcel.generic.Type[0], Const.INVOKESPECIAL));
					currentClassFunctionCall = className;
				}
				// Variable for instance f.function()
				else if (!s.matches("[0-9]+")) {
					System.out.println("8");
					
					// Non-field case
					if (variableToLocation.containsKey(s)){
						String typeName = currentScope.getVariableNonRecursive(s).getType().getTypeName();

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
					}
					// class field case
					else {
						String typeName = currentScope.getVariable(s).getType().getBcelType().getSignature();
						instructionList.append(new ALOAD(0));
						instructionList.append(new GETFIELD(constantPool.addFieldref(classGen.getClassName(), s, typeName)));
					}
					
					
					
				} 
				else {
					instructionList.append(new PUSH(constantPool, Integer.parseInt(s)));
				}
			}
		}
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
	
	private void generateIntrabracketBytecode(String infixExpression, int mode) {
		int firstBracket;
		int secondBracket;
		String intraBracket;
		String[] intraBracketTokens;
		
		if (mode == 0) {
			firstBracket = infixExpression.indexOf("[");
			secondBracket = infixExpression.indexOf("]");
			intraBracket = infixExpression.substring(firstBracket + 1, secondBracket);
			intraBracketTokens = intraBracket.split(" ");
		}
		else {
			intraBracket = infixExpression;
			intraBracketTokens = intraBracket.split(" ");
		}
		
		for (String s : intraBracketTokens) {
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
			else {
				if (isEvaluable(intraBracket)) {
					instructionList.append(new PUSH(constantPool, Integer.parseInt(s)));
				}
				else {
					instructionList.append(new ILOAD(variableToLocation.get(s)));
				}
			}
		}
	}
	
	@Override
	public void visit(Identifier id) {
		
		if (isInAssignment) {
			String varName = id.nodeToken.tokenImage;
			
			// Non-field case
			if (variableToLocation.containsKey(varName)){
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
			// Class field case
			else {
				String typeName = currentScope.getVariable(varName).getType().getBcelType().getSignature();
				
				instructionList.append(new PUTFIELD(constantPool.addFieldref(classGen.getClassName(), varName, typeName)));
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
