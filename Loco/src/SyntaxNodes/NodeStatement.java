package SyntaxNodes;

public class NodeStatement extends SyntaxNode{
	SyntaxNode operand1, operand2;
	
	public NodeStatement(SyntaxNode op1, SyntaxNode op2) {
		this.operand1 = op1;
		this.operand2 = op2;
		
	}
	
	public void printChildren(int tab) {
		printTab(tab);
		System.out.println("{");
		
		printTab(tab);
		System.out.println("Statement Node");
		System.out.print("0: ");
		printTab(tab);
		operand1.printChildren(tab + 1);
		System.out.print("1: ");
		printTab(tab);
		operand2.printChildren(tab + 1);
		
		printTab(tab);
		System.out.println("}");
		
	}
	
	
}
