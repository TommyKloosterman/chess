package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor teamColor;
    private PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
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
        return this.teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}; // The four diagonal directions

        for (int[] direction : directions) {
            int deltaX = direction[0];
            int deltaY = direction[1];
            ChessPosition position = new ChessPosition(myPosition.getRow() + deltaX, myPosition.getColumn() + deltaY);

            while (board.isPositionValid(position)) {
                if (!board.isPieceAt(position)) {
                    // Add move if the square is empty
                    moves.add(new ChessMove(myPosition, position, null));
                } else {
                    // Check if it's an enemy piece
                    if (board.getPiece(position).getTeamColor() != this.getTeamColor()) {
                        // Capture enemy piece
                        moves.add(new ChessMove(myPosition, position, null));
                    }
                    // Stop adding moves whether it's an enemy or an ally
                    break;
                }
                // Move to the next square in the diagonal direction
                position = new ChessPosition(position.getRow() + deltaX, position.getColumn() + deltaY);
            }
        }

        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceType == that.pieceType && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, teamColor);
    }


}
