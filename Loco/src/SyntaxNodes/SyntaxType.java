package SyntaxNodes;

public enum SyntaxType {
	root,
	statement,
	expression,
	assignment,
	flowcontrol,
	
	comment,
	newvar,
	literal,
	varid,
	
	mathop,
	boolop,
	cmpop,
	infarop,
	
	concat,
	print
}
