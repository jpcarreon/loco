package frontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	public static void main(String[] args) {
		String text = new String();
		Scanner sc = new Scanner(System.in);
		File fp = new File("src/sample.lol");
		
		
		
		/*
		Lexer lexer = new Lexer(text);
		
		while (true) {
			Token token = lexer.nextToken();
			
			
			if (token.getKind() == TokenKind.eofToken) {
				break;
			}
			token.viewToken();
			
		}
		*/
		
		Lexer lexer = new Lexer(fp);
		
		/*
		while (true) {
			Token token = lexer.nextToken();
			
			if (token.getKind() == TokenKind.eofToken) {
				break;
			}
			
			token.viewToken();
		}
		*/
		
		lexer.scanLine();
		lexer.viewState();
		
		
	}
	
	

}
