/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
/* Generated By:JJTree: Do not edit this line. ASTProgram.java */
/* JJT: 0.3pre1 */

package Mini;
import java.io.PrintWriter;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.PUTSTATIC;
import org.apache.bcel.generic.RETURN;
import org.apache.bcel.generic.Type;

/**
 * Root node of everything, direct children are nodes of type FunDecl
 *
 * @version $Id$
 */
public class ASTProgram extends SimpleNode
implements MiniParserConstants, MiniParserTreeConstants, org.apache.bcel.Constants {
  private ASTFunDecl[] fun_decls; // Children: Function declarations
  private Environment  env;       // Environment contains variables and functions

  ASTProgram(int id) {
    super(id);

    env = new Environment();

    /* Add predefined functions WRITE/READ.
     * WRITE has one arg of type T_INT, both return T_INT.
     */
    ASTIdent   ident  = new ASTIdent("WRITE", T_INT, -1, -1);
    ASTIdent[] args   = { new ASTIdent("", T_INT, -1, -1) }; 
    Function   fun    = new Function(ident, args, true);
    env.put(fun);

    ident = new ASTIdent("READ", T_INT, -1, -1);
    args  = new ASTIdent[0];
    fun   = new Function(ident, args, true);
    env.put(fun);
    
    /* Add predefined idents TRUE/FALSE of type T_BOOLEAN
     */
    ident = new ASTIdent("TRUE", T_BOOLEAN, -1, -1);
    Variable var = new Variable(ident, true);
    env.put(var);

    ident = new ASTIdent("FALSE", T_BOOLEAN, -1, -1);
    var   = new Variable(ident, true);
    env.put(var);
  }

  ASTProgram(MiniParser p, int id) {
    super(p, id);
  }

  public static Node jjtCreate(MiniParser p, int id) {
    return new ASTProgram(p, id);
  }

  /**
   * Overrides SimpleNode.closeNode().
   * Cast children to appropiate type.
   */
  @Override
  public void closeNode() {
    if(children != null) { // Non-empty program ?
      fun_decls = new ASTFunDecl[children.length];
      System.arraycopy(children, 0, fun_decls, 0, children.length);
      children=null; // Throw away old reference
    }
  }

  /**
   * First pass of parse tree.
   *
   * Put everything into the environment, which is copied appropiately to each
   * recursion level, i.e. each FunDecl gets its own copy that it can further
   * manipulate. 
   *
   * Checks for name clashes of function declarations.
   */
  public ASTProgram traverse() {
    ASTFunDecl f;
    ASTIdent   name;
    String     fname;
    EnvEntry   fun;
    Function   main=null;

    if(fun_decls != null) {
      // Put function names into hash table aka. environment
      for(int i=0; i < fun_decls.length; i++) {
        f     = fun_decls[i];
        name  = f.getName();
        fname = name.getName();
        fun   = env.get(fname); // Lookup in env
        
        if(fun != null) {
        MiniC.addError(f.getLine(), f.getColumn(),
                         "Redeclaration of " + fun + ".");
    } else {
        env.put(new Function(name, null)); // `args' will be set by FunDecl.traverse()
    }

        
      }

      // Go for it
      for(int i=0; i < fun_decls.length; i++) {
        fun_decls[i] = fun_decls[i].traverse((Environment)env.clone());
        
        // Look for `main' routine
        fname = fun_decls[i].getName().getName();
        if(fname.equals("main")) {
        main = (Function)env.get(fname);
    }
      }
      
      if(main == null) {
        MiniC.addError(0, 0, "You didn't declare a `main' function.");
    } else if(main.getNoArgs() != 0) {
        MiniC.addError(main.getLine(), main.getColumn(), 
                        "Main function has too many arguments declared.");
    }
    }

    return this;
  }

  /** 
   * Second pass, determine type of each node, if possible.
   */
  public void eval(int pass) {

    for(int i=0; i < fun_decls.length; i++) {
      fun_decls[i].eval(pass);

      if(pass == 3) { // Final check for unresolved types
        ASTIdent name = fun_decls[i].getName();

        if(name.getType() == T_UNKNOWN) {
        MiniC.addError(name.getColumn(), name.getLine(),
                         "Type of function " + name.getName() +
                         " can not be determined (infinite recursion?).");
    }
      }
    }
  }

  /**
   * Fifth pass, produce Java code.
   */
  public void code(PrintWriter out, String name) {
    out.println("import java.io.BufferedReader;");
    out.println("import java.io.InputStreamReader;");
    out.println("import java.io.IOException;\n");

    out.println("public final class " + name + " {");
    out.println("  private static BufferedReader _in = new BufferedReader" + 
                "(new InputStreamReader(System.in));\n");

    out.println("  private static int _readInt() throws IOException {\n" +
                "    System.out.print(\"Please enter a number> \");\n" + 
                "    return Integer.parseInt(_in.readLine());\n  }\n");

    out.println("  private static int _writeInt(int n) {\n" +
                "    System.out.println(\"Result: \" + n);\n    return 0;\n  }\n");

    for(int i=0; i < fun_decls.length; i++) {
        fun_decls[i].code(out);
    }

    out.println("}");
  }

  /**
   * Fifth pass, produce Java byte code.
   */
  public void byte_code(ClassGen class_gen, ConstantPoolGen cp) {
    /* private static BufferedReader _in;
     */
    class_gen.addField(new Field(ACC_PRIVATE | ACC_STATIC,
                                 cp.addUtf8("_in"),
                                 cp.addUtf8("Ljava/io/BufferedReader;"),
                                 null, cp.getConstantPool()));

    MethodGen       method;
    InstructionList il = new InstructionList();
    String          class_name = class_gen.getClassName();

    /* Often used constant pool entries
     */
    int             _in = cp.addFieldref(class_name, "_in", "Ljava/io/BufferedReader;");

    int             out = cp.addFieldref("java.lang.System", "out",
                                         "Ljava/io/PrintStream;");

    il.append(new GETSTATIC(out));
    il.append(new PUSH(cp, "Please enter a number> "));
    il.append(new INVOKEVIRTUAL(cp.addMethodref("java.io.PrintStream",
                                                "print",
                                                "(Ljava/lang/String;)V")));
    il.append(new GETSTATIC(_in));
    il.append(new INVOKEVIRTUAL(cp.addMethodref("java.io.BufferedReader",
                                                "readLine",
                                                "()Ljava/lang/String;")));
    il.append(new INVOKESTATIC(cp.addMethodref("java.lang.Integer",
                                                "parseInt",
                                                "(Ljava/lang/String;)I")));
    il.append(InstructionConstants.IRETURN);

    /* private static int _readInt() throws IOException
     */
    method = new MethodGen(ACC_STATIC | ACC_PRIVATE | ACC_FINAL,
                           Type.INT, Type.NO_ARGS, null,
                           "_readInt", class_name, il, cp);

    method.addException("java.io.IOException");

    method.setMaxStack(2);
    class_gen.addMethod(method.getMethod());

    /* private static int _writeInt(int i) throws IOException
     */
    Type[]   args = { Type.INT };
    String[] argv = { "i" } ;
    il = new InstructionList();
    il.append(new GETSTATIC(out));
    il.append(new NEW(cp.addClass("java.lang.StringBuffer")));
    il.append(InstructionConstants.DUP);
    il.append(new PUSH(cp, "Result: "));
    il.append(new INVOKESPECIAL(cp.addMethodref("java.lang.StringBuffer",
                                                "<init>",
                                                "(Ljava/lang/String;)V")));

    il.append(new ILOAD(0));
    il.append(new INVOKEVIRTUAL(cp.addMethodref("java.lang.StringBuffer",
                                                "append",
                                                "(I)Ljava/lang/StringBuffer;")));

    il.append(new INVOKEVIRTUAL(cp.addMethodref("java.lang.StringBuffer",
                                                "toString",
                                                "()Ljava/lang/String;")));
    
    il.append(new INVOKEVIRTUAL(cp.addMethodref("java.io.PrintStream",
                                                "println",
                                                "(Ljava/lang/String;)V")));
    il.append(new PUSH(cp, 0));
    il.append(InstructionConstants.IRETURN); // Reuse objects, if possible

    method = new MethodGen(ACC_STATIC | ACC_PRIVATE | ACC_FINAL,
                           Type.INT, args, argv,
                           "_writeInt", class_name, il, cp);

    method.setMaxStack(4);
    class_gen.addMethod(method.getMethod());

    /* public <init> -- constructor
     */
    il.dispose(); // Dispose instruction handles for better memory utilization

    il = new InstructionList();
    il.append(new ALOAD(0)); // Push `this'
    il.append(new INVOKESPECIAL(cp.addMethodref("java.lang.Object",
                                                "<init>", "()V")));
    il.append(new RETURN());

    method = new MethodGen(ACC_PUBLIC, Type.VOID, Type.NO_ARGS, null,
                           "<init>", class_name, il, cp);
    
    method.setMaxStack(1);
    class_gen.addMethod(method.getMethod());

    /* class initializer
     */
    il.dispose(); // Dispose instruction handles for better memory utilization
    il = new InstructionList();
    il.append(new NEW(cp.addClass("java.io.BufferedReader")));
    il.append(InstructionConstants.DUP);
    il.append(new NEW(cp.addClass("java.io.InputStreamReader")));
    il.append(InstructionConstants.DUP);
    il.append(new GETSTATIC(cp.addFieldref("java.lang.System", "in",
                                           "Ljava/io/InputStream;")));
    il.append(new INVOKESPECIAL(cp.addMethodref("java.io.InputStreamReader",
                                                "<init>", "(Ljava/io/InputStream;)V")));
    il.append(new INVOKESPECIAL(cp.addMethodref("java.io.BufferedReader",
                                                "<init>", "(Ljava/io/Reader;)V")));
    il.append(new PUTSTATIC(_in));
    il.append(InstructionConstants.RETURN); // Reuse instruction constants

    method = new MethodGen(ACC_STATIC, Type.VOID, Type.NO_ARGS, null,
                           "<clinit>", class_name, il, cp);

    method.setMaxStack(5);
    class_gen.addMethod(method.getMethod());

    for(int i=0; i < fun_decls.length; i++) {
        fun_decls[i].byte_code(class_gen, cp);
    }
  }

  @Override
  public void dump(String prefix) {
    System.out.println(toString(prefix));

    for(int i = 0; i < fun_decls.length; ++i) {
        fun_decls[i].dump(prefix + " ");
    }
  }
}
