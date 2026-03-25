package ui;

import model.*;
import org.glassfish.grizzly.utils.EchoFilter;

import java.util.Scanner;


public class PostLoginUI {
    private static Scanner scanner = new Scanner(System.in);

    public static void run(AuthData authData, ServerFacade serverFacade) throws Exception{
        System.out.println("Welcome " + authData.username() + "!");
        System.out.println("Type help to get started.");
        while (true) {
            String input = scanner.nextLine();
            switch(input) {
                case "help" -> {
                    System.out.println("help");
                    System.out.println("logout");
                    System.out.println("create-game");
                    System.out.println("list-games");
                    System.out.println("play-game");
                    System.out.println("observe-game");

                }
                case "logout" -> PreLoginUI.run();
                case "create-game" -> createGame(authData, serverFacade);
                case "list-games" -> listGames();
                case "play-game" -> playGame();
                case "observe-game" -> observeGame();
                default -> System.out.println("Invalid command, valid commands are: help, logout, create-game, list-games, play-game, observe-game");
            }
        }
    }
    public static void createGame(AuthData authData, ServerFacade serverFacade) throws Exception {
        try {
        System.out.println("Name of game:");
        String gameName = scanner.nextLine();
        GameData gameData = new GameData(0, null, null, gameName, null);

        serverFacade.createGame(authData.authToken(), gameData);
        System.out.println("Game Created");
        } catch (Exception e) {
            System.out.println("Invalid game data, try again");
        }

    }
    public static void listGames() {}
    public static void playGame() {}
    public static void observeGame() {}
}
