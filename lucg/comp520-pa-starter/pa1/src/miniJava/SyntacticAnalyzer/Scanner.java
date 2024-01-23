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
		// TODO: This function should check the current char to determine what the token could be.
		
		// TODO: Consider what happens if the current char is whitespace
		
		// TODO: Consider what happens if there is a comment (// or /* */)
		
		// TODO: What happens if there are no more tokens?
		
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
	
	private void nextChar() {
		try {
			int c = _in.read();
			_currentChar = (char)c;
			
			if (c == -1) {
				eot = true;
			}
			
			// TODO: What happens if c is not a regular ASCII character?
			
		} catch( IOException e ) {
			scanError("I/O Exception!");
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
