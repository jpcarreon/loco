package backend;

public enum TokenKind {
	haiToken("HAI", 1), byeToken("KTHXBYE", 1),

	numbrToken("-?[0-9]+", 1), numbarToken("-?[0-9]+\\.[0-9]+", 1), yarnToken("(\".+?|\"[^\"]+\")", -1), troofToken("WIN|FAIL", 1), typeToken("(TROOF|NUMBR|NUMBAR|YARN)", 1),

	btwToken("BTW", 1), obtwToken("OBTW", 1), tldrToken("TLDR", 1),

	ihasToken("I\sHAS\sA", 3), itzToken("ITZ", 1), rToken("^R$", 1), 

	sumOpToken("SUM\sOF", 2), 
	diffOpToken("DIFF\sOF", 2), 
	divOpToken("QUOSHUNT\sOF", 2), 
	mulOpToken("PRODUKT\sOF", 2), 
	modOpToken("MOD\sOF", 2), 
	minOpToken("SMALLR\sOF", 2), 
	maxOpToken("BIGGR\sOF", 2),
	
	bothOpToken("BOTH\sOF", 2), 
	eitherOpToken("EITHER\sOF", 2), 
	wonOpToken("WON\sOF", 2), 
	notOpToken("NOT", 1), 
	anyOpToken("ANY\sOF", 2), 
	allOpToken("ALL\sOF", 2),
	
	bothSameOpToken("BOTH\sSAEM", 2),
	diffrntOpToken("DIFFRINT", 1),
	
	smooshToken("SMOOSH", 1),
	
	isNowToken("IS\sNOW\sA", 3),
	maekToken("MAEK", 1),
	mkayToken("MKAY", 1),
	aToken("^A$", 1),
	anToken("^AN$", 1), 
	
	printToken("VISIBLE", 1),
	scanToken("GIMMEH", 1),
	
	ifStartToken("O\sRLY\\?", 2),
	
	ifBlockToken("YA\sRLY", 2),
	elifBlockToken("MEBBE", 1),
	elseBlockToken("NO\sWAI", 2),
	
	switchToken("WTF\\?", 1),
	caseToken("^OMG$", 1),
	defaultToken("OMGWTF", 1),
	breakToken("GTFO", 1),
	
	ifEndToken("OIC", 1),
	
	loopStartToken("IM\sIN\sYR", 3),
	
	incToken("UPPIN", 1),
	decToken("NERFIN", 1),
	yrToken("^YR$", 1),
	tilToken("TIL", 1),
	wileToken("WILE", 1),
	
	loopEndToken("IM\sOUTTA\sYR", 3),
	
	idToken("[a-zA-Z][a-zA-Z0-9_]*", 1), 

	quoteToken("\"", 1), 
	
	eolToken("\n", 1), eofToken("\0", 1), 
	
	miscToken("[^\"\s]+", 1), badToken("", 1);

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
