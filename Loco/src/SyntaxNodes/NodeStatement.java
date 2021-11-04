package SyntaxNodes;

public class NodeStatement extends SyntaxNode{
	SyntaxNode operand1, operand2;
	int lineCounter;
	static int counter = 0;
	
	public NodeStatement(SyntaxNode op1, SyntaxNode op2) {
		this.operand1 = op1;
		this.operand2 = op2;
		
		this.lineCounter = ++counter;
		
	}
	
	public void printChildren(int tab) {
		operand1.printChildren(tab);
		operand2.printChildren(tab);
	}
	
	private void printTree(int tab) {
		
		printTab(tab);
		System.out.println("{");
		
		printTab(tab);
		System.out.println("Line " + (Math.abs(lineCounter - counter) + 2) + ":");
		operand1.printChildren(tab + 1);
		
		
		printTab(tab);
		System.out.println("Line " + (Math.abs(lineCounter - counter) + 3) + ":");
		operand2.printChildren(tab + 1);
		
		printTab(tab);
		System.out.println("}");
		
	}
}
