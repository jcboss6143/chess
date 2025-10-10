package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.model.CreateGameRequest;
import service.model.ListGamesResult;
import service.model.LoginRequest;

public class GameServiceTests {
    private AuthData testUser1AuthData = UserService.register(new UserData("Joe", "test1", "Joe.Email@google.com"));
    private AuthData testUser2AuthData = UserService.register(new UserData("Jonny", "test2", "Jonny.Email@google.com"));
    private AuthData testUser3AuthData = UserService.register(new UserData("Josh", "test3", "Josh.Email@google.com"));
    private AuthData testUser4AuthData = UserService.register(new UserData("new_player", "test4", "new_player.Email@google.com"));


    public GameServiceTests() throws ServiceException, DataAccessException {
    }

    public void createTestGames() throws ServiceException, DataAccessException {
        DatabaseService.deleteGameData();
        GameService.createGame(new CreateGameRequest(testUser1AuthData.authToken(), "Test_Game1"));
        GameService.createGame(new CreateGameRequest(testUser2AuthData.authToken(), "Test_Game2"));
        GameService.createGame(new CreateGameRequest(testUser3AuthData.authToken(), "Test_Game3"));
    }


    @Test
    @DisplayName("create new game")
    public void createGameSuccess() throws DataAccessException {
        DatabaseService.deleteGameData();
        Assertions.assertDoesNotThrow(() -> {
            CreateGameRequest newGameRequest = new CreateGameRequest(testUser1AuthData.authToken(), "Test_Game1");
            GameService.createGame(newGameRequest);
        });
    }

    @Test
    @DisplayName("user didn't enter a name")
    public void createGameBadName() throws DataAccessException {
        DatabaseService.deleteGameData();
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            CreateGameRequest newGameRequest = new CreateGameRequest(testUser1AuthData.authToken(), null);
            GameService.createGame(newGameRequest);
        });
        Assertions.assertEquals("400", exception.getMessage());
    }

    @Test
    @DisplayName("user had bad authToken when creating game")
    public void createGameBadAuth() throws DataAccessException {
        DatabaseService.deleteGameData();
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            CreateGameRequest newGameRequest = new CreateGameRequest("Bad AuthToken", "Test_Game1");
            GameService.createGame(newGameRequest);
        });
        Assertions.assertEquals("401", exception.getMessage());
    }


    @Test
    @DisplayName("list games")
    public void joinGameSuccess() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> {
            GameService.changeNextGameNumber(1);
            createTestGames();
            ListGamesResult activeGames = GameService.listGames(testUser4AuthData.authToken());
            Assertions.assertEquals(3, activeGames.games().length);
            for (GameData game: activeGames.games()){
                Assertions.assertInstanceOf(int.class, game.gameID());
                Assertions.assertNull(game.whiteUsername());
                Assertions.assertNull(game.blackUsername());
                Assertions.assertNotNull(game.gameName());
            }
        });
    }







    @Test
    @DisplayName("list games")
    public void listGameSuccess() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> {
            createTestGames();
            ListGamesResult activeGames = GameService.listGames(testUser4AuthData.authToken());
            Assertions.assertEquals(3, activeGames.games().length);
            for (GameData game: activeGames.games()){
                Assertions.assertInstanceOf(int.class, game.gameID());
                Assertions.assertNull(game.whiteUsername());
                Assertions.assertNull(game.blackUsername());
                Assertions.assertNotNull(game.gameName());
            }
        });
    }


    @Test
    @DisplayName("user had bad authToken when listing games")
    public void listGameBadAuth() throws DataAccessException {
        DatabaseService.deleteGameData();
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            createTestGames();
            GameService.listGames("bad AuthToken");
        });
        Assertions.assertEquals("401", exception.getMessage());
    }
}
