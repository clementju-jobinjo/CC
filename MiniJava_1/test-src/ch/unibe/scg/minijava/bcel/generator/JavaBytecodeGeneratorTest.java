package ch.unibe.scg.minijava.bcel.generator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.InstructionList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.unibe.scg.javacc.MiniJavaImpl;
import ch.unibe.scg.javacc.ParseException;
import ch.unibe.scg.minijava.MiniJava;
import ch.unibe.scg.minijava.MiniJavaTypes;
import ch.unibe.scg.minijava.bcel.generator.JavaBytecodeGenerator;

public class JavaBytecodeGeneratorTest {
	private MiniJavaRuntimeSupport testSupport;

	@Test
	public void testConstant1() throws ParseException {
		String str = "1";
		int expected = 1;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);
	}

	@Test
	public void testConstant2() throws ParseException {
		String str = "2";
		int expected = 2;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);

	}

	@Test
	public void testConstant3() throws ParseException {
		String str = "1234";
		int expected = 1234;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);

	}

	@Test
	public void testBoolean() throws ParseException {
		String str = "true";
		boolean expected = true;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testBoolean2() throws ParseException {
		String str = "false";
		boolean expected = false;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics() throws ParseException {
		String str = "1 + 2";
		int expected = 3;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics2() throws ParseException {
		String str = "1 + 2 + 3";
		int expected = 6;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics3() throws ParseException {
		String str = "1 - 2";
		int expected = -1;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics4() throws ParseException {
		String str = "1 - 2 + 3";
		int expected = 2;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics5() throws ParseException {
		String str = "2 * 3";
		int expected = 6;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics6() throws ParseException {
		String str = "2 * 3 - 1";
		int expected = 5;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics6b() throws ParseException {
		String str = "1 + 2 * 3";
		int expected = 7;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics7() throws ParseException {
		String str = "2 + 3 - 1 + 3 - 4";
		int expected = 3;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics8() throws ParseException {
		String str = "2 > 2";
		boolean expected = false;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics9() throws ParseException {
		String str = "3 > 2";
		boolean expected = true;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics10() throws ParseException {
		String str = "true && false";
		boolean expected = false;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics11() throws ParseException {
		String str = "false && false";
		boolean expected = false;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics12() throws ParseException {
		String str = "false && true";
		boolean expected = false;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics13() throws ParseException {
		String str = "true && true";
		boolean expected = true;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics14() throws ParseException {
		String str = "!true";
		boolean expected = false;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics15() throws ParseException {
		String str = "!false";
		boolean expected = true;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics16() throws ParseException {
		String str = "1 + 4 > 3 && !false ";
		boolean expected = true;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics17() throws ParseException {
		String str = "(1 + 2) * 3";
		int expected = 9;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics18() throws ParseException {
		String str = "1 + (2 * 3)";
		int expected = 7;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics19() throws ParseException {
		String str = "3 < 2";
		boolean expected = false;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics20() throws ParseException {
		String str = "2 < 3";
		boolean expected = true;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics21() throws ParseException {
		String str = "2 == 2";
		boolean expected = true;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics22() throws ParseException {
		String str = "2 == 3";
		boolean expected = false;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics23() throws ParseException {
		String str = "false && false";
		boolean expected = false;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArithmetics24() throws ParseException {
		String str = "!false && false";
		boolean expected = false;
		Object result = evaluateExpression(str, MiniJavaTypes.BOOL);
		assertEquals(expected, result);

	}

	@Test
	public void testArray() throws ParseException {
		String str = "new int[2]";
		int[] expected = new int[2];
		int[] result = (int[]) evaluateExpression(str, MiniJavaTypes.ARRAY);
		assertArrayEquals(expected, result);

	}

	@Test
	public void testArray2() throws ParseException {
		String str = "new int[2 * 2]";
		int[] expected = new int[4];
		int[] result = (int[]) evaluateExpression(str, MiniJavaTypes.ARRAY);
		assertArrayEquals(expected, result);
	}

	@Test
	public void testArrayLen() throws ParseException {
		String str = "new int[6].length";
		int expected = 6;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);
	}

	@Test
	public void testArrayLen2() throws ParseException {
		String str = "new int[2 + 3].length";
		int expected = 5;
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);
	}

	@Test
	public void testArrayAccess() throws ParseException {
		String str = "new int[2 + 3][0]";
		int expected = 0; // default java behaviour
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);
	}

	@Test
	public void testArrayAccess2() throws ParseException {
		String str = "new int[2 + 3][4]";
		int expected = 0; // default java behaviour
		Object result = evaluateExpression(str, MiniJavaTypes.INT);
		assertEquals(expected, result);
	}

	@Test
	public void testVariable() throws ParseException {
		String str = "public int foo() { int a; a = 123; return a; }";
		int expected = 123;
		Object result = evaluateMethod(str);
		assertEquals(expected, result);
	}

	@Test
	public void testVariable2() throws ParseException {
		String str = "public int foo() { int a; int b; a = 123; b = 23; return a - b; }";
		int expected = 100;
		Object result = evaluateMethod(str);
		assertEquals(expected, result);
	}

	@Test
	public void testVariable3() throws ParseException {
		String str = "public boolean foo() { boolean a; a = false; return a; }";
		boolean expected = false;
		Object result = evaluateMethod(str);
		assertEquals(expected, result);
	}

	@Test
	public void testVariable4() throws ParseException {
		String str = "public boolean foo() { boolean a; a = true && true; return a; }";
		boolean expected = true;
		Object result = evaluateMethod(str);
		assertEquals(expected, result);
	}

	@Test
	public void testVariable5() throws ParseException {
		String str = "public int foo() { " + "int a; int b; int c; int d; " + "a = 1; b = 1; c = 1; d =1; "
				+ "return a + b + c + d; }";
		int expected = 4;
		Object result = evaluateMethod(str);
		assertEquals(expected, result);
	}

	@Test
	public void testMethod() throws ParseException {
		String str = "public int foo() { return 1; }";
		int expected = 1;
		Object result = evaluateMethod(str);
		assertEquals(expected, result);
	}

	@Test
	public void testMethod2() throws ParseException {
		String str = "public int foo() { return 1 + 3; }";
		int expected = 4;
		Object result = evaluateMethod(str);
		assertEquals(expected, result);
	}

	@Test
	public void testMethodArgument() throws ParseException {
		String str = "public int foo(int a) { return a; }";
		int expected = 1;
		Object result = evaluateMethod(str, 1);
		assertEquals(expected, result);
	}

	@Test
	public void testMethodArgument2() throws ParseException {
		String str = "public int foo(int a) { return a; }";
		int expected = 123;
		Object result = evaluateMethod(str, 123);
		assertEquals(expected, result);
	}

	@Test
	public void testMethodArgument3() throws ParseException {
		String str = "public boolean foo(boolean a) { return a; }";
		boolean expected = true;
		Object result = evaluateMethod(str, true);
		assertEquals(expected, result);
	}

	@Test
	public void testMethodArgument4() throws ParseException {
		String str = "public boolean foo(boolean a, int b) { return a && (b > 2); }";
		boolean expected = false;
		Object result = evaluateMethod(str, true, 2);
		assertEquals(expected, result);
	}

	@Test
	public void testMethodArgument5() throws ParseException {
		String str = "public boolean foo(boolean a, int b) { return a && (b > 2); }";
		boolean expected = false;
		Object result = evaluateMethod(str, false, 3);
		assertEquals(expected, result);
	}

	@Test
	public void testMethodArgument6() throws ParseException {
		String str = "public boolean foo(boolean a, int b) { return a && (b > 2); }";
		boolean expected = true;
		Object result = evaluateMethod(str, true, 3);
		assertEquals(expected, result);
	}

	@Test
	public void testIf() throws ParseException {
		String str = "public boolean foo(boolean a) {" + "  boolean retval; "
				+ "  if (a) { retval = true; } else { retval = false ;}" + "  return retval;" + "}";
		boolean expected = true;
		Object result = evaluateMethod(str, true);
		assertEquals(expected, result);

		expected = false;
		result = evaluateMethod(str, false);
		assertEquals(expected, result);
	}

	@Test
	public void testIf2() throws ParseException {
		String str = "public boolean foo(boolean a) {" + "  boolean retval; "
				+ "  if (a) { retval = false; } else { retval = true ;}" + "  return retval;" + "}";
		boolean expected = false;
		Object result = evaluateMethod(str, true);
		assertEquals(expected, result);

		expected = true;
		result = evaluateMethod(str, false);
		assertEquals(expected, result);
	}

	@Test
	public void testIf3() throws ParseException {
		String str = "public boolean foo(int a) {" + "  boolean retval; "
				+ "  if (a < 1) { retval = true; } else { retval = false ;}" + "  return retval;" + "}";
		boolean expected = false;
		Object result = evaluateMethod(str, 2);
		assertEquals(expected, result);

		expected = true;
		result = evaluateMethod(str, 0);
		assertEquals(expected, result);
	}

	@Test
	public void testWhile() throws ParseException {
		String str = "public int foo(int i) {" + "  int j; " + "  j = 0;" + "  while (i > 0) { j = j + 10; i = i - 1; }"
				+ "  return j;" + "}";
		int expected = 10;
		Object result = evaluateMethod(str, 1);
		assertEquals(expected, result);

		expected = 100;
		result = evaluateMethod(str, 10);
		assertEquals(expected, result);
	}

	@Test
	public void testSysout() throws ParseException {
		String str = "public int bar(int i) {" + "  System.out.println(i); " + "  return i;" + "}";

		int expected = 9999;
		Object result = evaluateMethod(str, 9999);
		assertEquals(expected, result);
		// hard to test, I just beleive you
	}

	@Test
	public void testArrayAssignment() throws ParseException {
		String str = "public int bar() {" + "  int[] array;" + "  array = new int[1];" + "  array[0] = 23;"
				+ "  return array[0];" + "}";

		int expected = 23;
		Object result = evaluateMethod(str);
		assertEquals(expected, result);
	}

	@Test
	public void testArrayAssignment2() throws ParseException {
		String str = "public int bar(int i, int index) {" + "  int[] array;" + "  array = new int[3];"
				+ "  array[0] = i;" + "  array[1] = i+1; " + "  array[2] = i+2; " + "  return array[index];" + "}";

		int expected = 10;
		Object result = evaluateMethod(str, 10, 0);
		assertEquals(expected, result);

		expected = 11;
		result = evaluateMethod(str, 10, 1);
		assertEquals(expected, result);

		expected = 12;
		result = evaluateMethod(str, 10, 2);
		assertEquals(expected, result);
	}

	@Test
	public void testClass() throws ParseException {
		String str = "class Foo {" + "  public int bar(int retval) { return retval; }" + "}";

		int expected = 10;
		compile("ClassDeclaration", str);

		Object instance = instantiate("Foo");
		Object result = callMethod(instance, "bar", 10);

		assertEquals(expected, result);

	}

	@Test
	public void testBytecodeSize() throws ParseException {
		String str = "class Foo {" + "  public int bar() { return 0; }" + "}";

		int expected = 2;
		JavaBytecodeGenerator jbcg = compile("ClassDeclaration", str);
		assertEquals(expected, jbcg.getInstructionCount());

	}

	@Test
	public void testBytecodeSize2() throws ParseException {
		String str = "class Foo {" + "  public int bar() { return 0; }" + "  public int baz() { return 0; }" + "}";

		int expected = 4;
		JavaBytecodeGenerator jbcg = compile("ClassDeclaration", str);
		assertEquals(expected, jbcg.getInstructionCount());
	}

	@Test
	public void testBytecodeSize3() throws ParseException {
		String str = "class Foo {" + "  public Object bar() { return new Object(); }"
				+ "  public int baz() { return 0; }" + "}";

		int expected = 6;
		JavaBytecodeGenerator jbcg = compile("ClassDeclaration", str);
		assertEquals(expected, jbcg.getInstructionCount());
	}

	@Test
	public void testClass2() throws ParseException {
		String str = "class Foo {" + "  public int bar(int retval) { return retval; }"
				+ "  public int baz(int retval) { return retval + 1; }"
				+ "  public boolean bag(int retval) { return retval > 5; }" + "}";

		int expected = 10;
		compile("ClassDeclaration", str);

		Object instance = instantiate("Foo");
		Object result = callMethod(instance, "bar", 10);
		assertEquals(expected, result);

		expected = 11;
		result = callMethod(instance, "baz", 10);
		assertEquals(expected, result);

		result = callMethod(instance, "bag", 10);
		assertEquals(true, result);

		result = callMethod(instance, "bag", 1);
		assertEquals(false, result);
	}

	@Test
	public void testMethodCall() throws ParseException {
		String str = "class Foo {" + "  public int bar() { return 121; }"
				+ "  public int baz() { int i; i = this.bar(); return 123; }" + "}";

		int expected = 121;
		compile("ClassDeclaration", str);

		Object instance = instantiate("Foo");
		Object result = callMethod(instance, "bar");
		assertEquals(expected, result);

		expected = 123;
		result = callMethod(instance, "baz");
		assertEquals(expected, result);
	}

	@Test
	public void testMethodCall2() throws ParseException {
		String str = "class Foo {" + "  public int bar() { return 121; }" + "  public int baz() { return this.bar();}"
				+ "}";

		int expected = 121;
		compile("ClassDeclaration", str);

		Object instance = instantiate("Foo");
		Object result = callMethod(instance, "bar");
		assertEquals(expected, result);

		result = callMethod(instance, "baz");
		assertEquals(expected, result);
	}

	@Test
	public void testMethodCall3() throws ParseException {
		String str = "class Foo {" + "  public int bar(int i) { return i; }"
				+ "  public int baz() { return this.bar(1223);}" + "}";

		int expected = 1221;
		compile("ClassDeclaration", str);

		Object instance = instantiate("Foo");
		Object result = callMethod(instance, "bar", 1221);
		assertEquals(expected, result);

		expected = 1223;
		result = callMethod(instance, "baz");
		assertEquals(expected, result);
	}

	@Test
	public void testMethodCall4() throws ParseException {
		String str = "class Foo {" + "  public int bar(int i) { return i; }"
				+ "  public int baz(int j) { return this.bar(j);}" + "}";

		int expected = 1221;
		compile("ClassDeclaration", str);

		Object instance = instantiate("Foo");
		Object result = callMethod(instance, "bar", 1221);
		assertEquals(expected, result);

		expected = 1225;
		result = callMethod(instance, "baz", 1225);
		assertEquals(expected, result);
	}

	@Test
	public void testField() throws ParseException {
		String str = "class Foo {" + "  int bar;" + "  public int bar() { bar = 321; return bar; }"
				+ "  public int baz() { int tmp; tmp = this.bar(); return bar; }" + "}";

		int expected = 321;
		compile("ClassDeclaration", str);

		Object instance = instantiate("Foo");
		Object result = callMethod(instance, "bar");
		assertEquals(expected, result);

		expected = 321;
		result = callMethod(instance, "baz");
		assertEquals(expected, result);
	}

	@Test
	public void testField2() throws ParseException {
		String str = "class Foo {" + "  int bar;" + "  int baz;"
				+ "  public int bar() { bar = 321; baz = 123; return bar + baz; }"
				+ "  public int baz() { baz = this.bar(); return bar; }"
				+ "  public int bang() { baz = this.bar(); return baz; }" + "}";

		int expected = 321 + 123;
		compile("ClassDeclaration", str);

		Object instance = instantiate("Foo");
		Object result = callMethod(instance, "bar");
		assertEquals(expected, result);

		expected = 321;
		result = callMethod(instance, "baz");
		assertEquals(expected, result);

		expected = 321 + 123;
		result = callMethod(instance, "bang");
		assertEquals(expected, result);
	}

	@Test
	public void testField3() throws ParseException {
		String str = "class Foo {"
//                + "  int sz;"
				+ "  int[] numbers;" + "  public int Init() { "
//                + "    int[] numbers;"
//                + "    sz = 1;"
				+ "    numbers = new int[1]; " + "    numbers[0] = 2; " + "    return numbers[0]; " + "  }" + "}";

		int expected = 2;
		compile("ClassDeclaration", str);

		Object instance = instantiate("Foo");
		Object result = callMethod(instance, "Init");
		assertEquals(expected, result);

	}

	@Test
	public void testConstructor() throws ParseException {
		String str = "class Foo {" + "  public int baz() { return 1234; }"
				+ "  public int bar() { Foo f; f = new Foo(); return f.baz(); }" + "}";

		int expected = 1234;
		compile("ClassDeclaration", str);

		Object instance = instantiate("Foo");
		Object result = callMethod(instance, "bar");
		assertEquals(expected, result);
	}

	@Test
	public void testConstructor2() throws ParseException {
		String str = "class Foo {" + "  public int baz() { return 1234; }"
				+ "  public int bar() { return new Foo().baz(); }" + "}";

		int expected = 1234;
		compile("ClassDeclaration", str);

		Object instance = instantiate("Foo");
		Object result = callMethod(instance, "bar");
		assertEquals(expected, result);
	}

	@Test
	public void testConstructor3() throws ParseException {
		String str = "class Foo {" + "  public Foo foo() { return new Foo(); }" + "  public int baz() { return 12345; }"
				+ "  public int bar() { return this.foo().baz(); }" + "}";

		int expected = 12345;
		compile("ClassDeclaration", str);

		Object instance = instantiate("Foo");
		Object result = callMethod(instance, "bar");
		assertEquals(expected, result);
	}

	@Test
	public void testConstructor4() throws ParseException {
		String str = "class Foo {" + "  public Foo foo() { return new Foo(); }" + "  public int baz() { return 12345; }"
				+ "  public int bar() { return new Foo().foo().baz(); }" + "}";

		int expected = 12345;
		compile("ClassDeclaration", str);

		Object instance = instantiate("Foo");
		Object result = callMethod(instance, "bar");
		assertEquals(expected, result);
	}

	@Test
	public void testProgram() throws ParseException {
		String str = "class Main { public static void main(String[] args) { } } " + "class Foo {"
				+ "  public Bar bar() { return new Bar(); }" + "}" + "" + "class Bar {"
				+ "  public int baz() { return 678; }" + "}" + "class Baz {"
				+ "  public int baq() { return new Foo().bar().baz(); }" + "}";

		int expected = 678;
		compile("Goal", str);

		Object instance = instantiate("Baz");
		Object result = callMethod(instance, "baq");
		assertEquals(expected, result);
	}

	@Before
	public void setUp() {
		testSupport = new MiniJavaRuntimeSupport();
	}

	@After
	public void tearDown() {

	}

	private Object evaluateExpression(String input, int resultType) {
		JavaBytecodeGenerator jbcg = compile("Expression", input);

		ClassGen cg = jbcg.getClassGen();
		InstructionList il = jbcg.getInstructionList();

		return testSupport.evaluateExpression(cg, il, resultType);
	}

	private Object evaluateMethod(String input, Object... args) {
		JavaBytecodeGenerator jbcg = compile("MethodDeclaration", input);

		ClassGen cg = jbcg.getClassGen();
		Method m = jbcg.getMethod();

		return testSupport.evaluateMethod(cg, m, args);
	}

	private Object callMethod(Object receiver, String name, Object... args) {
		try {
			java.lang.reflect.Method m = receiver.getClass().getMethod(name, testSupport.collectArgTypes(args));
			return m.invoke(receiver, args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	private JavaBytecodeGenerator compile(String rule, String input) {
		JavaBytecodeGenerator jbcg = new JavaBytecodeGenerator();
		Object node = getAst(rule, input);
		jbcg.generate(node);
		return jbcg;
	}

	private Object instantiate(String className) {
		return testSupport.instantiate(className);
	}

	private Object getAst(String method, String str) {
		try {
			MiniJava parser = new MiniJavaImpl(getStream(str));
			Object node = parser.getClass().getMethod(method).invoke(parser);
			return node;
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.toString());
		}
		return null;
	}

	private InputStream getStream(String str) throws UnsupportedEncodingException {
		return new ByteArrayInputStream(str.getBytes("UTF-8"));

	}

}
