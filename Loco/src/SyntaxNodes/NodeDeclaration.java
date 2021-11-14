package SyntaxNodes;

import backend.Token;

public class NodeDeclaration extends SyntaxNode {
	Token operation, varid;
	SyntaxNode value;
	
	public NodeDeclaration(Token operation, Token varid) {
		super(SyntaxType.newvar);
		this.operation = operation;
		this.varid = varid;
	}
	
	public NodeDeclaration(Token operation, Token varid, SyntaxNode value) {
		super(SyntaxType.newvar);
		this.operation = operation;
		this.varid = varid;
		this.value = value;
	}
	
	
	public void printChildren(int tab) {
		printTab(tab);
		System.out.println("{");
		
		printTab(tab);
		System.out.println(operation.getValue() + ": ");
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
