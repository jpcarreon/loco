package SyntaxNodes;

import frontend.Token;

public class NodeLiteral extends SyntaxNode {
	Token literalToken;
	
	public NodeLiteral(Token token) {
		this.literalToken = token;
	}
	
	public void printChildren(int tab) {
		System.out.println(literalToken.getValue());
	}
}
