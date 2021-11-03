package SyntaxNodes;

public abstract class SyntaxNode {
	
	public void printTab (int x) {
		while (x-- > 0) {
			System.out.print("\t");
		}
	}
	
	public abstract void printChildren(int tab);

}
