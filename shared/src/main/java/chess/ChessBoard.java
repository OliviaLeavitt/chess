package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] squares;


    public ChessBoard() {
        squares = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    public ChessPosition getChessPosition(ChessPiece piece) {
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                ChessPosition position = new ChessPosition(row + 1, column + 1);
                if (getPiece(position) == piece) {
                    return position;
                }
            }
        }
        return null;
    }

            public ArrayList<ChessPiece> getAllChessCurrPieces(ChessGame.TeamColor teamColor) {
                ArrayList<ChessPiece> currPieces = new ArrayList<>();
                for (int row = 0; row < 8; row++) {
                    for (int column = 0; column < 8 ; column++) {
                        ChessPiece piece = squares[row][column];
                        if (piece != null && piece.getTeamColor() == teamColor) {
                            currPieces.add(piece);
                        }
                    }
                }
                return currPieces;
            }


            /**
             * Gets a chess piece on the chessboard
             *
             * @param position The position to get the piece from
             * @return Either the piece at the position, or null if no piece is at that
             * position
             */
            public ChessPiece getPiece(ChessPosition position) {
                return squares[position.getRow() -1 ][position.getColumn() -1 ];
            }

            /**
             * Sets the board to the default starting board
             * (How the game of chess normally starts)
             */
            public void resetBoard() {
                setUpWhiteSideOfBoard();
                setUpBlackSideOfBoard();

            }

            void setUpBlackSideOfBoard() {
                //setup rooks
                ChessPiece blackRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                ChessPosition firstBlackRookPosition = new ChessPosition(8,1);
                ChessPosition secondBlackRookPosition = new ChessPosition(8, 8);
                addPiece(firstBlackRookPosition, blackRook);
                addPiece(secondBlackRookPosition, blackRook);

                //setup knights
                ChessPiece blackKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                ChessPosition firstBlackKnightPosition = new ChessPosition(8,2);
                ChessPosition secondBlackKnightPosition = new ChessPosition(8,7);
                addPiece(firstBlackKnightPosition, blackKnight);
                addPiece(secondBlackKnightPosition, blackKnight);

                //setupBishops
                ChessPiece blackBishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                ChessPosition firstBlackBishopPosition = new ChessPosition(8,3);
                ChessPosition secondBlackBishopPosition = new ChessPosition(8,6);
                addPiece(firstBlackBishopPosition, blackBishop);
                addPiece(secondBlackBishopPosition, blackBishop);

                //setupQueen
                ChessPiece blackQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                ChessPosition blackQueenPosition = new ChessPosition(8,4);
                addPiece(blackQueenPosition, blackQueen);

                //setupKing
                ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                ChessPosition blackKingPosition = new ChessPosition(8,5);
                addPiece(blackKingPosition, blackKing);

                //setupPawns
                ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                for (int col = 1; col<9; col++) {
                    ChessPosition blackPawnPosition = new ChessPosition(7, col);
                    addPiece(blackPawnPosition, blackPawn);
                }

            }


            void setUpWhiteSideOfBoard() {
                //setup rooks
                ChessPiece whiteRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                ChessPosition firstWhiteRookPosition = new ChessPosition(1,1);
                ChessPosition secondWhiteRookPosition = new ChessPosition(1,8);
                addPiece(firstWhiteRookPosition, whiteRook);
                addPiece(secondWhiteRookPosition, whiteRook);

                //setup knights
                ChessPiece whiteKnight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                ChessPosition firstWhiteKnightPosition = new ChessPosition(1,2);
                ChessPosition secondWhiteKnightPosition = new ChessPosition(1,7);
                addPiece(firstWhiteKnightPosition, whiteKnight);
                addPiece(secondWhiteKnightPosition, whiteKnight);

                //setupBishops
                ChessPiece whiteBishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                ChessPosition firstWhiteBishopPosition = new ChessPosition(1,3);
                ChessPosition secondWhiteBishopPosition = new ChessPosition(1,6);
                addPiece(firstWhiteBishopPosition, whiteBishop);
                addPiece(secondWhiteBishopPosition, whiteBishop);

                //setupQueen
                ChessPiece whiteQueen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                ChessPosition whiteQueenPosition = new ChessPosition(1,4);
                addPiece(whiteQueenPosition, whiteQueen);

                //setupKing
                ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                ChessPosition whiteKingPosition = new ChessPosition(1,5);
                addPiece(whiteKingPosition, whiteKing);

                //setupPawns
                ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                for (int col = 1; col<9; col++) {
                    ChessPosition whitePawnPosition = new ChessPosition(2, col);
                    addPiece(whitePawnPosition, whitePawn);
                }

            }


            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                ChessBoard that = (ChessBoard) o;
                return Objects.deepEquals(squares, that.squares);
            }

            @Override
            public int hashCode() {
                return Arrays.deepHashCode(squares);
            }

            @Override
            public String toString() {
                return "ChessBoard{" +
                        "squares=" + Arrays.deepToString(squares) +
                        '}';
            }
        }



