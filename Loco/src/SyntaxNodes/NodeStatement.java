package SyntaxNodes;

public class NodeStatement extends SyntaxNode{
	SyntaxNode operand1, operand2;
	
	public NodeStatement(SyntaxNode op1, SyntaxNode op2) {
		this.operand1 = op1;
		this.operand2 = op2;
		
	}
	
	public void printChildren(int tab) {
		super.printTab(tab);
		System.out.println("{");
		
		super.printTab(tab);
		System.out.println("Statement Node");
		System.out.print("0: ");
		super.printTab(tab);
		operand1.printChildren(tab + 1);
		System.out.print("1: ");
		super.printTab(tab);
		operand2.printChildren(tab + 1);
		
		super.printTab(tab);
		System.out.println("}");
		
	}
	
	
}
