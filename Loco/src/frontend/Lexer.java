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
	private ArrayList<String> lexemes;
	private int position;

	Lexer(File file) {
		this.file = file;
		this.position = 0;

		this.lexemes = new ArrayList<String>();
		parseFile();

	}

	private void parseFile() {
		try {
			Scanner sc = new Scanner(new File("src/sample.lol"));
			while (sc.hasNextLine()) {
				String curLine = sc.nextLine();

				for (String i : curLine.split("\s")) {
					i = i.replaceAll("\t", "");
					lexemes.add(i);
				}

				if (!curLine.isBlank() && sc.hasNextLine())
					lexemes.add("\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void next() {
		position++;
	}

	private void peek(int offset) {
		position = position + offset;
	}

	private String currentLexeme() {
		return lexemes.get(position);
	}

	private String readMultiLength(int length) {
		String multiLex = new String();

		if (length < 0) {
			if (currentLexeme().matches("\".+?\"")) {
				return currentLexeme();
			}

			do {
				multiLex = multiLex + currentLexeme() + " ";
				next();
			} while (!currentLexeme().matches(".+?\""));

			return multiLex + currentLexeme();
		}

		while (length-- > 0) {
			multiLex = multiLex + currentLexeme() + " ";
			next();
		}
		peek(-1);

		return multiLex.trim();
	}

	public Token nextToken() {
		if (position >= lexemes.size()) {
			return new Token(TokenKind.eofToken, "\0", position);
		}

		for (TokenKind kind : TokenKind.values()) {
			if (currentLexeme().matches(kind.getRegex()) && kind.getLength() == 1) {
				return new Token(kind, currentLexeme(), position++);
			} else if (currentLexeme().matches(kind.getRegex())) {
				return new Token(kind, readMultiLength(kind.getLength()), position++);
			}
		}

		return new Token(TokenKind.miscToken, currentLexeme(), position++);
	}

	public void viewLexemes() {
		int count = 0;
		for (String i : lexemes)
			System.out.println("Lexeme #" + ++count + ": " + i);
	}

}
