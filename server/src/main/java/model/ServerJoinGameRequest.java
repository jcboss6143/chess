package model;

public record ServerJoinGameRequest(String authToken, String playerColor, int gameID) {
}
