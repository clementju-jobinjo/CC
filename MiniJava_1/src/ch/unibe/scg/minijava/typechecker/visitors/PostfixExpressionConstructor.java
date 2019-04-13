package ch.unibe.scg.minijava.typechecker.visitors;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

public class PostfixExpressionConstructor {
	
	private String precedence;
	
	public PostfixExpressionConstructor() {
		precedence = "&&><!+-*/";
	}
	

    private boolean hasHigherPrecedence(String operator1, String operator2) {
    	return (precedence.indexOf(operator2) >= precedence.indexOf(operator1));
    }

    public String postfix(String infixExpression) {
        StringBuilder postFixExpression = new StringBuilder();
        Deque<String> stack  = new LinkedList<>();

        for (String token : infixExpression.split("\\s")) {
            // operator
            if (precedence.contains(token)) {
                while ( ! stack.isEmpty() && hasHigherPrecedence(token, stack.peek()))
                	postFixExpression.append(stack.pop()).append(' ');
                stack.push(token);

            // left parenthesis
            } else if (token.equals("(")) {
                stack.push(token);

            // right parenthesis
            } else if (token.equals(")")) {
                while ( ! stack.peek().equals("("))
                	postFixExpression.append(stack.pop()).append(' ');
                stack.pop();

            // digit
            } else {
            	postFixExpression.append(token).append(' ');
            }
        }

        while ( ! stack.isEmpty())
        	postFixExpression.append(stack.pop()).append(' ');

        return postFixExpression.toString();
    }
	
}
