package backend;

import java.io.File;
import java.util.ArrayList;

import SyntaxNodes.*;

public class Parser {
	private ArrayList<Token> tokens;
	private ArrayList<String> diagnostics;
	private int position;
	private int lineCounter;
	
	public Parser (File file) {
		this.tokens = new ArrayList<Token>();
		this.diagnostics = new ArrayList<String>();
		this.position = 0;
		this.lineCounter = 1;
		
		Lexer lexer = new Lexer(file);
		Token curToken;
		
		do {
			curToken = lexer.nextToken();
			
			// ignore single line comment
			if (curToken.getTokenKind() == TokenKind.btwToken) {
				while (curToken.getTokenKind() != TokenKind.eolToken) {
					curToken = lexer.nextToken();
				}
			}
			
			if (curToken.getTokenKind() != TokenKind.badToken) {
				tokens.add(curToken);
			}
			
		} while (curToken.getTokenKind() != TokenKind.eofToken);
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
		
		if (current().getTokenKind() == kind) {
			if (kind == TokenKind.eolToken) lineCounter++;
			return nextToken();
		}
		
		diagnostics.add("Line "+ lineCounter + ": Unexpected <"+ current().getTokenKind() 
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
		
		if (current().getTokenKind() != TokenKind.byeToken) {
			SyntaxNode statement = parseStatement();
			Token end = match(TokenKind.byeToken);
			
			root = new NodeRoot (start, statement, end);
		} else {
			Token end = match(TokenKind.byeToken);
			root = new NodeRoot (start, end);
		}
		
		
		match(TokenKind.eofToken);
		root.printChildren(0);
		
		return root;
	}
	
	private SyntaxNode parseStatement() {
		SyntaxNode expression;
		
		if (isAssignment()) expression = parseAssignment();
		else if (isFlowControl()) expression = parseExpression();
		else expression = parseExpression();
		
		match(TokenKind.eolToken);
		consumeEOL();
		
		while (current().getTokenKind() != TokenKind.byeToken) {
			expression = new NodeStatement(expression, parseStatement());
		}
		
		return expression;
	}
	
	private SyntaxNode parseExpression() {
		if (isMathOp()) {
			return new NodeExpression(parseMathOp(), lineCounter);
			
		} else if (isComment()) {
			return new NodeExpression(parseComment(), lineCounter);
			
		} else if (isBoolOp()) {
			return new NodeExpression(parseBoolOp(), lineCounter);
			
		} else if (isInfArOp()) {
			SyntaxNode infAr = new NodeExpression(parseInfArOp(nextToken()), lineCounter);
			match(TokenKind.mkayToken);
			return infAr;
			
		} else if (isCmpOp()) {
			return new NodeExpression(parseCmpOp(), lineCounter);
			
		} else if (isConcatenation()) {
			return new NodeExpression(parseConcat(nextToken()), lineCounter);
			
		} else if (isPrint()) {
			return new NodeExpression(parsePrint(nextToken()), lineCounter);
			
		}
		
		diagnostics.add("Line "+ lineCounter + ": Invalid Keyword");
		return new NodeLiteral(nextToken());
	}
	
	private SyntaxNode parseAssignment() {
		if (isDeclaration()) {
			return new NodeAssignment(parseDeclaration(), lineCounter);
			
		} else if (current().getTokenKind() == TokenKind.idToken) {
			Token varid = nextToken();
			
			if (isVarChange()) {
				return new NodeAssignment(parseVarChange(varid), lineCounter);
						
			} else if (isTypeCast()) {
				
			}
		}
		
		diagnostics.add("Line "+ lineCounter + ": Invalid Keyword");
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
		
		while (current().getTokenKind() != TokenKind.tldrToken && current().getTokenKind() != TokenKind.byeToken) {
			if (current().getValue() == "\n") lineCounter++;
			inner.add(nextToken());
		}
		Token end = match(TokenKind.tldrToken);
		
		return new NodeComment(start, inner, end);
	}
	
	private SyntaxNode parseDeclaration() {
		Token operation = nextToken();
		Token varid = match(TokenKind.idToken);
		
		if (current().getTokenKind() == TokenKind.itzToken) {
			nextToken();
			
			if (isLiteral()) {
				return new NodeDeclaration(operation, varid, parseLiteral());
			} else if (isMathOp()) {
				return new NodeDeclaration(operation, varid, parseMathOp());
			} else if (current().getTokenKind() == TokenKind.idToken) {
				if (current().getValue() != varid.getValue()) {
					//	TODO symbol table for variables
					return new NodeDeclaration(operation, varid);
				}
			}
			
			diagnostics.add("Line "+ lineCounter + ": Invalid assignment; expected valid Literal/VarId/Expression");
			
		}
		
		return new NodeDeclaration(operation, varid);

	}
	
	private SyntaxNode parseBoolOp() {
		Token operation = nextToken();
		
		if (operation.getTokenKind() != TokenKind.notOpToken) {
			SyntaxNode operand1 = parseLiteral();
			match(TokenKind.anToken);
			SyntaxNode operand2 = parseLiteral();
			
			return new NodeOperation(operation, operand1, operand2);
		} else {
			SyntaxNode operand1 = parseLiteral();
			
			return new NodeOperation(operation, operand1);
		}
	}
	
	private SyntaxNode parseInfArOp(Token operation) {
		SyntaxNode operand1 = parseLiteral();
		
		while (current().getTokenKind() != TokenKind.mkayToken && current().getTokenKind() != TokenKind.eolToken) {
			match(TokenKind.anToken);
			operand1 = new NodeOperation(operation, operand1, parseInfArOp(operation));
		}
		
		return operand1;
	}
	
	private SyntaxNode parseCmpOp() {
		Token operation = nextToken();
		
		SyntaxNode operand1 = parseLiteral();
		match(TokenKind.anToken);
		SyntaxNode operand2 = parseLiteral();
		
		return new NodeOperation(operation, operand1, operand2);
	}
	
	private SyntaxNode parseConcat(Token operation) {
		SyntaxNode operand1 = parseLiteral();
		
		if (current().getTokenKind() != TokenKind.eolToken) {
			while (current().getTokenKind() != TokenKind.eolToken) {
				match(TokenKind.anToken);
				operand1 = new NodeOperation(operation, operand1, parseConcat(operation));
			}
			
			return operand1;
		} else {
			return new NodeOperation(operation, operand1);
		}
		

	}
	
	private SyntaxNode parseVarChange(Token varid) {
		Token operation = nextToken();
		
		SyntaxNode terminal = parseLiteral();
		
		return new NodeDeclaration(operation, varid, terminal);
	}
	
	private SyntaxNode parsePrint(Token operation) {
		SyntaxNode operand1 = parseLiteral();
		
		if (current().getTokenKind() != TokenKind.eolToken) {
			while (current().getTokenKind() != TokenKind.eolToken) {
				operand1 = new NodeOperation(operation, operand1, parsePrint(operation));
			}
			
			return operand1;
		} else {
			return new NodeOperation(operation, operand1);
		}
	}
	
	private SyntaxNode parseLiteral() {
		if (isMathOp()) {
			return parseMathOp();
		} else if (isBoolOp()) {
			return parseBoolOp();
		} else if (isCmpOp()) {
			return parseCmpOp();
		} else if (current().getTokenKind() == TokenKind.quoteToken) {
			return new NodeLiteral(parseYarn());
		} else if (isLiteral()) {
			return new NodeLiteral(nextToken());
		} else if (current().getTokenKind() == TokenKind.idToken) {
			//	TODO variable handler
			return new NodeLiteral(nextToken());
		}
		
		return new NodeLiteral(match(TokenKind.numbrToken));
	}
	
	private Token parseYarn() {
		String value = new String();
		nextToken();
		
		while (current().getTokenKind() != TokenKind.quoteToken && current().getTokenKind() != TokenKind.eolToken) {
			value = value + nextToken().getValue() + " ";
		}
		
		match(TokenKind.quoteToken);
		
		return new Token(TokenKind.yarnToken, value, current().getPosition());
	}
	
	private void consumeEOL() {
		while (current().getTokenKind() == TokenKind.eolToken) {
			lineCounter++;
			nextToken();
		}
	}
	
	private boolean isMathOp() {
		if (current().getTokenKind() == TokenKind.sumOpToken ||
			current().getTokenKind() == TokenKind.diffOpToken||
			current().getTokenKind() == TokenKind.mulOpToken ||
			current().getTokenKind() == TokenKind.divOpToken ||
			current().getTokenKind() == TokenKind.modOpToken ||
			current().getTokenKind() == TokenKind.minOpToken ||
			current().getTokenKind() == TokenKind.maxOpToken) {
			return true;
		}
		
		return false;
	}
	
	private boolean isBoolOp() {
		if (current().getTokenKind() == TokenKind.bothOpToken ||
			current().getTokenKind() == TokenKind.eitherOpToken||
			current().getTokenKind() == TokenKind.wonOpToken ||
			current().getTokenKind() == TokenKind.notOpToken) {
			return true;
		}
		
		return false;
	}

	private boolean isInfArOp() {
		if (current().getTokenKind() == TokenKind.anyOpToken ||
			current().getTokenKind() == TokenKind.allOpToken) {
			return true;
		}
		
		return false;
	}
	
	private boolean isCmpOp() {
		if (current().getTokenKind() == TokenKind.bothSameOpToken ||
			current().getTokenKind() == TokenKind.diffrntOpToken) {
			return true;
		}
		
		return false;
	}
	
	private boolean isComment() {
		if (current().getTokenKind() == TokenKind.obtwToken) {
			return true;
		}
		
		return false;
	}
	
	private boolean isLiteral() {
		if (current().getTokenKind() == TokenKind.numbrToken ||
			current().getTokenKind() == TokenKind.numbarToken ||
			current().getTokenKind() == TokenKind.quoteToken ||
			current().getTokenKind() == TokenKind.troofToken ) {
			return true;
		}
		
		return false;
	}
	
	private boolean isDeclaration() {
		if (current().getTokenKind() == TokenKind.ihasToken) {
			return true;
		}
		return false;
	}
	
	private boolean isConcatenation() {
		if (current().getTokenKind() == TokenKind.smooshToken) {
			return true;
		}
		
		return false;
	}
	
	private boolean isVarChange() {
		if (current().getTokenKind() == TokenKind.rToken) {
			return true;
		}
		
		return false;
	}
	
	private boolean isTypeCast() {
		if (current().getTokenKind() == TokenKind.maekToken ||
			current().getTokenKind() == TokenKind.isNowToken) {
			return true;
		}
		
		return false;
	}
	
	private boolean isPrint() {
		if (current().getTokenKind() == TokenKind.printToken) {
			return true;
		}
		
		return false;
	}
	
	private boolean isScan() {
		if (current().getTokenKind() == TokenKind.scanToken) {
			return true;
		}
		
		return false;
	}
	
	private boolean isAssignment() {
		if (current().getTokenKind() == TokenKind.ihasToken ||
			current().getTokenKind() == TokenKind.idToken ||
			current().getTokenKind() == TokenKind.maekToken) {
			return true;
		}
		
		return false;
	}
	
	private boolean isFlowControl() {
		if (current().getTokenKind() == TokenKind.ifStartToken ||
			current().getTokenKind() == TokenKind.loopStartToken) {
			return true;
		}
		
		return false;
	}
	
}
