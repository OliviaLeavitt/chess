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
        board.resetBoard();

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
        TeamColor teamColor = piece.getTeamColor();
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : pieceMoves) {
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece endPiece = board.getPiece(endPosition);

            //temp move
            board.addPiece(endPosition, piece);
            board.addPiece(startPosition, null);

            boolean inCheck = isInCheck(teamColor);

            //undo temp move
            board.addPiece(startPosition, piece);
            board.addPiece(endPosition, endPiece);

            if (!inCheck) {
                validMoves.add(move);
            }

        }
        return validMoves;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        TeamColor team = getTeamTurn();

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
        return !findAttackersPositions(teamColor).isEmpty();
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */

    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);

        if (isInCheck(teamColor)) {
            for (int row =1; row<=8; row++) {
                for (int col =1; col<=8; col++) {
                    ChessPosition position = new ChessPosition(row,col);
                    ChessPiece piece = board.getPiece(position);
                    if ((piece != null) && teamColor == piece.getTeamColor())
                        if (!validMoves(position).isEmpty()) {
                            return false;
                        }
                }

            }
            return true;

        }
        else {
            return false;
        }
    }

    public boolean canCaptureAttacker(TeamColor teamColor, ChessPosition attackerPos) {
        ArrayList<ChessPiece> teamChessPieces = board.getAllChessCurrPieces(teamColor);
        for (ChessPiece piece : teamChessPieces) {
            ChessPosition piecePosition = board.getChessPosition(piece);
            Collection<ChessMove> pieceMoves = piece.pieceMoves(board, piecePosition);

            for (ChessMove move : pieceMoves) {
                if (move.getEndPosition().equals(attackerPos)) {
                    ChessPiece attackerPiece = board.getPiece(attackerPos);
                    ChessPosition endPosition = move.getEndPosition();

                    board.addPiece(endPosition, piece);
                    board.addPiece(piecePosition, null);

                    boolean isInCheckAfterMove = isInCheck(teamColor);

                    board.addPiece(piecePosition, piece);
                    board.addPiece(attackerPos, attackerPiece);

                    if (!isInCheckAfterMove) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public ArrayList<ChessMove> getKingSafeMoves(TeamColor teamColor) {
        ArrayList<ChessMove> safeMoves = new ArrayList<>();
        ChessPosition kingPosition = getKingPosition(teamColor);
        ChessPiece kingPiece = board.getPiece(kingPosition);

        Collection<ChessMove> potentialKingMoves = kingPiece.pieceMoves(board, kingPosition);

        for (ChessMove move : potentialKingMoves) {
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece endPiece = board.getPiece(endPosition);

            //temp move
            board.addPiece(endPosition, kingPiece);
            board.addPiece(kingPosition, null);

            boolean isKingSafe = !isInCheck(teamColor);

            //undo temp move
            board.addPiece(kingPosition, kingPiece);
            board.addPiece(endPosition, endPiece);

            if (isKingSafe) {
                safeMoves.add(move);
            }

        }
        return safeMoves;

    }

    public ArrayList<ChessPosition> findAttackersPositions(TeamColor teamColor) {
        ArrayList<ChessPosition> attackers = new ArrayList<>();
        ChessPosition myKingPosition = getKingPosition(teamColor);
        TeamColor opponentColor = (teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        ArrayList<ChessPiece> opposingChessPieces = board.getAllChessCurrPieces(opponentColor);

        for (ChessPiece opposingChessPiece : opposingChessPieces) {
            ChessPosition opposingPiecePosition = board.getChessPosition(opposingChessPiece);
            Collection<ChessMove> opposingChessPieceMoves = opposingChessPiece.pieceMoves(board, opposingPiecePosition);

            for (ChessMove move : opposingChessPieceMoves) {
                if (move.getEndPosition().equals(myKingPosition)) {
                    attackers.add(opposingPiecePosition);

                }
            }
        }
        return attackers;
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
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);

        if (!isInCheck(teamColor)) {
            for (int row =1; row<=8; row++) {
                for (int col =1; col<=8; col++) {
                    ChessPosition position = new ChessPosition(row,col);
                    ChessPiece piece = board.getPiece(position);
                    if ((piece != null) && teamColor == piece.getTeamColor())
                        if (!validMoves(position).isEmpty()) {
                            return false;
                        }
                }

            }
            return true;

        }
        else {
            return false;
        }
    }
    public boolean thereIsSafeMoveForKing(boolean validMovesAvailable, Collection<ChessMove> validMoves, TeamColor teamColor, ChessPiece piece, ChessPosition piecePosition) {
        if (!validMovesAvailable) {
            return false;
        }
        for (ChessMove move : validMoves) {
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece endPiece = board.getPiece(endPosition);

            //temp move
            board.addPiece(endPosition, piece);
            board.addPiece(piecePosition, null);

            boolean isKingInCheckAfterMove = isInCheck(teamColor);

            //undo temp move
            board.addPiece(piecePosition, piece);
            board.addPiece(endPosition, endPiece);

            if (!isKingInCheckAfterMove) {
                return true;
            }

        }
        return false;
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
