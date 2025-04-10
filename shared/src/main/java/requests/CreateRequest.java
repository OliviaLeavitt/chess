package requests;

import chess.ChessGame;
import model.Game;

public record CreateRequest(String gameName, ChessGame chessGame) {
}