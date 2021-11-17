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
	vartype,
	
	newvar,
	varchange,
	vartypechange,
	
	mathop,
	boolop,
	cmpop,
	infarop,
	
	concat,
	print,
	scan,
	
	ifblock,
	switchcase,
	loop,
	
	invalid
}
