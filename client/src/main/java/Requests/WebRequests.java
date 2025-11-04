package Requests;

import com.google.gson.Gson;

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

    public WebRequests(String url, int webPort) throws Exception {
        serverURL = url;
        port = webPort;
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

    private HttpResponse<String> sendHttpRequest(HttpRequest request) throws BadResponseExeption {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new BadResponseExeption(ex.getMessage());
        }
    }


}
