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
			ControlFlowGraph.Block currBlock = new ControlFlowGraph.Block(0, 0, null, null);
			StringBuilder builder = new StringBuilder();
			int currPosition = 0;
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
				} else if ( token.kind == ParserTokenManager.LBRACE 
						&& ( currBlock.getKind() != null || builder.toString().endsWith(")") ) ) {
					builder = new StringBuilder();
					currBlock.setStartPosition(currPosition);
				} else if ( ( token.kind == ParserTokenManager.RBRACE && currBlock.getKind() != null )
						|| ( token.kind == ParserTokenManager.SEMICOLON && currBlock.getKind() == null ) ) {
					if ( token.kind == ParserTokenManager.SEMICOLON )
						builder.append(token.image);
					currBlock.setStatements(builder.toString().trim());
					currBlock.setEndPosition(currPosition);
					graph.addBlock(vector, currBlock);
					currBlock = new ControlFlowGraph.Block(currPosition + 1, 0, null, null);
					builder = new StringBuilder();
				} else {
					if ( token.kind != ParserTokenManager.SEMICOLON )
						builder.append(" ");
					builder.append(token.image);
				}
				currPosition++;
			}
			if ( !builder.toString().isEmpty() && currBlock.getStatements() == null ) {
				currBlock.setStatements(builder.toString().trim());
				currBlock.setEndPosition(currPosition);
				graph.addBlock(vector, currBlock);
			}
		}
		return graph;
	}
}
