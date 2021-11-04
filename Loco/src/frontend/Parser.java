package frontend;

import java.io.File;
import java.util.ArrayList;

import SyntaxNodes.*;

public class Parser {
	private ArrayList<Token> tokens;
	private ArrayList<String> diagnostics;
	private int position;
	private int lineCounter;
	
	Parser (File file) {
		this.tokens = new ArrayList<Token>();
		this.diagnostics = new ArrayList<String>();
		this.position = 0;
		this.lineCounter = 1;
		
		Lexer lexer = new Lexer(file);
		Token curToken;
		
		do {
			curToken = lexer.nextToken();
			
			// ignore single line comment
			if (curToken.getKind() == TokenKind.btwToken) {
				while (curToken.getKind() != TokenKind.eolToken) {
					curToken = lexer.nextToken();
				}
			}
			
			if (curToken.getKind() != TokenKind.badToken) {
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
		
		if (current().getKind() == kind) {
			if (kind == TokenKind.eolToken) lineCounter++;
			return nextToken();
		}
		
		diagnostics.add("Line "+ lineCounter + ": Unexpected Token <"+ current().getKind() 
						+ "> expected <"+ kind + ">");
		return new Token(kind, null, current().getPosition());
	}
	
	public void viewTokens() {
		for (Token i : tokens) i.viewToken();
	}
	
	public void viewErrors() {
		for (String i : diagnostics) System.out.println(i);
	}
	
	public SyntaxNode parse() {
		NodeRoot root;
		
		Token start = match(TokenKind.haiToken);
		match(TokenKind.eolToken);
		consumeEOL();
		
		if (current().getKind() != TokenKind.byeToken) {
			SyntaxNode statement = parseStatement();
			Token end = match(TokenKind.byeToken);
			
			root = new NodeRoot (start, statement, end);
		} else {
			Token end = match(TokenKind.byeToken);
			root = new NodeRoot (start, end);
		}
		
		
		Token endOfFile = match(TokenKind.eofToken);
		root.printChildren(0);
		
		return root;
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
	
	private boolean isBoolOp() {
		if (current().getKind() == TokenKind.bothOpToken ||
			current().getKind() == TokenKind.eitherOpToken||
			current().getKind() == TokenKind.wonOpToken ||
			current().getKind() == TokenKind.notOpToken) {
			return true;
		}
		
		return false;
	}
	
	private boolean isComment() {
		if (current().getKind() == TokenKind.obtwToken) {
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
	
	private boolean isAssignment() {
		if (current().getKind() == TokenKind.ihasToken) {
			return true;
		}
		return false;
	}
	
	private void consumeEOL() {
		while (current().getKind() == TokenKind.eolToken) {
			lineCounter++;
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
			return new NodeExpression(parseMathOp(), lineCounter);
		} else if (isComment()) {
			return new NodeExpression(parseComment(), lineCounter);
		} else if (isAssignment()) {
			return new NodeExpression(parseAssignment(), lineCounter);
		} else if (isBoolOp()) {
			return new NodeExpression(parseBoolOp(), lineCounter);
		}
		
		return new NodeLiteral(nextToken());
	}
	
	private SyntaxNode parseMathOp() {
		Token operation = nextToken();
		
		SyntaxNode operand1 = parseLiteral();
		match(TokenKind.anToken);
		SyntaxNode operand2 = parseLiteral();
		
		return new NodeOperation(operation, operand1, operand2);
		
	}
	
	private SyntaxNode parseComment() {
		ArrayList<Token> inner = new ArrayList<Token>();
		Token start = nextToken();
		
		while (current().getKind() != TokenKind.tldrToken) {
			
			inner.add(nextToken());
		}
		Token end = match(TokenKind.tldrToken);
		
		return new NodeComment(start, inner, end);
	}
	
	private SyntaxNode parseAssignment() {
		Token operation = nextToken();
		Token varid = match(TokenKind.idToken);
		
		if (current().getKind() == TokenKind.itzToken) {
			nextToken();
			
			if (isLiteral()) {
				return new NodeAssignment(operation, varid, parseLiteral());
			} else if (isMathOp()) {
				return new NodeAssignment(operation, varid, parseMathOp());
			} else if (current().getKind() == TokenKind.idToken) {
				if (current().getValue() != varid.getValue()) {
					//	TODO symbol table for variables
					return new NodeAssignment(operation, varid);
				}
			}
			
			diagnostics.add("Line "+ lineCounter + ": Invalid assignment; expected valid Literal/VarId/Expression");
			
		}
		
		return new NodeAssignment(operation, varid);

	}
	
	private SyntaxNode parseBoolOp() {
		Token operation = nextToken();
		
		if (operation.getKind() != TokenKind.notOpToken) {
			SyntaxNode operand1 = parseLiteral();
			match(TokenKind.anToken);
			SyntaxNode operand2 = parseLiteral();
			
			return new NodeOperation(operation, operand1, operand2);
		} else {
			SyntaxNode operand1 = parseLiteral();
			
			return new NodeOperation(operation, operand1);
		}
	}
	
	private SyntaxNode parseLiteral() {
		if (isMathOp()) {
			return parseMathOp();
		} else if (isBoolOp()) {
			return parseBoolOp();
		} else if (isLiteral()) {
			return new NodeLiteral(nextToken());
		}
		
		return new NodeLiteral(match(TokenKind.numbrToken));
	}	
	
	
}
