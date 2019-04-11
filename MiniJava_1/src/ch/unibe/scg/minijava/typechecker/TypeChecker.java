/**
 * 
 */
package ch.unibe.scg.minijava.typechecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.unibe.scg.javacc.syntaxtree.INode;
import ch.unibe.scg.minijava.typechecker.scopes.*;
import ch.unibe.scg.minijava.typechecker.types.RootObject;
import ch.unibe.scg.minijava.typechecker.types.Boolean;
import ch.unibe.scg.minijava.typechecker.types.Int;
import ch.unibe.scg.minijava.typechecker.types.IntArray;
import ch.unibe.scg.minijava.typechecker.visitors.AllScopesBuilder;
import ch.unibe.scg.minijava.typechecker.visitors.AllTypesVisitor;

/**
 * Change at will.
 * 
 * @author kursjan
 *
 */
public class TypeChecker {

	public boolean check(Object node) {
		INode n = (INode) node;
		
		try {
			Map<String, String> inheritances = createAllTypes(n);
			List<Scope> allScopes = createAllScopes(n, inheritances);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private Map<String, String> createAllTypes(INode n) {
		AllTypesVisitor visitor = new AllTypesVisitor();
		n.accept(visitor);
		// List<String> classNames = visitor.getClassNames();
		Map<String, String> inheritances = visitor.getInheritances();
		
		for (Map.Entry<String, String> in : inheritances.entrySet()) {
			System.out.println(in);
		}
		
		return inheritances;
	}
	
	
	private List<Scope> createAllScopes(INode n, Map<String, String> inheritances) throws Exception {
		Scope globalScope = new Scope(null);
		
		// add primitive types
		globalScope.addClass(RootObject.RootObjectSingleton);
		globalScope.addClass(Boolean.BooleanSingleton);
		globalScope.addClass(Int.IntSingleton);
		globalScope.addClass(new IntArray());
		
		AllScopesBuilder visitor = new AllScopesBuilder(globalScope, inheritances);
		n.accept(visitor);
		List<Scope> allScopes = visitor.getAllScopes();
		
		return allScopes;
	}

}
