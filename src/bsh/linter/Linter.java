package bsh.linter;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import bsh.JavaCharStream;
import bsh.Parser;
import bsh.ParserTokenManager;
import bsh.Token;

public class Linter {
	private Map<String,String> errors = new HashMap<String,String>();
	
	public Map<String,String> getErrors() {
		return this.errors;
	}
	
	public Map<String,String> lint(Reader reader) {
			
		Deque<Token> methodStack = new ArrayDeque<Token>();
		boolean stackHasLBrace = false;
		
		ParserTokenManager ptk = new ParserTokenManager(new JavaCharStream(reader));
		while ( true ) {
			Token token = ptk.getNextToken();
			if ( token == null ) {
				break;
			} 
			methodStack.push(token);
			if ( token.kind == ParserTokenManager.RBRACE
					|| ( token.kind == ParserTokenManager.SEMICOLON && !stackHasLBrace ) ) {
				analyzeStack(methodStack);
				stackHasLBrace = false;
			} else if ( token.kind == ParserTokenManager.LBRACE ) {
				stackHasLBrace = true;
			} else if ( token.kind == ParserTokenManager.EOF ) {
				break;
			}
		}
		
		return this.getErrors();
	}
	
	private void analyzeStack(Deque<Token> methodStack) {
		System.out.println(methodStack);
		while ( !methodStack.isEmpty() ) {
			methodStack.pop();
		}
	}
}
