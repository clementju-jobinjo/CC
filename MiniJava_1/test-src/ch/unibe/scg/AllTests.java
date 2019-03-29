package ch.unibe.scg;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.unibe.scg.minijava.SyntaxTest;
import ch.unibe.scg.minijava.prettyprint.PrettyPrintTest;
import ch.unibe.scg.minijava.typechecker.TypeCheckerTest;

@RunWith(Suite.class)
@SuiteClasses({ PrettyPrintTest.class, SyntaxTest.class, TypeCheckerTest.class })
public class AllTests {

}
