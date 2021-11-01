package frontend;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		String text = new String();
		Scanner sc = new Scanner(System.in);
		
		
		while (!text.equals("exit")) {
			System.out.print("> ");
			text = sc.nextLine();
			
			if (text.isBlank()) continue;
			
			
			Lexer lexer = new Lexer(text);
			
			while (true) {
				Token token = lexer.nextToken();
				
				
				if (token.getKind() == TokenKind.eofToken) {
					break;
				}
				token.viewToken();
				
			}
		}
		
		System.out.println("END");
		
		
	}

}
