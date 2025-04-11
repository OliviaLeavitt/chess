package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;
    boolean gameOver;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();

    }

    public boolean getGameOver() {
        return this.gameOver;
    }
    public void setGameOver(boolean bool) {
        this.gameOver = bool;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
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

            //make move
            makeOrUndoTempMove(startPosition, endPosition, piece, null);

            boolean inCheck = isInCheck(teamColor);

            //undo temp move
            makeOrUndoTempMove(endPosition, startPosition, piece, endPiece);

            if (!inCheck) {
                validMoves.add(move);
            }

        }
        return validMoves;
    }

    void makeOrUndoTempMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece piece, ChessPiece endPiece) {
        board.addPiece(endPosition, piece);
        board.addPiece(startPosition, endPiece);
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        TeamColor currTeamTurn = getTeamTurn();
        ChessPosition movePosition = move.getStartPosition();

        if (board.getPiece(movePosition) == null) {
            throw new InvalidMoveException("No piece on move: " + move);

        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move: " + move);
        } else {
            ChessPiece piece = board.getPiece(move.getStartPosition());
            if (currTeamTurn != piece.getTeamColor()) {
                throw new InvalidMoveException("Wrong team turn: " + move);
            }
            if (move.getPromotionPiece() != null) {
                board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            } else {
                board.addPiece(move.getEndPosition(), piece);
            }
            board.addPiece(move.getStartPosition(), null);


            TeamColor nextTeamTurn = (currTeamTurn == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK;
            setTeamTurn(nextTeamTurn);

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
        if (isInCheck(teamColor)) {
            return hasNoMovesLeft(teamColor);

        } else {
            return false;
        }
    }

    private boolean hasNoMovesLeft(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if ((piece != null) && teamColor == piece.getTeamColor()) {
                    if (!validMoves(position).isEmpty()) {
                        return false;
                    }
                }
            }

        }
        return true;
    }


    public ArrayList<ChessPosition> findAttackersPositions(TeamColor teamColor) {
        ArrayList<ChessPosition> attackers = new ArrayList<>();
        ChessPosition myKingPosition = getKingPosition(teamColor);

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition opposingPiecePosition = new ChessPosition(row, col);

                ChessPiece opposingChessPiece = board.getPiece(opposingPiecePosition);
                if (opposingChessPiece != null && opposingChessPiece.getTeamColor() != teamColor) {
                    getAttackers(attackers, opposingPiecePosition, opposingChessPiece, myKingPosition);

                }
            }
        }
        return attackers;
    }

    public void getAttackers(ArrayList<ChessPosition> attackers, ChessPosition opposingPiecePosition,
                             ChessPiece opposingChessPiece, ChessPosition myKingPosition) {
        Collection<ChessMove> opposingChessPieceMoves = opposingChessPiece.pieceMoves(board, opposingPiecePosition);

        for (ChessMove move : opposingChessPieceMoves) {
            if (move.getEndPosition().equals(myKingPosition)) {
                attackers.add(opposingPiecePosition);

            }
        }
    }

    public ChessPosition getKingPosition(TeamColor teamColor) {
        for (ChessPiece piece : board.getAllChessCurrPieces(teamColor)) {
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                return board.getKingOnBoardPosition(piece);
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
        if (!isInCheck(teamColor)) {
            return hasNoMovesLeft(teamColor);

        } else {
            return false;
        }
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
