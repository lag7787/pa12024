/**
 *   COMP 520 
 *   Simple expression scanner and parser
 *     following package structure of a full compiler
 *
 *  Parser grammar:
 *   S ::= E '$'
 *   E ::= T (oper T)*     
 *   T ::= num | '(' E ')'
 *
 *  Scanner grammar:
 *   num ::= digit digit*
 *   digit ::= '0' | ... | '9'
 *   oper ::= '+' | '*'
 */

package miniArith;

import java.io.InputStream;

import miniArith.SyntacticAnalyzer.Parser;
import miniArith.SyntacticAnalyzer.Scanner;

/**
 * Recognize whether input entered through the keyboard is a valid
 * arithmetic expression as defined by the simple CFG and scanner grammar above.  
 * 
 */
public class Recognizer {

	public static void main(String[] args) {

		/*
		 * note that a compiler should read the file specified by args[0]
		 * instead of reading from the keyboard!
		 */
		System.out.print("Enter arithmetic expression: ");
		InputStream inputStream = System.in;
		
		ErrorReporter reporter = new ErrorReporter();
		Scanner scanner = new Scanner(inputStream, reporter);
		Parser parser = new Parser(scanner, reporter);

		System.out.println("Syntactic analysis ... ");
		parser.parse();
		System.out.print("Syntactic analysis complete:  ");
		
		if (reporter.hasErrors()) {
			System.out.println("INVALID arithmetic expression");
			// return code for invalid input
			System.exit(4);
		}
		else {
			System.out.println("valid arithmetic expression");
			// return code for valid input
			System.exit(0);
		}
	}
}







