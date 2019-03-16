package ch.unibe.scg.minijava.prettyprint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import ch.unibe.scg.javacc.MiniJavaImpl;
import ch.unibe.scg.javacc.ParseException;
import ch.unibe.scg.minijava.MiniJava;
import ch.unibe.scg.minijava.prettyprint.PrettyPrinter;

/**
 * Do not change this class, please! If you think, there is something wrong with
 * these tests, send me an email to cc-staff@iam.unibe.ch
 * 
 * @author kursjan
 *
 */
public class PrettyPrintTest {
	@Test
	public void testIdentifier() throws ParseException {
		String str = "blahBlah123";
		String expected = "blahBlah123";
		String result = prettyPrint("Identifier", str);
		assertEquals(expected, result);
	}

	@Test
	public void testType() throws ParseException {
		String str = "int";
		String expected = "int";
		String result = prettyPrint("Type", str);
		assertEquals(expected, result);
	}

	@Test
	public void testType2() throws ParseException {
		String str = "int [ ]";
		String expected = "int[]";
		String result = prettyPrint("Type", str);
		assertEquals(expected, result);
	}

	@Test
	public void testType3() throws ParseException {
		String str = "FooBar";
		String expected = "FooBar";
		String result = prettyPrint("Type", str);
		assertEquals(expected, result);
	}

	@Test
	public void testType4() throws ParseException {
		String str = "boolean";
		String expected = "bool";
		String result = prettyPrint("Type", str);
		assertEquals(expected, result);
	}

	@Test
	public void testType5() throws ParseException {
		String str = "void";
		String expected = "void";
		String result = prettyPrint("Type", str);
		assertEquals(expected, result);
	}

	@Test
	public void testVarDeclaration() throws ParseException {
		String str = "int a;";
		String expected = "int a;";
		String result = prettyPrint("VarDeclaration", str);
		assertEquals(expected, result);
	}

	@Test
	public void testVarDeclaration2() throws ParseException {
		String str = "int [] \nfooBar  ;";
		String expected = "int[] fooBar;";
		String result = prettyPrint("VarDeclaration", str);
		assertEquals(expected, result);
	}

	@Test
	public void testVarDeclaration3() throws ParseException {
		String str = "Foo \nBar \n\n;";
		String expected = "Foo Bar;";
		String result = prettyPrint("VarDeclaration", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionTrue() throws ParseException {
		String str = "true\n";
		String expected = "true";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionFalse() throws ParseException {
		String str = "\nfalse \n";
		String expected = "false";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionIdentifier() throws ParseException {
		String str = "\nFooBar";
		String expected = "FooBar";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionThis() throws ParseException {
		String str = " this ";
		String expected = "this";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionThisCall() throws ParseException {
		String str = " this . foo( ) ";
		String expected = "this.foo()";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionNew() throws ParseException {
		String str = " new   \n int [ 3]";
		String expected = "new int[3]";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionNew2() throws ParseException {
		String str = " new   \n int [ 12\n ] ";
		String expected = "new int[12]";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionNew3() throws ParseException {
		String str = " new FooBar(\n) ";
		String expected = "new FooBar()";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionNegate() throws ParseException {
		String str = " ! 3 ";
		String expected = "!3";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionNegate2() throws ParseException {
		String str = " ! true";
		String expected = "!true";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionLength() throws ParseException {
		String str = "a. length";
		String expected = "a.length";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionBrackets() throws ParseException {
		String str = "(1)";
		String expected = "(1)";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionBrackets2() throws ParseException {
		String str = "( 1 )";
		String expected = "(1)";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionBrackets3() throws ParseException {
		String str = "(\n 1\n)";
		String expected = "(1)";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionArithmetics() throws ParseException {
		String str = "1+2";
		String expected = "1 + 2";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionArithmetics2() throws ParseException {
		String str = "1* 2 -3";
		String expected = "1 * 2 - 3";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionArithmetics3() throws ParseException {
		String str = "foo>bar";
		String expected = "foo > bar";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionArithmetics4() throws ParseException {
		String str = "foo &&( bar - 3)";
		String expected = "foo && (bar - 3)";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionArrayAccess() throws ParseException {
		String str = "foo [ a ]";
		String expected = "foo[a]";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionArrayConsturctor() throws ParseException {
		String str = "new    int\n [ 12 ]";
		String expected = "new int[12]";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionMethodCall() throws ParseException {
		String str = "foo . bar( )";
		String expected = "foo.bar()";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionMethodCall2() throws ParseException {
		String str = "foo . bar( zorg )";
		String expected = "foo.bar(zorg)";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionMethodCall3() throws ParseException {
		String str = "foo . bar( zorg, qwark, bark )";
		String expected = "foo.bar(zorg, qwark, bark)";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testExpressionMethodCall4() throws ParseException {
		String str = "foo . bar(). qwark() .bark( )";
		String expected = "foo.bar().qwark().bark()";
		String result = prettyPrint("Expression", str);
		assertEquals(expected, result);
	}

	@Test
	public void testStatementAssignment() throws ParseException {
		String str = "a = 1 ;";
		String expected = "a = 1;";
		String result = prettyPrint("Statement", str);
		assertEquals(expected, result);
	}

	@Test
	public void testStatementAssignment2() throws ParseException {
		String str = "fooBar\n=(1 + 1) ;";
		String expected = "fooBar = (1 + 1);";
		String result = prettyPrint("Statement", str);
		assertEquals(expected, result);
	}

	@Test
	public void testStatementArrayAssignment() throws ParseException {
		String str = "fooBar [ 1 ]=2;";
		String expected = "fooBar[1] = 2;";
		String result = prettyPrint("Statement", str);
		assertEquals(expected, result);
	}

	@Test
	public void testStatementArrayAssignment2() throws ParseException {
		String str = "foo [ bar ]= zorg2;";
		String expected = "foo[bar] = zorg2;";
		String result = prettyPrint("Statement", str);
		assertEquals(expected, result);
	}

	@Test
	public void testStatementList() throws ParseException {
		String str = "{a = 1; b = 2;}";
		String expected = "{\n    a = 1;\n    b = 2;\n}";
		String result = prettyPrint("Statement", str);
		assertEquals(expected, result);
	}

	@Test
	public void testStatementWhile() throws ParseException {
		String str = "while ( 1)foo = 1; ";
		String expected = "while (1) foo = 1;";
		String result = prettyPrint("Statement", str);
		assertEquals(expected, result);
	}

	@Test
	public void testStatementWhile2() throws ParseException {
		String str = "while ( 1) { a = 1; foo = a; }";
		String expected = "while (1) {\n    a = 1;\n    foo = a;\n}";
		String result = prettyPrint("Statement", str);
		assertEquals(expected, result);
	}

	@Test
	public void testStatementIf() throws ParseException {
		String str = "if (true) retval = 1;  else \nretval =2;";
		String expected = "if (true) retval = 1; else retval = 2;";
		String result = prettyPrint("Statement", str);
		assertEquals(expected, result);
	}

	@Test
	public void testStatementIf2() throws ParseException {
		String str = "if (a > 1) { retval = 1; }  else retval =2;";
		String expected = "if (a > 1) {\n    retval = 1;\n} else retval = 2;";
		String result = prettyPrint("Statement", str);
		assertEquals(expected, result);
	}

	@Test
	public void testStatementSysout() throws ParseException {
		String str = "System.out.println(  123);";
		String expected = "System.out.println(123);";
		String result = prettyPrint("Statement", str);
		assertEquals(expected, result);
	}

	@Test
	public void testMetodDelcaration() throws ParseException {
		String str = "public  void  foo () { return 1 ; }";
		String expected = "public void foo() {\n    return 1;\n}";
		String result = prettyPrint("MethodDeclaration", str);
		assertEquals(expected, result);
	}

	@Test
	public void testMetodDelcaration2() throws ParseException {
		String str = "public  void  foo () " + "{" + "int a;\n" + "Foo bar; Zorg qwark;" + "a=bar.foo(a);\n"
				+ "   a = \n qwark.bar(a); return 1 ; \n" + "}";
		String expected = "public void foo() {\n" + "    int a;\n" + "    Foo bar;\n" + "    Zorg qwark;\n" + "\n"
				+ "    a = bar.foo(a);\n" + "    a = qwark.bar(a);\n" + "    return 1;\n" + "}";
		String result = prettyPrint("MethodDeclaration", str);
		assertEquals(expected, result);
	}

	@Test
	public void testMetodDelcaration3() throws ParseException {
		String str = "public  void  foo (A b , C  d,E f) " + "{" + "int a;" + "return 1 ; \n" + "}";
		String expected = "public void foo(A b, C d, E f) {\n" + "    int a;\n" + "\n" + "    return 1;\n" + "}";
		String result = prettyPrint("MethodDeclaration", str);
		assertEquals(expected, result);
	}

	@Test
	public void testClassDelcaration() throws ParseException {
		String str = "class Foo   \n\n {\n" + "\n" + "        public Bar foo() { return 1;}"
				+ "\n public Foo bar(Foo a) { return a; \n}" + "}\n";
		String expected = "class Foo {\n" + "\n" + "    public Bar foo() {\n" + "        return 1;\n" + "    }\n" + "\n"
				+ "    public Foo bar(Foo a) {\n" + "        return a;\n" + "    }\n" + "\n" + "}";
		String result = prettyPrint("ClassDeclaration", str);
		assertEquals(expected, result);
	}

	@Test
	public void testClassDelcaration2() throws ParseException {
		String str = "class Foo   \nextends Bar\n {\n" + " int a;" + " int b;" + "\n"
				+ "        public Bar foo() { return 1;}" + "\n public Foo bar(Foo a) { return a; \n}" + "}\n";
		String expected = "class Foo extends Bar {\n" + "    int a;\n" + "    int b;\n" + "\n"
				+ "    public Bar foo() {\n" + "        return 1;\n" + "    }\n" + "\n"
				+ "    public Foo bar(Foo a) {\n" + "        return a;\n" + "    }\n" + "\n" + "}";
		String result = prettyPrint("ClassDeclaration", str);
		assertEquals(expected, result);
	}

	@Test
	public void testMainClass() throws ParseException {
		String str = "class MainClass   \n\n {\n" + "\n" + "     public static void main( String [ ] args) {"
				+ "          args[0] = foo.bar();" + "     }" + "}";
		String expected = "class MainClass {\n" + "\n" + "    public static void main(String[] args) {\n"
				+ "        args[0] = foo.bar();\n" + "    }\n" + "\n" + "}";
		String result = prettyPrint("MainClass", str);
		assertEquals(expected, result);
	}

	@Test
	public void testMiniJavaProgram() throws ParseException {
		String str = "class MainClass   \n\n {\n" + "\n" + "     public static void main( String [ ] args) {"
				+ "          args[0] = foo.bar();" + "     }" + "}";
		String expected = "//Pretty Printer says Hi There!\n" + "class MainClass {\n" + "\n"
				+ "    public static void main(String[] args) {\n" + "        args[0] = foo.bar();\n" + "    }\n" + "\n"
				+ "}" + "\n" + "\n";
		String result = prettyPrint("Goal", str);
		assertEquals(expected, result);
	}

	@Test
	public void testMiniJavaProgram2() throws ParseException {
		String str = "class MainClass   \n\n {\n" + "\n" + "     public static void main( String [ ] args) {"
				+ "          args[0] = foo.bar();" + "     }" + "}" + ""
				+ "class Foo extends Bar { public void bar() { return 0; }}";
		String expected = "//Pretty Printer says Hi There!\n" + "class MainClass {\n" + "\n"
				+ "    public static void main(String[] args) {\n" + "        args[0] = foo.bar();\n" + "    }\n" + "\n"
				+ "}\n" + "\n" + "class Foo extends Bar {\n" + "\n" + "    public void bar() {\n"
				+ "        return 0;\n" + "    }\n" + "\n" + "}\n" + "\n";
		String result = prettyPrint("Goal", str);
		assertEquals(expected, result);
	}

	private String prettyPrint(String method, String str) {
		try {
			MiniJava parser = new MiniJavaImpl(getStream(str));
			Object node = parser.getClass().getMethod(method).invoke(parser);
			PrettyPrinter prettyPrinter = new PrettyPrinter();
			// And pretty print on object as well with cast in the prettyPrint method?
			String result = prettyPrinter.prettyPrint(node);
			return result;
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
