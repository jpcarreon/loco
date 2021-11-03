package frontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Lexer {
	private File file;
	private ArrayList<String> lexemes;
	private int position;

	Lexer(File file) {
		this.file = file;
		this.position = 0;
		this.lexemes = new ArrayList<String>();
		
		parseFile();
		fixLexemes();

	}

	//	Stores lexemes into the ArrayList lexemes
	private void parseFile() {
		try {
			Scanner sc = new Scanner(file);
			//	Read file line by line
			while (sc.hasNextLine()) {
				String curLine = sc.nextLine();
				
				//	Extract words/digits in the current line
				for (String i : curLine.split("")) {
					//	Remove any indentation in the current word
					i = i.replaceAll("\t", "");
					
					lexemes.add(i);
				}
				
				//	Append a "\n" lexeme to symbolize that the current line has ended
				if (!curLine.isBlank() && sc.hasNextLine())
					lexemes.add("\n");
			}
			
			sc.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void fixLexemes() {
		ArrayList<String> fixedList = new ArrayList<String>();
		String newLexeme = new String();
		
		for (String i : lexemes) {

			//if (i == "\n") System.out.println("NewLine");

			if (i.matches("[^\s\"\n]")) {
				newLexeme = newLexeme + i;
			} else {
				
				if (!newLexeme.isBlank()) {
					fixedList.add(newLexeme);
					newLexeme = new String();
				} 
				
				if (!i.isBlank()) {
					fixedList.add(i);
				} else if (i == "\n") {
					fixedList.add(i);
				}
				
			}
		}
		
		//for (String i : fixedList) System.out.println("S: "+i);
		lexemes = fixedList;
		
	}

	private void next() {
		position++;
	}

	private String peek(int offset) {
		if (position + offset >= lexemes.size()) return lexemes.get(position);
		
		return lexemes.get(position + offset);
	}

	private String currentLexeme() {
		return peek(0);
	}

	//	Scan a TokenType that has multiple 'words' e.g. SUM OF, I HAS A
	private String readMultiLength(int length) {
		String multiLex = new String();
		next();
		
		while (length-- > 0) {
			
		}

		peek(-1);
		

		return multiLex.trim();
	}

	//	Get next token from the list of lexemes
	public Token nextToken() {
		String lexeme = new String();
		
		//	Check if current position of the lexer has exceeded the number of lexemes
		if (position >= lexemes.size()) {
		    return new Token(TokenKind.eofToken, "\0", position);
		}
		
		lexeme = currentLexeme() + " " + peek(1) + " " + peek(2);
		for (TokenKind kind : TokenKind.values()) {
			if (kind.getLength() == 3 && lexeme.matches(kind.getRegex())) {
				position = position + 3;
				return new Token(kind, lexeme, position - 3);
			}
		}
		
		lexeme = currentLexeme() + " " + peek(1);
		for (TokenKind kind : TokenKind.values()) {
			if (kind.getLength() == 2 && lexeme.matches(kind.getRegex())) {
				position = position + 2;
				return new Token(kind, lexeme, position - 2);
			}
		}

		for (TokenKind kind : TokenKind.values()) {
			if (kind.getLength() == 1 && currentLexeme().matches(kind.getRegex())) {
				return new Token(kind, currentLexeme(), position++);
			} 
		}
		
		return new Token(TokenKind.badToken, currentLexeme(), position++);
	}

	public void viewLexemes() {
		int count = 0;
		for (String i : lexemes)
			System.out.println("Lexeme #" + ++count + ":" + i);
	}

}
