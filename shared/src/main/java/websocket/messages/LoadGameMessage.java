package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    GameData game;
    public LoadGameMessage(ServerMessageType type, GameData gameData) {
        super(type);
        game = gameData;
    }

    public GameData getGameData() { return this.game; }


}
