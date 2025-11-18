package ui.terminalstates;

import chess.ChessMove;
import chess.ChessPosition;
import ui.*;

import java.io.IOException;
import java.net.URISyntaxException;

import ui.ServerFacade;
import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.Gson;
import model.CreateGameResult;
import model.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.*;

import static ui.EscapeSequences.*;

public class InGameState extends State implements ServerMessageHandler {
    ChessGame game;
    boolean invert;
    private final WebsocketFacade ws;

    public InGameState(String userName, String token, ServerFacade serverFacade, ChessGame game, boolean invert) {
        super(serverFacade);
        displayName = userName;
        serverFacade.updateAuthToken(token);
        this.game = game;
        this.invert = !invert; // accidentally built it inverted, so I had to invert the invert lol
        ws = new WebsocketFacade(serverFacade.getServerURL(), this);

        System.out.print(showBoard(null));
    }

    @Override
    String evaluateCommand(String cmd, String[] params) throws URISyntaxException, IOException, InterruptedException {
        return switch (cmd) {
            case "redraw" -> showBoard(null);
            case "leave" -> leave();
            case "move" -> makeMove(params);
            case "resign" -> resign();
            case "highlight" -> highlight(params);
            case "help" -> help();
            default -> "INVALID COMMAND: Use 'help' command to see a list of valid commands";
        };
    }

    private String makeMove(String[] params) {
        if (params.length != 2) { return SET_TEXT_COLOR_YELLOW + "INVALID NUMBER OF PARAMETERS: use the 'help' command to view command syntax"; }
        ChessPosition startPosition = translateLetterNumber(params[0]);
        ChessPosition endPosition = translateLetterNumber(params[1]);
        if (startPosition == null || endPosition == null) {  return SET_TEXT_COLOR_YELLOW + "InvalidPosition. Please try again"; }
        return "implement";
    }

    private String resign() {
        return "implement";
    }

    private String leave() {
        continueLoop = false;
        return "Leaving Game...";
    }

    private String highlight(String[] params) {
        if (params.length != 1) { return SET_TEXT_COLOR_YELLOW + "INVALID NUMBER OF PARAMETERS: use the 'help' command to view command syntax"; }
        ChessPosition position = translateLetterNumber(params[0]);
        if (position == null) {  return SET_TEXT_COLOR_YELLOW + "InvalidPosition. Please try again"; }
        return showBoard(position);
    }

    private String help() {
        return """
                redraw - create a new game
                leave - leave the game
                move <StartLetterNumber> <EndLetterNumber> - make a move
                resign - surrender
                highlight <LetterNumber> - highlight piece at this position
                help - list possible commands
                """;
    }


    private ChessPosition translateLetterNumber(String letterNumber) {
        char letter = letterNumber.charAt(0); // corresponds with y
        char number = letterNumber.charAt(1); // corresponds with x
        int x;
        int y = switch (letter) {
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default -> 0;
        };
        x = switch (number) {
            case '1' -> 1;
            case '2' -> 2;
            case '3' -> 3;
            case '4' -> 4;
            case '5' -> 5;
            case '6' -> 6;
            case '7' -> 7;
            case '8' -> 8;
            default -> 0;
        };
        if (x == 0 || y == 0) {
            return null;
        }
        return new ChessPosition(x, y);
    }


    private String showBoard(ChessPosition position) {
        // get all possible moves that the piece can move to
        Collection<ChessPosition> positions = null;
        if (position != null) {
            Collection<ChessMove> moves = game.validMoves(position);
            if (moves != null) {
                positions = new HashSet<>();
                for (ChessMove move : moves) {
                    positions.add(move.getEndPosition());
                }
            }
        }

        char[] letters = new char[]{'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
        char[] numbers = new char[]{'1', '2', '3', '4', '5', '6', '7', '8'};
        if (invert) {
            letters = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
            numbers = new char[]{'8', '7', '6', '5', '4', '3', '2', '1'};
        }

        StringBuilder returnString = new StringBuilder();
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                updateBoardCell(x, y, returnString, letters, numbers, positions, position);
            }
            returnString.append("\n");
        }
        returnString.append(RESET_BG_COLOR);
        return returnString.toString();
    }


    private void updateBoardCell(int x, int y, StringBuilder returnString, char[] letters, char[] numbers, Collection<ChessPosition> positions, ChessPosition position) {
        if (y == 0 || x == 0 || y == 9 || x == 9) {
            returnString.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE);
            if ((y == 0 || y == 9) && (x != 0 && x != 9)) { returnString.append(" \u2009"+letters[x-1]+"\u2009 "); }
            else if ((x == 0 || x == 9) && (y != 0 && y != 9)) { returnString.append(" \u2009"+numbers[y-1]+"\u2009 "); }
            else { returnString.append(EMPTY); }
            returnString.append(RESET_BG_COLOR);
        }
        else {

            // setting background color
            returnString.append(SET_TEXT_BOLD);
            boolean blackSquare;
            if ((x+y)%2==0) { returnString.append(SET_BG_COLOR_WHITE); blackSquare = false;}
            else { returnString.append(SET_BG_COLOR_BLACK); blackSquare = true;}

            int boardY = y;
            int boardX = 9 - x;

            if (invert) {
                boardY = 9 - y;
                boardX = x;
            }

            ChessPosition currentCell = new ChessPosition(boardY, boardX);
            ChessPiece piece = game.getPiece(boardY, boardX);

            if (positions != null) {
                if (positions.contains(currentCell)) { // if the piece can move to this position
                    if (blackSquare) { returnString.append(SET_BG_COLOR_DARK_GREEN); }
                    else { returnString.append(SET_BG_COLOR_GREEN); }
                }
            }
            if (position != null && position.equals(currentCell)) {
                returnString.append(SET_BG_COLOR_YELLOW);
            }


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

    @Override
    public void notifyLoadGame(LoadGameMessage message) {

    }

    @Override
    public void notifyNotification(NotificationMessage message) {

    }

    @Override
    public void notifyError(ErrorMessage message) {

    }
}
