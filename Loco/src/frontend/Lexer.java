package frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
	private File file;
	private ArrayList<Token> lexemes;
	private ArrayList<String> lines;
	private int position;
	
	Lexer(File file) {
		this.file = file;

		this.lexemes = new ArrayList<Token>();
		this.lines = new ArrayList<String>();
		parseFile();

	}
	
	private void parseFile() {
		try {
			Scanner sc = new Scanner(new File("src/sample.lol"));
			while (sc.hasNextLine()) {
				String temp = sc.nextLine();
				temp = temp.replaceAll("\t", "");
				if (!temp.isBlank()) lines.add(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void next() {
		position++;
	}
	
	private String currentLine() {
		return lines.get(position);
	}
	
	public void viewState() {
		for (Token i : lexemes) {
			i.viewToken();
		}
		System.out.println();
	}
	
	private void findMatches(String regex, TokenKind kind) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(currentLine());
		
		while(matcher.find()) {
			Token newToken = new Token(kind, currentLine().substring(matcher.start(), matcher.end()), position);
			
			lexemes.add(newToken);
		}
		lines.set(position, currentLine().replaceAll(regex, ""));
	}

	public void scanLine() {
		Token newToken;
		
		while (position < lines.size() - 1) {
			
			//	Start keywords
			findMatches("HAI", TokenKind.haiToken);
			
			//	Literal Strings and Bool
			findMatches("\".+?\"", TokenKind.yarnToken);
			findMatches("(WIN|FAIL)", TokenKind.troofToken);
			
			//	Comment keywords
			findMatches("BTW", TokenKind.btwToken);
			findMatches("OBTW", TokenKind.obtwToken);
			findMatches("TLDR", TokenKind.tldrToken);
			
			//	Var declaration keyword
			findMatches("I\sHAS\sA", TokenKind.ihasToken);
			
			
			//	Arithmetic keywords
			findMatches("SUM\sOF", TokenKind.sumOpToken);
			findMatches("DIFF\sOF", TokenKind.diffOpToken);
			findMatches("PRODUKT\sOF", TokenKind.mulOpToken);
			findMatches("QUOSHUNT\sOF", TokenKind.divOpToken);
			
			//	Conjunction keyword
			findMatches("AN", TokenKind.anToken);

			
			findMatches("(?<=(IM\\sIN\\sYR\\s))[a-zA-Z][a-zA-Z0-9_]*", TokenKind.loopIdToken);
			
			findMatches("IM\sIN\sYR", TokenKind.loopToken);
			
			findMatches("[a-zA-Z][a-zA-Z0-9_]*", TokenKind.varIdToken);
			
			//	Literal int and float
			findMatches("-?[0-9]+\\.[0-9]+", TokenKind.numbarToken);
			findMatches("-?[0-9]+", TokenKind.numbrToken);
			
			findMatches("[a-zA-Z0-9]+", TokenKind.miscToken);
			
			newToken = new Token(TokenKind.eolToken, "\n", position);
			lexemes.add(newToken);
			
			next();
		}
		findMatches("KTHXBYE", TokenKind.byeToken);
	}
	
	
	
}
