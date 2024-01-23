package miniJava.SyntacticAnalyzer;

import miniJava.ErrorReporter;
import miniJava.SyntacticAnalyzer.TokenKind;

public class Parser {
	
	private Scanner scanner;
	private ErrorReporter errorReporter;
	private Token token;
	private boolean trace = true;
	
	public Parser(Scanner scanner, ErrorReporter errorReporter) {
		this.scanner = scanner;
		this.errorReporter = errorReporter;
	}
	
	class SyntaxError extends Error {
		private static final long serialVersionUID = 1L;	
	}

	public void parse() {
		token = scanner.scan();
		try {
			parseProgram();
		} catch (SyntaxError e) {
			System.out.println("ERROR");
		}
		return;
	}
	
	//Program ::= (ClassDeclaration)* eot
	private void parseProgram() throws SyntaxError {
		while (token.getKind() == TokenKind.CLASS) {
			parseClassDeclaration();
		}
		accept(TokenKind.EOT);	
		return;
	}
	
/* 
 * ::= class id 
{ 
	( 
		( public | private )?  (static)?  
			(
				Type id ( ; | L )  
				| void id L 
			)
	)* 
} 

 */
	private void parseClassDeclaration() throws SyntaxError {
		acceptIt();
		accept(TokenKind.IDENTIFIER);
		accept(TokenKind.LBRACKET);
		while (
				token.getKind() == TokenKind.PUBLIC ||
				token.getKind() == TokenKind.PRIVATE ||
				token.getKind() == TokenKind.STATIC ||
				token.getKind() == TokenKind.VOID ||
				token.getKind() == TokenKind.BOOLEAN ||
				token.getKind() == TokenKind.INT ||
				token.getKind() == TokenKind.IDENTIFIER
				) {
			
			if (token.getKind() == TokenKind.PUBLIC || token.getKind() == TokenKind.PRIVATE) {
				acceptIt();
			}
			
			if (token.getKind() == TokenKind.STATIC) {
				acceptIt();
			}
			
			if (token.getKind() == TokenKind.VOID) {
				acceptIt();
				accept(TokenKind.IDENTIFIER);
				parseL();
			} else {
				parseType();
				accept(TokenKind.IDENTIFIER);
				if (token.getKind() == TokenKind.SEMICOLON) acceptIt();
				else parseL();
			}
		}
		accept(TokenKind.RBRACKET);
		return;
	}
	
	// L ::= '(' ParameterList? ')' {Statement*}
	private void parseL() throws SyntaxError {
		
		accept(TokenKind.LPAREN);
		if (
				token.getKind() == TokenKind.INT ||
				token.getKind() == TokenKind.BOOLEAN ||
				token.getKind() == TokenKind.IDENTIFIER
				) {
				parseParameterList();
			}
		accept(TokenKind.RPAREN);
		accept(TokenKind.LBRACKET);
		while (
				token.getKind() == TokenKind.LBRACKET ||
				token.getKind() == TokenKind.INT ||
				token.getKind() == TokenKind.BOOLEAN ||
				token.getKind() == TokenKind.THIS ||
				token.getKind() == TokenKind.IDENTIFIER ||
				token.getKind() == TokenKind.IF ||
				token.getKind() == TokenKind.RETURN ||
				token.getKind() == TokenKind.WHILE
				) {
			parseStatement();
		}
		accept(TokenKind.RBRACKET);
		return;
	}
	
	/*
	 * Statement ::=
	{ Statement* }
	| (int (  | [] ) | Boolean) id = Expression ;
	| this  ( . id )* J ;
	| id 
		( 
		( | id ) = Expression 
		| [  (  ] id = Expression | Expression ] = Expression ) 
		| ( (Expression ( , Expression )*)? ) 
		| .id(.id)* J
	    );
	| return Expression? ; 
	| if ( Expression ) Statement ( else Statement ) ?
	| while ( Expression ) Statement
	 */
	
	private void parseStatement() throws SyntaxError {
		switch(token.getKind()) {
		case LBRACKET:
			acceptIt();
			while (
					token.getKind() == TokenKind.LBRACKET ||
					token.getKind() == TokenKind.INT ||
					token.getKind() == TokenKind.BOOLEAN ||
					token.getKind() == TokenKind.THIS ||
					token.getKind() == TokenKind.IDENTIFIER ||
					token.getKind() == TokenKind.IF ||
					token.getKind() == TokenKind.RETURN ||
					token.getKind() == TokenKind.WHILE
					) {
				parseStatement();
			}
			accept(TokenKind.RBRACKET);
			return;
		case INT: case BOOLEAN:
			if (token.getKind() == TokenKind.INT) {
				acceptIt();
				if (token.getKind() == TokenKind.SQLBRACKET) {
					acceptIt();
					accept(TokenKind.SQRBRACKET);
				}
			} else acceptIt();
			accept(TokenKind.IDENTIFIER);
			accept(TokenKind.ASSIGNMENT);
			parseExpression();
			accept(TokenKind.SEMICOLON);
			return;
		case THIS:
			acceptIt();
			while (token.getKind() == TokenKind.DOT) {
				acceptIt();
				accept(TokenKind.IDENTIFIER);			
			}
			parseJ();
			accept(TokenKind.SEMICOLON);
			return;
		case IDENTIFIER:
			acceptIt();
			switch(token.getKind()) {
			case IDENTIFIER:
				acceptIt();
				accept(TokenKind.ASSIGNMENT);
				parseExpression();
				break;
			case ASSIGNMENT:
				acceptIt();
				parseExpression();
				break;
			case SQLBRACKET:
				acceptIt();
				if (token.getKind() == TokenKind.SQRBRACKET) {
					acceptIt();
					accept(TokenKind.IDENTIFIER);
				} else {
					parseExpression();
					accept(TokenKind.SQRBRACKET);
				}
				accept(TokenKind.ASSIGNMENT);
				parseExpression();
				break;
			case LPAREN:
				acceptIt();
				//too lazy to determine expression starters rn
				if (token.getKind() != TokenKind.RPAREN) {
					parseExpression();
					while (token.getKind() == TokenKind.COMMA) {
						acceptIt();
						parseExpression();
					}
				}
				acceptIt();
				break;
			case DOT:
				acceptIt();
				accept(TokenKind.IDENTIFIER);
				while (token.getKind() == TokenKind.DOT) {
					acceptIt();
					accept(TokenKind.IDENTIFIER);
				}
				parseJ();
				break;
			default:
				parseError("Invalid Term - found " + token.getKind());
				return;
			}
			accept(TokenKind.SEMICOLON);
			return;
		case RETURN:
			acceptIt();
			if (token.getKind() != TokenKind.SEMICOLON) parseExpression();
			acceptIt();
			return;
		case IF:
			acceptIt();
			accept(TokenKind.LPAREN);
			parseExpression();
			accept(TokenKind.RPAREN);
			parseStatement();
			if (token.getKind() == TokenKind.ELSE) {
				acceptIt();
				parseStatement();
			}
			return;
		case WHILE:
			acceptIt();
			accept(TokenKind.LPAREN);
			parseExpression();
			accept(TokenKind.RPAREN);
			parseStatement();
			return;
			
		default:
			parseError("Invalid Term - found " + token.getKind());
			return;
		}
	}
	
	// J:: = ( = Expression | [ Expression ] = Expression | ( (Expression ( , Expression )*)? ))
	private void parseJ() throws SyntaxError {
		switch(token.getKind()) {
		case ASSIGNMENT:
			acceptIt();
			parseExpression();
			return;
		case SQLBRACKET:
			acceptIt();
			parseExpression();
			accept(TokenKind.SQRBRACKET);
			accept(TokenKind.ASSIGNMENT);
			parseExpression();
			return;
		case LPAREN:
			acceptIt();
			if (token.getKind() != TokenKind.RPAREN) {
				parseExpression();
				while (token.getKind() != TokenKind.COMMA) {
					acceptIt();
					parseExpression();
				}
			}
			acceptIt();
			return;
		default:
			parseError("Invalid Term - found " + token.getKind());
			return;
		}
	}
	
	/* 
	 * Expression ::=
		( Id | this ) ( . id )* (  | [ Expression ] | '('(Expression ( , Expression )*)? ')' )
		| unop Expression
		| ( Expression )
		| num | true | false | new ( id() | … | … )
		(binop Expression)*
	 */
	
	private void parseExpression() throws SyntaxError {
		
		switch (token.getKind()) {
		case IDENTIFIER: case THIS:
			acceptIt();
			while (token.getKind() == TokenKind.DOT) {
				acceptIt();
				accept(TokenKind.IDENTIFIER);
			}
			if (token.getKind() == TokenKind.SQLBRACKET) {
				acceptIt();
				parseExpression();
				accept(TokenKind.SQRBRACKET);
			} else if (token.getKind() == TokenKind.LPAREN) {
				acceptIt();
				if (token.getKind() != TokenKind.RPAREN) {
					parseExpression();
					while (token.getKind() != TokenKind.COMMA) {
						acceptIt();
						parseExpression();
					}
				}
				acceptIt();
			}
			break;
		case LPAREN:
			acceptIt();
			parseExpression();
			accept(TokenKind.RPAREN);
			break;
		case NEW:
			acceptIt();
			if (token.getKind() == TokenKind.IDENTIFIER) {
				acceptIt();
				if (token.getKind() == TokenKind.LPAREN) {
					acceptIt();
					accept(TokenKind.RPAREN);
				} else if (token.getKind() == TokenKind.SQLBRACKET) {
					acceptIt();
					parseExpression();
					accept(TokenKind.SQRBRACKET);
				} else {
					parseError("Invalid Term - found " + token.getKind());
					return;
				}
			} else if (token.getKind() == TokenKind.INT) {
				acceptIt();
				accept(TokenKind.SQLBRACKET);
				parseExpression();
				accept(TokenKind.SQRBRACKET);
			} else {
				parseError("Invalid Term - found " + token.getKind());
				return;
			}
			break;
		case NUMBER: case TRUE: case FALSE:
			acceptIt();
			break;
		case OPERATOR:
			if (token.getSpelling().equals("!") || token.getSpelling().equals("-")) {
				acceptIt();
				parseExpression();
			}
			break;
		default:
			parseError("Invalid Term - found " + token.getKind());
			break;
		}
		while (token.getKind() == TokenKind.OPERATOR && token.getSpelling() != "!") {
			acceptIt();
			parseExpression();
		}
		return;
		
	}
	
	//Type ::= int (  | [] ) | id (  | [] ) | boolean
	private void parseType() throws SyntaxError {
		switch(token.getKind()) {
		case INT: case IDENTIFIER:
			acceptIt();
			if (token.getKind() == TokenKind.SQLBRACKET) {
				acceptIt();
				accept(TokenKind.SQRBRACKET);
			}
			return;
		case BOOLEAN:
			acceptIt();
			return;
		default:
			parseError("Invalid Term - found " + token.getKind());
			return;
		} 
	}
	
	//ParameterList ::= Type id ( , Type id )* 
	private void parseParameterList() throws SyntaxError {
		
		parseType();
		accept(TokenKind.IDENTIFIER);
		while(token.getKind() == TokenKind.COMMA) {
			acceptIt();
			parseType();
			accept(TokenKind.IDENTIFIER);
		}
		return;
	}
	
	private void acceptIt() throws SyntaxError {
		accept(token.getKind());
	}
	
	
	private void accept(TokenKind expectedTokenKind) throws SyntaxError {
		if (token.getKind() == expectedTokenKind) {
			if (trace)
				pTrace();
			token = scanner.scan();
		}
		else
			parseError("expecting '" + expectedTokenKind +
					"' but found '" + token.getKind() + "'");
	}
	
	private void parseError(String e) throws SyntaxError {
		errorReporter.reportError("Parse error: " + e);
		throw new SyntaxError();
	}
	
	private void pTrace() {
		StackTraceElement [] stl = Thread.currentThread().getStackTrace();
		for (int i = stl.length - 1; i > 0 ; i--) {
			if(stl[i].toString().contains("parse"))
				System.out.println(stl[i]);
		}
		System.out.println("accepting: " + token.getKind() + " (\"" + token.getSpelling() + "\")");
		System.out.println();
	}

}
