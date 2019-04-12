/**
 * 
 */
package ch.unibe.scg.minijava.typechecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.unibe.scg.javacc.syntaxtree.INode;
import ch.unibe.scg.minijava.typechecker.scopes.*;
import ch.unibe.scg.minijava.typechecker.types.RootObject;
import ch.unibe.scg.minijava.typechecker.types.Type;
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
			Map<String, Type> stringToTypes = createAllTypes(n);
			
//			for (Map.Entry<String, Type> in : stringToTypes.entrySet()) {
//				System.out.println(in.getKey() + " -> " + ((in.getValue().getParentType() == null) ? "null" : in.getValue().getParentType().getTypeName()));
//			}
			List<Scope> allScopes = createAllScopes(n, stringToTypes);
			
			for (Scope s : allScopes) {
				System.out.println(s.toString());
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private Map<String, Type> createAllTypes(INode n) {
		AllTypesVisitor visitor = new AllTypesVisitor();
		n.accept(visitor);
		// List<String> classNames = visitor.getClassNames();
		Map<String, String> inheritances = visitor.getInheritances();
		
//		for (Map.Entry<String, String> in : inheritances.entrySet()) {
//			System.out.println(in);
//		}
		
		// creation of a map <ClassName, TypeObject>, required during the visit
		Map<String, Type> stringToType = createClassObjects(inheritances);
		
		return stringToType;
	}
	
	
	private List<Scope> createAllScopes(INode n, Map<String, Type> stringToType) throws Exception {
		Scope globalScope = new Scope(null, n);
		
		// add primitive types
		globalScope.addClass(RootObject.RootObjectSingleton);
		globalScope.addClass(Boolean.BooleanSingleton);
		globalScope.addClass(Int.IntSingleton);
		globalScope.addClass(new IntArray());
		
		AllScopesBuilder visitor = new AllScopesBuilder(globalScope, stringToType);
		n.accept(visitor);
		List<Scope> allScopes = visitor.getAllScopes();
		
		return allScopes;
	}
	
	private Map<String, Type> createClassObjects(Map<String, String> inheritances){
		
		List<String> classNamesToRemove = new ArrayList<String>();
		List<String> classNames = new ArrayList<String>(inheritances.keySet());
		
		Map<String, Type> stringToType = new HashMap<String, Type>();
		stringToType.put("Object", RootObject.RootObjectSingleton);
		
		
		while (!classNames.isEmpty()) {
			for (int i = 0; i < classNames.size(); i++) {
				
				String currentClass = classNames.get(i);
				String dependency = inheritances.get(currentClass);
				
				if (dependency.equals("Object")) {
					stringToType.put(currentClass, new Type(currentClass, RootObject.RootObjectSingleton));
					classNamesToRemove.add(currentClass);
				}
				else if(stringToType.containsKey(dependency)) {
					Type parentClass = stringToType.get(dependency);
					stringToType.put(currentClass, new Type(currentClass, parentClass));
					classNamesToRemove.add(currentClass);
				}
			}
			
			for (String cl : classNamesToRemove) {
				classNames.remove(cl);
			}
		}
		
		return stringToType;
	}

}
