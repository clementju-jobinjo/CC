package ch.unibe.scg.minijava.typechecker.visitors;

import java.util.Deque;
import java.util.LinkedList;
import ch.unibe.scg.minijava.typechecker.types.Boolean;
import ch.unibe.scg.minijava.typechecker.types.Int;

public class PostfixExpressionConstructor {
	
	private String precedence;
	
	public PostfixExpressionConstructor() {
		precedence = "==&&><!+-*/";
	}

	
	// check precedence between two operators 
    private boolean hasHigherPrecedence(String operator1, String operator2) {
    	return (precedence.indexOf(operator2) >= precedence.indexOf(operator1));
    }
    
    
    // returns the corresponding postfix expression (reverse polish notation) corresponding to the given infix expression
    // algorithm adapted from https://eddmann.com/posts/shunting-yard-implementation-in-java/
    public String postfix(String infixExpression) {
        StringBuilder postfixExpression = new StringBuilder();
        Deque<String> stack  = new LinkedList<>();

        for (String token : infixExpression.split("\\s")) {
            // operator
            if (precedence.contains(token)) {
                while ( ! stack.isEmpty() && hasHigherPrecedence(token, stack.peek())) {
                	postfixExpression.append(stack.pop()).append(' ');
                }
                stack.push(token);
            } 
            // left parenthesis
            else if (token.equals("(")) {
                stack.push(token);
            }
            // right parenthesis
            else if (token.equals(")")) {
                while ( ! stack.peek().equals("(")) {
                	postfixExpression.append(stack.pop()).append(' ');
                }
                stack.pop();
            } 
            // digit
            else {
            	postfixExpression.append(token).append(' ');
            }
        }

        while ( ! stack.isEmpty()) {
        	postfixExpression.append(stack.pop()).append(' ');
        }
        	
        return postfixExpression.toString();
    }
    
    // returns the resulting type (String) of an expression
    public String evaluatePostfix(String postfixExpression) {
    	
    	String[] tokens = postfixExpression.trim().split("\\s+");
    	Deque<String> stack  = new LinkedList<>();
    	
    	 	
    	for (String token : tokens) {
    		// token is a type
    		if (!precedence.contains(token)) {
    			stack.push(token);
    		}
    		// token is an operator
    		else {
    			String op = token;
    			
    			switch(op) {
    				case "!":
    					String s = stack.pop();
    					if (s.equals(Boolean.BooleanSingleton.getTypeName())) {
    						stack.push(Boolean.BooleanSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException("Trying to apply a negation on a non boolean.");
    					}
    					break;
    				
    				case "&&":
    					String s1 = stack.pop();
    					String s2 = stack.pop();
    					if (s1.equals(Boolean.BooleanSingleton.getTypeName()) && s2.equals(Boolean.BooleanSingleton.getTypeName())) {
    						stack.push(Boolean.BooleanSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException("&& operator can only be applied to two booleans.");
    					}
    					break;
    				
    				case ">":
    					String s3 = stack.pop();
    					String s4 = stack.pop();
    					if (s3.equals(Int.IntSingleton.getTypeName()) && s4.equals(Int.IntSingleton.getTypeName())) {
    						stack.push(Boolean.BooleanSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException("Comparison only possible between two integers.");
    					}
    					break;
    					
    				case "<":
    					String s5 = stack.pop();
    					String s6 = stack.pop();
    					if (s5.equals(Int.IntSingleton.getTypeName()) && s6.equals(Int.IntSingleton.getTypeName())) {
    						stack.push(Int.IntSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException("Comparison only possible between two integers.");
    					}
    					break;
    					
    				case "+":
    					String s7 = stack.pop();
    					String s8 = stack.pop();
    					if (s7.equals(Int.IntSingleton.getTypeName()) && s8.equals(Int.IntSingleton.getTypeName())) {
    						stack.push(Int.IntSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException("Addition only possible between two integers.");
    					}
    					break;
    				
    				case "-":
    					String s9 = stack.pop();
    					String s10 = stack.pop();
    					if (s9.equals(Int.IntSingleton.getTypeName()) && s10.equals(Int.IntSingleton.getTypeName())) {
    						stack.push(Int.IntSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException("Substraction only possible between two integers.");
    					}
    					break;
    				
    				case "*":
    					String s11 = stack.pop();
    					String s12 = stack.pop();
    					if (s11.equals(Int.IntSingleton.getTypeName()) && s12.equals(Int.IntSingleton.getTypeName())) {
    						stack.push(Int.IntSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException("Multiplication only possible between two integers.");
    					}
    					break;
    					
    				case "/":
    					String s13 = stack.pop();
    					String s14 = stack.pop();
    					if (s13.equals(Int.IntSingleton.getTypeName()) && s14.equals(Int.IntSingleton.getTypeName())) {
    						stack.push(Int.IntSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException("Division only possible between two integers.");
    					}
    					break;
    					
    				default:
    					throw new RuntimeException();
    			}
    		}
    	}
    	
    	String resultingType = stack.pop();
    	
    	if (!stack.isEmpty()) {
    		throw new RuntimeException("Expression error. Verify your expression.");
    	}
    	
    	return resultingType;
    }
    
 // returns the resulting type (String) of an expression
    public String evaluatePostfixValue(String postfixExpression) {
    	
    	String[] tokens = postfixExpression.trim().split("\\s+");
    	Deque<String> stack  = new LinkedList<>();
    	
    	 	
    	for (String token : tokens) {
    		// token is a type
    		if (!precedence.contains(token)) {
    			stack.push(token);
    		}
    		// token is an operator
    		else {
    			String op = token;
    			
    			switch(op) {
    				case "!":
    					String s = stack.pop();
    					if (s.equals("true")) {
    						stack.push("false");
    					}
    					else if (s.equals("false")){
    						stack.push("true");
    					}
    					else {
    						throw new RuntimeException("Trying to apply a negation on a non boolean.");
    					}
    					break;
    				
    				case "&&":
    					java.lang.Boolean s1 = java.lang.Boolean.parseBoolean(stack.pop());
    					java.lang.Boolean s2 = java.lang.Boolean.parseBoolean(stack.pop());
    					java.lang.Boolean res = s1 && s2;
    					
    					stack.push(res.toString());
    					
    					break;
    				
    				case ">":
    					int s3 = Integer.parseInt(stack.pop());
    					int s4 = Integer.parseInt(stack.pop());
    					
    					java.lang.Boolean res2 = s4 > s3;
    					
    					stack.push(res2.toString());

    					break;
    					
    				case "<":
    					int s5 = Integer.parseInt(stack.pop());
    					int s6 = Integer.parseInt(stack.pop());
    					
    					java.lang.Boolean res3 = s6 < s5;
    					
    					stack.push(res3.toString());
    					
    					break;
    					
    				case "+":
    					Integer s7 = Integer.parseInt(stack.pop());
    					Integer s8 = Integer.parseInt(stack.pop());
    					
    					Integer res4 = s7 + s8;
    					
    					stack.push(res4.toString());
    					
    					break;
    				
    				case "-":
    					Integer s9 = Integer.parseInt(stack.pop());
    					Integer s10 = Integer.parseInt(stack.pop());
    					
    					Integer res5 = s10 - s9;
    					
    					stack.push(res5.toString());
    					
    					break;
    				
    				case "*":
    					Integer s11 = Integer.parseInt(stack.pop());
    					Integer s12 = Integer.parseInt(stack.pop());
    					
    					Integer res6 = s11 * s12;
    					
    					stack.push(res6.toString());
    					
    					break;
    					
    				case "/":
    					Integer s13 = Integer.parseInt(stack.pop());
    					Integer s14 = Integer.parseInt(stack.pop());
    					
    					if (s13 == 0) {
    						throw new RuntimeException("Division by zero.");
    					}
    					
    					Integer res7 = s14 / s13;
    					
    					stack.push(res7.toString());
    					
    					break;
    					
    				case "==":
    					Integer s15 = Integer.parseInt(stack.pop());
    					Integer s16 = Integer.parseInt(stack.pop());
    					java.lang.Boolean res8 = s15 == s16;
    					
    					stack.push(res8.toString());
    					
    					break;
    					
    				default:
    					throw new RuntimeException();
    			}
    		}
    	}
    	
    	String resultingType = stack.pop();
    	
    	if (!stack.isEmpty()) {
    		throw new RuntimeException("Expression error. Verify your expression.");
    	}
    	
    	return resultingType;
    }
	
}
