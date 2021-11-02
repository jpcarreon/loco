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
		
		/*
		try (InputStream in = new FileInputStream(file)) {
			int content;
			String curValue = new String();
			
			while ((content = in.read()) != -1) {
				if (!((char)content == '\r')) {
					curValue = curValue + (char) content;
				}
			}
			
			System.out.println(curValue);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		try {
			Scanner sc = new Scanner(new File("src/sample.lol"));
			while (sc.hasNextLine()) {
				/*
				for (String i : sc.nextLine().split("\s")) {
					i = i.replaceAll("\t", "");
					if (!i.isBlank()) lexemes.add(i);
				}
				*/
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
	
	/*
	if (currentToken().contains("\"")) {
				String tempString = new String();
				
				do {
					tempString = tempString + currentToken() + " ";
					next();
				} while (!currentToken().contains("\""));
				
				return new Token(TokenKind.yarnToken, tempString+currentToken(), position++);
				
			}
			
			if (currentToken().matches("-?[0-9]+")) {
				return new Token(TokenKind.numbrToken, currentToken(), position++);
			} else if (currentToken().matches("-?[0-9]+.?[0-9]+")) {
				return new Token(TokenKind.numbarToken, currentToken(), position++);
			} else if (currentToken().matches("(WIN|FAIL)")) {
				return new Token(TokenKind.troofToken, currentToken(), position++);
			} else if (currentToken().matches("(TROOF|NOOB|NUMBR|NUMBAR|YARN)")) {
				return new Token(TokenKind.typeToken, currentToken(), position++);
			
			} else if (currentToken().matches("HAI")) {
				return new Token(TokenKind.haiToken, currentToken(), position++);
			} else if (currentToken().matches("KTHXBYE")) {
				return new Token(TokenKind.byeToken, currentToken(), position++);
				
			} else if (currentToken().matches("BTW")) {
				return new Token(TokenKind.btwToken, currentToken(), position++);
			} else if (currentToken().matches("OBTW")) {
				return new Token(TokenKind.obtwToken, currentToken(), position++);
			} else if (currentToken().matches("TLDR")) {
				return new Token(TokenKind.tldrToken, currentToken(), position++);
			} else if (currentToken().matches("I\sHAS\sA")) {
				return new Token(TokenKind.ihasToken, currentToken(), position++);
			} else if (currentToken().matches("ITZ")) {
				return new Token(TokenKind.itzToken, currentToken(), position++);
			} else if (currentToken().matches("R")) {
				return new Token(TokenKind.rToken, currentToken(), position++);
				
			} else if (currentToken().matches("SUM\sOF")) {
				return new Token(TokenKind.sumOpToken, currentToken(), position++);
			} else if (currentToken().matches("DIFF\sOF")) {
				return new Token(TokenKind.diffOpToken, currentToken(), position++);
			} else if (currentToken().matches("QUOSHUNT\sOF")) {
				return new Token(TokenKind.divOpToken, currentToken(), position++);
			} else if (currentToken().matches("PRODUKT\sOF")) {
				return new Token(TokenKind.mulOpToken, currentToken(), position++);
			} else if (currentToken().matches("MOD\sOF")) {
				return new Token(TokenKind.modOpToken, currentToken(), position++);
			} else if (currentToken().matches("BIGGR\sOF")) {
				return new Token(TokenKind.maxOpToken, currentToken(), position++);
			} else if (currentToken().matches("SMALLR\sOF")) {
				return new Token(TokenKind.minOpToken, currentToken(), position++);
			}
	*/
	
	public void scanLine() {
		while (position < lines.size()) {
			
			//	Start & End keywords
			findMatches("HAI", TokenKind.haiToken);
			findMatches("KTHXBYE", TokenKind.byeToken);
			
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
			
			next();
		}
		Token newToken = new Token(TokenKind.eofToken, "\0", position);
		lexemes.add(newToken);
	}
	
	
	
}
