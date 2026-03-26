package ui;

import model.GameData;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessBoardBuilder {

    public static void main(String[] args) {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(0, "w", "b", "testGame", chess);
        ChessBoardBuilder boardBuilder = new ChessBoardBuilder();
        boardBuilder.buildBoard(game, "BLACK");
    }

    public static void buildBoard(GameData gameData, String color) {
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        if (color == "WHITE") {
            printBoard(out, board, false);
        } else {
            printBoard(out, board, true);
        }

    }

    private static void printBoard(PrintStream out, ChessBoard board, boolean flipped) {
        int[] rows = flipped ? new int[]{1,2,3,4,5,6,7,8} : new int[]{8,7,6,5,4,3,2,1};
        String[] cols = flipped ? new String[]{" H\u2003"," G\u2003"," F\u2003"," E\u2003"," D\u2003"," C\u2003"," B\u2003"," A\u2003"}
                : new String[]{" A\u2003", " B\u2003", " C\u2003", " D\u2003", " E\u2003", " F\u2003", " G\u2003", " H\u2003"};


        // Top label row
        out.print(SET_BG_COLOR_RED + SET_TEXT_COLOR_BLACK + "   ");
        for (String col : cols) { out.print(col); }
        out.print("   ");
        out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);

        // Meat
        for (int row : rows) {
            out.print(SET_BG_COLOR_RED + SET_TEXT_COLOR_BLACK + " " + row + " ");
            for (int col = flipped ? 8 : 1; flipped ? col >= 1 : col <= 8; col += flipped ? -1 : 1) {
                boolean isLight = (row + col) % 2 == 0;
                out.print(isLight ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLUE);
                ChessPiece piece = board.getPiece(new chess.ChessPosition(row, col));
                out.print(getPieceSymbol(piece));
            }
            out.print(SET_BG_COLOR_RED + SET_TEXT_COLOR_BLACK + " " + row + " ");
            out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
        }

        // Bottom label row
        out.print(SET_BG_COLOR_RED + SET_TEXT_COLOR_BLACK + "   ");
        for (String col : cols) { out.print(col); }
        out.print("   ");
        out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) { return EMPTY; }
        return switch (piece.getPieceType()) {
            case KING   -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING   : BLACK_KING;
            case QUEEN  -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN  : BLACK_QUEEN;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK   -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK   : BLACK_ROOK;
            case PAWN   -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN   : BLACK_PAWN;
        };
    }

}
