package backend;

import java.io.File;
import java.util.ArrayList;

import SyntaxNodes.SyntaxNode;

public class Evaluator {
	private ArrayList<Token> tokens;
	private ArrayList<String> diagnostics;
	private ArrayList<SymTabEntry> SymbolTable;
	
	private SyntaxNode root;
	private int lineCounter;
	
	public Evaluator(File file) {
		this.SymbolTable = new ArrayList<SymTabEntry>();
		this.lineCounter = 1;
		
		Parser parser = new Parser(file);
		this.root = parser.parse();
		
		this.tokens = parser.getTokens();
		this.diagnostics = parser.getDiagnostics();
		this.SymbolTable.add(new SymTabEntry("IT", TokenKind.badToken, ""));
	}
	
	public Evaluator(String strFile) {
		this.SymbolTable = new ArrayList<SymTabEntry>();
		this.lineCounter = 1;
		
		Parser parser = new Parser(strFile);
		this.root = parser.parse();
		
		this.tokens = parser.getTokens();
		this.diagnostics = parser.getDiagnostics();
		this.SymbolTable.add(new SymTabEntry("IT", TokenKind.badToken, ""));
	}
	
	public void viewErrors() {
		for (String i : diagnostics) System.out.println(i);
	}

	public void viewParseTree() {
		root.printChildren(0);
	}

	public ArrayList<Token> getTokens() {
		return tokens;
	}
	
	public ArrayList<SymTabEntry> getSymbolTable() {
		return SymbolTable;
	}
	
	public ArrayList<String> getDiagnostics() {
		return diagnostics;
	}
	
}
