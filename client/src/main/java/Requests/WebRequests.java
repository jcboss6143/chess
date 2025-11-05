package Requests;

import com.google.gson.Gson;
import model.*;
import model.ErrorMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class WebRequests {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final String serverURL;
    private final int port;

    public WebRequests(String url, int webPort) {
        serverURL = url;
        port = webPort;
    }

    public String makeRequest(String method, String path, Object body) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = makeHttpRequest(method, path, body);
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            return httpResponse.body();
        } else {
            ErrorMessage message = new Gson().fromJson(httpResponse.body(), ErrorMessage.class);
            throw new BadResponseExeption(message.message());
        }
    }

    public HttpRequest makeHttpRequest(String method, String path, Object body) throws URISyntaxException {
        String targetURL = String.format(Locale.getDefault(), "http://%s:%d%s", serverURL, port, path);
        var request = HttpRequest.newBuilder().uri(new URI(targetURL))
                .timeout(java.time.Duration.ofMillis(10000)); // will timeout after 10 seconds
        if (body != null) {
            request.method(method, HttpRequest.BodyPublishers.ofString(new Gson().toJson(body)))
                    .setHeader("Content-Type", "application/json");
        } else {
            request.method(method, HttpRequest.BodyPublishers.noBody());
        }
        return request.build();
    }





}
