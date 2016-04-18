package bsh.linter;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import bsh.JavaCharStream;
import bsh.ParseException;
import bsh.Parser;
import bsh.ParserTokenManager;
import bsh.Token;

public class Linter {
	private Map<String,String> errors = new HashMap<String,String>();
	
	public Map<String,String> getErrors() {
		return this.errors;
	}
	
	/**
	 * Method performs 'linting' of a BeanShell script provided a reader object.
	 * We want the script passed in via a reader implementation as we leverage
	 * BeanShell's JavaCharStream object for lexing purposes and it loves readers.
	 * 
	 * @param reader
	 * @return
	 */
	public Map<String,String> lint(Reader reader) {
		// Get list of different vectors from the provided script
		// In this sense a vector will either be a method definition
		// or a single line that could be a method call or assignment
		List<Vector<Token>> vectors = getVectors(reader);
		for (Vector<Token> vector : vectors) {
			analyzeVector(vector); // Analyze each vector for errors
		}
		// return the errors
		return this.getErrors();
	}
	
	/**
	 * Method for analyzing a given vector object. Vectors are expected to be
	 * actual vectors of BeanShell string tokens in order of first to last. 
	 * This method will build out a string object from the vector and then
	 * let BeanShell's Parser object find the syntax errors in the vector.
	 * 
	 * @param methodVector
	 */
	private void analyzeVector(Vector<Token> methodVector) {
		// Build a string to pass to the parse for checking
		StringBuilder builder = new StringBuilder();
		Iterator<Token> iterator = methodVector.iterator();
		while ( iterator.hasNext() ) {
			builder.append(iterator.next().image); // The image attribute is the actual string token
		}
		// Check the beanshell code by throwing it at the parser
		Parser parser = new Parser(new StringReader(builder.toString()));
		try {
			// Let the parser try building an AST with the provided input
			// This will throw a parse exception if there is a syntax error
			while ( parser.Line() ) {
				parser.popNode();
			}
		} catch (ParseException e) {
			// Catch the error to return back
			addError(methodVector, builder.toString(), e.getMessage());
		}
	}
	
	/**
	 * Method for breaking a BeanShell script into a list of vectors. Vectors
	 * here will be BeanShell string tokens for either method definitions
	 * or singles lines involving method calls or variable assignment. The tokens
	 * in a vector will be order from first to last in how the appear in the script.
	 * 
	 * @param reader
	 * @return
	 */
	private List<Vector<Token>> getVectors(Reader reader) {
		List<Vector<Token>> vectors = new ArrayList<Vector<Token>>(); // List of tokens to return
		ParserTokenManager ptk = new ParserTokenManager(new JavaCharStream(reader)); // Leverage the ptk as a lexer
		Token token = ptk.getNextToken(); // Current token from reader
		boolean hasLeftBrace = false; // We'll use this to track vectors that are method bodies
		Vector<Token> vector = new Vector<Token>(); // Current vector being built
		while (token != null && token.kind != ParserTokenManager.EOF ) { // End the loop once we hit the end-of-file
			vector.add(token);
			// Check if we are at a statement end or method declaration end
			if ( token.kind == ParserTokenManager.RBRACE
					|| ( token.kind == ParserTokenManager.SEMICOLON && !hasLeftBrace ) ) {
				vectors.add(vector);
				hasLeftBrace = false;
				vector = new Vector<Token>();
			} else if ( token.kind == ParserTokenManager.LBRACE ) { // Check if this is the beginning of a method declaration
				hasLeftBrace = true;
			}
			token = ptk.getNextToken();
		}
		return vectors;
	}
	
	/**
	 * Method for parsing the error encountered by the BeanShell Parser and determining the
	 * line value for where this exception was thrown. This method will then add the error
	 * to the errors map attribute for the Linter object.
	 * 
	 * @param originalVector
	 * @param evaluatedScript
	 * @param error
	 */
	private void addError(Vector<Token> originalVector, String evaluatedScript, String error) {
		// Error messages will be in the format of
		// error: Parse error at line #, column #.  <cause>
		String[] values = error.split("\\."); // So we'll split on the '.'
		values[0] = values[0].replaceFirst("[0-9]", "").replaceAll("\\D", ""); // get the column number so we can find the token
		values[1] = values[1].trim(); // Clean up the error message
	}
}
