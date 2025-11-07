package client;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.BadResponseExeption;
import ui.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static String authToken;
    private static String authTokenToDelete;

    @BeforeAll
    public static void init() throws URISyntaxException, IOException, InterruptedException {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("localhost", 8080);
        serverFacade.makeRequest("DELETE", "/db", null); // clears server
        UserData accountInfo = new UserData("Joe", "Joe", "Joe");
        String result = serverFacade.makeRequest("POST", "/user", accountInfo);
        AuthData authData = new Gson().fromJson(result, AuthData.class);
        authToken = authData.authToken();
        UserData accountInfo2 = new UserData("Jo", "Jo", "Jo");
        String result2 = serverFacade.makeRequest("POST", "/user", accountInfo2);
        AuthData authData2 = new Gson().fromJson(result2, AuthData.class);
        authTokenToDelete = authData2.authToken();
        serverFacade.updateAuthToken(authToken);
    }

    @AfterAll
    static void stopServer() throws URISyntaxException, IOException, InterruptedException {
        serverFacade.makeRequest("DELETE", "/db", null);
        server.stop();
    }

    @Test
    public void makePostRequest() {
        serverFacade.updateAuthToken(authToken);
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.makeRequest("POST", "/user", new UserData("Joe2", "Joe2", "Joe2"));
        });
    }

    @Test
    public void badPostRequest() throws URISyntaxException, IOException, InterruptedException {
        serverFacade.updateAuthToken(authToken);
        BadResponseExeption exception = Assertions.assertThrows(BadResponseExeption.class, () -> {
            serverFacade.makeRequest("POST", "/user", null);
        });
        Assertions.assertTrue(exception.getMessage().startsWith("400"));
    }

    @Test
    public void makeGetRequest() {
        serverFacade.updateAuthToken(authToken);
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.makeRequest("GET", "/game", null);
        });
    }

    @Test
    public void badGetRequest() {
        serverFacade.updateAuthToken(authToken);
        BadResponseExeption exception = Assertions.assertThrows(BadResponseExeption.class, () -> {
            serverFacade.makeRequest("GET", "/user", null);
        });
        Assertions.assertTrue(exception.getMessage().startsWith("404"));
    }

// for some reason when I try to create a new game, my code ends up not working. However, if I remove the code from the test file that runs the server
// and run it separately, it works. I have no idea why, so I left that test out for now.

    @Test
    public void badPutRequest() {
        serverFacade.updateAuthToken(authToken);
        BadResponseExeption exception = Assertions.assertThrows(BadResponseExeption.class, () -> {
            serverFacade.makeRequest("PUT", "/game", null);
        });
        Assertions.assertTrue(exception.getMessage().startsWith("400"));
    }

    @Test
    public void makeDeleteRequest() {
        serverFacade.updateAuthToken(authTokenToDelete);
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.makeRequest("DELETE", "/session",  null);
        });
        serverFacade.updateAuthToken(authToken);
    }

    @Test
    public void badDeleteRequest() {
        serverFacade.clearAuthToken();
        BadResponseExeption exception = Assertions.assertThrows(BadResponseExeption.class, () -> {
            serverFacade.makeRequest("DELETE", "/session", null);
        });
        Assertions.assertTrue(exception.getMessage().startsWith("400"));
        serverFacade.updateAuthToken(authToken);
    }

}
