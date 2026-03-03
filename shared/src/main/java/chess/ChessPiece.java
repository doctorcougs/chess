package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() {
        return team;
    }

    public PieceType getPieceType() {
        return type;
    }

    private void relativeToGlobal(ChessBoard board, ChessPosition myPosition,
                                  int[][] relativeMoves, Collection<ChessMove> out) {
        for (int[] move : relativeMoves) {
            int row = myPosition.getRow() + move[0];
            int col = myPosition.getColumn() + move[1];
            if (col > 8 || col < 1 || row > 8 || row < 1) {
                continue;
            }
            ChessPosition globalPosMove = new ChessPosition(row, col);
            if (board.getPiece(globalPosMove) != null
                    && board.getPiece(globalPosMove).getTeamColor() == team) {
                continue;
            }
            out.add(new ChessMove(myPosition, globalPosMove, null));
        }
    }

    private void directionalityToMoves(ChessBoard board, ChessPosition myPosition,
                                       int[][] directionality, Collection<ChessMove> out) {
        for (int[] direction : directionality) {
            int rowMove = direction[0];
            int colMove = direction[1];
            int dist = 1;
            boolean hitEnemy = false;
            while (true) {
                if (hitEnemy) {
                    break;
                }
                int row = myPosition.getRow() + (rowMove * dist);
                int col = myPosition.getColumn() + (colMove * dist);
                if (col > 8 || col < 1 || row > 8 || row < 1) {
                    break;
                }
                ChessPosition globalPosMove = new ChessPosition(row, col);
                if (board.getPiece(globalPosMove) != null
                        && board.getPiece(globalPosMove).getTeamColor() == team) {
                    break;
                }
                if (board.getPiece(globalPosMove) != null) {
                    hitEnemy = true;
                }
                out.add(new ChessMove(myPosition, globalPosMove, null));
                dist++;
            }
        }
    }

    private void addPromotionMoves(ChessPosition from, ChessPosition to, Collection<ChessMove> out) {
        out.add(new ChessMove(from, to, PieceType.KNIGHT));
        out.add(new ChessMove(from, to, PieceType.BISHOP));
        out.add(new ChessMove(from, to, PieceType.ROOK));
        out.add(new ChessMove(from, to, PieceType.QUEEN));
    }

    private void addPawnAttack(ChessBoard board, ChessPosition myPosition, ChessPosition attackPos,
                               int promotionRow, int newRow, Collection<ChessMove> out) {
        ChessPiece target = board.getPiece(attackPos);
        if (target == null || target.getTeamColor() == team) {
            return;
        }
        if (newRow == promotionRow) {
            addPromotionMoves(myPosition, attackPos, out);
        } else {
            out.add(new ChessMove(myPosition, attackPos, null));
        }
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> out = new ArrayList<>();
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
                int direction = (team == ChessGame.TeamColor.WHITE) ? 1 : -1;
                int startRow = (team == ChessGame.TeamColor.WHITE) ? 2 : 7;
                int promotionRow = (team == ChessGame.TeamColor.WHITE) ? 8 : 1;

                int newRow = myPosition.getRow() + direction;
                if (newRow < 1 || newRow > 8) {
                    return out;
                }

                ChessPosition moveOne = new ChessPosition(newRow, myPosition.getColumn());
                if (board.getPiece(moveOne) == null) {
                    if (newRow == promotionRow) {
                        addPromotionMoves(myPosition, moveOne, out);
                    } else {
                        out.add(new ChessMove(myPosition, moveOne, null));
                    }
                    if (myPosition.getRow() == startRow) {
                        ChessPosition moveTwo = new ChessPosition(newRow + direction, myPosition.getColumn());
                        if (board.getPiece(moveTwo) == null) {
                            out.add(new ChessMove(myPosition, moveTwo, null));
                        }
                    }
                }

                if (myPosition.getColumn() < 8) {
                    ChessPosition rightAttack = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);
                    addPawnAttack(board, myPosition, rightAttack, promotionRow, newRow, out);
                }

                if (myPosition.getColumn() > 1) {
                    ChessPosition leftAttack = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);
                    addPawnAttack(board, myPosition, leftAttack, promotionRow, newRow, out);
                }

                return out;
            }
        }
        return out;
    }
}