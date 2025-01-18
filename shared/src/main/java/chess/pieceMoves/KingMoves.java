package chess.pieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMoves {
    public Collection<ChessMove> pieceMoves (ChessPosition myPosition, ChessBoard board, ChessPiece piece) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        List<ChessPosition> potentialMoves = List.of(
                new ChessPosition(myPosition.row + 1, myPosition.col),
                new ChessPosition(myPosition.row - 1, myPosition.col),
                new ChessPosition(myPosition.row, myPosition.col + 1),
                new ChessPosition(myPosition.row, myPosition.col - 1),
                new ChessPosition(myPosition.row - 1, myPosition.col - 1),
                new ChessPosition(myPosition.row + 1, myPosition.col + 1),
                new ChessPosition(myPosition.row - 1, myPosition.col + 1),
                new ChessPosition(myPosition.row + 1, myPosition.col - 1)
        );
        for (ChessPosition potentialMove : potentialMoves) {
            if (potentialMove.getRow() >= 1 && potentialMove.getRow() <= 8) {
                if (potentialMove.getColumn() >= 1 && potentialMove.getColumn() <= 8) {
                    ChessPiece pieceOnPotentialMove = board.getPiece(potentialMove);
                    if (pieceOnPotentialMove == null || pieceOnPotentialMove.getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, potentialMove, null));
                    }
                }
            }
        } return validMoves;
    }
}
