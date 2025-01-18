package chess.pieceMoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoves {
    public Collection<ChessMove> pieceMoves (ChessPosition myPosition, ChessBoard board, Collection<ChessMove> validMoves, ChessPiece piece) {
        // Start moves for white pawn
        ChessPosition startUpwardMovePosition = new ChessPosition(myPosition.row + 2, myPosition.col);
        ChessPiece pieceOnIntermediateWhiteMove = board.getPiece(new ChessPosition(myPosition.row + 1, myPosition.col));

        if (startUpwardMovePosition.getRow() >= 1 && startUpwardMovePosition.getRow() <= 8) {
            if (startUpwardMovePosition.getColumn() >= 1 && startUpwardMovePosition.getColumn() <= 8) {
                ChessPiece pieceOnUpwardMoveFromStart = board.getPiece(startUpwardMovePosition);
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (myPosition.getRow() == 2 && pieceOnUpwardMoveFromStart == null && pieceOnIntermediateWhiteMove == null) {
                        validMoves.add(new ChessMove(myPosition, startUpwardMovePosition, null));
                    }
                }
            }
        }

        //START
        ChessPosition startDownwardMovePosition = new ChessPosition(myPosition.row - 2, myPosition.col);
        ChessPiece pieceOnIntermediateBlackMove = board.getPiece(new ChessPosition(myPosition.row - 1, myPosition.col));

        if (startDownwardMovePosition.getRow() >= 1 && startDownwardMovePosition.getRow() <= 8) {
            if (startDownwardMovePosition.getColumn() >= 1 && startDownwardMovePosition.getColumn() <= 8) {
                ChessPiece pieceOnDownwardMoveFromStart = board.getPiece(startDownwardMovePosition);
                if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    if (myPosition.getRow() == 7 && pieceOnDownwardMoveFromStart == null && pieceOnIntermediateBlackMove == null) {
                        validMoves.add(new ChessMove(myPosition, startDownwardMovePosition, null));
                    }
                }
            }
        }


        //up move
        ChessPosition upwardMovePosition = new ChessPosition(myPosition.row + 1, myPosition.col);
        ChessPiece pieceOnUpwardMove = board.getPiece(upwardMovePosition);

        if (upwardMovePosition.getRow() >= 1 && upwardMovePosition.getRow() <= 8) {
            if (upwardMovePosition.getColumn() >= 1 && upwardMovePosition.getColumn() <= 8) {
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (pieceOnUpwardMove == null) {
                        if (upwardMovePosition.getRow() == 8) {
                            validMoves.add(new ChessMove(myPosition, upwardMovePosition, ChessPiece.PieceType.QUEEN));
                            validMoves.add(new ChessMove(myPosition, upwardMovePosition, ChessPiece.PieceType.BISHOP));
                            validMoves.add(new ChessMove(myPosition, upwardMovePosition, ChessPiece.PieceType.ROOK));
                            validMoves.add(new ChessMove(myPosition, upwardMovePosition, ChessPiece.PieceType.KNIGHT));
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, upwardMovePosition, null));
                        }
                    }
                }
            }
        }
        //down move
        ChessPosition downwardMovePosition = new ChessPosition(myPosition.row -1, myPosition.col);
        ChessPiece pieceOnDownwardMove = board.getPiece(downwardMovePosition);
        if (downwardMovePosition.getRow() >= 1 && downwardMovePosition.getRow() <= 8) {
            if (downwardMovePosition.getColumn() >= 1 && downwardMovePosition.getColumn() <= 8) {
                if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    if (pieceOnDownwardMove == null) {
                        if (downwardMovePosition.getRow() == 1) {
                            validMoves.add(new ChessMove(myPosition, downwardMovePosition, ChessPiece.PieceType.QUEEN));
                            validMoves.add(new ChessMove(myPosition, downwardMovePosition, ChessPiece.PieceType.BISHOP));
                            validMoves.add(new ChessMove(myPosition, downwardMovePosition, ChessPiece.PieceType.ROOK));
                            validMoves.add(new ChessMove(myPosition, downwardMovePosition, ChessPiece.PieceType.KNIGHT));
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, downwardMovePosition, null));
                        }
                    }
                }
            }
        }

        //down diagonals
        List<ChessPosition> downwardDiagonalMoves = List.of(
                new ChessPosition(myPosition.row - 1, myPosition.col + 1),
                new ChessPosition(myPosition.row - 1, myPosition.col - 1)
        );

        for (ChessPosition diagonalMove : downwardDiagonalMoves) {
            if (diagonalMove.getRow() >= 1 && diagonalMove.getRow() <= 8) {
                if (diagonalMove.getColumn() >= 1 && diagonalMove.getColumn() <= 8) {
                    ChessPiece pieceOnDiagonalMove = board.getPiece(diagonalMove);
                    if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        if (pieceOnDiagonalMove != null && pieceOnDiagonalMove.getTeamColor() != piece.getTeamColor()) {
                            if (diagonalMove.getRow() == 1) {
                                validMoves.add(new ChessMove(myPosition, diagonalMove, ChessPiece.PieceType.QUEEN));
                                validMoves.add(new ChessMove(myPosition, diagonalMove, ChessPiece.PieceType.BISHOP));
                                validMoves.add(new ChessMove(myPosition, diagonalMove, ChessPiece.PieceType.ROOK));
                                validMoves.add(new ChessMove(myPosition, diagonalMove, ChessPiece.PieceType.KNIGHT));
                            }
                            else {
                                validMoves.add(new ChessMove(myPosition, diagonalMove, null));
                            }
                        }
                    }

                }
            }
        }

        //up diagonal moves
        List<ChessPosition> upwardDiagonalMoves = List.of(
                new ChessPosition(myPosition.row + 1, myPosition.col + 1),
                new ChessPosition(myPosition.row + 1, myPosition.col - 1)
        );

        for (ChessPosition diagonalMove : upwardDiagonalMoves) {
            if (diagonalMove.getRow() >= 1 && diagonalMove.getRow() <= 8) {
                if (diagonalMove.getColumn() >= 1 && diagonalMove.getColumn() <= 8) {
                    ChessPiece pieceOnDiagonalMove = board.getPiece(diagonalMove);
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        if (pieceOnDiagonalMove != null && pieceOnDiagonalMove.getTeamColor() != piece.getTeamColor()) {
                            if (diagonalMove.getRow() == 8) {
                                validMoves.add(new ChessMove(myPosition, diagonalMove, ChessPiece.PieceType.QUEEN));
                                validMoves.add(new ChessMove(myPosition, diagonalMove, ChessPiece.PieceType.BISHOP));
                                validMoves.add(new ChessMove(myPosition, diagonalMove, ChessPiece.PieceType.ROOK));
                                validMoves.add(new ChessMove(myPosition, diagonalMove, ChessPiece.PieceType.KNIGHT));
                            }
                            else {
                                validMoves.add(new ChessMove(myPosition, diagonalMove, null));
                            }
                        }
                    }

                }
            }
        }

        return validMoves;
    }
}
