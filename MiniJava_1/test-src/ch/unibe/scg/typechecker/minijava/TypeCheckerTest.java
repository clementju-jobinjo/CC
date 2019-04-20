package ch.unibe.scg.typechecker.minijava;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import ch.unibe.scg.javacc.MiniJavaImpl;
import ch.unibe.scg.minijava.MiniJava;
import ch.unibe.scg.minijava.typechecker.TypeChecker;

public class TypeCheckerTest {

	@Test
	public void testSimpleArithmetics1() {
		String str = "1 + 2";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testSimpleArithmeitcs2() {
		String str = "1 + true";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testSimpleArithmetics3() {
		String str = "(1 > 2)";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testSimpleArithmetics4() {
		String str = "(1 > false)";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testSimpleArithmetics5() {
		String str = "1 && false";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testSimpleArithmetics6() {
		String str = "1 * 2";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testSimpleArithmetics7() {
		String str = "true * 2";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testSimpleArithmetics8() {
		String str = "false - 2";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testSimpleArithmetics9() {
		String str = "12 - 2";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testSimpleArithmetics10() {
		String str = "12 && 2";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testSimpleArithmetics11() {
		String str = "true && false";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testSimpleArithmetics12() {
		String str = "!true";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testSimpleArithmetics13() {
		String str = "!false";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testSimpleArithmetics14() {
		String str = "!123";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testArithmetics1() {
		String str = "true && false - 1";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testArithmetics2() {
		String str = "true && false && (1 > 2)";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testArithmetics3() {
		String str = "true && false * (1 > 2)";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testArithmetics4() {
		String str = "true && (1 > 2)";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testArithmetics5() {
		String str = "!true && false";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testArithmetics6() {
		String str = "!true - 3";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testArithmetics7() {
		String str = "!true && !(1 > 3)";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testArithmetics8() {
		String str = "!true && !(1 * 3)";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testArithmetics9() {
		String str = "1 + 1 + 1 + 1 + 1 + 1 + true + 1";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testArithmetics9b() {
		String str = "false && false && false &&  1 + 2 + 3 > 1";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testArithmetics10() {
		String str = "true && 2 > 3";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testArithmetics11() {
		String str = "1 > 2 && true";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testArray() {
		String str = "new int[12].length";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testArray2() {
		String str = "new int[12] + 1";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testArray3() {
		String str = "new int[12] && true";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testArray4() {
		String str = "1 > new int[12]";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testArray5() {
		String str = "1 + (new int[12] [1])";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testArray6() {
		String str = "1 + new int[12] [1].length";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testArray7() {
		String str = "true [1]";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testArray8() {
		String str = "new int [true]";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testArray9() {
		String str = "new int [1 + 2]";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testArray10() {
		String str = "new int [1 + 2 > 3]";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testLength() {
		String str = "new int[12].length + 1";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testLength2() {
		String str = "new int[12].length && true";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testLength3() {
		String str = "3 > (new int[12].length)";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testLength4() {
		String str = "true && (new int[12].length)";
		assertFalse(typeCheck("Expression", str));
	}

	@Test
	public void testLength5() {
		String str = "3 > new int[12].length";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testLength6() {
		String str = "3 + new int[12].length";
		assertTrue(typeCheck("Expression", str));
	}

	@Test
	public void testId() {
		String str = "public int foo() {int a; a = a + 1; return 0;}";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testId2() {
		String str = "public int foo() {int a; a = a + false; return 0;}";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodRetval() {
		String str = "public int foo() { return 0; }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodRetval1() {
		String str = "public int foo() { return true; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodRetval2() {
		String str = "public int foo(int a, int b) { return true; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodRetval3() {
		String str = "public int foo(int a, int b) { return a; }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodRetval4() {
		String str = "public int foo(int a, boolean b) { return b; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodRetval5() {
		String str = "public int[] foo() { int[] i; return i; }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodRetval6() {
		String str = "public void foo() { int i; return i; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodParam() {
		String str = "public int foo(int a, int b) { return a * b; }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodParam2() {
		String str = "public int foo(int a, int b) { return a && b; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodParam3() {
		String str = "public boolean foo(int a, int b) { return a > b; }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodParam4() {
		String str = "public boolean foo(int a, int b) { return a + b; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodParam5() {
		String str = "public boolean foo(int a, int b) { return (a + b); }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodParam6() {
		String str = "public boolean foo(int a, int b) { return (a > b); }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodParam7() {
		String str = "public boolean foo(int a, int b) { return (1 + a > b); }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodParam8() {
		String str = "public boolean foo(int a, int b) { return (1 + (a > b)); }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodParam9() {
		String str = "public boolean foo(int a, int b) { return (1 > a + b); }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodParam10() {
		String str = "public boolean foo(int a, int b) { return a > new int[b].length; }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodParam11() {
		String str = "public int foo(int a, int b) { return a > new int[b].length; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodParam12() {
		String str = "public boolean foo(int a, int b) { return a > new int[b][a]; }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodScope() {
		String str = "public boolean foo(int a) { boolean a; a = true; return a; }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodScope2() {
		String str = "public boolean foo(int a) { boolean a; a = 1; return a; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodScope3() {
		String str = "public int foo(int a, int b) { boolean b; a = 1; return b; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testMethodScope4() {
		String str = "public boolean foo(int a, int b, boolean c) { boolean a; boolean b; int c; a = b; return c; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testClass() {
		String str = "class Foo { public int bar() { return 1;} }";
		assertTrue(typeCheck("ClassDeclaration", str));
	}

	@Test
	public void testClass2() {
		String str = "class Foo { public int bar() { return true;} }";
		assertFalse(typeCheck("ClassDeclaration", str));
	}

	@Test
	public void testClassVariable() {
		String str = "class Foo { int i; public int bar() { return i;} }";
		assertTrue(typeCheck("ClassDeclaration", str));
	}

	@Test
	public void testClassId() {
		String str = "class Foo { public Foo bar() { Foo a; return a;} }";
		assertTrue(typeCheck("ClassDeclaration", str));
	}

	@Test
	public void testClassId2() {
		String str = "class Main { public static void main(String[] args) { } }" + "class Bar { }"
				+ "class Foo { public Bar bar() { Bar a; return a;} }";
		assertTrue(typeCheck("Goal", str));
	}

	@Test
	public void testClassId3() {
		String str = "class Main { public static void main(String[] args) { } }" + "class Bar { }"
				+ "class Foo { public Bar bar() { Foo a; return a;} }";
		assertFalse(typeCheck("Goal", str));
	}

	@Test
	public void testClassId4() {
		String str = "class Foo { public Foo bar() { return new Foo();} }";
		assertTrue(typeCheck("ClassDeclaration", str));
	}

	@Test
	public void testClassId5() {
		String str = "class Main { public static void main(String[] args) { } }" + "class Bar { }"
				+ "class Foo { public Bar bar() { return new Bar();} }";
		assertTrue(typeCheck("Goal", str));
	}

	@Test
	public void testClassId6() {
		String str = "class Main { public static void main(String[] args) { } }" + "class Bar { }"
				+ "class Foo { public Bar bar() { return new Foo();} }";
		assertFalse(typeCheck("Goal", str));
	}

	@Test
	public void testScoping() {
		String str = "class Main { " + "  int a; " + "  public boolean foo() { " + "    boolean a; " + "    return a;"
				+ "  }  " + "}";
		assertTrue(typeCheck("ClassDeclaration", str));
	}

	@Test
	public void testScoping2() {
		String str = "class Main { " + "  int a; " + "  public int foo() { " + "    boolean a; " + "    return a;"
				+ "  }  " + "}";
		assertFalse(typeCheck("ClassDeclaration", str));
	}

	@Test
	public void testThis() {
		String str = "class Foo { public Foo bar() { return this;} }";
		assertTrue(typeCheck("ClassDeclaration", str));
	}

	@Test
	public void testThis2() {
		String str = "class Main { public static void main(String[] args) { } }" + "class Bar {  }"
				+ "class Foo { public Bar bar() { return this;} }";
		assertFalse(typeCheck("Goal", str));
	}

	@Test
	public void testThis3() {
		String str = "class Main { public static void main(String[] args) { } }" + "class Bar {  }"
				+ "class Foo { public int bar() { return 1;} public int foo() { return this.bar(); }}";
		assertTrue(typeCheck("Goal", str));
	}

	@Test
	public void testThis4() {
		String str = "class Main { public static void main(String[] args) { } }" + "class Bar {  }"
				+ "class Foo { public int bar() { return 1;} public boolean foo() { return this.bar(); }}";
		assertFalse(typeCheck("Goal", str));
	}

	@Test
	public void testClassSuperclassLookup() {
		String str = "class Main { public static void main(String[] args) { } }"
				+ "class Foo { public int bar() { return 1;} }"
				+ "class Bar extends Foo { public int foo() { return this.bar(); } }";
		assertTrue(typeCheck("Goal", str));
	}

	@Test
	public void testClassSuperclassLookup2() {
		String str = "class Main { public static void main(String[] args) { } }"
				+ "class Foo { public int bar() { return 1;} }"
				+ "class Bar { public int foo() { return this.bar(); } }";
		assertFalse(typeCheck("Goal", str));
	}

	@Test
	public void testClassSuperclassFieldLookup() {
		String str = "class Main { public static void main(String[] args) { } }" + "class Foo { Foo varA; }"
				+ "class Bar extends Foo { public Foo foo() { return varA; } }";
		assertTrue(typeCheck("Goal", str));
	}

	@Test
	public void testClassSuperclassFieldLookup2() {
		String str = "class Main { public static void main(String[] args) { } }" + "class Foo { Foo varA; }"
				+ "class Bar extends Foo { int varA; public Foo foo() { return varA; } }";
		assertFalse(typeCheck("Goal", str));
	}

	@Test
	public void testClassSuperclassFieldLookup3() {
		String str = "class Main { public static void main(String[] args) { } }" + "class Foo { Foo varA; }"
				+ "class Bar extends Foo { int varA; public int foo() { return varA; } }";
		assertTrue(typeCheck("Goal", str));
	}

	@Test
	public void testMethodCall() {
		String str = "class Main { public static void main(String[] args) { } }"
				+ "class Bar { public int zorg() { return 1; } }"
				+ "class Foo { public int bar() { return new Bar().zorg(); } }";
		assertTrue(typeCheck("Goal", str));
	}

	@Test
	public void testMethodCall2() {
		String str = "class Main { public static void main(String[] args) { } }"
				+ "class Bar { public int zorg() { return 1; } }"
				+ "class Foo { public int bar() { return new Bar().zorg2(); } }";
		assertFalse(typeCheck("Goal", str));
	}

	@Test
	public void testMethodCall3() {
		String str = "class Main { public static void main(String[] args) { } }"
				+ "class Bar { public int zorg() { return 1; } }"
				+ "class Foo { public boolean bar() { return new Bar().zorg(); } }";
		assertFalse(typeCheck("Goal", str));
	}

	@Test
	public void testMethodCall4() {
		String str = "class Main { public static void main(String[] args) { } }"
				+ "class Bar { public int zorg(int a) { return a; } }"
				+ "class Foo { public boolean bar() { return new Bar().zorg(1); } }";
		assertFalse(typeCheck("Goal", str));
	}

	@Test
	public void testMethodCall5() {
		String str = "class Main { public static void main(String[] args) { } }"
				+ "class Bar { public int zorg(int a) { return a; } }"
				+ "class Foo { public int bar() { return new Bar().zorg(1); } }";
		assertTrue(typeCheck("Goal", str));
	}

	@Test
	public void testMethodCall6() {
		String str = "class Main { public static void main(String[] args) { } }"
				+ "class Bar { public int zorg(int a) { return a; } }"
				+ "class Foo { public boolean bar() { return new Bar().zorg(false); } }";
		assertFalse(typeCheck("Goal", str));
	}

	@Test
	public void testMethodCall7() {
		String str = "class Main { public static void main(String[] args) { } }"
				+ "class Bar { public boolean zorg(int a, boolean b) { return b; } }"
				+ "class Foo { public boolean bar() { return new Bar().zorg(1, false); } }";
		assertTrue(typeCheck("Goal", str));
	}

	@Test
	public void testMethodCall8() {
		String str = "class Main { public static void main(String[] args) { } }"
				+ "class Bar { public int zorg(int a, boolean b) { return a; } }"
				+ "class Foo { public boolean bar() { return new Bar().zorg(false, 1); } }";
		assertFalse(typeCheck("Goal", str));
	}

	@Test
	public void testMethodCall9() {
		String str = "class Main { public static void main(String[] args) { } }"
				+ "class Bar { public int zorg(int a, int b) { return a; } }"
				+ "class Foo { public boolean bar() { return new Bar().zorg(1, 2, 3); } }";
		assertFalse(typeCheck("Goal", str));
	}

	@Test
	public void testMethodCall10() {
		String str = "class Main { public static void main(String[] args) { } }"
				+ "class Bar { public int zorg(int a, int b) { return a; } }"
				+ "class Foo { public boolean bar() { return new Bar().zorg(1); } }";
		assertFalse(typeCheck("Goal", str));
	}

	@Test
	public void testMethodCall11() {
		String str = "class Main { public static void main(String[] args) { } }" + "class Foo { "
				+ "   public Foo foo() { return new Foo(); } " + "   public int zorg() { return 1; }"
				+ "   public int bar() { return new Foo().foo().zorg(); } }";
		assertTrue(typeCheck("Goal", str));
	}

	@Test
	public void testArrayAssignment() {
		String str = "public int foo() { int[] i; i = new int[10]; i [1] = 1; return i[1]; }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testArrayAssignment2() {
		String str = "public int foo() { int[] i; i = new int[10]; i [true] = 1; return i[1]; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testArrayAssignment3() {
		String str = "public int foo() { int[] i; i = new int[10]; i [1] = false; return i[1]; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testArrayAssignment4() {
		String str = "public int foo() { int[] i; i = true; i [1] = false; return i[1]; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testIf() {
		String str = "public int foo() { int i; if (true) i = 1;  else i = 2; return i; }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testIf2() {
		String str = "public int foo() { int i; if (1) i = 1;  else i = 2; return i; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testWhile() {
		String str = "public int foo() { int i; i = 0; while (10 > i) { i = i + 1; } return i; }";
		assertTrue(typeCheck("MethodDeclaration", str));
	}

	@Test
	public void testWhile2() {
		String str = "public int foo2() { int i; i = 0; while (i) { i = i + 1; } return i; }";
		assertFalse(typeCheck("MethodDeclaration", str));
	}

	private boolean typeCheck(String method, String str) {
		TypeChecker tc = new TypeChecker();
		Object node = getAst(method, str);
		return tc.check(node);
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
