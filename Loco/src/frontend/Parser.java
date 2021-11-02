package frontend;

import java.io.File;
import java.util.ArrayList;

public class Parser {
	private ArrayList<Token> tokens;
	
	Parser (File file) {
		tokens = new ArrayList<Token>();
		Lexer lexer = new Lexer(file);
		Token curToken;
		
		do {
			curToken = lexer.nextToken();
			
			if (curToken.getKind() != TokenKind.miscToken) {
				tokens.add(curToken);
			}
		} while (curToken.getKind() != TokenKind.eofToken);
	}
	
	public void viewTokens() {
		for (Token i : tokens) i.viewToken();
	}
}
