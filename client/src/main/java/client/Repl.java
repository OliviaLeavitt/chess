package client;

import java.util.Scanner;
import ui.EscapeSequences;


public class Repl {
    private final client.ChessClient client;

    public Repl(String serverUrl) {
        client = new client.ChessClient(serverUrl);
    }

    public void run() {
        System.out.println("Welcome 240 chess. Type Help to get started.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result + EscapeSequences.RESET_TEXT_COLOR);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + msg + EscapeSequences.RESET_TEXT_COLOR);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + ">>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);

    }

}
