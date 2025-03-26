package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawChessBoard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;

    public static void drawChessboard(ChessGame game, String playerColor) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        boolean isBlackPerspective = "BLACK".equalsIgnoreCase(playerColor);
        ChessBoard board = initializeBoard();

        drawHeaders(out, isBlackPerspective);
        drawBoard(out, board, isBlackPerspective);
    }

    private static ChessBoard initializeBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return board;
    }

    private static void drawHeaders(PrintStream out, boolean isBlackPerspective) {
        out.print(SET_TEXT_COLOR_WHITE);
        out.print("  ");
        String[] headers = new String[0];
        String[] whiteHeaders = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] blackHeaders = {"h", "g", "f", "e", "d", "c", "b", "a"};

        headers = isBlackPerspective ? blackHeaders : whiteHeaders;;

        for (String header : headers) {
            out.print(" " + header + "  ");
        }
        out.println();
    }

    private static void drawBoard(PrintStream out, ChessBoard board, boolean isBlackPerspective) {
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            drawRow(out, board, row, isBlackPerspective);
        }
        drawHeaders(out, isBlackPerspective);
    }
    private static void drawRow(PrintStream out, ChessBoard board, int row, boolean isBlackPerspective) {
        int sideHeaders = isBlackPerspective ? row + 1 : BOARD_SIZE_IN_SQUARES - row;
        out.print(sideHeaders);
        out.print(" ");

        colorRow(out, board, row, isBlackPerspective);

        out.print(RESET_BG_COLOR);
        out.print(" ");
        out.print(sideHeaders);
        System.out.println();
    }

    private static void colorRow(PrintStream out, ChessBoard board, int row, boolean isBlackPerspective) {
        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
            out.print(SET_TEXT_COLOR_WHITE);
            drawSquare(out, board, row, col, isBlackPerspective);
            out.print(SET_TEXT_COLOR_WHITE);
        }
    }

    private static void drawSquare(PrintStream out, ChessBoard board, int row, int col, boolean isBlackPerspective) {
        out.print(SET_TEXT_COLOR_BLACK);
        int actualCol = isBlackPerspective ? BOARD_SIZE_IN_SQUARES - col - 1 : col;
        int actualRow = isBlackPerspective ? row : BOARD_SIZE_IN_SQUARES - row - 1;

        ChessPosition position = new ChessPosition(actualRow + 1, actualCol + 1);
        ChessPiece piece = board.getPiece(position);

        setSquareColor(out, actualRow, actualCol);
        drawPieceOrEmpty(out, piece);
    }


    private static void setSquareColor(PrintStream out, int row, int col) {
        boolean isDarkSquare = (row + col) % 2 == 1;
        out.print(isDarkSquare ? SET_BG_COLOR_MAGENTA : SET_BG_COLOR_WHITE);
    }

    private static void drawPieceOrEmpty(PrintStream out, ChessPiece piece) {
        if (piece != null) {
            out.print(getPieceSymbol(piece));
        } else {
            out.print("    ");
        }
    }

    private static String getPieceSymbol(ChessPiece piece) {
        String color = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "W" : "B";
        String symbol = switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN -> "P";
        };

        return " " + color + symbol + " ";
    }

}
