package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

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
        // Create an empty list to store all possible moves for the bishop.
        List<ChessMove> moves = new ArrayList<>();

        // Define the four diagonal directions in which a bishop can move.
        // Each direction is represented as a pair of integers (deltaX, deltaY).
        // For example, (1, 1) represents moving diagonally up-right.
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        // Iterate over each of the four directions.
        for (int[] direction : directions) {
            // Extract the x (deltaX) and y (deltaY) movement components for this direction.
            int deltaX = direction[0];
            int deltaY = direction[1];

            // Start from the current position of the bishop.
            ChessPosition position = myPosition;

            // Keep moving the bishop in the current direction.
            while (true) {
                // Calculate the bishop's next position based on its movement delta.
                position = new ChessPosition(position.getRow() + deltaX, position.getColumn() + deltaY);

                // Check if the new position is still on the board.
                // If it's not, break out of the while loop and stop exploring this direction.
                if (!board.isPositionValid(position)) {
                    break;
                }

                // Check if there is a piece at the new position.
                if (board.isPieceAt(position)) {
                    // Check if the piece at the new position is an opponent's piece.
                    // This is done by comparing the team colors of the two pieces.
                    if (board.getPiece(position).getTeamColor() != this.getTeamColor()) {
                        // If it's an opponent's piece, add this move as it captures the opponent's piece.
                        moves.add(new ChessMove(myPosition, position, this.getPieceType()));
                    }
                    // Break out of the while loop as the bishop can't jump over other pieces.
                    break;
                }

                // If the position is valid and there's no piece, add this as a valid move.
                moves.add(new ChessMove(myPosition, position, this.getPieceType()));
            }
        }

        // Return the list of all valid moves found for the bishop.
        return moves;
    }
}
