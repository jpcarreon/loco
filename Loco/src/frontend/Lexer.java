package frontend;

import java.util.ArrayList;

public class Lexer {
	private ArrayList<String> text;
	private int position;
	
	Lexer(String text) {
		this.text = new ArrayList<String>();
		for (String i : text.split("\s")) this.text.add(i);
	}
	
	private void next() {
		position++;
	}
	
	private String currentToken() {
		return this.text.get(position);
	}
	
	public void viewState() {
		for (String i : this.text) System.out.print(i+" ");
		System.out.println();
	}
	
	public Token nextToken() {
		if (position >= text.size()) {
			return new Token(TokenKind.eofToken, "\0", position++);
		}
		
		if (currentToken().matches("[0-9]+")) {
			return new Token(TokenKind.digitToken, currentToken(), position++);
		} else if (currentToken().equals("+")) {
			return new Token(TokenKind.plusToken, currentToken(), position++);
		} else if (currentToken().equals("-")) {
			return new Token(TokenKind.minusToken, currentToken(), position++);
		} else if (currentToken().equals("*")) {
			return new Token(TokenKind.mulToken, currentToken(), position++);
		} else if (currentToken().equals("/")) {
			return new Token(TokenKind.divToken, currentToken(), position++);
		} else if (currentToken().equals("(")) {
			return new Token(TokenKind.OpenParenthesisToken, currentToken(), position++);
		} else if (currentToken().equals(")")) {
			return new Token(TokenKind.CloseParenthesisToken, currentToken(), position++);
		}  
		
		return new Token(TokenKind.badToken, currentToken(), position++);
	}
	
}
