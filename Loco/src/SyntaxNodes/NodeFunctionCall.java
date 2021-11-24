package SyntaxNodes;

import java.util.ArrayList;

import backend.Token;

public class NodeFunctionCall extends SyntaxNode {
	Token operation, functionid;
	ArrayList<SyntaxNode> parameters;
	
	public NodeFunctionCall(Token operation, Token functionid, ArrayList<SyntaxNode> parameters) {
		super(SyntaxType.functioncall);
		this.operation = operation;
		this.functionid = functionid;
		this.parameters = parameters;
	}

	
	public void printChildren(int tab) {
		int i = 0;
		
		printTab(tab);
		System.out.println("{");
		
		printTab(tab);
		System.out.println(operation.getValue() + ": ");
		printTab(tab);
		System.out.print("varID: ");
		System.out.println(functionid.getValue());
		printTab(tab);
		System.out.println("value: ");
		
		
		if (parameters.size() > 0) {
			for (i = 0; i < parameters.size(); i++) {
				printTab(tab);
				System.out.println(i + ": \n");
				parameters.get(i).printChildren(tab + 1);
			}
		} else {
			printTab(tab + 1);
			System.out.println("<Empty>");
		}
		
		printTab(tab);
		System.out.println("}");
		
	}

	
	public String getStrChildren(int tab) {
		String string = new String();
		
		return string;
	}
	
	public Token getOperation() {
		return operation;
	}

	public Token getFunctionid() {
		return functionid;
	}
	
	public ArrayList<SyntaxNode> getParameters() {
		return parameters;
	}
}
