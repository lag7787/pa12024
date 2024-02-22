package miniJava;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import miniJava.SyntacticAnalyzer.Scanner;
import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.ASTDisplay;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenType;

public class Compiler {
	// Main function, the file to compile will be an argument.
	public static void main(String[] args) {

		ErrorReporter errorReporter = new ErrorReporter();

		if (args.length < 1) {
			System.exit(1);
		}
		
		String sourceFilePath = args[0];
		
		if (!(sourceFilePath.endsWith(".java") || sourceFilePath.endsWith(".mjava"))) {
			System.exit(1);
		}
		
		InputStream sourceFileStream = readFile(sourceFilePath);
		BufferedInputStream bufferedStream = new BufferedInputStream(sourceFileStream);
		Scanner scanner = new Scanner(bufferedStream, errorReporter);
		Parser parser = new Parser(scanner, errorReporter);
		AST programAST = parser.parse();
		if (errorReporter.hasErrors()) {
			System.out.println("Error");
			errorReporter.outputErrors();
		} else {
			ASTDisplay display = new ASTDisplay();
			display.showTree(programAST);
		}
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
