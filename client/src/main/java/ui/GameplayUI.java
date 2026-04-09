package ui;

import chess.*;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Collection;
import java.util.Scanner;

public class GameplayUI implements ServerMessageHandler {
    private static final Scanner SCANNER = new Scanner(System.in);
    private final GameData gameData;
    private final AuthData authData;
    private final ServerFacade serverFacade;
    private final String color;
    private final WebSocketFacade ws;
    private ChessGame currentGame;
    private boolean gameOver = false;

    public GameplayUI(GameData gameData, AuthData authData, ServerFacade serverFacade,
                      String color, String serverUrl) throws Exception {
        this.gameData = gameData;
        this.authData = authData;
        this.serverFacade = serverFacade;
        this.color = color;
        this.ws = new WebSocketFacade(serverUrl, this);
        this.currentGame = gameData.game() != null ? gameData.game() : new ChessGame();
    }

    public static void run(GameData gameData, AuthData authData, ServerFacade serverFacade,
                           String color, String serverUrl) throws Exception {
        new GameplayUI(gameData, authData, serverFacade, color, serverUrl).gameLoop();
    }

    private void gameLoop() throws Exception {
        ws.sendCommand(new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authData.authToken(),
                gameData.gameID()
        ));

        boolean inGame = true;
        while (inGame) {
            System.out.println("[" + authData.username() + "] > ");
            String input = SCANNER.nextLine().trim().toLowerCase();
            switch (input) {
                case "help" -> printHelp();
                case "redraw" -> redraw();
                case "leave" -> {
                    leave();
                    inGame = false;
                }
                case "move" -> move();
                case "resign" -> resign();
                case "wthmcim" -> highlight();
                default -> System.out.println("Unknown command. Type 'help' for options.");
            }
        }

        PostLoginUI.run(authData, serverFacade);
    }

    // case statement functions

    private void printHelp() {
        System.out.println("  help     - show possible commands");
        System.out.println("  redraw   - redraw the  board");
        System.out.println("  move     - make a move");
        System.out.println("  wthmcim - show legal moves for a piece");
        System.out.println("  resign   - cowards way out");
        System.out.println("  leave    - leave the game");
    }

    private void redraw() {
        GameData current = new GameData(gameData.gameID(), gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), currentGame);
        ChessBoardBuilder.buildBoard(current, color);
    }

    private void move() throws Exception {
        if (gameOver) {
            System.out.println("The game is over, no more moves can be made.");
            return;
        }
        System.out.print("From (ex: e2): ");
        String from = SCANNER.nextLine().trim();
        System.out.print("To (ex: e4): ");
        String to = SCANNER.nextLine().trim();

        try {
            ChessPosition start = parsePosition(from);
            ChessPosition end = parsePosition(to);

            ChessPiece.PieceType promotion = null;
            ChessPiece piece = currentGame.getBoard().getPiece(start);
            if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                int endRow = end.getRow();
                if (endRow == 8 || endRow == 1) {
                    System.out.print("Promote to QUEEN, ROOK, BISHOP, or a KNIGHT): ");
                    promotion = ChessPiece.PieceType.valueOf(SCANNER.nextLine().trim().toUpperCase());
                }
            }

            ChessMove move = new ChessMove(start, end, promotion);
            ws.sendCommand(new MakeMoveCommand(authData.authToken(), gameData.gameID(), move));
//        } catch (Exception e) {
//            System.out.println("Invalid position. format like 'e2'.");
//        }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void resign() throws Exception {
        System.out.print("You really just gonna give up? (yes/no): ");
        String confirm = SCANNER.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            ws.sendCommand(new UserGameCommand(
                    UserGameCommand.CommandType.RESIGN,
                    authData.authToken(),
                    gameData.gameID()
            ));
        } else {
            System.out.println("Resign cancelled.");
        }
    }

    private void leave() throws Exception {
        ws.sendCommand(new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authData.authToken(),
                gameData.gameID()
        ));
        ws.close();
    }

    private void highlight() {
        System.out.print("Which piece? (e.g. e2): ");
        String pos = SCANNER.nextLine().trim();
        try {
            ChessPosition position = parsePosition(pos);
            Collection<ChessMove> moves = currentGame.validMoves(position);
            GameData current = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), currentGame);
            ChessBoardBuilder.buildBoardWithHighlights(current, color, moves, position);
        } catch (Exception e) {
            System.out.println("Invalid position. Use format like 'e2'.");
        }
    }

    // server messeges

    @Override
    public void onLoadGame(LoadGameMessage message) {
        this.currentGame = message.getGame();
        GameData current = new GameData(gameData.gameID(), gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), currentGame);
        ChessBoardBuilder.buildBoard(current, color);
        System.out.println();
    }

    @Override
    public void onNotification(NotificationMessage message) {
        System.out.println("\n*** " + message.getMessage() + " ***");
        if (message.getMessage().contains("checkmate") ||
                message.getMessage().contains("stalemate") ||
                message.getMessage().contains("resigned")) {
            gameOver = true;
        }
    }

    @Override
    public void onError(ErrorMessage message) {
        System.out.println("\n[ERROR] " + message.getErrorMessage());
    }

    //helper functions

    private ChessPosition parsePosition(String input) {
        input = input.trim().toLowerCase();
        int col = input.charAt(0) - 'a' + 1;
        int row = input.charAt(1) - '0';
        return new ChessPosition(row, col);
    }
}