package SyntaxNodes;

public class NodeExpression extends SyntaxNode {
	SyntaxType type;
	SyntaxNode node;
	int lineCounter;
	
	public NodeExpression(SyntaxNode node, int lineCounter) {
		super(SyntaxType.expression);
		this.node = node;
		this.lineCounter = lineCounter;
		
	}
	
	public void printChildren(int tab) {
		printTab(tab);
		System.out.println("{");
		
		printTab(tab);
		System.out.println("Line "+lineCounter+": (Expression)");
		node.printChildren(tab + 1);
		
		printTab(tab);
		System.out.println("}");
	}
	
	public SyntaxNode getNode() {
		return node;
	}
	
	public int getLineCounter() {
		return lineCounter;
	}
}
