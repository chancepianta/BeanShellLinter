package cfg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import bsh.ParserTokenManager;
import bsh.Token;

public class ControlFlowGraph {
	private Vector<Block> blocks = new Vector<Block>();
	private Map<Block, Set<Block>> edges = new HashMap<Block, Set<Block>>();
	
	public Vector<Block> getBlocks() {
		return this.blocks;
	}
	
	public Vector<Block> getBlocks(String scope) {
		Vector<Block> vector = new Vector<Block>();
		for (Block block : blocks)
			if ( block.getScope().equals(scope) )
				vector.add(block);
		return vector;
	}
	
	public Map<Block, Set<Block>> getEdges() {
		return this.edges;
	}
	
	public void addBlock(int startPosition, int endPosition, Vector<Token> tokens, Integer kind, String scope) {
		this.addBlock(new Block(startPosition, endPosition, tokens, kind, scope));
	}
	
	public void addBlock(Block block) {
		if ( !this.blocks.contains(block) ) {
			this.blocks.add(block);
		} 
	}
	
	public void addBlocks(Vector<Block> blocks) {
		this.blocks.addAll(blocks);
	}
	
	public void addEdge(Block fromBlock, Block toBlock) {
		if ( this.edges.containsKey(fromBlock) ) {
			this.edges.get(fromBlock).add(toBlock);
		} else {
			Set<Block> set = new HashSet<Block>();
			set.add(toBlock);
			this.edges.put(fromBlock, set);
		}
	}
	
	public String toJSON() {
		return "To Implement";
	}
	

	public void findEdges() {
		// Build initial graph without method invocations
		for (int i = 0; i < this.blocks.size(); i++) {
			Block currBlock = this.blocks.get(i);
			for (int j = i - 1; j > -1; j--) {
				if ( this.blocks.get(j).getScope().equals(currBlock.getScope()) ) {
					if ( this.blocks.get(j).containsReturn() 
							|| ( currBlock.isLogical() && this.blocks.get(j).isLogical() ) ) continue;
					this.addEdge(this.blocks.get(j), currBlock);
				}
			}
		}
		// Check for method invocations and add edges as appropriate
		for (Block block : this.blocks) {
			for (Token token : block.getTokens()) {
				Vector<Block> scopeBlocks = this.getBlocks(token.image);
				if ( scopeBlocks != null && !scopeBlocks.isEmpty() ) this.addEdge(block, scopeBlocks.get(0));
			}
		}
	}
	
	public static class Block {
		private int startPosition, endPosition;
		private boolean containsReturn, isLogical;
		private Vector<Token> tokens;
		private Integer kind;
		private String scope;
		
		public Block() {}
		
		public Block(int startPosition, int endPosition, Vector<Token> tokens, Integer kind, String scope) {
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			this.tokens = tokens;
			this.kind = kind;
			this.containsReturn = hasReturnToken(tokens);
			this.isLogical = isLogical(tokens);
			this.scope = scope;
		}
		
		public int getStartPosition() {
			return this.startPosition;
		}
		
		public int getEndPosition() {
			return this.endPosition;
		}
		
		public Vector<Token> getTokens() {
			return this.tokens;
		}
		
		public Integer getKind() {
			return this.kind;
		}
		
		public String getScope() {
			return this.scope;
		}
		
		public boolean containsReturn() {
			return this.containsReturn;
		}
		
		public boolean isLogical() {
			return this.isLogical;
		}
		
		public void setStartPosition(int startPosition) {
			this.startPosition = startPosition;
		}
		
		public void setEndPosition(int endPosition) {
			this.endPosition = endPosition;
		}
		
		public void setTokens(Vector<Token> tokens) {
			this.containsReturn = hasReturnToken(tokens);
			this.isLogical = isLogical(tokens);
			this.tokens = tokens;
		}
		
		public void setKind(Integer kind) {
			this.kind = kind;
		}
		
		public void setScope(String scope) {
			this.scope = scope;
		}
		
		public int hashCode() {
			return this.startPosition 
					+ this.endPosition 
					+ (this.tokens != null ? this.tokens.hashCode() : "".hashCode())
					+ new Boolean(this.containsReturn).hashCode() 
					+ new Boolean(this.isLogical).hashCode()
					+ (this.kind != null ? kind.hashCode() : Integer.hashCode(0))
					+ this.scope.hashCode();
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for (Token token : this.tokens) {
				if ( token.kind != ParserTokenManager.SEMICOLON
						&& token.kind != ParserTokenManager.LPAREN
						&& token.kind != ParserTokenManager.RPAREN
						&& token.kind != ParserTokenManager.INCR
						&& token.kind != ParserTokenManager.DECR
						&& token.kind != ParserTokenManager.DOT
						&& !builder.toString().endsWith("(")
						&& !builder.toString().endsWith(".") ) {
					builder.append(" ");
				}
				builder.append(token.image);
			}
			return this.scope + ": " + builder.toString().trim();
		}
		
		private boolean hasReturnToken(Vector<Token> tokens) {
			if ( tokens != null )
				for (Token token : tokens)
					if ( token.kind == ParserTokenManager.RETURN )
						return true;
			return false;
		}
		
		private boolean isLogical(Vector<Token> tokens) {
			if ( tokens != null )
				for (Token token : tokens)
					if ( token.kind == ParserTokenManager.IF || token.kind == ParserTokenManager.ELSE )
						return true;
			return false;
		}
	}
}
