import Requests.WebRequests;
import Requests.BadResponseExeption;
import com.google.gson.Gson;
import model.*;
import chess.ChessGame;

import java.io.IOException;
import java.net.URISyntaxException;

import static ui.EscapeSequences.*;


public class PreLoginState extends UniversalState {

    public PreLoginState(WebRequests webRequests) {
        super(webRequests);
    }

    @Override
    String evaluateCommand(String cmd, String[] params) throws URISyntaxException, IOException, InterruptedException {
        return switch (cmd) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> quit();
            case "help" -> help();
            default -> "INVALID COMMAND: Use 'help' command to see a list of valid commands";
        };
    }

    private String register(String[] params) throws URISyntaxException, IOException, InterruptedException {
        if (params.length != 3) { throw new BadResponseExeption("INVALID NUMBER OF PARAMETERS: use the 'help' command to view command syntax"); }
        UserData accountInfo = new UserData(params[0], params[1], params[2]);
        String result = requestHandler.makeRequest("POST", "/user", accountInfo);
        AuthData authData = new Gson().fromJson(result, AuthData.class);
        System.out.println(RESET_TEXT_COLOR + "Successfully registered as " + authData.username());
        // logging the user in. Because we are going to the PostLoginState, we will only return once we are logged out
        return logUserIn(authData);
    }

    private String login(String[] params) {
        if (params.length != 2) { throw new BadResponseExeption("INVALID NUMBER OF PARAMETERS: use the 'help' command to view command syntax"); }
        LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
        return "implement";
    }

    private String quit() {
        continueLoop = false;
        return "Exiting...";
    }

    private String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - create an account and login
                login <USERNAME> <PASSWORD> - login to your account
                quit - go back to your shell
                help - list possible commands
                """;
    }


    private String logUserIn(AuthData authData) {
        System.out.println(RESET_TEXT_COLOR + "logging in...");
        new PostLoginState(authData.username(), authData.authToken(), requestHandler).mainLoop(); // will continue in the main loop until user logs out
        return  "logged out successfully";
    }

}
