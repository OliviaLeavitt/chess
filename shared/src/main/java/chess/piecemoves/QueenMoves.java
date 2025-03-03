package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public class QueenMoves {
    public Collection<ChessMove> pieceMoves(ChessPosition myPosition, ChessBoard board, Collection<ChessMove> validMoves, ChessPiece piece) {
        int currRow = myPosition.row;
        int currCol = myPosition.col;

        //upward move
        piece.addValidMovesForADirection(currCol, currRow, 0, 1, myPosition, board, validMoves);
        //downward move
        piece.addValidMovesForADirection(currCol, currRow, 0, -1, myPosition, board, validMoves);
        //right side
        piece.addValidMovesForADirection(currCol, currRow, 1, 0, myPosition, board, validMoves);
        //left side
        piece.addValidMovesForADirection(currCol, currRow, -1, 0, myPosition, board, validMoves);
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

