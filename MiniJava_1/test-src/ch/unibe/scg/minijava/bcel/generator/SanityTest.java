package ch.unibe.scg.minijava.bcel.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.unibe.scg.javacc.MiniJavaImpl;
import ch.unibe.scg.javacc.ParseException;
import ch.unibe.scg.minijava.MiniJava;
import ch.unibe.scg.minijava.bcel.generator.JavaBytecodeGenerator;

public class SanityTest {
	private static final String PATH = "../../programs/";
	private MiniJavaRuntimeSupport testSupport;

	@Test
	public void testScaling() throws ParseException {
		InputStream input = loadFile(PATH + "Scaling.minijava");

		compile("Goal", input);
		Object instance = instantiate("SC");

		int expected = 1609 * 2;
		Object result = callMethod(instance, "DoubleMile");
		assertEquals(expected, result);

		expected = 22;
		result = callMethod(instance, "Double", 11);
		assertEquals(expected, result);

		expected = 45;
		result = callMethod(instance, "Scale", 15, 3);
		assertEquals(expected, result);
	}

	@Test
	public void testFibonacci() throws ParseException {
		InputStream input = loadFile(PATH + "Fibonacci.minijava");

		compile("Goal", input);
		Object instance = instantiate("Fib");

		int expected = 8;
		Object result = callMethod(instance, "Fibonacci", 5);
		assertEquals(expected, result);

		expected = 144;
		result = callMethod(instance, "Fibonacci", 11);
		assertEquals(expected, result);

		expected = 987;
		result = callMethod(instance, "Fibonacci", 15);
		assertEquals(expected, result);
	}

	@Test
	public void testFactorial() throws ParseException {
		InputStream input = loadFile(PATH + "Factorial.minijava");

		compile("Goal", input);

		Object instance = instantiate("Fac");

		int expected = 120;
		Object result = callMethod(instance, "ComputeFac", 5);
		assertEquals(expected, result);

		expected = 720;
		result = callMethod(instance, "ComputeFac", 6);
		assertEquals(expected, result);

	}

	@Test
	public void testBinarySearch() throws ParseException {
		InputStream input = loadFile(PATH + "BinarySearch.minijava");

		compile("Goal", input);

		Object instance = instantiate("BS");

		boolean expected = true;
		Object result = callMethod(instance, "Start", 20, 20);
		System.out.println("ICI1"+result);
		assertEquals(expected, result);

		expected = true;
		result = callMethod(instance, "Start", 20, 37);
		System.out.println("ICI2"+result);
		assertEquals(expected, result);

		expected = false;
		result = callMethod(instance, "Start", 20, 38);
		System.out.println("ICI3"+result);
		assertEquals(expected, result);

		expected = false;
		result = callMethod(instance, "Start", 20, 17);
		assertEquals(expected, result);
	}

	private InputStream loadFile(String name) {
		return SanityTest.class.getResourceAsStream(name);
	}

	@Before
	public void setUp() {
		testSupport = new MiniJavaRuntimeSupport();
	}

	@After
	public void tearDown() {

	}

	private JavaBytecodeGenerator compile(String rule, InputStream input) {
		JavaBytecodeGenerator jbcg = new JavaBytecodeGenerator();
		Object node = getAst(rule, input);
		System.out.println(node.toString());
		jbcg.generate(node);
		return jbcg;
	}

	private Object getAst(String method, InputStream str) {
		try {
			MiniJava parser = new MiniJavaImpl(str);
			Object node = parser.getClass().getMethod(method).invoke(parser);
			return node;
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.toString());
		}
		return null;
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

	private Object instantiate(String className) {
		return testSupport.instantiate(className);
	}
}
