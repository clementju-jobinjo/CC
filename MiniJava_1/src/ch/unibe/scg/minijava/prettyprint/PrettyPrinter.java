package ch.unibe.scg.minijava.prettyprint;

import ch.unibe.scg.javacc.syntaxtree.*;
import ch.unibe.scg.javacc.visitor.*;

/**
 * Change at will!
 * 
 * @author kursjan
 *
 */
public class PrettyPrinter extends DepthFirstVoidVisitor {
	// Add your implementation here
	private StringBuffer strBuffer;
	
	public PrettyPrinter() {
		super();
		this.strBuffer = new StringBuffer();
	}

	public String prettyPrint(Object node) {
		// TODO
		INode n = (INode) node;
		n.accept(this);
		return strBuffer.toString();
	}
	
	// Goal
	// MainClass() ( ClassDeclaration() )* < EOF >
	@Override
	public void visit(Goal goal) {
		strBuffer.append("//Pretty Printer says Hi There!\n");
		
		// MainClass
		MainClass mainClass = goal.mainClass;
		mainClass.accept(this);
		
		// ClassDeclaration
		NodeListOptional nodeList = goal.nodeListOptional;
		if (nodeList.present()) {
			for (int i = 0; i < nodeList.size(); i++) {
				INode node = nodeList.elementAt(i);
				node.accept(this);
			}
		}
		
		// EOF
		NodeToken nodeToken = goal.nodeToken;
		nodeToken.accept(this);
		
	}
	
	// ClassDeclaration
	// "class" Identifier() ( "extends" Identifier() )? "{" ( VarDeclaration() )* ( MethodDeclaration() )* "}"
	@Override
	public void visit(ClassDeclaration classDeclaration) {
		// "class"
		NodeToken nodeToken = classDeclaration.nodeToken;
		nodeToken.accept(this);
		
		// Identifier()
		Identifier identifier = classDeclaration.identifier;
		identifier.accept(this);
		
		// ( "extends" Identifier() )?
		// ? 
		NodeOptional nodeOptional = classDeclaration.nodeOptional;
		if (nodeOptional.present()) {
			NodeSequence nodeSequence = (NodeSequence) nodeOptional.node;
			// "extends"
			INode nodeExtends = nodeSequence.elementAt(0);
			nodeExtends.accept(this);
			
			// Identifier
			INode nodeIdentifier = nodeSequence.elementAt(1);
			nodeIdentifier.accept(this);
		}
		
		// "{"
		NodeToken nodeToken1 = classDeclaration.nodeToken1;
		nodeToken1.accept(this);
		
		//( VarDeclaration() )*
		NodeListOptional nodeListOptional = classDeclaration.nodeListOptional;
		if (nodeListOptional.present()) {
			for (int i = 0; i < nodeListOptional.size(); i++) {
				INode node = nodeListOptional.elementAt(i);
				node.accept(this);
			}
		}
		
		// ( MethodDeclaration() )*
		NodeListOptional nodeListOptional1 = classDeclaration.nodeListOptional1;
		if (nodeListOptional1.present()) {
			for (int i = 0; i < nodeListOptional1.size(); i++) {
				INode node = nodeListOptional1.elementAt(i);
				node.accept(this);
			}
		}
		
		// "}"
		NodeToken nodeToken2 = classDeclaration.nodeToken2;
		nodeToken2.accept(this);
	}
	
	// VarDeclaration
	// Type() Identifier() ";"
	@Override
	public void visit(VarDeclaration varDeclaration) {
		varDeclaration.type.accept(this);
		varDeclaration.identifier.accept(this);
		varDeclaration.nodeToken.accept(this);
	}
	
	// MethodDeclaration
	// "public" Type() Identifier() "(" (Type() Identifier() ("," Type() Identifier() )* )? ")" 
	// "{" ( LOOKAHEAD(2) VarDeclaration() )* (Statement() )* "return" Expression() ";" "}"
	@Override
	public void visit(MethodDeclaration methodDeclaration) {
		
	}
	
	// Type
	// LOOKAHEAD(2) "int" "[" "]" | "int" | "boolean" | "void" | Identifier()
	@Override
	public void visit(Type type) {
		
	}
	
	// Statement
	// "{" ( Statement() )* "}"
	//	  | "if" "(" Expression() ")" Statement() "else" Statement()
	//	  | "while" "(" Expression() ")" Statement()
	//	  | "System.out.println" "(" Expression() ")" ";"
	//	  | LOOKAHEAD(2) Identifier() "=" Expression() ";"
	//	  | Identifier() "[" Expression() "]" "=" Expression() ";"
	@Override
	public void visit(Statement statement) {
		
	}
	
	// Expression
	// LOOKAHEAD(2) "new" "int" "[" Expression() "]" ExpPrime()
	//	  | "new" Identifier() "(" ")" ExpPrime()
	//	  | Identifier() ExpPrime()
	//	  | "!" Expression() ExpPrime()
	//	  | "(" Expression() ")" ExpPrime()
	//	  | < INTEGER_LITERAL > ExpPrime()
	//	  | "this" ExpPrime()
	//	  | "true" ExpPrime()
	//	  | "false" ExpPrime()
	@Override
	public void visit(Expression expression) {
		
	}
	
	// Tokens -> Identifiers, Integer_literals
	@Override
	public void visit(NodeToken node) {
		strBuffer.append(node.tokenImage);
	}
}
