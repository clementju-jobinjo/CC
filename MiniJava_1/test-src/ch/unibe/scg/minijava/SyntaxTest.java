package ch.unibe.scg.minijava;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import ch.unibe.scg.javacc.MiniJavaImpl;
import ch.unibe.scg.javacc.ParseException;
import ch.unibe.scg.minijava.MiniJava;

public class SyntaxTest {

	@Test
	public void test() throws ParseException {
		String str = "class Main { public static void main (String [] args ) {} }\n"
				+ "class Foo { public void bar() { return 1; } }";
		MiniJava parser = parserFor(str);

		parser.Goal();
		assertTrue(true);
	}

	@Test
	public void testIf() throws ParseException {
		String str = "if (true) { } else { }";
		MiniJava parser = parserFor(str);

		parser.Statement();
		assertTrue(true);
	}

	@Test
	public void testWhile() throws ParseException {
		String str = "while (true) { } ";
		MiniJava parser = parserFor(str);

		parser.Statement();
		assertTrue(true);
	}

	@Test
	public void testSysout() throws ParseException {
		String str = "System.out.println(true);";
		MiniJava parser = parserFor(str);

		parser.Statement();
		assertTrue(true);
	}

	@Test
	public void testAssignment() throws ParseException {
		String str = "ClassA = true;";
		MiniJava parser = parserFor(str);

		parser.Statement();
		assertTrue(true);
	}

	@Test
	public void testArrayAssignment() throws ParseException {
		String str = "ClassA[123] = true;";
		MiniJava parser = parserFor(str);

		parser.Statement();
		assertTrue(true);
	}

	@Test
	public void testStatement() throws ParseException {
		String str = "{}";
		MiniJava parser = parserFor(str);

		parser.Statement();
		assertTrue(true);
	}

	@Test
	public void testStatement2() throws ParseException {
		String str = "a = a.foo();";
		MiniJava parser = parserFor(str);

		parser.Statement();
		assertTrue(true);
	}

	@Test
	public void testMethodDeclaration() throws ParseException {
		String str = "public ClassA returnClassA(ClassB b, int c) { return ClassB; }";
		MiniJava parser = parserFor(str);

		parser.MethodDeclaration();
		assertTrue(true);
	}

	@Test
	public void testType() throws ParseException {
		String str = "int";
		MiniJava parser = parserFor(str);
		parser.Type();
		assertTrue(true);
	}

	@Test
	public void testType2() throws ParseException {
		String str = "int[]";
		MiniJava parser = parserFor(str);
		parser.Type();
		assertTrue(true);
	}

	@Test
	public void testType3() throws ParseException {
		String str = "int [ ] ";
		MiniJava parser = parserFor(str);
		parser.Type();
		assertTrue(true);
	}

	@Test
	public void testId() throws ParseException {
		String str = "id";
		MiniJava parser = parserFor(str);
		parser.Identifier();
		assertTrue(true);
	}

	@Test
	public void testId2() throws ParseException {
		String str = "_id";

		MiniJava parser = parserFor(str);
		try {
			parser.Identifier();
		} catch (Throwable e) {
			assertTrue(true);
			return;
		}
		fail("Exception expected");
	}

	@Test
	public void testId3() throws ParseException {
		String str = "-id";
		MiniJava parser = parserFor(str);
		try {
			parser.Identifier();
		} catch (Throwable e) {
			assertTrue(true);
			return;
		}
		fail("Exception expected");
	}

	@Test
	public void testId4() throws ParseException {
		String str = "Bye_Bye";
		MiniJava parser = parserFor(str);
		parser.Identifier();
		assertTrue(true);
	}

	@Test
	public void testExpression() throws ParseException {
		String str = "1+2";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression2() throws ParseException {
		String str = "1 + 2 + 3 - 4 * 5";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression3() throws ParseException {
		String str = "a[1]";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression4() throws ParseException {
		String str = "a[1*b[2]]";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression5() throws ParseException {
		String str = "a[2].length";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression6() throws ParseException {
		String str = "a.foo()";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression7() throws ParseException {
		String str = "a.foo().bar()";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression8() throws ParseException {
		String str = "a.foo(a).bar(b, c)";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression9() throws ParseException {
		String str = "a.foo(a).bar(b, 2*3)";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression10() throws ParseException {
		String str = "a.foo(a).bar(b, true.zorg()*3.length)";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression11() throws ParseException {
		String str = "123";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression12() throws ParseException {
		String str = "true";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression13() throws ParseException {
		String str = "false";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression14() throws ParseException {
		String str = "idDentifier123";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression15() throws ParseException {
		String str = "new int [ a + 2 ]";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression16() throws ParseException {
		String str = "new Class()";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression17() throws ParseException {
		String str = "(1)";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression18() throws ParseException {
		String str = "((2))";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression19() throws ParseException {
		String str = "!true";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression20() throws ParseException {
		String str = "1 + 2 -";
		MiniJava parser = parserFor(str);

		try {
			parser.Expression();
		} catch (Exception e) {
			assertTrue(true);
			return;
		}
		fail("Should throw exception");
	}

	@Test
	public void testExpression21() throws ParseException {
		String str = "((1)";
		MiniJava parser = parserFor(str);

		try {
			parser.Expression();
		} catch (Exception e) {
			assertTrue(true);
			return;
		}
		fail("Should throw exception");
	}

	@Test
	public void testExpression22() throws ParseException {
		String str = "-1";
		MiniJava parser = parserFor(str);

		try {
			parser.Expression();
		} catch (Exception e) {
			assertTrue(true);
			return;
		}
		fail("Should throw exception");
	}

	@Test
	public void testExpression23() throws ParseException {
		String str = "a[1][2]";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression24() throws ParseException {
		String str = "a[1].length";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression25() throws ParseException {
		String str = "a[1].length > 123 && c";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression26() throws ParseException {
		String str = "1 < 2";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression27() throws ParseException {
		String str = "1 == 2";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression30() throws ParseException {
		String str = "foo() == bar()";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testExpression31() throws ParseException {
		String str = "retval = foo(n + 1) + bar(n + 2)";
		MiniJava parser = parserFor(str);
		parser.Expression();
		assertTrue(true);
	}

	@Test
	public void testVarDeclaration() throws ParseException {
		String str = "int a;";
		MiniJava parser = parserFor(str);
		parser.VarDeclaration();
		assertTrue(true);
	}

	@Test
	public void testVarDeclaration2() throws ParseException {
		String str = "int[] a;";
		MiniJava parser = parserFor(str);
		parser.VarDeclaration();
		assertTrue(true);
	}

	@Test
	public void testClassDeclaration() throws ParseException {
		String str = "class Foo extends Bar { } ";
		MiniJava parser = parserFor(str);
		parser.ClassDeclaration();
		assertTrue(true);
	}

	@Test
	public void testClassDeclaration2() throws ParseException {
		String str = "class Foo extends Bar { int a; public void bar () { return 1; }} ";
		MiniJava parser = parserFor(str);
		parser.ClassDeclaration();
		assertTrue(true);
	}

	@Test
	public void testProgram1() throws ParseException {
		InputStream is = loadFile("programs/prog1.minijava");
		MiniJava parser = parserFor(is);
		parser.Goal();
		assertTrue(true);
	}

	@Test
	public void testProgram2() throws ParseException {
		InputStream is = loadFile("programs/prog2.minijava");
		MiniJava parser = parserFor(is);
		parser.Goal();
		assertTrue(true);
	}

	@Test
	public void testProgram3() throws ParseException {
		InputStream is = loadFile("programs/prog3.minijava");
		MiniJava parser = parserFor(is);
		parser.Goal();
		assertTrue(true);
	}

	@Test
	public void testFibonacci() throws ParseException {
		InputStream str = loadFile("programs/Fibonacci.minijava");
		MiniJava parser = parserFor(str);

		parser.Goal();
		assertTrue(true);
	}

	@Test
	public void testField() throws ParseException {
		String str = "int[] numbers;";
		MiniJava parser = parserFor(str);

		parser.VarDeclaration();
		assertTrue(true);
	}

	private MiniJava parserFor(InputStream stream) {
		MiniJava parser = new MiniJavaImpl(stream);
		return parser;
	}

	private MiniJava parserFor(String str) {
		try {
			return parserFor(getStream(str));
		} catch (UnsupportedEncodingException e) {
			fail("Encoding problem?");
		}
		return null;

	}

	private InputStream getStream(String str) throws UnsupportedEncodingException {
		return new ByteArrayInputStream(str.getBytes("UTF-8"));

	}

	private InputStream loadFile(String name) {
		return SyntaxTest.class.getResourceAsStream(name);
	}

}
