package ui;

import chess.*;
import model.Game;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class DrawChessBoard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;

    public static void drawChessboard(Game game, String playerColor, Collection<ChessMove> highlights) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        boolean isBlackPerspective = "BLACK".equalsIgnoreCase(playerColor);
        ChessBoard board = (game != null) ? game.game().getBoard() : initializeBoard();

        drawHeaders(out, isBlackPerspective);
        drawBoard(out, board, isBlackPerspective, highlights);
        drawHeaders(out, isBlackPerspective);
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

        headers = isBlackPerspective ? blackHeaders : whiteHeaders;

        for (String header : headers) {
            out.print(" " + header + "  ");
        }
        out.println();
    }

    private static void drawBoard(PrintStream out, ChessBoard board, boolean isBlackPerspective, Collection<ChessMove> highlights) {
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            drawRow(out, board, row, isBlackPerspective, highlights);
        }
    }
    private static void drawRow(PrintStream out, ChessBoard board, int row, boolean isBlackPerspective, Collection<ChessMove> highlights) {
        int sideHeaders = isBlackPerspective ? row + 1 : BOARD_SIZE_IN_SQUARES - row;
        out.print(sideHeaders);
        out.print(" ");

        colorRow(out, board, row, isBlackPerspective, highlights);

        out.print(RESET_BG_COLOR);
        out.print(" ");
        out.print(sideHeaders);
        out.println();
    }

    private static void colorRow(PrintStream out, ChessBoard board, int row, boolean isBlackPerspective, Collection<ChessMove> highlights) {
        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
            out.print(SET_TEXT_COLOR_WHITE);
            drawSquare(out, board, row, col, isBlackPerspective, highlights);
            out.print(SET_TEXT_COLOR_WHITE);
        }
    }


    private static void drawSquare(PrintStream out, ChessBoard board, int row, int col, boolean isBlackPerspective, Collection<ChessMove> highlights) {
        out.print(SET_TEXT_COLOR_BLACK);
        int actualCol = isBlackPerspective ? BOARD_SIZE_IN_SQUARES - col - 1 : col;
        int actualRow = isBlackPerspective ? row : BOARD_SIZE_IN_SQUARES - row - 1;

        ChessPosition position = new ChessPosition(actualRow + 1, actualCol + 1);
        ChessPiece piece = board.getPiece(position);

        if (isHighlighted(position, highlights)) {
            out.print(SET_BG_COLOR_GREEN);
        } else {
            setSquareColor(out, actualRow, actualCol);
        }

        drawPiece(out, piece);
    }

    private static boolean isHighlighted(ChessPosition position, Collection<ChessMove> highlights) {
        if (highlights == null) return false;

        for (ChessMove move : highlights) {
            if (move.getEndPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }


    private static void setSquareColor(PrintStream out, int row, int col) {
        boolean isDarkSquare = (row + col) % 2 == 1;
        out.print(isDarkSquare ? SET_BG_COLOR_MAGENTA : SET_BG_COLOR_WHITE);
    }

    private static void drawPiece(PrintStream out, ChessPiece piece) {
        if (piece != null) {
            out.print(getPiece(piece));
        } else {
            out.print("    ");
        }
    }

    private static String getPiece(ChessPiece piece) {
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
