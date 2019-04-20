package ch.unibe.scg.minijava.bcel.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import ch.unibe.scg.minijava.MiniJavaTypes;

public class MiniJavaRuntimeSupport {

	private ReloadableClassLoader classLoader = new ReloadableClassLoader(getClass().getClassLoader());
	private List<String> loadedClasses = new ArrayList<String>();

	public MiniJavaRuntimeSupport() {
	}

	private void newClassLoader() {
		this.classLoader = new ReloadableClassLoader(getClass().getClassLoader());
	}

	public Object evaluateExpression(ClassGen cg, InstructionList instructions, int miniJavaType) {
		newClassLoader();
		String methodName = "run";

		try {
			appendReturn(instructions, miniJavaType);
			addMethod(methodName, miniJavaType, cg, instructions);
			createClassFile(cg);

			Object clazzInstance = instantiate(cg.getClassName());
			java.lang.reflect.Method runMethod = clazzInstance.getClass().getMethod(methodName);
			Object retval = runMethod.invoke(clazzInstance);
			return retval;

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return null;
	}

	public Object evaluateMethod(ClassGen cg, Method m, Object... args) {
		newClassLoader();
		String methodName = m.getName();

		try {
			createClassFile(cg);

			Class<?>[] types = collectArgTypes(args);

			Object clazzInstance = instantiate(cg.getClassName());
			java.lang.reflect.Method runMethod = clazzInstance.getClass().getMethod(methodName, types);
			Object retval = runMethod.invoke(clazzInstance, args);
			return retval;

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return null;
	}

	public Class<?>[] collectArgTypes(Object... args) {
		Class<?>[] types = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			// WTF what kind of hack I had to use to get
			// primitive type of Integer!!!!
			if (args[i].getClass() == Integer.class) {
				types[i] = Integer.TYPE;
			} else if (args[i].getClass() == Boolean.class) {
				types[i] = Boolean.TYPE;
			} else {
				types[i] = args[i].getClass();
			}
		}
		return types;
	}

	public void cleanClassDir() {
		File dir = new File("bin/");
		if (dir != null && dir.list() != null) {
			for (String fileName : dir.list()) {
				File myFile = new File(dir, fileName);
				myFile.delete();
			}
		}
	}

	public Type miniJavaToBcelType(int miniJavaType) {
		switch (miniJavaType) {
		case MiniJavaTypes.INT:
			return Type.INT;
		case MiniJavaTypes.BOOL:
			return Type.BOOLEAN;
		case MiniJavaTypes.ARRAY:
			return new ArrayType(Constants.T_INT, 1);
//            return Type.OBJECT;
		default:
			new IllegalArgumentException("Unsupported MiniJava type");
			return null;
		}
	}

	public Object instantiate(String className) {
		try {
			Class<?> aClass = loadClass(className);
			System.out.println("Class loaded: aClass.getName() = " + aClass.getName() + " hash: " + aClass.hashCode());
			Object aClassInstance = aClass.newInstance();
			return aClassInstance;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	private void addMethod(String methodName, int retType, ClassGen cg, InstructionList il) {
		Type rt = miniJavaToBcelType(retType);

		MethodGen mg = new MethodGen(Constants.ACC_PUBLIC, rt, new Type[] {}, new String[] {}, methodName,
				cg.getClassName(), il, cg.getConstantPool());

		mg.setMaxStack();
		mg.setMaxLocals();
		Method m = mg.getMethod();
		cg.addMethod(m);
	}

	private void createClassFile(ClassGen cg) throws IOException {
		JavaClass jclass = cg.getJavaClass();

		jclass.dump("bin/" + cg.getClassName().replaceAll("\\.", "/") + ".class");
	}

	private void appendReturn(InstructionList instructions, int miniJavaType) {
		instructions.append(InstructionFactory.createReturn(miniJavaToBcelType(miniJavaType)));
	}

	private Class<?> loadClass(String name) throws ClassNotFoundException {
		if (loadedClasses.contains(name)) {
			loadedClasses.add(name);
			return classLoader.loadClass(name);
		}
		return classLoader.reloadClass(name);
	}
}
