package ui;


import model.*;

import java.util.Scanner;

public class PreLoginUI {
    private static ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    private static Scanner scanner = new Scanner(System.in);
    private static boolean loggedIn = false;
    private static AuthData authData = null;

    public static void run(){
        System.out.println("Welcome to 240 Chess. Please type help to get started.");
        while (!loggedIn) {
            String input = scanner.nextLine();
            switch(input) {
                case "help" -> {
                    System.out.println("register");
                    System.out.println("login");
                    System.out.println("quit");
                    System.out.println("help");
                }
                case "login" -> login();
                case "register" -> register();
                case "quit" -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid command, valid commands are: help, login, register, quit");
            }
        }
    }
    private static void login() {
        try {
            System.out.println("Please enter your username:");
            String username = scanner.nextLine();
            System.out.println("Please enter your password:");
            String password = scanner.nextLine();

            UserData userData = new UserData(username, password, null);
            authData = serverFacade.login(userData);
            loggedIn = true;
        }
        catch (Exception e) {
            System.out.println("Invalid username or password");
            run();
        }
    }
    private static void register() {
        try {
            System.out.println("Please enter your username:");
            String username = scanner.nextLine();
            System.out.println("Please enter your password:");
            String password = scanner.nextLine();
            System.out.println("Please enter your email:");
            String email = scanner.nextLine();

            UserData userData = new UserData(username, password, email);
            authData = serverFacade.register(userData);
            loggedIn = true;
        }
        catch (Exception e) {
            System.out.println("Invalid username or password");
            run();
        }
    }}
