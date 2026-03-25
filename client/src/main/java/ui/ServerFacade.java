package ui;

import com.google.gson.Gson;
import model.*;
import java.net.URI;
import java.net.http.*;
import java.util.Map;

public class ServerFacade {
    private final String Url;


    public ServerFacade(String Url) {
        this.Url = Url;
    }

    public AuthData login(UserData user) throws Exception{
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(Url + "/session")).POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(user))).header("Content-Type", "application/json").build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200){
            return new Gson().fromJson(httpResponse.body(), AuthData.class);
        } else {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }

    public AuthData register(UserData user)  throws Exception{
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(Url + "/user")).POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(user))).header("Content-Type", "application/json").build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200){
            return new Gson().fromJson(httpResponse.body(), AuthData.class);
        } else {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }

    public void clear()  throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(Url + "/db")).method("DELETE", HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (httpResponse.statusCode() != 200) {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());

        }
    }

    public GameData createGame(String authToken, GameData game) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(Url + "/game")).POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(game))).header("Content-Type", "application/json").header("Authorization", authToken).build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200){
            return new Gson().fromJson(httpResponse.body(), GameData.class);
        } else {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(Url + "/game")).GET().header("Authorization", authToken).build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200) {
            return new Gson().fromJson(httpResponse.body(), ListGamesResult.class);
        } else {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }

    public void joinGame(String authToken, int gameId, String playerColor) throws Exception{
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(Url + "/game")).PUT(HttpRequest.BodyPublishers.ofString(new Gson().toJson(Map.of("playerColor", playerColor, "gameID", gameId)))).header("Content-Type", "application/json").header("Authorization", authToken).build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200){
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }
}
