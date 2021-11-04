package SyntaxNodes;

public class NodeExpression extends SyntaxNode {
	SyntaxNode node;
	int lineCounter;
	
	public NodeExpression(SyntaxNode node, int lineCounter) {
		this.node = node;
		this.lineCounter = lineCounter;
		
	}
	public void printChildren(int tab) {
		printTab(tab);
		System.out.println("{");
		
		printTab(tab);
		System.out.println("Line "+lineCounter+": ");
		node.printChildren(tab + 1);
		
		printTab(tab);
		System.out.println("}");
	}
}
