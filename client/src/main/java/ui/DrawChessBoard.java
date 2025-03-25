package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawChessBoard {
    //dimensions
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;


    public static void drawChessboard(ChessGame game, String playerColor) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawHeaders(out);
        drawBoard(out, game.getBoard(), playerColor);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out) {

        setBlack(out);

        String[] headers = {"a", "b", "c", "d", "e", "f", "g", "h"};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
            }
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

        setBlack(out);
    }

    private static void drawBoard(PrintStream out, ChessBoard board, String playerColor) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            drawRowOfSquares(out, board, boardRow);

            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                // Draw horizontal row separator.
                drawHorizontalLine(out);
                setBlack(out);
            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, ChessBoard board, int boardRow) {
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if ((boardRow + boardCol) % 2 == 0) {
                    setWhite(out);
                } else {
                    setBlack(out);
                }

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    ChessPosition position = new ChessPosition(boardRow + 1, boardCol + 1);
                    ChessPiece piece = board.getPiece(position);
                    printPiece(out, piece);
                } else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }

                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                    setBlack(out);
                    out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
                }
            }
            out.println();
        }
    }

    private static void drawHorizontalLine(PrintStream out) {

        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            setRed(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));

            setBlack(out);
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPiece(PrintStream out, ChessPiece piece) {
        if (piece == null) {
            out.print(EMPTY);
            return;
        }

        String pieceType = getPieceType(piece);
        out.print(pieceType);
    }

    private static String getPieceType(ChessPiece piece) {
        String color = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "W" : "B";
        return switch (piece.getPieceType()) {
            case PAWN -> color + "P";
            case ROOK -> color + "R";
            case KNIGHT -> color + "N";
            case BISHOP -> color + "B";
            case QUEEN -> color + "Q";
            case KING -> color + "K";
        };
    }

}
