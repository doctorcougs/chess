package ui;

import chess.ChessGame;
import model.*;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;


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
                case "play-game" -> playGame(authData, serverFacade, true);
                case "observe-game" -> playGame(authData, serverFacade, false);
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
            int gameIndex = 1;
            if (games != null && games.games().size() > 0) {
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
    public static void playGame(AuthData authData, ServerFacade serverFacade, boolean playing) throws Exception{
        try {
            System.out.println("Please enter the game ID you want to play:");
            var games = serverFacade.listGames(authData.authToken());
            Map<Integer, Integer> gameMap = new HashMap<>();
            int gameIndex = 1;
            if (games != null && games.games().size() > 0) {
                for (GameData game : games.games()) {
                    gameMap.put(gameIndex, game.gameID());
                    gameIndex++;
                }
            }

            System.out.println("Please enter the gameID");
            int selectedGame = Integer.parseInt(scanner.nextLine());
            int gameID = gameMap.get(selectedGame);

            if (playing) {
                System.out.println("Please enter your color, WHITE or BLACK");
                String color = scanner.nextLine().toUpperCase();
                serverFacade.joinGame(authData.authToken(), gameID, color);
            }
            serverFacade.joinGame(authData.authToken(), gameID, "observer");
            System.out.println("Game joined");
            ChessBoardBuilder chessBoard = new ChessBoardBuilder ();
//            chessBoard.printBoard();
        } catch (Exception e) {
            System.out.println("Error joining game, try again please.");
        }
    }
}
