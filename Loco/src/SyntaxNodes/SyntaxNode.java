package SyntaxNodes;

public abstract class SyntaxNode {
	protected SyntaxType type;
	
	public SyntaxNode(SyntaxType type) {
		this.type = type;
	}
	
	public void printTab (int x) {
		while (x-- > 0) {
			System.out.print("\t");
		}
	}
	
	public abstract void printChildren(int tab);
	
	public SyntaxType getType() {
		return type;
	};
}
