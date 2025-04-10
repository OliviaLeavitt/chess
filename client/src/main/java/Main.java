import chess.*;
import client.Repl;
import exception.ResponseException;

public class Main {
    public static void main(String[] args) throws ResponseException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        String serverUrl = "http://localhost:8081";
        new Repl(serverUrl).run();

    }
}