package ch.unibe.scg.minijava.bcel.generator.optimization;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import ch.unibe.scg.javacc.MiniJavaImpl;
import ch.unibe.scg.minijava.MiniJava;
import ch.unibe.scg.minijava.bcel.generator.JavaBytecodeGenerator;

public class BytecodesSizeTest {

	private static final String PATH = "../../../programs/";

	int size;

	public static void main(String[] args) {
		new BytecodesSizeTest().reportOnBytecodeSize();
	}

	@Before
	public void setUp() {
		size = Integer.MAX_VALUE;
	}

	@Test
	public void testFibonacci() {
		size = getSize(PATH + "Fibonacci.minijava");
		assertTrue(size <= 35);
	}

	@Test
	public void testFactorial() {
		size = getSize(PATH + "Factorial.minijava");
		assertTrue(size <= 33);
	}

	@Test
	public void testBinarySearch() {
		size = getSize(PATH + "BinarySearch.minijava");
		assertTrue(size <= 261);
	}

	@Test
	public void testScaling() {
		size = getSize(PATH + "Scaling.minijava");
		assertTrue(size <= 72);
	}

	public void reportOnBytecodeSize() {
		reportOn(PATH + "Fibonacci.minijava");
		reportOn(PATH + "Factorial.minijava");
		reportOn(PATH + "BinarySearch.minijava");
		reportOn(PATH + "Scaling.minijava");
	}

	private int getSize(String name) {
		try {
			InputStream input;
			JavaBytecodeGenerator jbcg;

			input = loadFile(name);
			jbcg = compile("Goal", input);
			return jbcg.getInstructionCount();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return Integer.MAX_VALUE;
		}
	}

	private void reportOn(String name) {
		try {
			InputStream input;
			JavaBytecodeGenerator jbcg;

			input = loadFile(name);
			jbcg = compile("Goal", input);
			System.out.println(name + " size: " + jbcg.getInstructionCount());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private JavaBytecodeGenerator compile(String rule, InputStream input) {
		JavaBytecodeGenerator jbcg = new JavaBytecodeGenerator();
		Object node = getAst(rule, input);
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

	private InputStream loadFile(String name) throws FileNotFoundException {
		InputStream retval = BytecodesSizeTest.class.getResourceAsStream(name);

		if (retval == null) {
			throw new FileNotFoundException(name + " not found");
		}
		return retval;
	}

}
