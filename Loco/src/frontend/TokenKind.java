package frontend;

public enum TokenKind {
	haiToken("HAI", 1), byeToken("KTHXBYE", 1),

	numbrToken("-?[0-9]+", 1), numbarToken("-?[0-9]+\\.[0-9]+", 1), yarnToken("(\".+?|\".+?\")", -1), troofToken("WIN|FAIL", 1), typeToken("(TROOF|NUMBR|NUMBAR|YARN)", 1),

	btwToken("BTW", 1), obtwToken("OBTW", 1), tldrToken("TLDR", 1),

	ihasToken("I", 3), itzToken("ITZ", 1), rToken("R", 1),

	sumOpToken("SUM", 2), diffOpToken("DIFF", 2), divOpToken("QUOSHUNT", 2), mulOpToken("PRODUKT", 2), modOpToken("MOD", 2), minOpToken("SMALLR", 2), maxOpToken("BIGGR", 2),

	anToken("AN", 1), printToken("VISIBLE", 1),
	
	varIdToken("[a-zA-Z][a-zA-Z0-9_]*", 1), loopIdToken("(?<=(IM\sIN\sYR\s))[a-zA-Z][a-zA-Z0-9_]*", 1),

	eolToken("\n", 1), eofToken("\0", 1), miscToken(".+", 1);

	private final String regex;
	private final int length;

	TokenKind(String regex, int length) {
		this.regex = regex;
		this.length = length;
	}

	public String getRegex() {
		return regex;
	}
	
	public int getLength() {
		return length;
	}
}
