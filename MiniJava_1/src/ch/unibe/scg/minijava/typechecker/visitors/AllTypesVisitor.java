package ch.unibe.scg.minijava.typechecker.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.unibe.scg.javacc.syntaxtree.ClassDeclaration;
import ch.unibe.scg.javacc.syntaxtree.Goal;
import ch.unibe.scg.javacc.syntaxtree.INode;
import ch.unibe.scg.javacc.syntaxtree.Identifier;
import ch.unibe.scg.javacc.syntaxtree.MainClass;
import ch.unibe.scg.javacc.syntaxtree.NodeListOptional;
import ch.unibe.scg.javacc.syntaxtree.NodeOptional;
import ch.unibe.scg.javacc.syntaxtree.NodeSequence;
import ch.unibe.scg.javacc.syntaxtree.NodeToken;
import ch.unibe.scg.javacc.visitor.DepthFirstVoidVisitor;
import ch.unibe.scg.minijava.typechecker.types.RootObject;

public class AllTypesVisitor extends DepthFirstVoidVisitor {

	List<String> classNames;
	Map<String, String> inheritances;

	public AllTypesVisitor() {
		super();
		classNames = new ArrayList<String>();
		inheritances = new HashMap<String, String>();
	}

	public List<String> getClassNames(){
		return classNames;
	}

	public Map<String, String> getInheritances(){
		return inheritances;
	}

	// Goal
	// MainClass() ( ClassDeclaration() )* < EOF >
	@Override
	public void visit(Goal goal) {

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
	}


	// Mainclass
	//"class" Identifier() "{" "public" "static" "void" "main" "(" "String" "[" "]" Identifier() ")" "{" ( Statement() )? "}" "}"
	@Override
	public void visit(MainClass mainClass) {
		
		// Identifier()
		Identifier identifier = mainClass.identifier;
		
		// main class extends from object
		inheritances.put(identifier.nodeToken.tokenImage, RootObject.RootObjectSingleton.getTypeName());
			
	}


	// ClassDeclaration
	// "class" Identifier() ( "extends" Identifier() )? "{" ( VarDeclaration() )* ( MethodDeclaration() )* "}"
	@Override
	public void visit(ClassDeclaration classDeclaration) {

		// Identifier()
		Identifier identifier = classDeclaration.identifier;

		// ( "extends" Identifier() )?
		NodeOptional nodeOptional = classDeclaration.nodeOptional;
		if (nodeOptional.present()) {
			NodeSequence nodeSequence = (NodeSequence) nodeOptional.node;

			// Identifier
			INode nodeIdentifier = nodeSequence.elementAt(1);
			Identifier id = (Identifier)nodeIdentifier;
			inheritances.put(identifier.nodeToken.tokenImage, id.nodeToken.tokenImage);
		}
		else {
			// main class extends from object
			inheritances.put(identifier.nodeToken.tokenImage, RootObject.RootObjectSingleton.getTypeName());
		}

	}
}
