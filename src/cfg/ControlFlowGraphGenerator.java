package cfg;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Vector;

import bsh.ParserTokenManager;
import bsh.Token;
import bsh.linter.Linter;

public class ControlFlowGraphGenerator {
	public static ControlFlowGraph createControlFlowGraph(Reader reader) throws IOException {
		ControlFlowGraph graph = new ControlFlowGraph();
		
		List<Vector<Token>> vectors = Linter.getVectors(reader);
		for (Vector<Token> vector : vectors) {
			Vector<ControlFlowGraph.Block> blocks = new Vector<ControlFlowGraph.Block>();
			
			int currPosition = 0;
			ControlFlowGraph.Block currBlock = new ControlFlowGraph.Block(currPosition, 0, null, null);
			Vector<Token> currBlockTokens = new Vector<Token>();
			
			for (Token token : vector) {
				if ( token.kind == ParserTokenManager.IF
					|| token.kind == ParserTokenManager.ELSE
					|| token.kind == ParserTokenManager.FOR
					|| token.kind == ParserTokenManager.SWITCH
					|| token.kind == ParserTokenManager.TRY
					|| token.kind == ParserTokenManager.CATCH
					|| token.kind == ParserTokenManager.DO
					|| token.kind == ParserTokenManager.WHILE ) {
					currBlock.setKind(token.kind);
					currBlock.setStartPosition(currPosition);
				}
				if ( ( token.kind == ParserTokenManager.RBRACE && currBlock.getKind() != null )
						|| ( token.kind == ParserTokenManager.SEMICOLON && currBlock.getKind() == null ) ) {
					currBlockTokens.add(token);
					currBlock.setStatements(currBlockTokens);
					currBlock.setEndPosition(currPosition);
					blocks.add(currBlock);
					
					currBlock = new ControlFlowGraph.Block(currPosition + 1, 0, null, null);
					currBlockTokens = new Vector<Token>();
				} else {
					currBlockTokens.add(token);
				}
				currPosition++;
			}
			if ( currBlockTokens.size() > 1
					&& currBlock.getStatements() == null ) {
				currBlock.setStatements(currBlockTokens);
				currBlock.setEndPosition(currPosition);
				blocks.add(currBlock);
			}
			if ( !blocks.isEmpty() ) graph.setBlocks(vector, blocks);
		}
		return graph;
	}
}
