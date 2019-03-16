package ch.unibe.scg.minijava;

import ch.unibe.scg.javacc.ParseException;

/**
 * Please, do not change this interface. Let me know, if you need to change it
 * by sending email to cc-staff@iam.unibe.ch
 * 
 * @author kursjan
 *
 */
public interface MiniJava {

	public Object Goal() throws ParseException;

	public Object MainClass() throws ParseException;

	public Object ClassDeclaration() throws ParseException;

	public Object VarDeclaration() throws ParseException;

	public Object MethodDeclaration() throws ParseException;

	public Object Type() throws ParseException;

	public Object Statement() throws ParseException;

	public Object Expression() throws ParseException;

	public Object Identifier() throws ParseException;
}
