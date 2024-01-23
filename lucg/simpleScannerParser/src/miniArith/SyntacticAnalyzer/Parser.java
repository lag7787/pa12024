/**
 * Parser
 *
 * Grammar:
 *   S ::= E '$'
 *   E ::= T (oper T)*     
 *   T ::= num | '(' E ')'
 */
package miniArith.SyntacticAnalyzer;

import miniArith.SyntacticAnalyzer.Scanner;
import miniArith.SyntacticAnalyzer.TokenKind;
import miniArith.ErrorReporter;

public class Parser {

	private Scanner scanner;
	private ErrorReporter reporter;
	private Token token;
	private boolean trace = true;

	public Parser(Scanner scanner, ErrorReporter reporter) {
		this.scanner = scanner;
		this.reporter = reporter;
	}


	/**
	 * SyntaxError is used to unwind parse stack when parse fails
	 *
	 */
	class SyntaxError extends Error {
		private static final long serialVersionUID = 1L;	
	}

	/**
	 *  parse input, catch possible parse error
	 */
	public void parse() {
		token = scanner.scan();
		try {
			parseS();
		}
		catch (SyntaxError e) { }
	}

	//    S ::= E$
	private void parseS() throws SyntaxError {
		parseE();
		accept(TokenKind.EOT);
	}

	//    E ::= T (("+" | "*") T)*     
	private void parseE() throws SyntaxError {
		parseT();
		while (token.kind == TokenKind.PLUS || token.kind == TokenKind.TIMES) {
			acceptIt();
			parseT();
		} 
	}

	//   T ::= num | "(" E ")"
	private void parseT() throws SyntaxError {
		switch (token.kind) {

		case NUM:
			acceptIt();
			return;

		case LPAREN:
			acceptIt();
			parseE();
			accept(TokenKind.RPAREN);
			return;

		default:
			parseError("Invalid Term - expecting NUM or LPAREN but found " + token.kind);
		}
	}

	/**
	 * accept current token and advance to next token
	 */
	private void acceptIt() throws SyntaxError {
		accept(token.kind);
	}

	/**
	 * verify that current token in input matches expected token and advance to next token
	 * @param expectedToken
	 * @throws SyntaxError  if match fails
	 */
	private void accept(TokenKind expectedTokenKind) throws SyntaxError {
		if (token.kind == expectedTokenKind) {
			if (trace)
				pTrace();
			token = scanner.scan();
		}
		else
			parseError("expecting '" + expectedTokenKind +
					"' but found '" + token.kind + "'");
	}

	/**
	 * report parse error and unwind call stack to start of parse
	 * @param e  string with error detail
	 * @throws SyntaxError
	 */
	private void parseError(String e) throws SyntaxError {
		reporter.reportError("Parse error: " + e);
		throw new SyntaxError();
	}

	// show parse stack whenever terminal is  accepted
	private void pTrace() {
		StackTraceElement [] stl = Thread.currentThread().getStackTrace();
		for (int i = stl.length - 1; i > 0 ; i--) {
			if(stl[i].toString().contains("parse"))
				System.out.println(stl[i]);
		}
		System.out.println("accepting: " + token.kind + " (\"" + token.spelling + "\")");
		System.out.println();
	}

}
