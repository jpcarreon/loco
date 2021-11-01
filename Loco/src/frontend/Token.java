package frontend;

public class Token {
	private TokenKind tokenKind;
	private String value;
	private int position;
	
	Token (TokenKind kind, String value, int position) {
		this.tokenKind = kind;
		this.value = value;
		this.position = position;
	}
	
	public TokenKind getKind() {
		return this.tokenKind;
	}
	
	public void viewToken() {
		System.out.println(tokenKind + ": " + value);
	}
}
