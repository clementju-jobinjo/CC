package ch.unibe.scg.minijava.typechecker.visitors;

import java.util.Deque;
import java.util.LinkedList;
import ch.unibe.scg.minijava.typechecker.types.Boolean;
import ch.unibe.scg.minijava.typechecker.types.Int;

public class PostfixExpressionConstructor {
	
	private String precedence;
	
	public PostfixExpressionConstructor() {
		precedence = "&&><!+-*/";
	}
	

    private boolean hasHigherPrecedence(String operator1, String operator2) {
    	return (precedence.indexOf(operator2) >= precedence.indexOf(operator1));
    }

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

            // left parenthesis
            } else if (token.equals("(")) {
                stack.push(token);

            // right parenthesis
            } else if (token.equals(")")) {
                while ( ! stack.peek().equals("(")) {
                	postfixExpression.append(stack.pop()).append(' ');
                }
                stack.pop();

            // digit
            } else {
            	postfixExpression.append(token).append(' ');
            }
        }

        while ( ! stack.isEmpty()) {
        	postfixExpression.append(stack.pop()).append(' ');
        }
        	
        return postfixExpression.toString();
    }
    
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
    						throw new RuntimeException();
    					}
    					break;
    				
    				case "&&":
    					String s1 = stack.pop();
    					String s2 = stack.pop();
    					if (s1.equals(Boolean.BooleanSingleton.getTypeName()) && s2.equals(Boolean.BooleanSingleton.getTypeName())) {
    						stack.push(Boolean.BooleanSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException();
    					}
    					break;
    				
    				case ">":
    					String s3 = stack.pop();
    					String s4 = stack.pop();
    					if (s3.equals(Int.IntSingleton.getTypeName()) && s4.equals(Int.IntSingleton.getTypeName())) {
    						stack.push(Boolean.BooleanSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException();
    					}
    					break;
    					
    				case "<":
    					String s5 = stack.pop();
    					String s6 = stack.pop();
    					if (s5.equals(Int.IntSingleton.getTypeName()) && s6.equals(Int.IntSingleton.getTypeName())) {
    						stack.push(Int.IntSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException();
    					}
    					break;
    					
    				case "+":
    					String s7 = stack.pop();
    					String s8 = stack.pop();
    					if (s7.equals(Int.IntSingleton.getTypeName()) && s8.equals(Int.IntSingleton.getTypeName())) {
    						stack.push(Int.IntSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException();
    					}
    					break;
    				
    				case "-":
    					String s9 = stack.pop();
    					String s10 = stack.pop();
    					if (s9.equals(Int.IntSingleton.getTypeName()) && s10.equals(Int.IntSingleton.getTypeName())) {
    						stack.push(Int.IntSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException();
    					}
    					break;
    				
    				case "*":
    					String s11 = stack.pop();
    					String s12 = stack.pop();
    					if (s11.equals(Int.IntSingleton.getTypeName()) && s12.equals(Int.IntSingleton.getTypeName())) {
    						stack.push(Int.IntSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException();
    					}
    					break;
    					
    				case "/":
    					String s13 = stack.pop();
    					String s14 = stack.pop();
    					if (s13.equals(Int.IntSingleton.getTypeName()) && s14.equals(Int.IntSingleton.getTypeName())) {
    						stack.push(Int.IntSingleton.getTypeName());
    					}
    					else {
    						throw new RuntimeException();
    					}
    					break;
    					
    				default:
    					throw new RuntimeException();
    			}
    		}
    	}
    	
    	String resultingType = stack.pop();
    	
    	if (!stack.isEmpty()) {
    		throw new RuntimeException();
    	}
    	
    	return resultingType;
    }
	
}
