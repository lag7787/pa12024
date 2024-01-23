package miniJava.SyntacticAnalyzer;

public class Token {
	
	private TokenKind kind;
	private String spelling; 
	
	public Token(TokenKind kind, String spelling) {
		this.kind = kind;
		this.spelling = spelling;
	}

	public TokenKind getKind() {
		return kind;
	}

	public void setKind(TokenKind kind) {
		this.kind = kind;
	}
	
	public String getSpelling() {
		return spelling;
	}


}
