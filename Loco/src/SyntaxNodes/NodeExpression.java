package SyntaxNodes;

public class NodeExpression extends SyntaxNode {
	SyntaxNode node;
	
	public NodeExpression(SyntaxNode node) {
		this.node = node;
		
	}
	public void printChildren(int tab) {
		printTab(tab);
		System.out.println("{");
		
		printTab(tab);
		System.out.println("Expression Node: ");
		node.printChildren(tab + 1);
		
		printTab(tab);
		System.out.println("}");
	}
}
