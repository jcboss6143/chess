import chess.*;
import Requests.WebRequests;


public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        PreLoginState clientInterface = new PreLoginState(new WebRequests("localHost", 8080));
        clientInterface.mainLoop();

    }
}