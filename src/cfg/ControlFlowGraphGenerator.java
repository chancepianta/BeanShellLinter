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
			StringBuilder blockBuilder = new StringBuilder();
			StringBuilder vectorBuilder = new StringBuilder();
			
			Vector<ControlFlowGraph.Block> blocks = new Vector<ControlFlowGraph.Block>();
			
			int currPosition = 0;
			ControlFlowGraph.Block currBlock = new ControlFlowGraph.Block(currPosition, 0, null, null);
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
					if ( blockBuilder.toString().endsWith(";") ) {
						blockBuilder.append(" ");
						vectorBuilder.append(" ");
					}
					blockBuilder.append(token.image);
					vectorBuilder.append(token.image);
					
					currBlock.setStatements(blockBuilder.toString().trim());
					currBlock.setEndPosition(currPosition);
					blocks.add(currBlock);
					currBlock = new ControlFlowGraph.Block(currPosition + 1, 0, null, null);
					
					blockBuilder = new StringBuilder();
				} else {
					if ( token.kind != ParserTokenManager.SEMICOLON
							&& token.kind != ParserTokenManager.LPAREN
							&& token.kind != ParserTokenManager.RPAREN
							&& token.kind != ParserTokenManager.INCR
							&& token.kind != ParserTokenManager.DECR 
							&& !blockBuilder.toString().endsWith("(") ) {
						blockBuilder.append(" ");
						vectorBuilder.append(" ");
					}
					blockBuilder.append(token.image);
					vectorBuilder.append(token.image);
				}
				currPosition++;
			}
			if ( blockBuilder.toString().trim().length() > 1
					&& currBlock.getStatements() == null ) {
				currBlock.setStatements(blockBuilder.toString().trim());
				currBlock.setEndPosition(currPosition);
				blocks.add(currBlock);
			}
			for (ControlFlowGraph.Block block : blocks) {
				graph.addBlock(vectorBuilder.toString(), block);
			}
		}
		return graph;
	}
}
