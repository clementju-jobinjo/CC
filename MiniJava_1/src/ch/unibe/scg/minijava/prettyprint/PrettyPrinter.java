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
	private int indent;
	
	public PrettyPrinter() {
		super();
		this.strBuffer = new StringBuffer();
		this.indent=0;
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
		space();
		
		// Identifier()
		Identifier identifier = classDeclaration.identifier;
		identifier.accept(this);
		space();
		
		// ( "extends" Identifier() )?
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
		newLine(indent);

		
		//( VarDeclaration() )*
		NodeListOptional nodeListOptional = classDeclaration.nodeListOptional;
		if (nodeListOptional.present()) {
			for (int i = 0; i < nodeListOptional.size(); i++) {
				INode node = nodeListOptional.elementAt(i);
				node.accept(this);
			}
		}
		space();
		
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
		space();
		varDeclaration.identifier.accept(this);
		varDeclaration.nodeToken.accept(this);
	}
	
	// MethodDeclaration
	// "public" Type() Identifier() "(" (Type() Identifier() ("," Type() Identifier() )* )? ")" 
	// "{" ( LOOKAHEAD(2) VarDeclaration() )* (Statement() )* "return" Expression() ";" "}"
	@Override
	public void visit(MethodDeclaration methodDeclaration) {
		// public
		methodDeclaration.nodeToken.accept(this);
		space();
		
		// Type()
		methodDeclaration.type.accept(this);
		space();
		
		// Identifier()
		methodDeclaration.identifier.accept(this);
		
		
		// (
		methodDeclaration.nodeToken1.accept(this);
		
		// (Type() Identifier() ("," Type() Identifier() )* )?
		NodeOptional nodeOptional = methodDeclaration.nodeOptional;
		if (nodeOptional.present()) {
			NodeSequence nodeSequence = (NodeSequence) nodeOptional.node;
			
			// "Type"
			INode nodeType = nodeSequence.elementAt(0);
			nodeType.accept(this);
			space();
			
			// Identifier
			INode nodeIdentifier = nodeSequence.elementAt(1);
			nodeIdentifier.accept(this);
			
			//  ("," Type() Identifier() )*
			NodeListOptional nodeListOptional = (NodeListOptional) nodeSequence.elementAt(2);
			if (nodeListOptional.present()) {
				for (int i = 0; i < nodeListOptional.size(); i++) {
					INode node = nodeListOptional.elementAt(i);
					NodeSequence nodeSequence2 = (NodeSequence) node;
					INode comma = nodeSequence2.elementAt(0);
					comma.accept(this);
					space();
					INode type2 = nodeSequence2.elementAt(1);
					type2.accept(this);
					space();
					INode identifier2 = nodeSequence2.elementAt(2);
					identifier2.accept(this);
				}
			}
		}
		// )
		methodDeclaration.nodeToken2.accept(this);
		space();
		
		// {
		methodDeclaration.nodeToken3.accept(this);
		indent+=4;
		newLine(indent);
		
		// ( LOOKAHEAD(2) VarDeclaration() )*
		NodeListOptional nodeListOptional = methodDeclaration.nodeListOptional;
		if (nodeListOptional.present()) {
			for (int i = 0; i < nodeListOptional.size(); i++) {
				INode node = nodeListOptional.elementAt(i);
				node.accept(this);
				if(i==nodeListOptional.size()-1) {
					indent=0;
					newLine(indent);
				}
				else {
					newLine(indent);					
				}
				
			}
		}
		
		// (Statement() )*
		NodeListOptional nodeListOptional2 = methodDeclaration.nodeListOptional1;
		if (nodeListOptional2.present()) {
			indent+=4;
			newLine(indent);
			for (int i = 0; i < nodeListOptional2.size(); i++) {
				INode node = nodeListOptional2.elementAt(i);
				node.accept(this);
				newLine(indent);
			}
		}
		
		// return
		methodDeclaration.nodeToken4.accept(this);
		space();
		
		// Expression()
		methodDeclaration.expression.accept(this);
		
		// ;
		methodDeclaration.nodeToken5.accept(this);
		indent-=5;
		newLine(indent);

		// }
		methodDeclaration.nodeToken6.accept(this);
	}
	
	// Type
	// LOOKAHEAD(2) "int" "[" "]" | "int" | "boolean" | "void" | Identifier()
	@Override
	public void visit(Type type) {
		if(type.nodeChoice.which==2) {
			strBuffer.append("bool");
		}
		else {
			type.nodeChoice.choice.accept(this);
		}
		
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
		NodeSequence nodeSequence = (NodeSequence) statement.nodeChoice.choice;
		switch(statement.nodeChoice.which){
		case 0:
			// {
			INode bracket01 = nodeSequence.elementAt(0);
			bracket01.accept(this);
			indent+=4;
			newLine(indent);
			// Statement
			INode statement01 = nodeSequence.elementAt(1);
			statement01.accept(this);
			indent-=4;
			newLine(indent);
			// }
			INode bracket02 = nodeSequence.elementAt(2);
			bracket02.accept(this);
			break;
			
		case 1:
			// if
			INode ifnode11 = nodeSequence.elementAt(0);
			ifnode11.accept(this);
			space();
			// (
			INode  parenthesis11 = nodeSequence.elementAt(1);
			parenthesis11.accept(this);
			//Expression
			INode expression11 = nodeSequence.elementAt(2);
			expression11.accept(this);
			// )
			INode parenthesis12 = nodeSequence.elementAt(3);
			parenthesis12.accept(this);
			space();
			// Statement()
			INode statement11 = nodeSequence.elementAt(4);
			statement11.accept(this);
			// else
			space();
			INode else11 = nodeSequence.elementAt(5);
			else11.accept(this);
			space();
			// Statement()
			INode statement12 = nodeSequence.elementAt(6);
			statement12.accept(this);
			
			break;
		case 2:
			// while
			INode while21 = nodeSequence.elementAt(0);
			while21.accept(this);
			// (
			INode parenthesis21 = nodeSequence.elementAt(1);
			parenthesis21.accept(this);
			//Expression
			INode expression21 = nodeSequence.elementAt(2);
			expression21.accept(this);
			// )
			INode parenthesis22 = nodeSequence.elementAt(3);
			parenthesis22.accept(this);
			// Statement()
			INode statement21 = nodeSequence.elementAt(4);
			statement21.accept(this);
			break;
		case 4:
			//Identifier
			INode identifier41 = nodeSequence.elementAt(0);
			identifier41.accept(this);
			space();
			// =
			INode equals41 = nodeSequence.elementAt(1);
			equals41.accept(this);
			space();
			// Expression
			INode expression41 = nodeSequence.elementAt(2);
			expression41.accept(this);
			// ;
			INode semicolon41 = nodeSequence.elementAt(3);
			semicolon41.accept(this);
			break;
		case 5:
			// identifier
			INode identifier = nodeSequence.elementAt(0);
			identifier.accept(this);
			// [
			INode bracket = nodeSequence.elementAt(1);
			bracket.accept(this);
			// expression
			INode expression52 = nodeSequence.elementAt(2);
			expression52.accept(this);
			// ]
			INode bracket52 = nodeSequence.elementAt(3);
			bracket52.accept(this);
			space();
			// =
			INode equals = nodeSequence.elementAt(4);
			equals.accept(this);
			space();
			// Expression
			INode expression53 = nodeSequence.elementAt(5);
			expression53.accept(this);
			// ;
			INode semicolon= nodeSequence.elementAt(6);
			semicolon.accept(this);
			break;
		default:
			statement.nodeChoice.choice.accept(this);
		}
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
		switch(expression.nodeChoice.which) {
			case 0:
				NodeSequence nodeSequence = (NodeSequence) expression.nodeChoice.choice;
				// new
				INode newWord = nodeSequence.elementAt(0);
				newWord.accept(this);
				space();
				// int
				INode integer = nodeSequence.elementAt(1);
				integer.accept(this);
				// [
				INode bracket = nodeSequence.elementAt(2);
				bracket.accept(this);
				// expression
				INode expression2 = nodeSequence.elementAt(3);
				expression2.accept(this);
				// ]
				INode bracket2 = nodeSequence.elementAt(4);
				bracket2.accept(this);
				// ExpPrime
				INode expprime = nodeSequence.elementAt(5);
				expprime.accept(this);
				break;
			default:
				expression.nodeChoice.choice.accept(this);
		}
		
		
		
	}
	
	// ExpPrime
	//( "&&" | "<" | "+" | "-" | "*" | ">" ) Expression() ExpPrime()
	//	| "[" Expression() "]" ExpPrime()
	//	| LOOKAHEAD(2) "." "length" ExpPrime()
	//	| "." Identifier() "(" ( Expression() ( "," Expression() )* )? ")" ExpPrime()
	//	| Epsilon()
	@Override
	public void visit(ExpPrime expPrime) {
		if(expPrime.nodeChoice.which==0) {
			
			// ( "&&" | "<" | "+" | "-" | "*" | ">" )
			NodeSequence nodeSequence = (NodeSequence) expPrime.nodeChoice.choice;
			space();
			INode operator = nodeSequence.elementAt(0);
			operator.accept(this);
			space();
			// Expression()
			INode expression = nodeSequence.elementAt(1);
			expression.accept(this);
			
			// ExpPrime()
			INode expprime = nodeSequence.elementAt(2);
			expprime.accept(this);

		}
		else {
			expPrime.nodeChoice.accept(this);			
		}
		
	}
	
	// Identifier --> Token
	@Override
	public void visit(Identifier identifier) {
		identifier.nodeToken.accept(this);
	}
	
	// IntegerLiteral 
	public void visit(IntegerLiteral integerLiteral) {
		integerLiteral.nodeToken.accept(this);
	}
	
	// Tokens -> Identifiers, Integer_literals
	@Override
	public void visit(NodeToken node) {
		strBuffer.append(node.tokenImage);
	}
	
	
	private void newLine(int tabs) {
		strBuffer.append("\n");
		for (int i = tabs; i > 0; i--)
			tab();
	}

	private void tab() {
		strBuffer.append(" ");
	}

	private void space() {
		strBuffer.append(" ");
}
}
