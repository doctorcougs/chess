package ui;

import model.*;
import java.util.Scanner;

public class PostLoginUI {
    private static Scanner scanner = new Scanner(System.in);
    private static AuthData authData;

    public static void run(AuthData authData) {
        authData = authData;
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
                case "create-game" -> createGame();
                case "list-games" -> listGames();
                case
            }
        }

    }
}
