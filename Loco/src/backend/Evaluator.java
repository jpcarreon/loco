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
	}
}
