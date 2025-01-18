package chess;
import chess.pieceMoves.KingMoves;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor=pieceColor;
        this.type=type;
    }
    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }
    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }
    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }
    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (PieceType.KING.equals(type)) {
            return new KingMoves().pieceMoves(myPosition, board, this);
        } else if (PieceType.QUEEN.equals(type)) {
            int currRow = myPosition.row;
            int currCol = myPosition.col;

            //upward move
            addValidMovesForADirection(currCol, currRow, 0, 1, myPosition, board, validMoves);
            //downward move
            addValidMovesForADirection(currCol, currRow, 0, -1, myPosition, board, validMoves);
            //right side
            addValidMovesForADirection(currCol, currRow, 1, 0, myPosition, board, validMoves);
            //left side
            addValidMovesForADirection(currCol, currRow, -1, 0, myPosition, board, validMoves);
            //right up diagonal
            addValidMovesForADirection(currCol, currRow, 1, 1, myPosition, board, validMoves);
            //left up diagonal
            addValidMovesForADirection(currCol, currRow, -1, 1, myPosition, board, validMoves);
            //left down diagonal
            addValidMovesForADirection(currCol, currRow, -1, -1, myPosition, board, validMoves);
            //right down diagonal
            addValidMovesForADirection(currCol, currRow, 1, -1, myPosition, board, validMoves);

            return validMoves;
        } else if (PieceType.BISHOP.equals(type)) {
            int currRow = myPosition.row;
            int currCol = myPosition.col;
            //right up diagonal
            addValidMovesForADirection(currCol, currRow, 1, 1, myPosition, board, validMoves);
            //left up diagonal
            addValidMovesForADirection(currCol, currRow, -1, 1, myPosition, board, validMoves);
            //left down diagonal
            addValidMovesForADirection(currCol, currRow, -1, -1, myPosition, board, validMoves);
            //right down diagonal
            addValidMovesForADirection(currCol, currRow, 1, -1, myPosition, board, validMoves);

            return validMoves;


        } else if (PieceType.KNIGHT.equals(type)) {
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
            for (ChessPosition potentialMove : potentialMoves) {
                if (potentialMove.getRow() >= 1 && potentialMove.getRow() <= 8) {
                    if (potentialMove.getColumn() >= 1 && potentialMove.getColumn() <= 8) {
                        ChessPiece pieceOnPotentialMove = board.getPiece(potentialMove);
                        if (pieceOnPotentialMove == null || pieceOnPotentialMove.getTeamColor() != pieceColor) {
                            validMoves.add(new ChessMove(myPosition, potentialMove, null));
                        }
                    }
                }
            } return validMoves;
        } else if (PieceType.ROOK.equals(type)) {
            int currRow = myPosition.row;
            int currCol = myPosition.col;
            //upward move
            addValidMovesForADirection(currCol, currRow, 0, 1, myPosition, board, validMoves);
            //downward move
            addValidMovesForADirection(currCol, currRow, 0, -1, myPosition, board, validMoves);
            //right side
            addValidMovesForADirection(currCol, currRow, 1, 0, myPosition, board, validMoves);
            //left side
            addValidMovesForADirection(currCol, currRow, -1, 0, myPosition, board, validMoves);
            return validMoves;

        } else if (PieceType.PAWN.equals(type)) {

            // Start moves for white pawn
            ChessPosition startUpwardMovePosition = new ChessPosition(myPosition.row + 2, myPosition.col);
            ChessPiece pieceOnIntermediateWhiteMove = board.getPiece(new ChessPosition(myPosition.row + 1, myPosition.col));

            if (startUpwardMovePosition.getRow() >= 1 && startUpwardMovePosition.getRow() <= 8) {
                if (startUpwardMovePosition.getColumn() >= 1 && startUpwardMovePosition.getColumn() <= 8) {
                    ChessPiece pieceOnUpwardMoveFromStart = board.getPiece(startUpwardMovePosition);
                    if (pieceColor == ChessGame.TeamColor.WHITE) {
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
                    if (pieceColor == ChessGame.TeamColor.BLACK) {
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
                    if (pieceColor == ChessGame.TeamColor.WHITE) {
                        if (pieceOnUpwardMove == null) {
                            if (upwardMovePosition.getRow() == 8) {
                                validMoves.add(new ChessMove(myPosition, upwardMovePosition, PieceType.QUEEN));
                                validMoves.add(new ChessMove(myPosition, upwardMovePosition, PieceType.BISHOP));
                                validMoves.add(new ChessMove(myPosition, upwardMovePosition, PieceType.ROOK));
                                validMoves.add(new ChessMove(myPosition, upwardMovePosition, PieceType.KNIGHT));
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
                    if (pieceColor == ChessGame.TeamColor.BLACK) {
                        if (pieceOnDownwardMove == null) {
                            if (downwardMovePosition.getRow() == 1) {
                                validMoves.add(new ChessMove(myPosition, downwardMovePosition, PieceType.QUEEN));
                                validMoves.add(new ChessMove(myPosition, downwardMovePosition, PieceType.BISHOP));
                                validMoves.add(new ChessMove(myPosition, downwardMovePosition, PieceType.ROOK));
                                validMoves.add(new ChessMove(myPosition, downwardMovePosition, PieceType.KNIGHT));
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
                        if (pieceColor == ChessGame.TeamColor.BLACK) {
                            if (pieceOnDiagonalMove != null && pieceOnDiagonalMove.getTeamColor() != pieceColor) {
                                if (diagonalMove.getRow() == 1) {
                                    validMoves.add(new ChessMove(myPosition, diagonalMove, PieceType.QUEEN));
                                    validMoves.add(new ChessMove(myPosition, diagonalMove, PieceType.BISHOP));
                                    validMoves.add(new ChessMove(myPosition, diagonalMove, PieceType.ROOK));
                                    validMoves.add(new ChessMove(myPosition, diagonalMove, PieceType.KNIGHT));
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
                        if (pieceColor == ChessGame.TeamColor.WHITE) {
                            if (pieceOnDiagonalMove != null && pieceOnDiagonalMove.getTeamColor() != pieceColor) {
                                if (diagonalMove.getRow() == 8) {
                                    validMoves.add(new ChessMove(myPosition, diagonalMove, PieceType.QUEEN));
                                    validMoves.add(new ChessMove(myPosition, diagonalMove, PieceType.BISHOP));
                                    validMoves.add(new ChessMove(myPosition, diagonalMove, PieceType.ROOK));
                                    validMoves.add(new ChessMove(myPosition, diagonalMove, PieceType.KNIGHT));
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


        } return null;
    }

    private void addValidMovesForADirection(int currCol, int currRow, int colIncrement, int rowIncrement,ChessPosition myPosition, ChessBoard board, Collection<ChessMove> validMoves) {
        currCol += colIncrement;
        currRow += rowIncrement;
        while (currCol > 0 && currCol < 9 && currRow > 0 && currRow < 9) {
            ChessPosition potentialMove = new ChessPosition(currRow, currCol);
            ChessPiece pieceOnPotentialMove = board.getPiece(potentialMove);
            if (pieceOnPotentialMove == null) {
                validMoves.add(new ChessMove(myPosition, potentialMove, null));
            }
            else if (pieceOnPotentialMove.getTeamColor() != pieceColor) {
                validMoves.add(new ChessMove(myPosition, potentialMove, null));
                break;
            }
            else {
                break;
            }
            currCol += colIncrement;
            currRow += rowIncrement;
        }

    }
}