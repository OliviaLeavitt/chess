package chess.piecemoves;

import chess.*;

import java.util.Collection;
import java.util.List;

public class PawnMoves {
    public Collection<ChessMove> pieceMoves(ChessPosition myPosition, ChessBoard board, Collection<ChessMove> validMoves, ChessPiece piece) {
        int normalDirectionToMove = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        int startDirectionToMove = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 2 : -2;
        int promoteRow = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 8 : 1;
        int startRow = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 2 : 7;

        // Start moves
        ChessPosition startUpwardMovePosition = new ChessPosition(myPosition.row + startDirectionToMove, myPosition.col);
        ChessPiece pieceOnIntermediateWhiteMove = board.getPiece(new ChessPosition(myPosition.row + normalDirectionToMove, myPosition.col));
        if (isInBounds(startUpwardMovePosition)) {
            ChessPiece pieceOnUpwardMoveFromStart = board.getPiece(startUpwardMovePosition);
            if (myPosition.getRow() == startRow && pieceOnUpwardMoveFromStart == null && pieceOnIntermediateWhiteMove == null) {
                validMoves.add(new ChessMove(myPosition, startUpwardMovePosition, null));
            }
        }

        //normalProgressMove
        ChessPosition normalProgressPosition = new ChessPosition(myPosition.row + normalDirectionToMove, myPosition.col);
        ChessPiece pieceOnNormalProgressMove = board.getPiece(normalProgressPosition);
        if (isInBounds(normalProgressPosition)) {
            if (pieceOnNormalProgressMove == null) {
                if (normalProgressPosition.getRow() == promoteRow) {
                    addPromotionMoves(myPosition, normalProgressPosition, validMoves);
                } else {
                    validMoves.add(new ChessMove(myPosition, normalProgressPosition, null));
                }
            }
        }

        getPawnAttackMoves(myPosition, normalDirectionToMove, promoteRow, board, piece, validMoves);

        return validMoves;
    }

    public void getPawnAttackMoves(ChessPosition myPosition, int normalDirectionToMove, int promoteRow, ChessBoard board, ChessPiece piece, Collection<ChessMove> validMoves) {
        List<ChessPosition> diagonalMovePositions = List.of(
                new ChessPosition(myPosition.row + normalDirectionToMove, myPosition.col + 1),
                new ChessPosition(myPosition.row + normalDirectionToMove, myPosition.col - 1)
        );
        for (ChessPosition diagonalMovePosition : diagonalMovePositions) {
            if (isInBounds(diagonalMovePosition)) {
                ChessPiece pieceOnDiagonalMove = board.getPiece(diagonalMovePosition);
                if (pieceOnDiagonalMove != null && pieceOnDiagonalMove.getTeamColor() != piece.getTeamColor()) {
                    if (diagonalMovePosition.getRow() == promoteRow) {
                        addPromotionMoves(myPosition, diagonalMovePosition, validMoves);
                    } else {
                        ChessMove diagonalAttackMove = new ChessMove(myPosition, diagonalMovePosition, null);
                        validMoves.add(diagonalAttackMove);
                    }
                }
            }
        }

    }


    public void addPromotionMoves(ChessPosition myPosition, ChessPosition movePosition, Collection<ChessMove> validMoves) {
        validMoves.add(new ChessMove(myPosition, movePosition, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(myPosition, movePosition, ChessPiece.PieceType.BISHOP));
        validMoves.add(new ChessMove(myPosition, movePosition, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(myPosition, movePosition, ChessPiece.PieceType.KNIGHT));
    }

    public boolean isInBounds(ChessPosition position) {
        if (position.getRow() >= 1 && position.getRow() <= 8) {
            return position.getColumn() >= 1 && position.getColumn() <= 8;
        }
        return false;
    }
}
