package ui.terminalstates;

import ui.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;

import ui.BadResponseExeption;
import ui.ServerFacade;
import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.Gson;
import model.CreateGameResult;
import model.*;
import java.util.*;

import static ui.EscapeSequences.*;

public class InGameState extends State {
    ChessGame game;
    boolean invert;

    public InGameState(String userName, String token, ServerFacade serverFacade, ChessGame game, boolean invert) {
        super(serverFacade);
        displayName = userName;
        serverFacade.updateAuthToken(token);
        this.game = game;
        this.invert = invert;
        System.out.print(showBoard());
    }

    @Override
    String evaluateCommand(String cmd, String[] params) throws URISyntaxException, IOException, InterruptedException {
        return switch (cmd) {
//            case "create" -> create(params);
//            case "list" -> list();
//            case "join" -> join(params);
//            case "observe" -> observe(params);
//            case "logout" -> logout();
            case "help" -> help();
            default -> "INVALID COMMAND: Use 'help' command to see a list of valid commands";
        };
    }

    private String help() {
        return """
                Redraw - create a new game
                leave - leave the game
                Move <startXY> <endXY> - make a move
                Resign - surrender
                Highlight <XY> - highlight piece at this position
                help - list possible commands
                """;
    }


    private String showBoard() {
        invert = !invert; // accidentally built it inverted, so I had to invert the invert lol
        StringBuilder returnString = new StringBuilder();
        char[] letters = new char[]{'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
        char[] numbers = new char[]{'1', '2', '3', '4', '5', '6', '7', '8'};
        if (invert) {
            letters = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
            numbers = new char[]{'8', '7', '6', '5', '4', '3', '2', '1'};
        }
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                updateBoardCell(x, y, returnString, letters, numbers);
            }
            returnString.append("\n");
        }
        returnString.append(RESET_BG_COLOR);
        return returnString.toString();
    }


    private void updateBoardCell(int x, int y, StringBuilder returnString, char[] letters, char[] numbers) {
        if (y == 0 || x == 0 || y == 9 || x == 9) {
            returnString.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE);
            if ((y == 0 || y == 9) && (x != 0 && x != 9)) { returnString.append(" \u2009"+letters[x-1]+"\u2009 "); }
            else if ((x == 0 || x == 9) && (y != 0 && y != 9)) { returnString.append(" \u2009"+numbers[y-1]+"\u2009 "); }
            else { returnString.append(EMPTY); }
            returnString.append(RESET_BG_COLOR);
        }
        else {
            returnString.append(SET_TEXT_BOLD);
            if ((x+y)%2==0) { returnString.append(SET_BG_COLOR_WHITE); }
            else { returnString.append(SET_BG_COLOR_BLACK); }
            ChessPiece piece = game.getPiece(y, 9-x);
            if (invert) { piece = game.getPiece(9-y, x); }
            if (piece != null) {
                ChessGame.TeamColor color = piece.getTeamColor();
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) { returnString.append(SET_TEXT_COLOR_BLUE); }
                else { returnString.append(SET_TEXT_COLOR_RED); }
                ChessPiece.PieceType type = piece.getPieceType();
                switch (type) {
                    case BISHOP -> { returnString.append(BLACK_BISHOP); }
                    case ROOK -> { returnString.append(BLACK_ROOK); }
                    case QUEEN -> { returnString.append(BLACK_QUEEN); }
                    case KNIGHT -> { returnString.append(BLACK_KNIGHT); }
                    case KING -> { returnString.append(BLACK_KING); }
                    case PAWN -> { returnString.append(BLACK_PAWN); }
                    case null, default -> { returnString.append(EMPTY); }
                }
            }
            else { returnString.append(EMPTY); }
            returnString.append(RESET_TEXT_BOLD_FAINT);
        }
    }
}
