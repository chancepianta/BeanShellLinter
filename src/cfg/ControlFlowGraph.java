package cfg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import bsh.ParserTokenManager;
import bsh.Token;

public class ControlFlowGraph {
	private Map<Vector<Token>, Vector<Block>> blocks = new HashMap<Vector<Token>, Vector<Block>>();
	private Map<Vector<Token>, Map<Block, Set<Block>>> edges = new HashMap<Vector<Token>, Map<Block, Set<Block>>>();
	
	public Map<Vector<Token>, Vector<Block>> getBlocks() {
		return this.blocks;
	}
	
	public Map<Vector<Token>, Map<Block, Set<Block>>> getEdges() {
		return this.edges;
	}
	
	public void addBlock(Vector<Token> vector, int startPosition, int endPosition, Vector<Token> tokens, Integer kind) {
		this.addBlock(vector, new Block(startPosition, endPosition, tokens, kind));
	}
	
	public void addBlock(Vector<Token> vector, Block block) {
		if ( this.blocks.containsKey(vector) ) {
			this.blocks.get(vector).add(block);
		} else {
			Vector<Block> blockVector = new Vector<Block>();
			blockVector.add(block);
			this.blocks.put(vector, blockVector);
		}
	}
	
	public void setBlocks(Vector<Token> vector, Vector<Block> blocks) {
		this.blocks.put(vector, blocks);
	}
	
	public String toJSON() {
		return "To Implement";
	}
	

	public void findEdges() {
		System.out.println("findEdges()");
	}
	
	public static class Block {
		private int startPosition, endPosition;
		boolean containsReturn;
		private Vector<Token> tokens;
		private Integer kind;
		
		public Block() {}
		
		public Block(int startPosition, int endPosition, Vector<Token> tokens, Integer kind) {
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			this.tokens = tokens;
			this.kind = kind;
			this.containsReturn = hasReturnToken(tokens);
		}
		
		public int getStartPosition() {
			return this.startPosition;
		}
		
		public int getEndPosition() {
			return this.endPosition;
		}
		
		public Vector<Token> getStatements() {
			return this.tokens;
		}
		
		public Integer getKind() {
			return this.kind;
		}
		
		public boolean containsReturn() {
			return this.containsReturn;
		}
		
		public void setStartPosition(int startPosition) {
			this.startPosition = startPosition;
		}
		
		public void setEndPosition(int endPosition) {
			this.endPosition = endPosition;
		}
		
		public void setStatements(Vector<Token> tokens) {
			this.containsReturn = hasReturnToken(tokens);
			this.tokens = tokens;
		}
		
		public void setKind(Integer kind) {
			this.kind = kind;
		}
		
		public int hashCode() {
			return this.startPosition 
					+ this.endPosition 
					+ (this.tokens != null ? this.tokens.hashCode() : "".hashCode())
					+ new Boolean(this.containsReturn).hashCode() 
					+ (this.kind != null ? kind.hashCode() : Integer.hashCode(0));
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for (Token token : this.tokens) {
				if ( token.kind != ParserTokenManager.SEMICOLON
						&& token.kind != ParserTokenManager.LPAREN
						&& token.kind != ParserTokenManager.RPAREN
						&& token.kind != ParserTokenManager.INCR
						&& token.kind != ParserTokenManager.DECR 
						&& !builder.toString().endsWith("(") ) {
					builder.append(" ");
				}
				builder.append(token.image);
			}
			return builder.toString().trim();
		}
		
		private boolean hasReturnToken(Vector<Token> tokens) {
			if ( tokens != null )
				for (Token token : tokens)
					if ( token.kind == ParserTokenManager.RETURN )
						return true;
			return false;
		}
	}
}