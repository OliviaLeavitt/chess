package chess;
import chess.pieceMoves.*;

import java.util.ArrayList;
import java.util.Collection;
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
            return new KingMoves().pieceMoves(myPosition, board, validMoves, this);
        } else if (PieceType.QUEEN.equals(type)) {
            return new QueenMoves().pieceMoves(myPosition, board, validMoves, this);
        } else if (PieceType.BISHOP.equals(type)) {
            return new BishopMoves().pieceMoves(myPosition, board, validMoves,this);
        } else if (PieceType.KNIGHT.equals(type)) {
            return new KnightMoves().pieceMoves(myPosition, board, validMoves, this);
        } else if (PieceType.ROOK.equals(type)) {
            return new RookMoves().pieceMoves(myPosition, board, validMoves,this);
        } else if (PieceType.PAWN.equals(type)) {
            return new PawnMoves().pieceMoves(myPosition, board, validMoves, this);
        } return null;
    }

    public void addValidMovesForADirection(int currCol, int currRow, int colIncrement, int rowIncrement, ChessPosition myPosition, ChessBoard board, Collection<ChessMove> validMoves) {
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