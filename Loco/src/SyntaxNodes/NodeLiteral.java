package SyntaxNodes;

import frontend.Token;
import frontend.TokenKind;

public class NodeLiteral extends SyntaxNode {
	Token literalToken;
	
	public NodeLiteral(Token token) {
		this.literalToken = token;
		
		
	}
	
	public void printChildren(int tab) {
		printTab(tab);
		System.out.println(literalToken.getValue());
	}
}
