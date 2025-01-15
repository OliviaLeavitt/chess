package chess;

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

    /**
     * pieceMoves: This method is similar to ChessGame.validMoves, except it does not
     * honor whose turn it is or check if the king is being
     * attacked. This method does account for enemy and friendly pieces blocking
     * movement paths. The pieceMoves method will need to take
     * into account the type of piece, and the location of other pieces on the board.
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (PieceType.KING.equals(type)) {

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
                        if (pieceOnPotentialMove == null || pieceOnPotentialMove.getTeamColor() != pieceColor) {
                            validMoves.add(new ChessMove(myPosition, potentialMove, null));
                        }
                    }
                }
            } return validMoves;

        } else if (PieceType.QUEEN.equals(type)) {
            int currRow = myPosition.row;
            int currCol = myPosition.col;


            ChessPosition potentialMove = new ChessPosition(currRow, currCol);


            //upward move
            while (currRow < 9) {
                ChessPiece pieceOnPotentialMove = board.getPiece(potentialMove);
                if (pieceOnPotentialMove == null || pieceOnPotentialMove.getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, potentialMove, null));

                }
            }


            return validMoves;







        } else if (PieceType.BISHOP.equals(type)) {
            //RETURN ROOK MOVES COLLECTION
        } else if (PieceType.KNIGHT.equals(type)) {
            //RETURN BISHOP MOVES COLLECTION
        } else if (PieceType.ROOK.equals(type)) {
            //RETURN KING MOVES COLLECTION
        } else if (PieceType.PAWN.equals(type)) {
            //RETURN queen MOVES COLLECTIon
        } return null;
    }
}