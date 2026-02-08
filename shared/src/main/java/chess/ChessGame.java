package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard gameBoard;

    public ChessGame() {
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */

    // change to restrict moves that put our king into check
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (gameBoard.getPiece(startPosition) == null) {
            return null;
        }
        ChessPiece guy = gameBoard.getPiece(startPosition);
        return guy.pieceMoves(gameBoard, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //default not in check
        boolean check = false;
        Map<ChessPosition, ChessPiece> enemyPositions = new HashMap<>();
        ChessPosition kingPos = null;

        //cycle through the board
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                //grab all the enemy pieces
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = gameBoard.getPiece(currentPosition);
                if (currentPiece.getTeamColor() != teamColor) {
                    //put the piece into a map that knows their position and who they are
                    enemyPositions.put(currentPosition, currentPiece);
                }
                if (currentPiece.getPieceType() == ChessPiece.PieceType.KING
                        && currentPiece.getTeamColor() == teamColor) {
                    kingPos = new ChessPosition(i, j);
                }
            }
        }

        //cycle through the enemies movesets
        for (var item : enemyPositions.entrySet()) {
            ChessPosition pos = item.getKey();
            ChessPiece enemy = item.getValue();
            Collection<ChessMove> moveSet = enemy.pieceMoves(gameBoard, pos);
            //check each moveset to see if the king is living in one of their attack spots
            for (ChessMove move : moveSet) {
                if (move.getEndPosition() == kingPos) {
                    check = true;
                    break;
                }
            }

        }

        return check;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard.resetBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
