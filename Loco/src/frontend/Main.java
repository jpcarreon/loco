package frontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	public static void main(String[] args) {
		File fp = new File("src/sample.lol");
		
		//Parser parser = new Parser(fp);
		
		//parser.parse();
		//parser.viewErrors();

		Lexer lexer = new Lexer(fp);

		Token token;
		
		
		do {
			token = lexer.nextToken();
			
			if (token.getKind() != TokenKind.badToken) token.viewToken();
		} while (token.getKind() != TokenKind.eofToken);
		
		
	}
}
