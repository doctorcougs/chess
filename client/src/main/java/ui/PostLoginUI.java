package ui;

import model.*;

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
                case "list-games" -> listGames(authData, serverFacade);
                case "play-game" -> playGame(authData, serverFacade);
                case "observe-game" -> observeGame(authData, serverFacade);
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
    public static void listGames(AuthData authData, ServerFacade serverFacade) throws Exception{
        try {
            var games = serverFacade.listGames(authData.authToken());
            int gameIndex = 0;
            if (games != null) {
                System.out.println("List of games:");
                for (GameData game : games.games()) {
                    System.out.println("Game ID: " + gameIndex);
                    System.out.println("Game Name: " + game.gameName());
                    System.out.println("White Player: " + game.whiteUsername());
                    System.out.println("Black Player: " + game.blackUsername());
                    System.out.println(" ");
                    gameIndex++;
                }
            } else {
                System.out.println("No games found");
            }
        }
        catch (Exception e) {
            System.out.println("Error listing games");
        }
    }
    public static void playGame(AuthData authData, ServerFacade serverFacade) {}
    public static void observeGame(AuthData authData, ServerFacade serverFacade) {}
}
