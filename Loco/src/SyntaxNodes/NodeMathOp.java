package SyntaxNodes;

import frontend.Token;

public class NodeMathOp extends SyntaxNode{
	Token operation;
	SyntaxNode operand1, operand2;
	
	public NodeMathOp (Token mathOp, SyntaxNode op1, SyntaxNode op2) {
		this.operation = mathOp;
		this.operand1 = op1;
		this.operand2 = op2;
		
	}
	
	public void printChildren(int tab) {
		printTab(tab);
		System.out.println("{");
		
		printTab(tab);
		System.out.println("MathOP: " + operation.getKind());
		printTab(tab);
		System.out.print("0: ");
		operand1.printChildren(tab + 1);
		printTab(tab);
		System.out.print("1: ");
		operand2.printChildren(tab + 1);
		
		printTab(tab);
		System.out.println("}");
		
	}
}
