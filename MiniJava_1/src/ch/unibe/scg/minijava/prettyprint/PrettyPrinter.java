package ch.unibe.scg.minijava.prettyprint;

import ch.unibe.scg.javacc.syntaxtree.*;
import ch.unibe.scg.javacc.visitor.*;

/**
 * Change at will!
 * 
 * @author Johan Jobin, Julien Clement
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
		newLine(0);
		newLine(0);
		// ClassDeclaration
		NodeListOptional nodeList = goal.nodeListOptional;
		if (nodeList.present()) {
			for (int i = 0; i < nodeList.size(); i++) {
				INode node = nodeList.elementAt(i);
				node.accept(this);
			}
		}
		
		// EOF
		if(nodeList.present()) {
			newLine(0);
			newLine(0);			
		}
		
		NodeToken nodeToken = goal.nodeToken;
		nodeToken.accept(this);
		
	}
	
	
	// Mainclass
	//"class" Identifier() "{" "public" "static" "void" "main" "(" "String" "[" "]" Identifier() ")" "{" ( Statement() )? "}" "}"
	@Override
	public void visit(MainClass mainClass) {
		// "class"
		NodeToken nodeToken = mainClass.nodeToken;
		nodeToken.accept(this);
		space();
		
		// Identifier()
		Identifier identifier = mainClass.identifier;
		identifier.accept(this);
		space();
		
		// {
		NodeToken nodeToken1 = mainClass.nodeToken1;
		nodeToken1.accept(this);
		newLine(0);
		indent+=4;
		newLine(indent);
		
		// public
		NodeToken nodeToken2 = mainClass.nodeToken2;
		nodeToken2.accept(this);
		space();
		
		// static
		NodeToken nodeToken3 = mainClass.nodeToken3;
		nodeToken3.accept(this);
		space();
		
		// void
		NodeToken nodeToken4 = mainClass.nodeToken4;
		nodeToken4.accept(this);
		space();
		
		// main
		NodeToken nodeToken5 = mainClass.nodeToken5;
		nodeToken5.accept(this);
		
		// (
		NodeToken nodeToken6 = mainClass.nodeToken6;
		nodeToken6.accept(this);
		
		// String
		NodeToken nodeToken7 = mainClass.nodeToken7;
		nodeToken7.accept(this);
		
		// [
		NodeToken nodeToken8 = mainClass.nodeToken8;
		nodeToken8.accept(this);
		
		// ]
		NodeToken nodeToken9 = mainClass.nodeToken9;
		nodeToken9.accept(this);
		space();
		
		// Identifier
		Identifier identifier1 = mainClass.identifier1;
		identifier1.accept(this);
		
		// )
		NodeToken nodeToken10 = mainClass.nodeToken10;
		nodeToken10.accept(this);
		space();
		 
		// {
		NodeToken nodeToken11 = mainClass.nodeToken11;
		nodeToken11.accept(this);
		indent+=4;
		newLine(indent);
		
		// (Statement())?
		NodeOptional nodeOptional = mainClass.nodeOptional;
			if (nodeOptional.present()) {
				INode node = nodeOptional.node;
				node.accept(this);
		}
		indent-=4;
		newLine(indent);
		
		
		// }
		NodeToken nodeToken12 = mainClass.nodeToken12;
		nodeToken12.accept(this);
		
		// }
		indent-=4;
		newLine(indent);
		newLine(indent);
		NodeToken nodeToken13 = mainClass.nodeToken13;
		nodeToken13.accept(this);		
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
			space();
			
			// Identifier
			INode nodeIdentifier = nodeSequence.elementAt(1);
			nodeIdentifier.accept(this);
			space();
		}
		
		// "{"
		NodeToken nodeToken1 = classDeclaration.nodeToken1;
		nodeToken1.accept(this);
		indent+=4;
		
		//( VarDeclaration() )*
		NodeListOptional nodeListOptional = classDeclaration.nodeListOptional;
		if (nodeListOptional.present()) {
			newLine(indent);
			for (int i = 0; i < nodeListOptional.size(); i++) {
				INode node = nodeListOptional.elementAt(i);
				node.accept(this);
				if(nodeListOptional.size()>1 && i<nodeListOptional.size()-1) {
					newLine(indent);
				}
				
			}
			newLine(0);
			newLine(indent);
		}
		
		// ( MethodDeclaration() )*
		NodeListOptional nodeListOptional1 = classDeclaration.nodeListOptional1;
		if (nodeListOptional1.present()) {
			if (!nodeListOptional.present()) {
				newLine(0);
				newLine(indent);				
			}
			
			for (int i = 0; i < nodeListOptional1.size(); i++) {
				INode node = nodeListOptional1.elementAt(i);
				node.accept(this);
				newLine(0);
				if(i!=nodeListOptional1.size()-1) {
					newLine(indent);
				}
				
			}
			newLine(0);
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
				if(i == nodeListOptional.size()-1) {
					newLine(0);
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
		indent-=4;
		newLine(indent);

		// }
		methodDeclaration.nodeToken6.accept(this);
	}
	
	
	// "int" < BRACKET_LEFT > < BRACKET_RIGHT >
	@Override
	public void visit(IntArrayDeclaration intArr) {
		// "int"
		intArr.nodeToken.accept(this);
		
		// < BRACKET_LEFT >
		intArr.nodeToken1.accept(this);
		
		// < BRACKET_RIGHT >
		intArr.nodeToken2.accept(this);
	}
	
	
	// "{" ( Statement() )* "}"
	@Override
	public void visit(BlockStatement blockStatement) {
		
		// "{"
		blockStatement.nodeToken.accept(this);
		indent+=4;
		newLine(indent);
		
		// Statement
		if (blockStatement.nodeListOptional.present()) {
			for (int i = 0; i < blockStatement.nodeListOptional.size(); ++i) {
				blockStatement.nodeListOptional.elementAt(i).accept(this);
				if(blockStatement.nodeListOptional.size()>1 && i<blockStatement.nodeListOptional.size()-1) {
					newLine(indent);
				}	
			}
			indent-=4;
			newLine(indent);
		}
		// "}"
		blockStatement.nodeToken1.accept(this);
	}
	
	
	// "if" < PARENTHESIS_LEFT > Expression() < PARENTHESIS_RIGHT > Statement() "else" Statement()
	@Override
	public void visit(IfStatement ifStatement) {
		// "if" 
		ifStatement.nodeToken.accept(this);
		space();
		
		// < PARENTHESIS_LEFT >
		ifStatement.nodeToken1.accept(this);
		
		
		// Expression()
		ifStatement.expression.accept(this);
		
		
		// < PARENTHESIS_RIGHT >
		ifStatement.nodeToken2.accept(this);
		space();
		
		// Statement()
		ifStatement.statement.accept(this);
		space();
		
		// "else"
		ifStatement.nodeToken3.accept(this);
		space();
		
		// Statement()
		ifStatement.statement1.accept(this);
	}
	
	
	// "while" < PARENTHESIS_LEFT > Expression() < PARENTHESIS_RIGHT > Statement()
	@Override
	public void visit(WhileStatement whileStatement) {
		// "while"
		whileStatement.nodeToken.accept(this);
		space();
		
		// < PARENTHESIS_LEFT > 
		whileStatement.nodeToken1.accept(this);
		
		// Expression()
		whileStatement.expression.accept(this);
		
		// < PARENTHESIS_RIGHT >
		whileStatement.nodeToken2.accept(this);
		space();
		
		// Statement()
		whileStatement.statement.accept(this);
	}
	
	
	// "System.out.println" < PARENTHESIS_LEFT > Expression() < PARENTHESIS_RIGHT > ";"
	@Override
	public void visit(PrintStatement printStatement) {
		// "System.out.println"
		printStatement.nodeToken.accept(this);
		
		// < PARENTHESIS_LEFT > 
		printStatement.nodeToken1.accept(this);
		
		// Expression()
		printStatement.expression.accept(this);
		
		// < PARENTHESIS_RIGHT >
		printStatement.nodeToken2.accept(this);
		
		// ";"
		printStatement.nodeToken3.accept(this);
	}
	
	
	// Identifier() "=" Expression() ";"
	@Override
	public void visit(AssignmentStatementIdentifierLeft assignmentStatementIdentifierLeft) {
		// Assigned()
		assignmentStatementIdentifierLeft.identifier.accept(this);
		space();
		
		// "=" 
		assignmentStatementIdentifierLeft.nodeToken.accept(this);
		space();
		
		// Expression()
		assignmentStatementIdentifierLeft.expression.accept(this);
		
		// ";"
		assignmentStatementIdentifierLeft.nodeToken1.accept(this);
	}
	
	
	@Override
	public void visit(AssignmentStatementArrayLeft assignmentStatementArrayLeft) {
		// Identifier()
		assignmentStatementArrayLeft.identifier.accept(this);
		
		// ArrayAccess()
		assignmentStatementArrayLeft.arrayAccess.accept(this);
		space();
		
		// "="
		assignmentStatementArrayLeft.nodeToken.accept(this);
		space();
		
		// Expression()
		assignmentStatementArrayLeft.expression.accept(this);
		
		// ";"
		assignmentStatementArrayLeft.nodeToken1.accept(this);
	}
	
	
	// < BRACKET_LEFT > Expression() < BRACKET_RIGHT >
	@Override
	public void visit(ArrayAccess arrayAccess) {
		// < BRACKET_LEFT >
		arrayAccess.nodeToken.accept(this);
		
		// Expression()
		arrayAccess.expression.accept(this);
		
		// < BRACKET_RIGHT >
		arrayAccess.nodeToken1.accept(this);
	}
	
	
	// "new" ConstructionCall()
	@Override
	public void visit(NewExpression newExpression) {
		// "new"
		newExpression.nodeToken.accept(this);
		space();
		
		// ConstructionCall()
		newExpression.constructionCall.accept(this);
	}
	
	
	// "int" < BRACKET_LEFT > Expression() < BRACKET_RIGHT >
	@Override
	public void visit(IntArrayConstructionCall constructionCall) {
		// "int"
		constructionCall.nodeToken.accept(this);
		
		// < BRACKET_LEFT >
		constructionCall.nodeToken1.accept(this);
		
		// Expression()
		constructionCall.expression.accept(this);
		
		// < BRACKET_RIGHT >
		constructionCall.nodeToken2.accept(this);
	}
	
	
	// Identifier() < PARENTHESIS_LEFT > <PARENTHESIS_RIGHT >
	@Override
	public void visit(ObjectConstructionCall constructionCall) {
		// Identifier()
		constructionCall.identifier.accept(this);
		
		// < PARENTHESIS_LEFT >
		constructionCall.nodeToken.accept(this);
		
		// <PARENTHESIS_RIGHT >
		constructionCall.nodeToken1.accept(this);
	}
	
	
	// UnaryOperator() Expression()
	@Override
	public void visit(UnaryExpression unaryExp) {
		// UnaryOperator()
		unaryExp.unaryOperator.accept(this);
		
		// Expression()
		unaryExp.expression.accept(this);
	}
	
	// < UNOP >
	@Override
	public void visit(UnaryOperator unOp) {
		unOp.nodeToken.accept(this);
	}
	
	
	// < PARENTHESIS_LEFT > Expression() < PARENTHESIS_RIGHT >
	@Override
	public void visit(ParenthesisExpression parExp) {
		// < PARENTHESIS_LEFT >
		parExp.nodeToken.accept(this);
		
		// Expression()
		parExp.expression.accept(this);
		
		// < PARENTHESIS_RIGHT >
		parExp.nodeToken1.accept(this);
	}
	

	@Override
	public void visit(BinaryOperator binOp) {
		space();
		binOp.nodeToken.accept(this);
		space();
	}
	
	// < BRACKET_LEFT > Expression() < BRACKET_RIGHT >
	@Override
	public void visit(ArrayCall arrayCall) {
		// < BRACKET_LEFT >
		arrayCall.nodeToken.accept(this);
		
		// Expression()
		arrayCall.expression.accept(this);
		
		// < BRACKET_RIGHT >
		arrayCall.nodeToken1.accept(this);
	}
	
	
	// < DOT > "length"
	@Override
	public void visit(DotArrayLength dotArray) {
		// < DOT >
		dotArray.nodeToken.accept(this);
		
		// "length"
		dotArray.nodeToken1.accept(this);
	}
	
	// < DOT > Identifier() < PARENTHESIS_LEFT > ( Expression() ( < COMMA > Expression() )* )? < PARENTHESIS_RIGHT >
	@Override
	public void visit(DotFunctionCall dotFct) {
		// < DOT >
		dotFct.nodeToken.accept(this);
		
		
		// Identifier()
		dotFct.identifier.accept(this);
		
		
		// < PARENTHESIS_LEFT >
		dotFct.nodeToken1.accept(this);
		
		
		// ( Expression() ( < COMMA > Expression() )* )?
		if(dotFct.nodeOptional.present()) {
			NodeSequence nodeSequence = (NodeSequence)dotFct.nodeOptional.node;
			INode exp = nodeSequence.elementAt(0);
			exp.accept(this);

			NodeListOptional exp2 = (NodeListOptional)nodeSequence.elementAt(1);
			if (exp2.present()) {
				for (int i = 0; i < exp2.size(); i++) {
					NodeSequence commaExpression = (NodeSequence) exp2.elementAt(i);
					INode comma = commaExpression.elementAt(0);
					comma.accept(this);
					space();
					INode expression33 = commaExpression.elementAt(1);
					expression33.accept(this);

				}
			}	
		}
		
		
		// < PARENTHESIS_RIGHT >
		dotFct.nodeToken2.accept(this);
	}
	
	
	// Boolean
	@Override
	public void visit(BooleanType booleanType) {
		strBuffer.append("bool");
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
	
	
	// creates a new line whose indentation = tabs spaces
	private void newLine(int spaces) {
		strBuffer.append("\n");
		for (int i = spaces; i > 0; i--)
			space();
	}
	
	
	// adds a blank space to the string buffer
	private void space() {
		strBuffer.append(" ");
	}
}
