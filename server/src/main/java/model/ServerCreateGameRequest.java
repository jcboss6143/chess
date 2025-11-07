package model;

public record ServerCreateGameRequest(String authToken, String gameName) {
}
