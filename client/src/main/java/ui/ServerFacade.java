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
//    public AuthData register(UserData user) throws Exception{
//        HttpRequest httpRequest =
//                HttpRequest.newBuilder().uri(URI.create(Url + "/session")).POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(user))).header("Content-Type", "application/json").build();
//
//        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
//
//        if (httpResponse.statusCode() == 200){
//            return new Gson().fromJson(httpResponse.body(), AuthData.class);
//        } else {
//            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
//        }
//    }
}
