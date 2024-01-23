package miniJava.SyntacticAnalyzer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import miniJava.ErrorReporter;

public class Scanner {
	private InputStream inputStream;
	private ErrorReporter errorReporter;
	private char currentChar;
	private StringBuilder currentSpelling;
	private boolean eot = false; 
	
	private static HashMap<String, TokenKind> keywordMap;
	
	static {
	    keywordMap = new HashMap<String, TokenKind>();
	    keywordMap.put("class", TokenKind.CLASS);
	    keywordMap.put("public", TokenKind.PUBLIC);
	    keywordMap.put("private", TokenKind.PRIVATE);
	    keywordMap.put("void", TokenKind.VOID);
	    keywordMap.put("static", TokenKind.STATIC);
	    keywordMap.put("int", TokenKind.INT);
	    keywordMap.put("boolean", TokenKind.BOOLEAN);
	    keywordMap.put("this", TokenKind.THIS);
	    keywordMap.put("return", TokenKind.RETURN);
	    keywordMap.put("if", TokenKind.IF);
	    keywordMap.put("else", TokenKind.ELSE);
	    keywordMap.put("while", TokenKind.WHILE);
	    keywordMap.put("true", TokenKind.TRUE);
	    keywordMap.put("false", TokenKind.FALSE);
	    keywordMap.put("new", TokenKind.NEW);
	    keywordMap.put("=", TokenKind.ASSIGNMENT);
	}
	
	public Scanner(InputStream inputStream1, ErrorReporter errorReporter) {
		this.inputStream = inputStream1;
		this.errorReporter = errorReporter;
		readChar();
	}
	
	public Token scan() {
		
		skipSeparators();
		currentSpelling = new StringBuilder();
		TokenKind type = scanToken();
		String spelling = currentSpelling.toString();

		// return new token
		return new Token(type, spelling);
	}
	
	public TokenKind scanToken() {
		
		if (eot)
			return(TokenKind.EOT); 

		// scan Token
		switch (currentChar) {
		case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': case 'h': case 'i':case 'j':
		case 'k': case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': case 'r': case 's': case 't':
		case 'u': case 'v': case 'w': case 'x': case 'y': case 'z':
		case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I':case 'J':
		case 'K': case 'L': case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T':
		case 'U': case 'V': case 'W': case 'X': case 'Y': case 'Z':
			takeIt();
			while (Character.isLetter(currentChar) || 
					Character.isDigit(currentChar) || 
					currentChar == '_')
				takeIt();
			if (keywordMap.containsKey(this.currentSpelling.toString().toLowerCase())) {
				return keywordMap.get(this.currentSpelling.toString().toLowerCase());
			} else {
				return TokenKind.IDENTIFIER;
			}
		//some characters require two character look ahead. 
		//single = is not an operator, but a keyword 
		case '+': case '-': case '*': case '/':
		case '&': case '|': case '!':
		case '>': case '<':
			if (currentChar == '/') {
				System.out.println();
			}
			char prevChar = currentChar;
			takeIt();
			//were trying to decide if we should take a second char. 
			switch (prevChar) {
			case '>': case '<': case '!':
				if (currentChar == '=')
					takeIt();
					break;
			case '&': case '|':
				if (currentChar == prevChar)
					takeIt();
					break;
			}
			return(TokenKind.OPERATOR);
		case '0': case '1': case '2': case '3': case '4': case '5':
		case '6': case '7': case '8': case  '9':
			takeIt();
			while (Character.isDigit(currentChar)) takeIt();
			return TokenKind.NUMBER;
		case '=':
			takeIt();
			if (currentChar == '=') {
				takeIt();
				return (TokenKind.OPERATOR);
			} else {
				return TokenKind.ASSIGNMENT;
			}
			
		case '.':
			takeIt();
			return (TokenKind.DOT);
		case '(': 
			takeIt();
			return(TokenKind.LPAREN);

		case ')':
			takeIt();
			return(TokenKind.RPAREN);
			
		case '{': 
			takeIt();
			return(TokenKind.LBRACKET);

		case '}':
			takeIt();
			return(TokenKind.RBRACKET);
		case '[' :
			takeIt();
			return(TokenKind.SQLBRACKET);
		case ']':
			takeIt();
			return(TokenKind.SQRBRACKET);
			
		case ';':
			takeIt();
			return(TokenKind.SEMICOLON);
		case ',':
			takeIt();
			return(TokenKind.COMMA);
		
		default:
			scanError("Unrecognized character '" + currentChar + "' in input");
			return(TokenKind.ERROR);
		}
		
	}
	
	private void takeIt() {
		currentSpelling.append(currentChar);
		nextChar();
	}
	
	private void skipIt() {
		nextChar();
	}
	
	private void nextChar() {
		if (!eot)
			readChar();
	}
	
	private void scanError(String m) {
		errorReporter.reportError("Scan Error:  " + m);
	}
	
	private void skipSeparators() {
		// could check two characters??
		switch (currentChar) {
		case ' ': case '\t': case '\r': case '\n':
			skipIt();
			skipSeparators();
			break;
		case '/':
			inputStream.mark(1);
			skipIt();
			if (currentChar == '*') {
				skipIt();
				char previousChar = currentChar;
				skipIt();
				while (!(eot || (previousChar == '*' && currentChar == '/'))) {
					previousChar = currentChar;
					skipIt();
				}
				skipIt();
			} else if (currentChar == '/') {
				skipIt();
				while (!(eot || currentChar == '\n' || currentChar == '\r')) {
					skipIt();
				}
				skipIt();
			} else {
				currentChar = '/';
				try {
					inputStream.reset();
				} catch (IOException e) {
					scanError(e.toString());
				}
				
				break;
			}
			skipSeparators();
			break;
		default:
			break;
		}
	}	
	
	private void readChar() {
		try {
			int c = inputStream.read();
			currentChar = (char) c;
			if (c == -1) {
				eot = true;
			}
		} catch (IOException e) {
			scanError("I/O Exception!");
			eot = true;
		}
	}
}
