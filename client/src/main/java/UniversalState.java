import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Scanner;
import Requests.WebRequests;
import model.GameData;
import model.AuthData;
import model.UserData;
import chess.ChessGame;

import static ui.EscapeSequences.*;



public class UniversalState {
    String displayName;
    boolean continueLoop;
    WebRequests requestHandler;

    public UniversalState(WebRequests webRequests) {
        requestHandler = webRequests;
        displayName = "[LOGGED_OUT]";
        continueLoop = true;
    }

    void mainLoop() {
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (continueLoop) {
            System.out.print("\n" + SET_TEXT_COLOR_WHITE + displayName + " >>> " + SET_TEXT_COLOR_LIGHT_GREY);
            try { result = RESET_TEXT_COLOR + tokenizeCommand(scanner.nextLine()); }
            catch (Throwable e) { result = SET_TEXT_COLOR_RED + e.toString(); }
            System.out.print(result);
        }
        System.out.println();
    }

    String tokenizeCommand(String input) throws URISyntaxException, IOException, InterruptedException {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return evaluateCommand(cmd, params);
    }


    String evaluateCommand(String cmd, String[] params) throws URISyntaxException, IOException, InterruptedException {
        return "Command Not Implemented";
    }
}
