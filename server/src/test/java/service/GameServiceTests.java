package service;

import dataaccess.*;
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
    private final AuthData testUser1AuthData;
    private final AuthData testUser2AuthData;
    private final AuthData testUser3AuthData;
    private final AuthData testUser4AuthData;
    private final AuthData testUser5AuthData;


    private final CommonServices commonServices;
    private final UserService userService;
    private final GameService gameService;



    public GameServiceTests() throws ServiceException, DataAccessException {
        AuthAccess authAccess = new AuthDataAccess();
        GameAccess gameAccess = new GameDataAccess();
        UserAccess userAccess = new UserDataAccess();
        this.commonServices = new CommonServices(authAccess, gameAccess, userAccess);
        this.userService = new UserService(authAccess, userAccess, commonServices);
        this.gameService = new GameService(commonServices, gameAccess, authAccess);
        commonServices.deleteAllData();
        testUser1AuthData = userService.register(new UserData("Joe", "test1", "Joe.Email@google.com"));
        testUser2AuthData = userService.register(new UserData("Jonny", "test2", "Jonny.Email@google.com"));
        testUser3AuthData = userService.register(new UserData("Josh", "test3", "Josh.Email@google.com"));
        testUser4AuthData = userService.register(new UserData("new_player", "test4", "new_player.Email@google.com"));
        testUser5AuthData = userService.register(new UserData("new_player2", "test5", "new_player2.Email@google.com"));
    }

    public void createTestGames() throws ServiceException, DataAccessException {
        commonServices.deleteGameData();
        gameService.changeNextGameNumber(1001);
        gameService.createGame(new CreateGameRequest(testUser1AuthData.authToken(), "Test_Game1"));
        gameService.createGame(new CreateGameRequest(testUser2AuthData.authToken(), "Test_Game2"));
        gameService.createGame(new CreateGameRequest(testUser3AuthData.authToken(), "Test_Game3"));
    }

    private void player4JoinGame() throws ServiceException, DataAccessException{
        JoinGameRequest joinRequest = new JoinGameRequest(testUser4AuthData.authToken(), "WHITE", 1001);
        gameService.joinGame(joinRequest);
    }


    @Test
    @DisplayName("create new game")
    public void createGameSuccess() throws DataAccessException {
        commonServices.deleteGameData();
        Assertions.assertDoesNotThrow(() -> {
            CreateGameRequest newGameRequest = new CreateGameRequest(testUser1AuthData.authToken(), "Test_Game1");
            gameService.createGame(newGameRequest);
        });
    }

    @Test
    @DisplayName("user didn't enter a name")
    public void createGameBadName() throws DataAccessException {
        commonServices.deleteGameData();
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            CreateGameRequest newGameRequest = new CreateGameRequest(testUser1AuthData.authToken(), null);
            gameService.createGame(newGameRequest);
        });
        Assertions.assertEquals("400", exception.getMessage());
    }

    @Test
    @DisplayName("user had bad authToken when creating game")
    public void createGameBadAuth() throws DataAccessException {
        commonServices.deleteGameData();
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            CreateGameRequest newGameRequest = new CreateGameRequest("Bad AuthToken", "Test_Game1");
            gameService.createGame(newGameRequest);
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
            JoinGameRequest joinRequest = new JoinGameRequest("Bad AuthToken", "WHITE", 1001);
            gameService.joinGame(joinRequest);
        });
        Assertions.assertEquals("401", exception.getMessage());
    }

    @Test
    @DisplayName("join a game with bad gameID")
    public void joinGameBadGameID() throws DataAccessException {
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            createTestGames();
            JoinGameRequest joinRequest = new JoinGameRequest(testUser4AuthData.authToken(), "WHITE", 27);
            gameService.joinGame(joinRequest);
        });
        Assertions.assertEquals("400", exception.getMessage());
    }

    @Test
    @DisplayName("join a game with invalid playerColor")
    public void joinGameBadPlayerColor() throws DataAccessException {
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            createTestGames();
            JoinGameRequest joinRequest = new JoinGameRequest(testUser4AuthData.authToken(), "notWhite", 1001);
            gameService.joinGame(joinRequest);
        });
        Assertions.assertEquals("400", exception.getMessage());
    }

    @Test
    @DisplayName("join a game that has been taken")
    public void joinGamePlaceTaken() throws DataAccessException {
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            createTestGames();
            player4JoinGame();
            JoinGameRequest joinRequest = new JoinGameRequest(testUser5AuthData.authToken(), "WHITE", 1001);
            gameService.joinGame(joinRequest);
        });
        Assertions.assertEquals("403", exception.getMessage());
    }

    @Test
    @DisplayName("List games")
    public void listGamesSuccess() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> {
            createTestGames();
            ListGamesResult activeGames = gameService.listGames(testUser4AuthData.authToken());
            Assertions.assertEquals(3, activeGames.games().size());
            for (GameData game: activeGames.games()){
                Assertions.assertNull(game.whiteUsername());
                Assertions.assertNull(game.blackUsername());
                Assertions.assertNotNull(game.gameName());
            }
        });
    }

    @Test
    @DisplayName("user had bad authToken when listing games")
    public void listGameBadAuth() throws DataAccessException {
        commonServices.deleteGameData();
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            createTestGames();
            gameService.listGames("bad AuthToken");
        });
        Assertions.assertEquals("401", exception.getMessage());
    }
}
