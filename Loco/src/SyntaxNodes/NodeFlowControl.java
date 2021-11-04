package SyntaxNodes;

public class NodeFlowControl extends SyntaxNode {
	SyntaxNode node;
	int lineCounter;
	
	public NodeFlowControl (SyntaxNode node, int lineCounter) {
		this.node = node;
		this.lineCounter = lineCounter;
	}
	
	public void printChildren(int tab) {
		printTab(tab);
		System.out.println("{");
		
		printTab(tab);
		System.out.println("Line "+lineCounter+": (Flow Control)");
		node.printChildren(tab + 1);
		
		printTab(tab);
		System.out.println("}");
	}
	
}
