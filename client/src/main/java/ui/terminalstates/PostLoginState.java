package ui.terminalstates;

import ui.BadResponseExeption;
import ui.ServerFacade;
import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.Gson;
import model.CreateGameResult;
import model.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static ui.EscapeSequences.*;



public class PostLoginState extends State {
    private Map<Integer, GameData> gameMap = new HashMap<>();
    String authToken;

    public PostLoginState(String userName, String token, ServerFacade serverFacade) {
        super(serverFacade);
        displayName = userName;
        serverFacade.updateAuthToken(token);
        authToken = token;
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
        if (params.length != 1) { return SET_TEXT_COLOR_YELLOW + "INVALID NUMBER OF PARAMETERS: use the 'help' command to view command syntax"; }
        CreateGameRequest requestObject = new CreateGameRequest(params[0]);
        String result = requestHandler.makeRequest("POST", "/game", requestObject);
        CreateGameResult resultObject = new Gson().fromJson(result, CreateGameResult.class);
        return "Game creation was successful!";
    }


    private String list() throws URISyntaxException, IOException, InterruptedException {
        String result = requestHandler.makeRequest("GET", "/game", null);
        ListGamesResult resultObject = new Gson().fromJson(result, ListGamesResult.class);
        return listGamesString(resultObject);
    }


    private String join(String[] params) throws URISyntaxException, IOException, InterruptedException {
        if (params.length != 2) { return SET_TEXT_COLOR_YELLOW + "INVALID NUMBER OF PARAMETERS: use the 'help' command to view command syntax"; }
        params[1] = params[1].trim().toUpperCase();
        if (Objects.equals(params[1], "WHITE") || Objects.equals(params[1], "BLACK")) {
            try {
                GameData requestedGame;
                try { requestedGame = getCachedGameData(params[0]); }
                catch (BadResponseExeption e) { return SET_TEXT_COLOR_YELLOW + e.getMessage(); }
                JoinGameRequest requestObject = new JoinGameRequest(params[1], requestedGame.gameID());
                String result = requestHandler.makeRequest("PUT", "/game", requestObject);
                InGameState gameState = new InGameState(displayName, authToken, requestHandler, requestedGame, Objects.equals(params[1], "BLACK"));
                gameState.mainLoop();
                return "Exited Successfully";
            } catch (BadResponseExeption e) {
                if (e.getMessage().startsWith("403")) { return SET_TEXT_COLOR_YELLOW + "That seat has already been filled. Please try again"; }
                else { throw new BadResponseExeption(e.getMessage()); }
            }
        }
        return SET_TEXT_COLOR_YELLOW + "INVALID PLAYER COLOR: Second parameter must be either WHITE or BLACK";
    }


    private String observe(String[] params) {
        if (params.length != 1) { return SET_TEXT_COLOR_YELLOW + "INVALID NUMBER OF PARAMETERS: use the 'help' command to view command syntax"; }
        GameData requestedGame;
        try { requestedGame = getCachedGameData(params[0]); }
        catch (BadResponseExeption e) { return SET_TEXT_COLOR_YELLOW + e.getMessage(); }
        InGameState gameState = new InGameState(displayName, authToken, requestHandler,
                requestedGame, Objects.equals(displayName, requestedGame.blackUsername()));
        gameState.mainLoop();
        return "Exited Successfully";
    }


    private String logout() throws URISyntaxException, IOException, InterruptedException {
        String result = requestHandler.makeRequest("DELETE", "/session", null);
        requestHandler.clearAuthToken();
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


    private GameData getCachedGameData(String number) {
        GameData requestedGame;
        try {
            requestedGame = gameMap.get(Integer.parseInt(number));
            if (requestedGame == null) { throw new BadResponseExeption("INVALID GAME ID: use the 'list' command to see valid game id's"); }
        }
        catch (Exception e) { throw new BadResponseExeption("INVALID GAME ID: use the 'list' command to see valid game id's"); }
        return  requestedGame;
    }
    
    
    private String listGamesString(ListGamesResult gameList) {
        gameMap = new HashMap<>();
        StringBuilder returnString = new StringBuilder();
        int i = 1;
        for (GameData gameData: gameList.games()) {
            returnString.append(Integer.toString(i) + " - Name: " + gameData.gameName());
            if (gameData.whiteUsername() != null) { returnString.append(", White: " + gameData.whiteUsername()); }
            if (gameData.blackUsername() != null) { returnString.append(", Black: " + gameData.blackUsername()); }
            returnString.append("\n");
            gameMap.put(i, gameData);
            i += 1;
        }
        return returnString.toString();
    }

}
