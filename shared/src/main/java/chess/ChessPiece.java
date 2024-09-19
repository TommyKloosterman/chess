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
    private ChessPosition currentPosition;

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

    public void setCurrentPosition(ChessPosition position) {
        this.currentPosition = position;
    }

    public ChessPosition getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (this.pieceType) {
            case KING:
                return KingMoves.calculateMoves(board, myPosition, this.pieceType, this.teamColor);
            case QUEEN:
                return QueenMoves.calculateMoves(board, myPosition, this.pieceType, this.teamColor);
            case ROOK:
                return RookMoves.calculateMoves(board, myPosition, this.pieceType, this.teamColor);
            case BISHOP:
                return BishopMoves.calculateMoves(board, myPosition, this.pieceType, this.teamColor);
            case KNIGHT:
                return KnightMoves.calculateMoves(board, myPosition, this.pieceType, this.teamColor);
            case PAWN:
                return PawnMoves.calculateMoves(board, myPosition, this.pieceType, this.teamColor);
            // ... handle other piece types ...
            default:
                throw new UnsupportedOperationException("Piece type not supported");
        }
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
