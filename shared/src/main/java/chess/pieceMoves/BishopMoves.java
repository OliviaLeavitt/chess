package chess.pieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public class BishopMoves {
    public Collection<ChessMove> pieceMoves(ChessPosition myPosition, ChessBoard board, Collection<ChessMove> validMoves, ChessPiece piece) {

        int currRow = myPosition.row;
        int currCol = myPosition.col;
        //right up diagonal
        piece.addValidMovesForADirection(currCol, currRow, 1, 1, myPosition, board, validMoves);
        //left up diagonal
        piece.addValidMovesForADirection(currCol, currRow, -1, 1, myPosition, board, validMoves);
        //left down diagonal
        piece.addValidMovesForADirection(currCol, currRow, -1, -1, myPosition, board, validMoves);
        //right down diagonal
        piece.addValidMovesForADirection(currCol, currRow, 1, -1, myPosition, board, validMoves);
        return validMoves;
    }

}
