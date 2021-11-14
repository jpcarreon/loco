package SyntaxNodes;

public class NodeAssignment extends SyntaxNode {
	SyntaxType type;
	SyntaxNode node;
	int lineCounter;
	
	public NodeAssignment(SyntaxNode node, int lineCounter) {
		super(SyntaxType.assignment);
		this.node = node;
		this.lineCounter = lineCounter;
		
	}
	public void printChildren(int tab) {
		printTab(tab);
		System.out.println("{");
		
		printTab(tab);
		System.out.println("Line "+lineCounter+": (Assignment)");
		node.printChildren(tab + 1);
		
		printTab(tab);
		System.out.println("}");
	}
}
