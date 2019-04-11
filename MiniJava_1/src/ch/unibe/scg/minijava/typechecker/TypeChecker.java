/**
 * 
 */
package ch.unibe.scg.minijava.typechecker;

import java.util.List;

import ch.unibe.scg.javacc.syntaxtree.INode;
import ch.unibe.scg.minijava.typechecker.scopes.*;
import ch.unibe.scg.minijava.typechecker.types.*;

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
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	private List<Scope> createAllScopes(INode n) {
		return null;
	}

}
