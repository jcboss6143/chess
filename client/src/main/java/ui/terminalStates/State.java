package ui.terminalStates;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Scanner;

import ui.BadResponseExeption;
import ui.ServerFacade;

import static ui.EscapeSequences.*;



public abstract class State {
    String displayName;
    boolean continueLoop;
    ServerFacade requestHandler;

    public State(ServerFacade serverFacade) {
        requestHandler = serverFacade;
        displayName = "[LOGGED_OUT]";
        continueLoop = true;
    }

    public void mainLoop() {
        Scanner scanner = new Scanner(System.in);
        String result;
        while (continueLoop) {
            System.out.print("\n" + SET_TEXT_COLOR_WHITE + displayName + " >>> " + SET_TEXT_COLOR_LIGHT_GREY);
            try { result = RESET_TEXT_COLOR + tokenizeCommand(scanner.nextLine().trim()); }
            catch (BadResponseExeption e) {
                if (e.getMessage().startsWith("400")) { result =  SET_TEXT_COLOR_YELLOW + "ERROR: a bad request was made to the server, please try again."; }
                else { result = SET_TEXT_COLOR_RED + e.toString(); }
            }
            catch (ConnectException e) {  result = SET_TEXT_COLOR_YELLOW + "Unable to connect to the server. Please try again later.";}
            catch (Throwable e) { result = SET_TEXT_COLOR_RED + e.toString(); }
            System.out.print(result);
        }
        System.out.println();
    }

    private String tokenizeCommand(String input) throws URISyntaxException, IOException, InterruptedException {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return evaluateCommand(cmd, params);
    }

    abstract String evaluateCommand(String cmd, String[] params) throws URISyntaxException, IOException, InterruptedException;
}
