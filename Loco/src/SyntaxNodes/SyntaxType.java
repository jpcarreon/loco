package SyntaxNodes;

public enum SyntaxType {
	root,
	statement,
	expression,
	assignment,
	flowcontrol,
	
	comment,
	literal,
	varid,
	
	newvar,
	varchange,
	
	mathop,
	boolop,
	cmpop,
	infarop,
	
	concat,
	print
}
