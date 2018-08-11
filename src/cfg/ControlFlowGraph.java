package cfg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import bsh.Token;

public class ControlFlowGraph {
	private Map<String, Vector<Block>> blocks = new HashMap<String, Vector<Block>>();
	private Map<Vector<Token>, Map<Block, Set<Block>>> edges = new HashMap<Vector<Token>, Map<Block, Set<Block>>>();
	
	public Map<String, Vector<Block>> getBlocks() {
		return this.blocks;
	}
	
	public Map<Vector<Token>, Map<Block, Set<Block>>> getEdges() {
		return this.edges;
	}
	
	public void addBlock(String vector, int startPosition, int endPosition, String value, Integer kind) {
		this.addBlock(vector, new Block(startPosition, endPosition, value, kind));
	}
	
	public void addBlock(String vector, Block block) {
		if ( this.blocks.containsKey(vector) ) {
			this.blocks.get(vector).add(block);
		} else {
			Vector<Block> blockVector = new Vector<Block>();
			blockVector.add(block);
			this.blocks.put(vector, blockVector);
		}
	}
	
	public void setBlocks(String vector, Vector<Block> blocks) {
		this.blocks.put(vector, blocks);
	}
	
	public String toJSON() {
		return "To Implement";
	}
	

	public void findEdges() {
		for (String vector : this.blocks.keySet()) {
			System.out.println(vector);
		}
	}
	
	public static class Block {
		private int startPosition, endPosition;
		boolean containsReturn;
		private String statements;
		private Integer kind;
		
		public Block() {}
		
		public Block(int startPosition, int endPosition, String statements, Integer kind) {
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			this.statements = statements;
			this.containsReturn = (statements != null && statements.toLowerCase().contains("return"));
			this.kind = kind;
		}
		
		public int getStartPosition() {
			return this.startPosition;
		}
		
		public int getEndPosition() {
			return this.endPosition;
		}
		
		public String getStatements() {
			return this.statements;
		}
		
		public Integer getKind() {
			return this.kind;
		}
		
		public void setStartPosition(int startPosition) {
			this.startPosition = startPosition;
		}
		
		public void setEndPosition(int endPosition) {
			this.endPosition = endPosition;
		}
		
		public void setStatements(String statements) {
			this.containsReturn = (statements != null && statements.toLowerCase().contains("return"));
			this.statements = statements;
		}
		
		public void setKind(Integer kind) {
			this.kind = kind;
		}
		
		public int hashCode() {
			return this.startPosition 
					+ this.endPosition 
					+ (this.statements != null ? this.statements.hashCode() : "".hashCode())
					+ new Boolean(this.containsReturn).hashCode() 
					+ (this.kind != null ? kind.hashCode() : Integer.hashCode(0));
		}
		
		public String toString() {
			return this.statements;
		}
	}
}
