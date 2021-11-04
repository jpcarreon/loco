package SyntaxNodes;

import frontend.Token;

public class NodeAssignment extends SyntaxNode {
	Token operation, varid;
	SyntaxNode value;
	
	public NodeAssignment(Token operation, Token varid) {
		this.operation = operation;
		this.varid = varid;
	}
	
	public NodeAssignment(Token operation, Token varid, SyntaxNode value) {
		this.operation = operation;
		this.varid = varid;
		this.value = value;
	}
	
	
	public void printChildren(int tab) {
		printTab(tab);
		System.out.println("{");
		
		printTab(tab);
		System.out.println("Assignment: " + operation.getKind());
		printTab(tab);
		System.out.print("varID: ");
		System.out.println(varid.getValue());
		printTab(tab);
		System.out.println("value: ");
		
		
		if (value != null) value.printChildren(tab + 1);
		else {
			printTab(tab + 1);
			System.out.println("<Empty>");
		}
		
		printTab(tab);
		System.out.println("}");
		
	}

}