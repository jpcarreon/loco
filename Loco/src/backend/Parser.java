package backend;

import java.io.File;
import java.util.ArrayList;

import SyntaxNodes.*;

public class Parser {
	private ArrayList<Token> tokens;
	private ArrayList<Token> allTokens;
	private ArrayList<String> diagnostics;
	private int position;
	private int lineCounter;
	
	public Parser (File file) {
		this.tokens = new ArrayList<Token>();
		this.allTokens = new ArrayList<Token>();
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
					allTokens.add(curToken);
					curToken = lexer.nextToken();
				}
			}
			
			if (curToken.getTokenKind() != TokenKind.badToken) {
				allTokens.add(curToken);
				tokens.add(curToken);
			}
			
		} while (curToken.getTokenKind() != TokenKind.eofToken);
	}
	
	public Parser (String strFile) {
		this.tokens = new ArrayList<Token>();
		this.allTokens = new ArrayList<Token>();
		this.diagnostics = new ArrayList<String>();
		this.position = 0;
		this.lineCounter = 1;
		
		Lexer lexer = new Lexer(strFile);
		Token curToken;
		
		do {
			curToken = lexer.nextToken();
			
			// ignore single line comment
			if (curToken.getTokenKind() == TokenKind.btwToken) {
				while (curToken.getTokenKind() != TokenKind.eolToken) {
					allTokens.add(curToken);
					curToken = lexer.nextToken();
				}
			}
			
			if (curToken.getTokenKind() != TokenKind.badToken) {
				allTokens.add(curToken);
				tokens.add(curToken);
			}
			
		} while (curToken.getTokenKind() != TokenKind.eofToken);
	}
	
	private Token peek(int offset) {
		if (position + offset >= tokens.size()) return tokens.get(position);
		
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
	
	private Token lazyMatch(TokenKind kind) {
		if (current().getTokenKind() == kind) {
			if (kind == TokenKind.eolToken) lineCounter++;
			return nextToken();
		}
		
		return new Token(kind, null, current().getPosition() - 1);
	}
	
	private Token match(TokenKind kind) {
		
		if (current().getTokenKind() == kind) {
			if (kind == TokenKind.eolToken) lineCounter++;
			return nextToken();
		}
		
		diagnostics.add("Line "+ lineCounter + ": Unexpected <"+ current().getTokenKind() 
						+ "> expected <"+ kind + ">");
		
		if (current().getTokenKind() == TokenKind.miscToken ||
			current().getTokenKind() == TokenKind.badToken  ||
			kind == TokenKind.eolToken) {
			nextToken();
		}
		
		return new Token(kind, null, current().getPosition() - 1);
	}

	public NodeRoot parse() {
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
		
		return root;
	}
	
	private SyntaxNode parseStatement() {
		SyntaxNode expression;
		
		if (isAssignment()) expression = parseAssignment();
		else if (isFlowControl()) expression = parseExpression();
		else expression = parseExpression();
		
		match(TokenKind.eolToken);
		consumeEOL();
		
		while (current().getTokenKind() != TokenKind.byeToken && current().getTokenKind() != TokenKind.eofToken) {
			expression = new NodeStatement(expression, parseStatement());
		}
		
		return expression;
	}
	
	
	private SyntaxNode parseExpression() {
		if (current().getTokenKind().getType() == "mathop") {
			return new NodeExpression(parseMathOp(), lineCounter);
			
		} else if (current().getTokenKind().getType() == "comment") {
			return new NodeExpression(parseComment(), lineCounter);
			
		} else if (current().getTokenKind().getType() == "boolop") {
			return new NodeExpression(parseBoolOp(), lineCounter);
			
		} else if (current().getTokenKind().getType() == "infarop") {
			SyntaxNode infAr = new NodeExpression(parseInfArOp(nextToken()), lineCounter);
			match(TokenKind.mkayToken);
			return infAr;
			
		} else if (current().getTokenKind().getType() == "cmpop") {
			return new NodeExpression(parseCmpOp(), lineCounter);
			
		}	else if (current().getTokenKind() == TokenKind.maekToken) {
			return new NodeExpression(parseExpTypecast(), lineCounter);
				
		} else if (current().getTokenKind() == TokenKind.smooshToken) {
			SyntaxNode concat = new NodeExpression(parseConcat(nextToken()), lineCounter);
			lazyMatch(TokenKind.mkayToken);
			return concat;
			
		} else if (current().getTokenKind() == TokenKind.printToken) {
			return new NodeExpression(parsePrint(nextToken()), lineCounter);
			
		}
		
		diagnostics.add("Line "+ lineCounter + ": Invalid Keyword");
		while (current().getTokenKind() != TokenKind.eolToken) {
			nextToken();
		}
		
		return new NodeLiteral(new Token(TokenKind.badToken, null, -1));
	}
	
	private SyntaxNode parseAssignment() {
		if (current().getTokenKind().getType() == "newvar") {
			return new NodeAssignment(parseDeclaration(), lineCounter);
			
		} else if (current().getTokenKind() == TokenKind.idToken) {
			Token varid = nextToken();
			
			if (current().getTokenKind() == TokenKind.rToken) {
				return new NodeAssignment(parseVarChange(varid), lineCounter);
						
			} else if (current().getTokenKind() == TokenKind.isNowToken) {
				return new NodeAssignment(parseAsnTypecast(varid), lineCounter);
			}
		} else if (current().getTokenKind().getType() == "scan") {
			return new NodeAssignment(parseScan(), lineCounter);
		}
		
		diagnostics.add("Line "+ lineCounter + ": Invalid Keyword");
		while (current().getTokenKind() != TokenKind.eolToken) {
			nextToken();
		}
		return new NodeLiteral(nextToken());
	}
	
	
	
	private SyntaxNode parseMathOp() {
		Token operation = nextToken();
		
		SyntaxNode operand1 = parseTerminal();
		match(TokenKind.anToken);
		SyntaxNode operand2 = parseTerminal();
		
		return new NodeOperation(SyntaxType.mathop, operation, operand1, operand2);
		
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
			
			/*
			if (current().getTokenKind().getType() == "literal") {
				return new NodeDeclaration(SyntaxType.newvar, operation, varid, parseLiteral());
			} else if (current().getTokenKind().getType() == "mathop") {
				return new NodeDeclaration(SyntaxType.newvar, operation, varid, parseMathOp());
			} else if (current().getTokenKind() == TokenKind.idToken) {
				if (current().getValue() != varid.getValue()) {
					return new NodeDeclaration(SyntaxType.newvar, operation, varid);
				}
			}
			*/
			
			return new NodeDeclaration(SyntaxType.newvar, operation, varid, parseTerminal());
			
			//diagnostics.add("Line "+ lineCounter + ": Invalid assignment; expected valid Literal/VarId/Expression");
			
		}
		
		return new NodeDeclaration(SyntaxType.newvar, operation, varid);

	}
	
	private SyntaxNode parseBoolOp() {
		Token operation = nextToken();
		
		if (operation.getTokenKind() != TokenKind.notOpToken) {
			SyntaxNode operand1 = parseTerminal();
			match(TokenKind.anToken);
			SyntaxNode operand2 = parseTerminal();
			
			return new NodeOperation(SyntaxType.boolop, operation, operand1, operand2);
		} else {
			SyntaxNode operand1 = parseTerminal();
			
			return new NodeOperation(SyntaxType.boolop, operation, operand1);
		}
	}
	
	private SyntaxNode parseInfArOp(Token operation) {
		SyntaxNode operand1 = parseTerminal();
		
		while (current().getTokenKind() != TokenKind.mkayToken && current().getTokenKind() != TokenKind.eolToken) {
			match(TokenKind.anToken);
			operand1 = new NodeOperation(SyntaxType.infarop , operation, operand1, parseInfArOp(operation));
		}
		
		return operand1;
	}
	
	private SyntaxNode parseCmpOp() {
		Token operation = nextToken();
		
		SyntaxNode operand1 = parseTerminal();
		match(TokenKind.anToken);
		SyntaxNode operand2 = parseTerminal();
		
		return new NodeOperation(SyntaxType.cmpop, operation, operand1, operand2);
	}
	
	private SyntaxNode parseConcat(Token operation) {
		SyntaxNode operand1 = parseTerminal();
		
		if (current().getTokenKind() != TokenKind.eolToken && current().getTokenKind() != TokenKind.mkayToken) {
			match(TokenKind.anToken);
			operand1 = new NodeOperation(SyntaxType.concat, operation, operand1, parseConcat(operation));
			
			return operand1;
		} else {
			return new NodeOperation(SyntaxType.concat, operation, operand1);
		}
		
		
	}
	
	private SyntaxNode parseVarChange(Token varid) {
		Token operation = nextToken();
		
		SyntaxNode terminal = parseTerminal();
		
		return new NodeDeclaration(SyntaxType.varchange, operation, varid, terminal);
	}
	
	private SyntaxNode parseAsnTypecast(Token varid) {
		Token operation = nextToken();
		SyntaxNode vartype = new NodeLiteral(SyntaxType.vartype, match(TokenKind.typeToken));
		
		return new NodeDeclaration(SyntaxType.vartypechange, operation, varid, vartype);
	}
	
	private SyntaxNode parseExpTypecast() {
		Token operation = nextToken();
		return new NodeLiteral(operation);
	}

	private SyntaxNode parsePrint(Token operation) {
		SyntaxNode operand1 = parseTerminal();
		
		if (current().getTokenKind() != TokenKind.eolToken && operand1.getType() != SyntaxType.invalid) {
			operand1 = new NodeOperation(SyntaxType.print, operation, operand1, parsePrint(operation));

			return operand1;
		} else if (operand1.getType() == SyntaxType.invalid) {
			nextToken();
			operand1 = new NodeOperation(SyntaxType.print, operation, operand1);
			
			return operand1;
		} else {
			
			return new NodeOperation(SyntaxType.print, operation, operand1);
		}
		
		
	}
	
	private SyntaxNode parseScan() {
		Token operation = nextToken();
		SyntaxNode varid = new NodeLiteral(SyntaxType.varid, match(TokenKind.idToken));
		
		return new NodeOperation(SyntaxType.scan, operation, varid);
	}
	
	private SyntaxNode parseTerminal() {
		if (current().getTokenKind().getType() == "mathop") {
			return parseMathOp();
		} else if (current().getTokenKind().getType() == "boolop") {
			return parseBoolOp();
		} else if (current().getTokenKind().getType() == "cmpop") {
			return parseCmpOp();
		} else if (current().getTokenKind() == TokenKind.quoteToken) {
			return new NodeLiteral(parseYarn());
		} else if (current().getTokenKind().getType() == "literal") {
			return new NodeLiteral(nextToken());
		} else if (current().getTokenKind() == TokenKind.idToken) {
			return new NodeLiteral(SyntaxType.varid, nextToken());
		}
		
		diagnostics.add("Line "+ lineCounter + ": Invalid operator; expected valid Literal/VarId/Expression");
		return new NodeLiteral(SyntaxType.invalid, new Token(TokenKind.badToken, null, -1));
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

	private boolean isAssignment() {
		if (current().getTokenKind() == TokenKind.ihasToken ||
			current().getTokenKind() == TokenKind.idToken ||
			current().getTokenKind() == TokenKind.scanToken) {
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
	
	protected ArrayList<Token> getTokens() {
		return allTokens;
	}
	
	protected ArrayList<String> getDiagnostics() {
		return diagnostics;
	}
	
	public void viewTokens() {
		for (Token i : tokens) i.viewToken();
	}
	
	public void viewErrors() {
		for (String i : diagnostics) System.out.println(i);
	}
}
