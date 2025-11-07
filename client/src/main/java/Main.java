import chess.*;
import ui.ServerFacade;
import ui.terminalStates.PreLoginState;


public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        PreLoginState clientInterface = new PreLoginState(new ServerFacade("localHost", 8080));
        clientInterface.mainLoop();

    }
}