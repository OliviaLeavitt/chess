package chess;

import chess.pieceMoves.PawnMoves;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece != null) {
            return piece.pieceMoves(board, startPosition);
        }
        else {
            return null;
        }
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move: " + move);
        }
        else {
            ChessPiece piece = board.getPiece(move.getStartPosition());
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition myKingPosition = getKingPosition(teamColor);
        ArrayList<ChessPiece> opposingChessPieces = board.getAllChessCurrPieces(teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);

        for (ChessPiece opposingChessPiece : opposingChessPieces) {
            ChessPosition opposingPiecePosition = board.getChessPosition(opposingChessPiece);
            Collection<ChessMove> opposingChessPieceMoves = opposingChessPiece.pieceMoves(board, opposingPiecePosition);

            for (ChessMove move : opposingChessPieceMoves) {
                if (move.getEndPosition().equals(myKingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }
    public ChessPosition getKingPosition(TeamColor teamColor) {
        for (ChessPiece piece : board.getAllChessCurrPieces(teamColor)) {
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                return board.getChessPosition(piece);
            }
        }
        return null;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        ChessPiece kingPiece = board.getPiece(kingPosition);
        Collection<ChessMove> potentialKingMoves = kingPiece.pieceMoves(board, kingPosition);

        if (isInCheck(teamColor)) {
            if (potentialKingMoves.isEmpty()) {
                return true;
            }
            return checkIfKingMovesInCheck(potentialKingMoves, kingPosition, teamColor);
        }
        return false;
    }

    public boolean checkIfKingMovesInCheck(Collection<ChessMove> potentialKingMoves, ChessPosition kingPosition, TeamColor teamColor) {
        for (ChessMove move : potentialKingMoves) {
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece tempKing = board.getPiece(kingPosition);

            board.addPiece(endPosition, tempKing);
            boolean isKingSafe = !isInCheck(teamColor);
            board.addPiece(kingPosition, tempKing);


            if (isKingSafe) {
                return false;
            }
        }
        return true;
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ArrayList<ChessPiece> teamPieces = board.getAllChessCurrPieces(teamColor);

        for (ChessPiece piece : teamPieces) {
            ChessPosition piecePosition = board.getChessPosition(piece);
            Collection<ChessMove> validMoves = piece.pieceMoves(board, piecePosition);
            if (!validMoves.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
