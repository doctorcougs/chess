package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor team;
    private final PieceType type;

    @Override
    public String toString() {
        return team + " " + type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return team == that.team && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, type);
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.team = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return team;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    //for king and knight
    private void relativeToGlobal(ChessBoard board, ChessPosition myPosition,
                                  int[][] relativeMoves, Collection<ChessMove> out) {
        for (int[] move : relativeMoves) {
            int Row = myPosition.getRow() + move[0];
            int Col = myPosition.getColumn() + move[1];
            ChessPosition globalPosMove = new ChessPosition(Row, Col);

            // if that move is off the board then go to next move
            if (Col > 8 || Col < 1 || Row > 8 || Row < 1) {
                continue;
            }

            // if there is a piece there then go to next move
            if (board.getPiece(globalPosMove) != null
                    && board.getPiece(globalPosMove).getTeamColor() == team) {
                continue;
            }
            ChessMove possibleMove = new ChessMove(myPosition, globalPosMove, null);
            out.add(possibleMove);
        }
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> out = new ArrayList<>();
        //use a switch to get relative moves and then translate them to board moves
        switch (type) {
            case KING -> {
                int[][] relativeMoves = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
                relativeToGlobal(board, myPosition, relativeMoves, out);
                return out;
            }
            case QUEEN -> {
                int [][] relativeMoves = {{}}
                return out;
            }
            case ROOK -> {

                return out;
            }
            case BISHOP -> {

                return out;
            }
            case KNIGHT -> {
                int[][] relativeMoves = {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};
                relativeToGlobal(board, myPosition, relativeMoves, out);
                return out;
            }
            case PAWN -> {

                return out;
            }
        }
    }
}
