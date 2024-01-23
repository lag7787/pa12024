package miniJava;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

public class Compiler {
	// Main function, the file to compile will be an argument.
	public static void main(String[] args) {
		// TODO: Instantiate the ErrorReporter object
		
		// TODO: Check to make sure a file path is given in args
		
		// TODO: Create the inputStream using new FileInputStream
		
		// TODO: Instantiate the scanner with the input stream and error object
		
		// TODO: Instantiate the parser with the scanner and error object
		
		// TODO: Call the parser's parse function
		
		// TODO: Check if any errors exist, if so, println("Error")
		//  then output the errors
		
		// TODO: If there are no errors, println("Success")

		int rc = 4;
		String sourceFilePath = args[0];
		
		if (!(sourceFilePath.endsWith(".java") || sourceFilePath.endsWith(".mjava"))) {
			System.exit(1);
		}
		
		InputStream sourceFileStream = readFile(sourceFilePath);
		BufferedInputStream bufferedStream = new BufferedInputStream(sourceFileStream);
		ErrorReporter errorReporter = new ErrorReporter();
		Scanner scanner = new Scanner(bufferedStream, errorReporter);
		Parser parser = new Parser(scanner, errorReporter);
		parser.parse();
		if (!errorReporter.hasErrors()) {
			rc = 0;
		}
				
		System.exit(rc);
	}

	private static InputStream readFile(String sourceFilePath) {
		
		try {
			File sourceFile = new File(sourceFilePath);
			InputStream sourceFileReader = new FileInputStream(sourceFile);
			return sourceFileReader;
		} catch (FileNotFoundException e) {
			System.out.println("Could not find source file");
			return null;
		} catch (SecurityException e) {
			System.out.println("Security exception");
			return null;
		}
		
	}
}
