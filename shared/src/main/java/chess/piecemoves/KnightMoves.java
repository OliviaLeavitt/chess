package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.List;
import static chess.util.KingKnightMoveHelper.findValidKingKnightMoves;

public class KnightMoves {
    public Collection<ChessMove> pieceMoves(ChessPosition myPosition, ChessBoard board, Collection<ChessMove> validMoves, ChessPiece piece) {
        List<ChessPosition> potentialMoves = List.of(
                new ChessPosition(myPosition.row + 1, myPosition.col - 2),
                new ChessPosition(myPosition.row + 1, myPosition.col + 2),
                new ChessPosition(myPosition.row + 2, myPosition.col + 1),
                new ChessPosition(myPosition.row + 2, myPosition.col - 1),
                new ChessPosition(myPosition.row - 1, myPosition.col - 2),
                new ChessPosition(myPosition.row - 1, myPosition.col + 2),
                new ChessPosition(myPosition.row - 2, myPosition.col + 1),
                new ChessPosition(myPosition.row - 2, myPosition.col - 1)
        );
        return findValidKingKnightMoves(myPosition, board, validMoves, piece, potentialMoves);
    }
}
