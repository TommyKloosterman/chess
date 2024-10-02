package chess;

import java.util.Arrays;
import java.util.Objects;

public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        // Constructor logic (if any)
    }

    // Copy constructor
    public ChessBoard(ChessBoard originalBoard) {
        this.squares = new ChessPiece[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece originalPiece = originalBoard.squares[row][col];
                if (originalPiece != null) {
                    this.squares[row][col] = new ChessPiece(originalPiece.getTeamColor(), originalPiece.getPieceType());
                    this.squares[row][col].setCurrentPosition(new ChessPosition(row + 1, col + 1));
                }
            }
        }
    }

    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (isPositionValid(position)) {
            squares[position.getRow() - 1][position.getColumn() - 1] = piece;
            if (piece != null) {
                piece.setCurrentPosition(position); // Update the current position of the piece
            }
        } else {
            throw new IllegalArgumentException("Position out of bounds");
        }
    }

    public ChessPiece getPiece(ChessPosition position) {
        if (!isPositionValid(position)) {
            throw new IllegalArgumentException("Position out of bounds");
        }
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    public boolean isPositionValid(ChessPosition position) {
        return position.getRow() >= 1 && position.getRow() <= 8 &&
                position.getColumn() >= 1 && position.getColumn() <= 8;
    }

    public boolean isPieceAt(ChessPosition position) {
        return getPiece(position) != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        // Compare every piece on the board for equality
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece thisPiece = this.squares[row][col];
                ChessPiece thatPiece = that.squares[row][col];
                if (!Objects.equals(thisPiece, thatPiece)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        // Use a deep hash code to incorporate all pieces in the board
        return Arrays.deepHashCode(squares);
    }

    public void resetBoard() {
        // Clear the board
        for (int row = 0; row < squares.length; row++) {
            Arrays.fill(squares[row], null);
        }

        // Set up the white pieces
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        // Set up the white pawns
        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        // Set up the black pawns
        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        // Set up the black pieces
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
    }
}
