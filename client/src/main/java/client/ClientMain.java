package client;

import chess.*;
import ui.PreLoginUI;


public class ClientMain {
    public static void main(String[] args) throws Exception{
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        PreLoginUI.run();
    }
}
