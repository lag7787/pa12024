package miniJava;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

public class Compiler {
	
	public static void main(String[] args) {
		
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
