package miniJava.SyntacticAnalyzer;

import java.io.IOException;
import java.io.InputStream;
import miniJava.ErrorReporter;

public class Scanner {
	private InputStream _in;
	private ErrorReporter _errors;
	private StringBuilder _currentText;
	private char _currentChar;
	private boolean eot = false;
	
	public Scanner( InputStream in, ErrorReporter errors ) {
		this._in = in;
		this._errors = errors;
		this._currentText = new StringBuilder();
		
		nextChar();
	}
	
	public Token scan() {
		skipSeparators();
		// TODO: This function should check the current char to determine what the token could be.
		
		// TODO: What happens if there are no more tokens?
		// how do we know if there are no more tokens? 
			// eot?  return EOT token? 
		
		// TODO: Determine what the token is. For example, if it is a number
		//  keep calling takeIt() until _currentChar is not a number. Then
		//  create the token via makeToken(TokenType.IntegerLiteral) and return it.
		return null;
	}
	
	private void takeIt() {
		_currentText.append(_currentChar);
		nextChar();
	}
	
	private void skipIt() {
		nextChar();
	}

	private void skipSeparators() {
		// could check two characters??
		switch (_currentChar) {
		case ' ': case '\t': case '\r': case '\n':
			skipIt();
			skipSeparators();
			break;
		case '/':
			// if next character isnt a forward slash or or * its not a comment, restore old byyes
			_in.mark(1);
			skipIt();
			if (_currentChar == '*') {
				skipIt();
				char previousChar = _currentChar;
				skipIt();
				while (!(eot || (previousChar == '*' && _currentChar == '/'))) {
					previousChar = _currentChar;
					skipIt();
				}
				skipIt();
			} else if (_currentChar == '/') {
				skipIt();
				while (!(eot || _currentChar == '\n' || _currentChar == '\r')) {
					skipIt();
				}
				skipIt();
			} else {
				_currentChar = '/';
				try {
					_in.reset();
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
	
	private void nextChar() {
		try {
			int c = _in.read();
			if (c > 128) {
				scanError("NonASCII input!");
				// handling for non ASCII input. not sure if this is correct
			}
			
			// result of (char) -1 ?
			if (c == -1) {
				eot = true;
			}

			_currentChar = (char)c;
			
		} catch( IOException e ) {
			scanError("I/O Exception!");
		}
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
	
	private Token makeToken( TokenType toktype ) {
		// TODO: return a new Token with the appropriate type and text
		//  contained in 
		return null;
	}

	private void scanError(String m) {
		// i need to provde line and colum numbers. how? 
		_errors.reportError(0,0, "Scan Error:  " + m);
	}
}
