//package ui;
//
//import chess.ChessBoard;
//import chess.ChessGame;
//import model.*;
//
//import java.util.Scanner;
//import java.util.Map;
//import java.util.HashMap;
//
//public class GameplayUI {
//    private static Scanner scanner = new Scanner(System.in);
//
//    public static void run(GameData gameData, ChessBoardBuilder boardBuilder, AuthData authData, ServerFacade serverFacade) throws Exception {
//        boolean playing = true;
//        while (playing) {
//            String input = scanner.nextLine();
//            switch(input) {
//                case "help" -> {
//                    System.out.println("help: display commands");
//                    System.out.println("redraw: redraws the chess board");
//                    System.out.println("leave: leaves the game");
//                    System.out.println("move: move a piece");
//                    System.out.println("resign: offers the chance to give up");
//                    System.out.println("wthmcim: highlights all legal moves for a piece");
//                }
//                case "redraw" -> {}
//                case "leave" -> {
//                    playing = false;
//                }
//                case "move" -> {}
//                case "resign" -> {}
//                case "wthmcim" -> {}
//            }
//        }
//        PostLoginUI.run(authData, serverFacade);
//    }
//
//}
