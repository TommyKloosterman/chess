package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()][position.getColumn()];
    }

    /**
     * Checks if a position is within the bounds of the chessboard.
     *
     * @param position The position to check
     * @return true if the position is within the bounds of the chessboard, false otherwise
     */
    public boolean isPositionValid(ChessPosition position) {
        return position.getRow() >= 0 && position.getRow() < squares.length &&
                position.getColumn() >= 0 && position.getColumn() < squares[position.getRow()].length;
    }

    /**
     * Checks if there is a piece at the specified position on the chessboard.
     *
     * @param position The position to check for a piece
     * @return true if there is a piece at the position, false otherwise
     */
    public boolean isPieceAt(ChessPosition position) {
        return getPiece(position) != null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }
}

