import Requests.BadResponseExeption;
import Requests.WebRequests;
import com.google.gson.Gson;
import model.CreateGameResult;
import model.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PostLoginState extends UniversalState {
    private Map<Integer, Integer> gameIDs = new HashMap<>();

    public PostLoginState(String userName, String token, WebRequests webRequests) {
        super(webRequests);
        displayName = userName;
        webRequests.updateAuthToken(token);
    }

    @Override
    String evaluateCommand(String cmd, String[] params) throws URISyntaxException, IOException, InterruptedException {
        return switch (cmd) {
            case "create" -> create(params);
            case "list" -> list();
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "logout" -> logout();
            case "help" -> help();
            default -> "INVALID COMMAND: Use 'help' command to see a list of valid commands";
        };
    }

    private String create(String[] params) throws URISyntaxException, IOException, InterruptedException {
        if (params.length != 1) { throw new BadResponseExeption("INVALID NUMBER OF PARAMETERS: use the 'help' command to view command syntax"); }
        CreateGameRequest requestObject = new CreateGameRequest(params[0]);
        String result = requestHandler.makeRequest("POST", "/game", requestObject);
        CreateGameResult resultObject = new Gson().fromJson(result, CreateGameResult.class);
        return "Game creation was successful! GameID: " + resultObject.gameID();
    }

    private String list() throws URISyntaxException, IOException, InterruptedException {
        String result = requestHandler.makeRequest("GET", "/game", null);
        ListGamesResult resultObject = new Gson().fromJson(result, ListGamesResult.class);
        return listGamesString(resultObject);
    }

    private String join(String[] params) throws URISyntaxException, IOException, InterruptedException {
        if (params.length != 2) { throw new BadResponseExeption("INVALID NUMBER OF PARAMETERS: use the 'help' command to view command syntax"); }
        if (!Objects.equals(params[1], "WHITE") && !Objects.equals(params[1], "BLACK")) {
            throw new BadResponseExeption("INVALID PLAYER COLOR: Second parameter must be either WHITE or BLACK");
        }
        JoinGameRequest requestObject = new JoinGameRequest(params[1], Integer.parseInt(params[0]));
        String result = requestHandler.makeRequest("POST", "/game", requestObject);
        return "Joined Game!"; // do more silly
    }

    private String observe(String[] params) {
        if (params.length != 1) { throw new BadResponseExeption("INVALID NUMBER OF PARAMETERS: use the 'help' command to view command syntax"); }

        return "implement";
    }

    private String logout() throws URISyntaxException, IOException, InterruptedException {
        String result = requestHandler.makeRequest("DELETE", "/session", null);
        continueLoop = false;
        return "Exiting...";
    }

    private String help() {
        return """
                create <NAME> - create a new game
                list - list active games
                join <ID> [WHITE|BLACK] - join a game. Second parameter must be either WHITE or BLACK
                observe <ID> - observe a game
                logout - for when you get tired of chess nerds
                help - list possible commands
                """;
    }
    
    
    private String listGamesString(ListGamesResult gameList) {
        return "implement";
    }

}
