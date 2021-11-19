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
		Token token;
		
		if (node.getType() == SyntaxType.comment) {
			return;
		} else if (node.getType() == SyntaxType.mathop) {
			token = evalMathOp((NodeOperation) node);

			symbolTable.get(0).setKindValue(token);
			
		} else if (node.getType() == SyntaxType.print) {
			token = evalPrint((NodeOperation) node);

			symbolTable.get(0).setKindValue(token);
			
			if (window != null) window.updateConsole(token.getValue());
		}
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

		while (true) {
			operand1 = currentNode.getOp1();
			
			if (operand1.getType() == SyntaxType.literal) {
				str += ((NodeLiteral) operand1).getToken().getValue();
				
				
			} else if (operand1.getType() == SyntaxType.varid) {
				symbolTableIdx = findVarValue(((NodeLiteral) operand1).getToken().getValue().trim());
				
				if (symbolTableIdx >= symbolTable.size() ||
					symbolTable.get(symbolTableIdx).getKind() == TokenKind.noobToken) {
					
					errorMsg = "Line " + lineCounter + ": Uninitialized variable "; 
					
				} else if (symbolTableIdx < symbolTable.size()) {
					str += symbolTable.get(symbolTableIdx).getValue();
				}
				
			}
			
			
			
			if (currentNode.getOp2() == null) break;
			else currentNode = (NodeOperation) currentNode.getOp2();
		}
		
		str = str.replaceAll("\\\\n", "\n");
		str = str.replaceAll("\\\\t", "\t");
		
		return new Token(TokenKind.yarnToken, str, -1);
	}
	
	private Token evalMathOp(NodeOperation node) {
		float op1, op2, result;
		
		op1 = op2 = (float) 0;
		
		Token operation = node.getOperation();
		Token operand1 = evalTerminal(node.getOp1());
		Token operand2 = evalTerminal(node.getOp2());
		
		
		if (operand1.getTokenKind() == TokenKind.yarnToken) {
			
			try {
				op1 = Float.parseFloat(operand1.getValue());
			} catch (Exception e) {
				errorMsg = "Line "+ lineCounter + ": Type mismatch <yarnToken> cannot be typecast for this operation";
				return new Token(TokenKind.badToken, null, -1);
			} 
			
		} else if (operand1.getTokenKind() == TokenKind.troofToken) {
			if (operand1.getValue().matches("WIN")) op1 = (float) 1.0;
			else op1 = (float) 0;
		} else if (operand1.getTokenKind() == TokenKind.numbrToken ||
				   operand1.getTokenKind() == TokenKind.numbarToken) {
			op1 = Float.parseFloat(operand1.getValue());
		} else {
			errorMsg = "Line "+ lineCounter + ": Unexpected <"+operand2.getTokenKind()+"> as operand";
			return new Token(TokenKind.badToken, null, -1);
		}
		
		if (operand2.getTokenKind() == TokenKind.yarnToken) {
			
			try {
				op2 = Float.parseFloat(operand2.getValue());
			} catch (Exception e) {
				errorMsg = "Line "+ lineCounter + ": Type mismatch <yarnToken> cannot be typecast for this operation";
				return new Token(TokenKind.badToken, null, -1);
			} 
			
		} else if (operand2.getTokenKind() == TokenKind.troofToken) {
			if (operand2.getValue().matches("WIN")) op2 = (float) 1.0;
			else op2 = (float) 0;
		} else if (operand2.getTokenKind() == TokenKind.numbrToken ||
				   operand2.getTokenKind() == TokenKind.numbarToken) {
			op2 = Float.parseFloat(operand2.getValue());
		} else {
			errorMsg = "Line "+ lineCounter + ": Unexpected <"+operand2.getTokenKind()+"> as operand";
			return new Token(TokenKind.badToken, null, -1);
		}
		
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
		
		if (operand1.getTokenKind() == TokenKind.numbrToken && operand2.getTokenKind() == TokenKind.numbrToken) {
			return new Token(TokenKind.numbrToken, Integer.toString((int)result), -1);
		}
		
		return new Token(TokenKind.numbarToken, Float.toString(result), -1);
		
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
		}
		
		return ((NodeLiteral) operand).getToken();
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
