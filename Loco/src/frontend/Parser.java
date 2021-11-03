package frontend;

import java.io.File;
import java.util.ArrayList;

import SyntaxNodes.*;

public class Parser {
	private ArrayList<Token> tokens;
	private ArrayList<String> diagnostics;
	private int position;
	
	Parser (File file) {
		this.tokens = new ArrayList<Token>();
		this.diagnostics = new ArrayList<String>();
		this.position = 0;
		
		Lexer lexer = new Lexer(file);
		Token curToken;
		
		do {
			curToken = lexer.nextToken();
			
			if (curToken.getKind() == TokenKind.btwToken) {
				while (curToken.getKind() != TokenKind.eolToken) {
					curToken = lexer.nextToken();
				}
			}
			
			if (curToken.getKind() != TokenKind.miscToken) {
				tokens.add(curToken);
			}
			
		} while (curToken.getKind() != TokenKind.eofToken);
	}
	
	private Token peek(int offset) {
		return tokens.get(position + offset);
	}
	
	private Token current() {
		return peek(0);
	}
	
	private Token nextToken() {
		Token curToken = current();
		position++;
		return curToken;
	}
	
	private Token match(TokenKind kind) {
		if (current().getKind() == kind) return nextToken();
		
		diagnostics.add("Error: Unexpected Token <"+ current().getKind() + "> expected <"+ kind + ">");
		return new Token(kind, null, current().getPosition());
	}
	
	public void viewTokens() {
		for (Token i : tokens) i.viewToken();
	}
	
	public void viewErrors() {
		for (String i : diagnostics) System.out.println(i);
	}
	
	public SyntaxNode parse() {
		Token start = match(TokenKind.haiToken);
		match(TokenKind.eolToken);
		//if (current().getKind() != TokenKind.byeToken) {
			SyntaxNode statement = parseStatement();
		//}
		Token end = match(TokenKind.byeToken);
		Token endOfFile = match(TokenKind.eofToken);
		
		statement.printChildren(0);
		
		return statement;
	}
	
	private boolean isMathOp() {
		if (current().getKind() == TokenKind.sumOpToken ||
			current().getKind() == TokenKind.diffOpToken||
			current().getKind() == TokenKind.mulOpToken ||
			current().getKind() == TokenKind.divOpToken ||
			current().getKind() == TokenKind.modOpToken ||
			current().getKind() == TokenKind.minOpToken ||
			current().getKind() == TokenKind.maxOpToken) {
			return true;
		}
		
		return false;
	}
	
	private boolean isLiteral() {
		if (current().getKind() == TokenKind.numbrToken ||
			current().getKind() == TokenKind.numbarToken ||
			current().getKind() == TokenKind.yarnToken ||
			current().getKind() == TokenKind.troofToken ) {
			return true;
		}
		
		return false;
	}
	
	private void consumeEOL() {
		while (current().getKind() == TokenKind.eolToken) {
			nextToken();
		}
	}
	
	private SyntaxNode parseStatement() {
		SyntaxNode expression = parseExpression();
		match(TokenKind.eolToken);
		
		while (current().getKind() != TokenKind.byeToken) {
			expression = new NodeStatement(expression, parseStatement());
		}
		
		return expression;
	}
	
	private SyntaxNode parseExpression() {
		if (isMathOp()) {
			return new NodeExpression(parseMathOp());
		} 
		
		return new NodeLiteral(nextToken());
	}
	
	private SyntaxNode parseMathOp() {
		Token operation = nextToken();
		
		SyntaxNode operand1 = parseLiteral();
		match(TokenKind.anToken);
		SyntaxNode operand2 = parseLiteral();
		
		return new NodeMathOp(operation, operand1, operand2);
		
	} 
	
	private SyntaxNode parseLiteral() {
		if (isMathOp()) {
			return parseExpression();
		} else if (isLiteral()) {
			return new NodeLiteral(nextToken());
		}
		
		return new NodeLiteral(match(TokenKind.numbrToken));
	}
	
	
	
}
