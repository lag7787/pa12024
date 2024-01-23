package miniJava.SyntacticAnalyzer;

public enum TokenKind {
	IDENTIFIER,//
	NUMBER,//
	BINOP,//
	UNOP,//
	LPAREN,//
	RPAREN, //
	LBRACKET, //
	RBRACKET, //
	SEMICOLON, //
	COMMA, //
	ERROR,
	CLASS, //
	PUBLIC, //
	PRIVATE, //
	VOID, //
	STATIC, //
	INT, ///
	BOOLEAN, //
	THIS, //
	RETURN,//
	IF, //
	ELSE, //
	WHILE, //
	NEW, //
	ASSIGNMENT, // need take two if were tokenizing as a equality binop 
	SQRLBRACKET, //
	SQRRBRACKET, //
	DOT, //
	EOT, //

}