package backend;

import java.io.File;
import java.util.ArrayList;

import SyntaxNodes.*;

import frontend.WindowController;

public class Evaluator {
	private Parser parser;
	private WindowController window;
	
	private ArrayList<Token> tokens;
	private ArrayList<SymTabEntry> symbolTable;
	private String errorMsg;
	
	private NodeRoot root;
	private SyntaxNode programCounter;
	private SyntaxNode currentInstruction;
	private int lineCounter;
	
	public Evaluator(File file) {
		this.errorMsg = new String();
		this.symbolTable = new ArrayList<SymTabEntry>();
		this.lineCounter = 1;
		
		parser = new Parser(file);
		this.root = parser.parse();
		
		this.tokens = parser.getTokens();
		this.symbolTable.add(new SymTabEntry("IT", TokenKind.noobToken, ""));
		
		this.programCounter = root.getStatements();
	}
	
	public Evaluator(String strFile, WindowController window) {
		this.window = window;
		
		this.errorMsg = new String();
		this.symbolTable = new ArrayList<SymTabEntry>();
		this.lineCounter = 1;
		
		parser = new Parser(strFile);
		this.root = parser.parse();
		
		this.tokens = parser.getTokens();
		this.symbolTable.add(new SymTabEntry("IT", TokenKind.noobToken, ""));
		
		this.programCounter = root.getStatements();
	}
	
	public void nextInstruction() {
		
		if (parser.getDiagnostics().size() > 0) {
			programCounter = null;
			return;
		}
		
		// check if PC holds only 1 line of code 
		if (programCounter.getType() == SyntaxType.expression ||
			programCounter.getType() == SyntaxType.assignment ||
			programCounter.getType() == SyntaxType.flowcontrol) {
			
			currentInstruction = programCounter;
			programCounter = null;
			
		} else {
			//	Type cast from generic SyntaxNode to NodeStatement
			
			NodeStatement ns = (NodeStatement) programCounter;
			
			currentInstruction = ns.getOp1();
			programCounter = ns.getOp2();
		}
		
		
		if (currentInstruction.getType() == SyntaxType.expression) {
			lineCounter = ((NodeExpression) currentInstruction).getLineCounter();
			evalExpression();
			
		} else if (currentInstruction.getType() == SyntaxType.assignment) {
			lineCounter = ((NodeAssignment) currentInstruction).getLineCounter();
			evalAssignment();
		}
		
		if (!errorMsg.isBlank()) programCounter = null;
	}
	
	private void evalExpression() {
		SyntaxNode node = ((NodeExpression) currentInstruction).getNode();
		Token token = new Token(TokenKind.noobToken, "", -1);
		
		if (node.getType() == SyntaxType.comment) {
			return;
		} else if (node.getType() == SyntaxType.mathop) {
			token = evalMathOp((NodeOperation) node);

			symbolTable.get(0).setKindValue(token);
		
		} else if (node.getType() == SyntaxType.boolop) {
			token = evalBoolOp((NodeOperation) node);

			symbolTable.get(0).setKindValue(token);
		
		} else if (node.getType() == SyntaxType.cmpop) {
			token = evalCmpOp((NodeOperation) node);
			
			symbolTable.get(0).setKindValue(token);
			
		} else if (node.getType() == SyntaxType.print) {
			token = evalPrint((NodeOperation) node);
			
			symbolTable.get(0).setKindValue(token);
			
			if (window != null && errorMsg.isEmpty()) window.updateConsole(token.getValue());
		}
		
		if (window == null && token != null) token.viewToken();
	}
	
	private void evalAssignment() {
		SyntaxNode node = ((NodeAssignment) currentInstruction).getNode();
		
		if (node.getType() == SyntaxType.newvar) {
			evalNewVar((NodeDeclaration) node);
		}
	}
	
	private void evalNewVar(NodeDeclaration node) {		
		SymTabEntry newVar;
		
		String varid = node.getVarID().getValue();
		
		if (findVarValue(varid) < symbolTable.size()) {
			errorMsg = "Line " + lineCounter + ": Duplicate instantiation of new variable";
			return;
		}
		
		if (node.getValue() != null) {
			Token value = evalTerminal(node.getValue());
			newVar = new SymTabEntry(varid, value.getTokenKind(), value.getValue());
		} else {
			newVar = new SymTabEntry(varid, TokenKind.noobToken, "");
		}
		
		symbolTable.add(newVar);
	}
	
	private Token evalPrint(NodeOperation node) {
		String str = new String();
		SyntaxNode operand1;
		NodeOperation currentNode = node;
		int symbolTableIdx;
		boolean suppressNL = false;

		while (true) {
			operand1 = currentNode.getOp1();
			
			
			if (operand1.getType() == SyntaxType.literal) {
				
				if (((NodeLiteral) operand1).getToken().getTokenKind() == TokenKind.exclamationToken) {
					suppressNL = true;
				} else {
					str += ((NodeLiteral) operand1).getToken().getValue();
				}	
				
			} else if (operand1.getType() == SyntaxType.varid) {
				symbolTableIdx = findVarValue(((NodeLiteral) operand1).getToken().getValue().trim());
				
				if (symbolTableIdx >= symbolTable.size() ||
					symbolTable.get(symbolTableIdx).getKind() == TokenKind.noobToken) {
					
					errorMsg = "Line " + lineCounter + ": Uninitialized variable "; 
					
				} else if (symbolTableIdx < symbolTable.size()) {
					str += symbolTable.get(symbolTableIdx).getValue();
				}
			
			//	TODO infarop (?)
			} else if (operand1.getType() == SyntaxType.mathop ||
					   operand1.getType() == SyntaxType.boolop ||
					   operand1.getType() == SyntaxType.cmpop) {
				str += evalTerminal(operand1).getValue();
			}
			
			
			
			if (currentNode.getOp2() == null) break;
			else currentNode = (NodeOperation) currentNode.getOp2();
		}
		
		str = str.replaceAll("\\\\n", "\n");
		str = str.replaceAll("\\\\t", "\t");
		
		if (!suppressNL) str += "\n";
		
		return new Token(TokenKind.yarnToken, str, -1);
	}
	
	private Token evalMathOp(NodeOperation node) {
		float op1, op2, result;
		boolean isFloat = false;
		
		Token operation = node.getOperation();
		Token operand1 = evalTerminal(node.getOp1());
		Token operand2 = evalTerminal(node.getOp2());
		
		if (operand1.getValue().contains(".") || operand2.getValue().contains(".")) isFloat = true;
		
		
		op1 = Float.parseFloat(typecastToken(operand1, TokenKind.numbarToken).getValue());
		op2 = Float.parseFloat(typecastToken(operand2, TokenKind.numbarToken).getValue());
		
		
		
		if (operation.getTokenKind() == TokenKind.sumOpToken) {
			result = op1 + op2;
		} else if (operation.getTokenKind() == TokenKind.diffOpToken) {
			result = op1 - op2;
		} else if (operation.getTokenKind() == TokenKind.mulOpToken) {
			result = op1 * op2;
		} else if (operation.getTokenKind() == TokenKind.divOpToken) {
			result = op1 / op2;
		} else if (operation.getTokenKind() == TokenKind.maxOpToken) {
			result = Math.max(op1, op2);
		} else if (operation.getTokenKind() == TokenKind.minOpToken) {
			result = Math.min(op1, op2);
		} else {
			result = op1 % op2;
		}
		
		if (operand1.getTokenKind() == TokenKind.numbarToken || operand2.getTokenKind() == TokenKind.numbarToken || isFloat) {
			return new Token(TokenKind.numbarToken, Float.toString(result), -1);
		}
		
		return new Token(TokenKind.numbrToken, Integer.toString((int)result), -1);
	}
	
	private Token evalBoolOp(NodeOperation node) {
		boolean op1, op2, result;
		
		op1 = op2 = true;
		
		Token operation = node.getOperation();
		Token operand1 = evalTerminal(node.getOp1());
		
		
		operand1 = typecastToken(operand1, TokenKind.troofToken);
		if (operand1.getValue().equals("WIN")) op1 = true;
		else op1 = false;
		
		
		if (operation.getTokenKind() != TokenKind.notOpToken) {
			Token operand2 = evalTerminal(node.getOp2());
			
			operand2 = typecastToken(operand2, TokenKind.troofToken);
			if (operand2.getValue().equals("WIN")) op2 = true;
			else op2 = false;
		}
		
		
		if (operation.getTokenKind() == TokenKind.bothOpToken) {
			result = op1 && op2;
		} else if (operation.getTokenKind() == TokenKind.eitherOpToken) {
			result = op1 || op2;
		} else if (operation.getTokenKind() == TokenKind.wonOpToken) {
			result = (op1 || op2) && (!op1 || !op2);
		} else {
			result = !op1;
		} 
		
		
		if (result) {
			return new Token(TokenKind.troofToken, "WIN", -1);
		}
		
		return new Token(TokenKind.troofToken, "FAIL", -1);
		
	}
	
	private Token evalCmpOp(NodeOperation node) {
		boolean result = false;
		
		Token operation = node.getOperation();
		Token operand1 = evalTerminal(node.getOp1());
		Token operand2 = evalTerminal(node.getOp2());
		
		
		if (operand1.getTokenKind() == operand2.getTokenKind()) {
			
			if (operation.getTokenKind() == TokenKind.bothSameOpToken) {
				
				if (operand1.getValue().equals(operand2.getValue())) result = true;
				else result = false;
				
			} else {
				
				if (operand1.getValue().equals(operand2.getValue())) result = false;
				else result = true;
			}
			
		}
		
		
		if (result) {
			return new Token(TokenKind.troofToken, "WIN", -1);
		}
		
		return new Token(TokenKind.troofToken, "FAIL", -1);
	}
	
	private Token evalTerminal(SyntaxNode operand) {		
		if (operand.getType() == SyntaxType.varid) {
			Token token = ((NodeLiteral) operand).getToken();
			int idx = findVarValue(token.getValue());
			
			if (idx == symbolTable.size()) {
				this.errorMsg = "Line "+ lineCounter + ": Unbound variable <"+token.getValue()+">";
				return new Token(TokenKind.numbrToken, "0", -1);
			} else if (symbolTable.get(idx).getValue().isEmpty()) {
				this.errorMsg = "Line "+ lineCounter + ": Uninitialized variable <"+token.getValue()+">";
				return new Token(TokenKind.numbrToken, "0", -1);
			} else {
				SymTabEntry entry = symbolTable.get(idx);
				return new Token(entry.getKind(), entry.getValue(), token.getPosition());
			}
			
		} else if (operand.getType() == SyntaxType.mathop) {
			return evalMathOp((NodeOperation) operand);
			
		} else if (operand.getType() == SyntaxType.boolop) {
			return evalBoolOp((NodeOperation) operand);
			
		} else if (operand.getType() == SyntaxType.cmpop) {
			return evalCmpOp((NodeOperation) operand);
					
		}
		
		return ((NodeLiteral) operand).getToken();
	}
	
	private Token typecastToken(Token token, TokenKind kind) {
		float numbar = 0;
		int numbr = 0;
		boolean troof = true, isFloat = true, isInt = true, isYarn = true;
		String yarn = new String();
		
		
		
		if (token.getTokenKind() == kind) return token;
		
		
		if (token.getTokenKind() == TokenKind.numbrToken) {
			numbar = Float.parseFloat(token.getValue());
			numbr = (int) Float.parseFloat(token.getValue());
			yarn = token.getValue();
			
			if (numbr == 0) troof = false;
			else troof = true;
			
		} else if (token.getTokenKind() == TokenKind.numbarToken) {
			numbar = Float.parseFloat(token.getValue());
			numbr = (int) Float.parseFloat(token.getValue());
			yarn = token.getValue();
			
			if (numbar == (float) 0) troof = false;
			else troof = true;
			
		} else if (token.getTokenKind() == TokenKind.yarnToken) {
			try {
				numbar = Float.parseFloat(token.getValue());
			} catch (Exception e) {
				isFloat = false;
			}
			
			try {
				numbr = Integer.parseInt(token.getValue());
			} catch (Exception e) {
				isInt = false;
			}
			
			yarn = token.getValue();
			
			if (yarn.isBlank() || yarn.isEmpty()) troof = false;
			else troof = true;
			
		} else if (token.getTokenKind() == TokenKind.troofToken) {
			//	troof -> yarn is not allowed
			//	TODO throw an error if attempted
			yarn = token.getValue();
			isYarn = false;
			
			if (yarn.equals("WIN")) {
				numbar = (float) 1;
				numbr = 1;
				troof = true;
				
			} else {
				numbar = (float) 0;
				numbr = 0;
				troof = false;
			}
		} else if (token.getTokenKind() == TokenKind.noobToken) {
			isInt = false;
			isFloat = false;
			yarn = new String();
			troof = true;
		}
		
 		
		
		
		if (kind == TokenKind.troofToken) {
			if (troof) return new Token(TokenKind.troofToken, "WIN", token.getPosition());
			else return new Token(TokenKind.troofToken, "FAIL", token.getPosition());
			
		} else if (kind == TokenKind.yarnToken && isYarn) {
			return new Token(TokenKind.yarnToken, yarn, token.getPosition());
		
		}
		
		if (kind == TokenKind.numbrToken && isInt) {
			return new Token(TokenKind.numbrToken, Integer.toString(numbr), token.getPosition());
			
		} else if (kind == TokenKind.numbarToken && isFloat) {
			return new Token(TokenKind.numbarToken, Float.toString(numbar), token.getPosition());
		}
		
		errorMsg = "Line "+ lineCounter + ": Type mismatch <" + token.getTokenKind() + "> cannot be typecasted to "
				+ "<" + kind + "> ";
		return new Token(TokenKind.badToken, "0", -1);
	}
	
	
	private int findVarValue(String varid) {
		int counter = 0;
		for (SymTabEntry entry : symbolTable) {
			if (entry.getIdentifier().equals(varid)) break;
			counter++;
		}
		
		return counter;
	}
	
	public void viewParserErrors() {
		for (String i : parser.getDiagnostics()) System.out.println(i);
	}
	
	public void viewParseTree() {
		root.printChildren(0);
	}
	
	public String getStrParseTree() {
		return root.getStrChildren(0);
	}
	
	public boolean isPCEmpty() {
		if (programCounter == null) return true;
		
		return false;
	}
	
	public ArrayList<Token> getTokens() {
		return tokens;
	}
	
	public ArrayList<SymTabEntry> getSymbolTable() {
		return symbolTable;
	}
	
	public ArrayList<String> getParserDiagnostics() {
		return parser.getDiagnostics();
	}
	
	public String getEvalDiagnostics() {
		return errorMsg;
	}

	public int getCurrentLine() {
		return lineCounter;
	}
	
}
