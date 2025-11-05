import Requests.BadResponseExeption;
import Requests.WebRequests;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.CreateGameResult;
import model.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static ui.EscapeSequences.*;



public class PostLoginState extends UniversalState {
    private Map<Integer, GameData> gameMap = new HashMap<>();

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
        GameData requestedGame = getCachedGameData(params[0]);
        JoinGameRequest requestObject = new JoinGameRequest(params[1], requestedGame.gameID());
        String result = requestHandler.makeRequest("PUT", "/game", requestObject);
        return showBoard(requestedGame.game(), Objects.equals(requestedGame.blackUsername(), displayName)); // do more silly
    }


    private String observe(String[] params) {
        if (params.length != 1) { throw new BadResponseExeption("INVALID NUMBER OF PARAMETERS: use the 'help' command to view command syntax"); }
        GameData requestedGame = getCachedGameData(params[0]);
        return showBoard(requestedGame.game(), Objects.equals(requestedGame.blackUsername(), displayName));
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


    private String showBoard(ChessGame game, boolean invert) {
        StringBuilder returnString = new StringBuilder();
        char[] letters = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        char[] numbers = new char[]{'8', '7', '6', '5', '4', '3', '2', '1'};
        if (invert) {
            letters = new char[]{'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
            numbers = new char[]{'1', '2', '3', '4', '5', '6', '7', '8'};
        }
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (y == 0 || x == 0 || y == 9 || x == 9) {
                    returnString.append(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLACK);
                    if ((y == 0 || y == 9) && (x != 0 && x != 9)) { returnString.append(" "+letters[x-1]+" "); }
                    else if ((x == 0 || x == 9) && (y != 0 && y != 9)) { returnString.append(" "+numbers[y-1]+" "); }
                    else { returnString.append("   "); }
                }
                else {
                    if (Math.floorDiv((x+y)/2, 2)==0) { returnString.append(SET_BG_COLOR_WHITE); }
                    else { returnString.append(SET_BG_COLOR_BLACK); }
                    ChessPiece piece = game.getPiece(x, y);
                    if (invert) { piece = game.getPiece(9-x, 9-y); }
                    if (piece != null) {
                        ChessGame.TeamColor color = piece.getTeamColor();
                        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) { returnString.append(SET_TEXT_COLOR_RED); }
                        else { returnString.append(SET_TEXT_COLOR_BLUE); }
                        ChessPiece.PieceType type = piece.getPieceType();
                        switch (type) {
                            case BISHOP -> { returnString.append(BLACK_BISHOP); }
                            case ROOK -> { returnString.append(BLACK_ROOK); }
                            case QUEEN -> { returnString.append(BLACK_QUEEN); }
                            case KNIGHT -> { returnString.append(BLACK_KNIGHT); }
                            case KING -> { returnString.append(BLACK_KING); }
                            case PAWN -> { returnString.append(BLACK_PAWN); }
                            case null, default -> { returnString.append("   "); }
                        }
                    }
                    else { returnString.append("   "); }
                }
            }
            returnString.append("\n");
        }
        return returnString.toString();
    }

}
