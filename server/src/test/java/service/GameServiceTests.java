package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.model.CreateGameRequest;
import service.model.JoinGameRequest;
import service.model.ListGamesResult;


public class GameServiceTests {
    private AuthData testUser1AuthData = UserService.register(new UserData("Joe", "test1", "Joe.Email@google.com"));
    private AuthData testUser2AuthData = UserService.register(new UserData("Jonny", "test2", "Jonny.Email@google.com"));
    private AuthData testUser3AuthData = UserService.register(new UserData("Josh", "test3", "Josh.Email@google.com"));
    private AuthData testUser4AuthData = UserService.register(new UserData("new_player", "test4", "new_player.Email@google.com"));
    private AuthData testUser5AuthData = UserService.register(new UserData("new_player2", "test5", "new_player2.Email@google.com"));


    public GameServiceTests() throws ServiceException, DataAccessException {
    }

    public void createTestGames() throws ServiceException, DataAccessException {
        DatabaseService.deleteGameData();
        GameService.createGame(new CreateGameRequest(testUser1AuthData.authToken(), "Test_Game1"));
        GameService.createGame(new CreateGameRequest(testUser2AuthData.authToken(), "Test_Game2"));
        GameService.createGame(new CreateGameRequest(testUser3AuthData.authToken(), "Test_Game3"));
        GameService.changeNextGameNumber(1);
    }


    private void player4JoinGame() throws ServiceException, DataAccessException{
        JoinGameRequest joinRequest = new JoinGameRequest(testUser4AuthData.authToken(), "WHITE", 1);
        GameService.joinGame(joinRequest);
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
    @DisplayName("join a game")
    public void joinGameSuccess() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> {
            createTestGames();
            player4JoinGame();
        });
    }

    @Test
    @DisplayName("join a game with bad AuthToken")
    public void joinGameBadAuth() throws DataAccessException {
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            createTestGames();
            JoinGameRequest joinRequest = new JoinGameRequest("Bad AuthToken", "WHITE", 1);
            GameService.joinGame(joinRequest);
        });
        Assertions.assertEquals("401", exception.getMessage());
    }

    @Test
    @DisplayName("join a game with bad gameID")
    public void joinGameBadGameID() throws DataAccessException {
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            createTestGames();
            JoinGameRequest joinRequest = new JoinGameRequest(testUser4AuthData.authToken(), "WHITE", 27);
            GameService.joinGame(joinRequest);
        });
        Assertions.assertEquals("400", exception.getMessage());
    }

    @Test
    @DisplayName("join a game with invalid playerColor")
    public void joinGameBadPlayerColor() throws DataAccessException {
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            createTestGames();
            JoinGameRequest joinRequest = new JoinGameRequest(testUser4AuthData.authToken(), "notWhite", 1);
            GameService.joinGame(joinRequest);
        });
        Assertions.assertEquals("400", exception.getMessage());
    }


    @Test
    @DisplayName("join a game that has been taken")
    public void joinGamePlaceTaken() throws DataAccessException {
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            createTestGames();
            player4JoinGame();
            JoinGameRequest joinRequest = new JoinGameRequest(testUser5AuthData.authToken(), "WHITE", 1);
            GameService.joinGame(joinRequest);
        });
        Assertions.assertEquals("403", exception.getMessage());
    }


    @Test
    @DisplayName("List games")
    public void listGamesSuccess() throws DataAccessException {
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
    @DisplayName("list games where players have already joined some")
    public void listGamesAlreadyJoinedSuccess() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> {
            createTestGames();
            player4JoinGame();
            ListGamesResult activeGames = GameService.listGames(testUser5AuthData.authToken());
            Assertions.assertEquals(3, activeGames.games().length);
            for (int i = 0; i < activeGames.games().length; i++){
                Assertions.assertInstanceOf(int.class, activeGames.games()[i].gameID());
                if (i == 0) {
                    Assertions.assertEquals("WHITE", activeGames.games()[i].whiteUsername());
                }
                else {
                    Assertions.assertNull(activeGames.games()[i].whiteUsername());
                }
                Assertions.assertNull(activeGames.games()[i].blackUsername());
                Assertions.assertNotNull(activeGames.games()[i].gameName());
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
