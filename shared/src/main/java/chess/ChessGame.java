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
    private final ChessBoard gameBoard;

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

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //check for existence
        if (gameBoard.getPiece(startPosition) == null) {
            return null;
        }
        ChessPiece guy = gameBoard.getPiece(startPosition);

        //list of valid moves
        Collection<ChessMove> validMoves = new ArrayList<>();

        //grab our possible moves
        Collection<ChessMove> possibleMoves = guy.pieceMoves(gameBoard, startPosition);
        for (ChessMove move : possibleMoves) {
            //take out our current piece and move to the end
            gameBoard.addPiece(startPosition, null);
            gameBoard.addPiece(move.getEndPosition(), guy);
            //check our check in current state and add if not in check
            if (!isInCheck(guy.getTeamColor())) {
                validMoves.add(move);
            }
            //reset back to how it was
            gameBoard.addPiece(move.getEndPosition(), null);
            gameBoard.addPiece(startPosition, guy);

        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */

    public void makeMove(ChessMove move) throws InvalidMoveException {
        //get list of valid moves
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        //if our valid moves are empty return null
        if (validMoves == null) {
            throw new InvalidMoveException("No valid moves");
        }

        //make sure the move is in the valids, and that it's the right teams turn
        boolean isValidMove = validMoves.contains(move);
        ChessPiece guy = gameBoard.getPiece(move.getStartPosition());
        boolean correctTurn = guy.getTeamColor() == teamTurn;

        if (isValidMove && correctTurn) {
            if (move.getPromotionPiece() != null) {
                guy = new ChessPiece(teamTurn, move.getPromotionPiece());
            }
            //remove and add piece
            gameBoard.addPiece(move.getStartPosition(), null);
            gameBoard.addPiece(move.getEndPosition(), guy);
            //if our team turn is white then black and vise versa
            setTeamTurn(teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        }
        else {
            throw new InvalidMoveException("Not a valid move or it's not your turn");
        }
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
        //if we can't make any valid moves and we are also in check then boom checkmate
        return isInStalemate(teamColor) && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        boolean stale = false;
        Collection<ChessMove> totalValidMoves = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = gameBoard.getPiece(currentPosition);
                //if our piece is the color we are checking then we are going to grab all of his valid moves
                //we add those to our total valid moves collection
                if (currentPiece.getTeamColor() == teamColor) {
                    totalValidMoves.addAll(validMoves(currentPosition));
                }
            }
        }
        //only if there is no valid moves then are we in stalemate
        if (totalValidMoves.isEmpty()) {
            stale = true;
        }
        return stale;
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
