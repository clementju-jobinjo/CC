package ch.unibe.scg.minijava.bcel.generator;

import java.util.Map;

import org.apache.bcel.Const;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;

import ch.unibe.scg.javacc.syntaxtree.ClassDeclaration;
import ch.unibe.scg.javacc.visitor.DepthFirstVoidVisitor;
import ch.unibe.scg.minijava.typechecker.scopes.Scope;

public class BytecodeGeneratorVisitor extends DepthFirstVoidVisitor {
	
	private JavaBytecodeGenerator bytecodeGenerator;
	private ClassGen classGen;
	private MethodGen methodGen;
	private InstructionList instructionList;
	private InstructionFactory instructionFactory;
	Map<String, Scope> classOrMethodOrVariableToScope;
	
	public BytecodeGeneratorVisitor(JavaBytecodeGenerator bytecodeGenerator, ClassGen classGen, MethodGen methodGen, InstructionList instructionList, InstructionFactory instructionFactory, Map<String, Scope> classOrMethodOrVariableToScope) {
		super();
		this.bytecodeGenerator = bytecodeGenerator;
		this.classGen = classGen;
		this.methodGen = methodGen;
		this.instructionList = instructionList;
		this.instructionFactory = instructionFactory;
		this.classOrMethodOrVariableToScope = classOrMethodOrVariableToScope;
	}
	
	// ClassDeclaration
	// "class" Identifier() ( "extends" Identifier() )? "{" ( VarDeclaration() )* ( MethodDeclaration() )* "}"
	@Override
	public void visit(ClassDeclaration classDeclaration) {
		
		String className = classDeclaration.nodeToken.tokenImage;
		String superClassName = classOrMethodOrVariableToScope.get(className).getScopeEnglobant().getTypeFromString(className).getBcelType().toString();
		
		classGen = new ClassGen(className, superClassName, className + ".java", Const.ACC_PUBLIC | Const.ACC_SUPER, new String[]{});
	}
	
}
