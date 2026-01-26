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

            // if that move is off the board then go to next move
            if (Col > 8 || Col < 1 || Row > 8 || Row < 1) {
                continue;
            }

            ChessPosition globalPosMove = new ChessPosition(Row, Col);

            // if there is a piece there then go to next move
            if (board.getPiece(globalPosMove) != null
                    && board.getPiece(globalPosMove).getTeamColor() == team) {
                continue;
            }

            // move passed the checks so we add
            ChessMove possibleMove = new ChessMove(myPosition, globalPosMove, null);
            out.add(possibleMove);
        }
    }

    private void directionalityToMoves(ChessBoard board, ChessPosition myPosition,
                                       int[][] directionality, Collection<ChessMove> out) {
        for (int[] direction : directionality) {
            int rowMove = direction[0];
            int colMove = direction[1];

            //keep adding moves until we hit a wall or a piece
            int dist = 1;
            boolean hitEnemy = false;
            while (true) {
                //check if we have hit an enemy because now we can't go farther
                if (hitEnemy) {
                    break;
                }

                int Row = myPosition.getRow() + (rowMove * dist);
                int Col = myPosition.getColumn() + (colMove * dist);

                //going off of the board so break
                if (Col > 8 || Col < 1 || Row > 8 || Row < 1) {
                    break;
                }

                ChessPosition globalPosMove = new ChessPosition(Row, Col);

                //hit one of our piece so break
                if (board.getPiece(globalPosMove) != null
                        && board.getPiece(globalPosMove).getTeamColor() == team) {
                    break;
                }
                //hit an enemy piece so we get this move but no more
                if (board.getPiece(globalPosMove) != null) {
                    hitEnemy = true;
                }

                // move passed the checks so we add
                ChessMove possibleMove = new ChessMove(myPosition, globalPosMove, null);
                out.add(possibleMove);

                //increment distance for next loop
                dist++;
            }
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
                int[][] directionality = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
                directionalityToMoves(board, myPosition, directionality, out);
                return out;
            }
            case ROOK -> {
                int[][] directionality = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
                directionalityToMoves(board, myPosition, directionality, out);
                return out;
            }
            case BISHOP -> {
                int[][] directionality = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
                directionalityToMoves(board, myPosition, directionality, out);
                return out;
            }
            case KNIGHT -> {
                int[][] relativeMoves = {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};
                relativeToGlobal(board, myPosition, relativeMoves, out);
                return out;
            }
            case PAWN -> {
                //different logic numbers for different teams
                int direction = (team == ChessGame.TeamColor.WHITE) ? 1 : -1;
                int startRow = (team == ChessGame.TeamColor.WHITE) ? 2 : 7;
                int promotionRow = (team == ChessGame.TeamColor.WHITE) ? 8 : 1;

                int newRow = myPosition.getRow() + direction;
                //this shouldn't happen but check row bounds
                if (newRow < 1 || newRow > 8) {
                    return out;
                }
                ChessPosition moveOne = new ChessPosition(newRow, myPosition.getColumn());

                //checks if the first move is possible, and then second as well
                if (board.getPiece(moveOne) == null) {
                    //promotion logic
                    if (newRow == promotionRow) {
                        out.add(new ChessMove(myPosition, moveOne, PieceType.KNIGHT));
                        out.add(new ChessMove(myPosition, moveOne, PieceType.BISHOP));
                        out.add(new ChessMove(myPosition, moveOne, PieceType.ROOK));
                        out.add(new ChessMove(myPosition, moveOne, PieceType.QUEEN));
                    } else {
                        out.add(new ChessMove(myPosition, moveOne, null));
                    }

                    //if we are moving two we can't also promote
                    if (myPosition.getRow() == startRow) {
                        ChessPosition moveTwo = new ChessPosition(newRow + direction, myPosition.getColumn());
                        if (board.getPiece(moveTwo) == null) {
                            out.add(new ChessMove(myPosition, moveTwo, null));
                        }
                    }
                }
                //attacking!
                if (myPosition.getColumn() < 8) {
                    ChessPosition rightAttack = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);

                    //if there is a piece diag right and it's not our team we can move there
                    if (board.getPiece(rightAttack) != null && board.getPiece(rightAttack).getTeamColor() != team) {
                        // can reuse newrow because we are still moving up one, checks for promotion
                        if (newRow == promotionRow) {
                            out.add(new ChessMove(myPosition, rightAttack, PieceType.KNIGHT));
                            out.add(new ChessMove(myPosition, rightAttack, PieceType.BISHOP));
                            out.add(new ChessMove(myPosition, rightAttack, PieceType.ROOK));
                            out.add(new ChessMove(myPosition, rightAttack, PieceType.QUEEN));
                        } else {
                            out.add(new ChessMove(myPosition, rightAttack, null));
                        }
                    }
                }
                //same logic for left attack
                if (myPosition.getColumn() > 1) {
                    ChessPosition leftAttack = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);

                    if (board.getPiece(leftAttack) != null && board.getPiece(leftAttack).getTeamColor() != team) {
                        // can reuse newrow because we are still moving up one, checks for promotion
                        if (newRow == promotionRow) {
                            out.add(new ChessMove(myPosition, leftAttack, PieceType.KNIGHT));
                            out.add(new ChessMove(myPosition, leftAttack, PieceType.BISHOP));
                            out.add(new ChessMove(myPosition, leftAttack, PieceType.ROOK));
                            out.add(new ChessMove(myPosition, leftAttack, PieceType.QUEEN));
                        } else {
                            out.add(new ChessMove(myPosition, leftAttack, null));
                        }
                    }
                }

                return out;
            }
        }
        return out;
    }
}
