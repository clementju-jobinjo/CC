package ch.unibe.scg;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.unibe.scg.minijava.SyntaxTest;
import ch.unibe.scg.minijava.bcel.generator.JavaBytecodeGeneratorTest;
import ch.unibe.scg.minijava.bcel.generator.SanityTest;
import ch.unibe.scg.minijava.bcel.generator.optimization.BytecodesSizeTest;
import ch.unibe.scg.minijava.prettyprint.PrettyPrintTest;
import ch.unibe.scg.typechecker.minijava.TypeCheckerTest;

@RunWith(Suite.class)
@SuiteClasses({ SyntaxTest.class, PrettyPrintTest.class, TypeCheckerTest.class, JavaBytecodeGeneratorTest.class,
		SanityTest.class, BytecodesSizeTest.class })
public class AllTests {

}
