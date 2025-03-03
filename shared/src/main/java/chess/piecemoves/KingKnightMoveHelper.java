package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import java.util.Collection;
import java.util.List;

public class KingKnightMoveHelper {

    public static Collection<ChessMove> findValidKingKnightMoves(ChessPosition currentPosition, ChessBoard board,
                                                                 Collection<ChessMove> validMoves, ChessPiece piece,
                                                                 List<ChessPosition> potentialMoves) {
        for (ChessPosition potentialMove : potentialMoves) {
            if (potentialMove.getRow() >= 1 && potentialMove.getRow() <= 8) {
                if (potentialMove.getColumn() >= 1 && potentialMove.getColumn() <= 8) {
                    ChessPiece pieceOnPotentialMove = board.getPiece(potentialMove);
                    if (pieceOnPotentialMove == null || pieceOnPotentialMove.getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(new ChessMove(currentPosition, potentialMove, null));
                    }
                }
            }
        }
        return validMoves;
    }
}