package backend;

import java.io.File;
import java.util.ArrayList;

import SyntaxNodes.NodeExpression;
import SyntaxNodes.NodeLiteral;
import SyntaxNodes.NodeOperation;
import SyntaxNodes.NodeRoot;
import SyntaxNodes.NodeStatement;
import SyntaxNodes.SyntaxNode;
import SyntaxNodes.SyntaxType;

public class Evaluator {
	private Parser parser;
	
	private ArrayList<Token> tokens;
	private ArrayList<SymTabEntry> SymbolTable;
	private String errorMsg;
	
	private NodeRoot root;
	private SyntaxNode programCounter;
	private SyntaxNode currentInstruction;
	private int lineCounter;
	
	public Evaluator(File file) {
		this.errorMsg = new String();
		this.SymbolTable = new ArrayList<SymTabEntry>();
		this.lineCounter = 1;
		
		parser = new Parser(file);
		this.root = parser.parse();
		
		this.tokens = parser.getTokens();
		this.SymbolTable.add(new SymTabEntry("IT", TokenKind.badToken, ""));
		
		this.programCounter = root.getStatements();
	}
	
	public Evaluator(String strFile) {
		this.errorMsg = new String();
		this.SymbolTable = new ArrayList<SymTabEntry>();
		this.lineCounter = 1;
		
		parser = new Parser(strFile);
		this.root = parser.parse();
		
		this.tokens = parser.getTokens();
		this.SymbolTable.add(new SymTabEntry("IT", TokenKind.badToken, ""));
		
		this.programCounter = root.getStatements();
	}
	
	public void nextInstruction() {
		
		++lineCounter;
		
		// check if PC holds only 1 line of code 
		if (programCounter.getType() == SyntaxType.expression ||
			programCounter.getType() == SyntaxType.assignment ||
			programCounter.getType() == SyntaxType.flowcontrol) {
			
			currentInstruction = programCounter;
			programCounter = null;
			
			if (currentInstruction.getType() == SyntaxType.expression) {
				evalExpression();
			}
			
			return;
		}
		
		//	Type cast from generic SyntaxNode to NodeStatement
		NodeStatement ns = (NodeStatement) programCounter;
		
		currentInstruction = ns.getOp1();
		programCounter = ns.getOp2();
		
		if (currentInstruction.getType() == SyntaxType.expression) {
			evalExpression();
		}
		
		if (!errorMsg.isBlank()) programCounter = null;
	}
	
	private void evalExpression() {
		SyntaxNode node = ((NodeExpression) currentInstruction).getNode();
		
		if (node.getType() == SyntaxType.comment) {
			return;
		} else if (node.getType() == SyntaxType.mathop) {
			Token token = evalMathOp((NodeOperation) node);
			SymbolTable.get(0).setKindValue(token);
		}	
	}
	
	private Token evalMathOp(NodeOperation node) {
		float op1, op2, result;
		
		Token operation = node.getOperation();
		Token operand1 = evalTerminal(node.getOp1());
		Token operand2 = evalTerminal(node.getOp2());
		
		if (operand1.getTokenKind() == TokenKind.yarnToken ||
			operand2.getTokenKind() == TokenKind.yarnToken) {
			
			errorMsg = "Line "+ lineCounter + ": Unexpected <yarnToken> as operand";
			
			return new Token(TokenKind.badToken, null, -1);
		}
		
		
		if (operand1.getTokenKind() == TokenKind.troofToken) {
			if (operand1.getValue().matches("WIN")) op1 = (float) 1.0;
			else op1 = (float) 0;
		} else {
			op1 = Float.parseFloat(operand1.getValue());
		}
		
		if (operand2.getTokenKind() == TokenKind.troofToken) {
			if (operand2.getValue().matches("WIN")) op2 = (float) 1.0;
			else op2 = (float) 0;
		} else {
			op2 = Float.parseFloat(operand2.getValue());
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
		
		if (op1 == (int)op1 && op2 == (int)op2) {
			return new Token(TokenKind.numbrToken, Integer.toString((int)result), -1);
		}
		
		return new Token(TokenKind.numbarToken, Float.toString(result), -1);
		
	}
	
	private Token evalTerminal(SyntaxNode operand) {
		if (operand.getType() != SyntaxType.literal) {
			
		}
		
		return ((NodeLiteral) operand).getToken();
	}
	
	
	public void viewParserErrors() {
		for (String i : parser.getDiagnostics()) System.out.println(i);
	}
	
	public void viewParseTree() {
		root.printChildren(0);
	}
	
	public boolean isPCEmpty() {
		if (programCounter == null) return true;
		
		return false;
	}
	
	public ArrayList<Token> getTokens() {
		return tokens;
	}
	
	public ArrayList<SymTabEntry> getSymbolTable() {
		return SymbolTable;
	}
	
	public ArrayList<String> getParserDiagnostics() {
		return parser.getDiagnostics();
	}
	
	public String getEvalDiagnostics() {
		return errorMsg;
	}

	
}
