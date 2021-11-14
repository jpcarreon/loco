package SyntaxNodes;

import java.util.ArrayList;

import backend.Token;

public class NodeComment extends SyntaxNode{
	Token start, end;	
	ArrayList<Token> inner;
	
	public NodeComment(Token start, ArrayList<Token> inner, Token end) {
		super(SyntaxType.comment);
		this.start = start;
		this.inner = inner;
		this.end = end;
	}
	
	
	public void printChildren(int tab) {
		printTab(tab);
		System.out.println("{");
		
		printTab(tab);
		System.out.println("Comment: ");
		
		for (Token i : inner) {
			printTab(tab + 1);
			System.out.print(i.getValue());
		}
		
		printTab(tab);
		System.out.println("}");
	}
	
}
