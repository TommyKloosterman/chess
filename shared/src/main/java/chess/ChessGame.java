package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChessGame {
    private ChessBoard board;
    private TeamColor currentPlayer;

    public ChessGame() {
        this.board = new ChessBoard();
        this.currentPlayer = TeamColor.WHITE;
    }

    public TeamColor getTeamTurn() {
        return this.currentPlayer;
    }

    public void setTeamTurn(TeamColor team) {
        this.currentPlayer = team;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        return piece.pieceMoves(board, startPosition);
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!isValidMove(move)) {
            throw new InvalidMoveException("Invalid move");
        }
        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        board.addPiece(move.getStartPosition(), null);
        setTeamTurn(currentPlayer == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    private boolean isValidMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != currentPlayer) {
            return false;
        }
        if (!piece.pieceMoves(board, move.getStartPosition()).contains(move)) {
            return false;
        }
        if (!isPathClear(move)) {
            return false;
        }
        // Simulate the move and check for check
        ChessPiece targetPiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
        boolean inCheck = isInCheck(currentPlayer);
        board.addPiece(move.getStartPosition(), piece);
        board.addPiece(move.getEndPosition(), targetPiece);
        return !inCheck;
    }

    private boolean isPathClear(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            return false;
        }

        int startRow = move.getStartPosition().getRow();
        int startColumn = move.getStartPosition().getColumn();
        int endRow = move.getEndPosition().getRow();
        int endColumn = move.getEndPosition().getColumn();

        int rowStep = Integer.compare(endRow, startRow);
        int columnStep = Integer.compare(endColumn, startColumn);

        // For Knight, path is always clear as it jumps over pieces
        if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return true;
        }

        int currentRow = startRow + rowStep;
        int currentColumn = startColumn + columnStep;

        // Check each square along the path for other pieces
        while (currentRow != endRow || currentColumn != endColumn) {
            if (board.getPiece(new ChessPosition(currentRow, currentColumn)) != null) {
                return false; // Path is not clear
            }
            currentRow += rowStep;
            currentColumn += columnStep;
        }

        return true; // Path is clear
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        for (ChessPiece piece : getAllPiecesOfOpposingTeam(teamColor)) {
            Collection<ChessMove> moves = piece.pieceMoves(board, piece.getCurrentPosition());
            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return this.board;
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        throw new IllegalStateException("King not found on board for team " + teamColor);
    }

    private Collection<ChessPiece> getAllPiecesOfOpposingTeam(TeamColor teamColor) {
        Collection<ChessPiece> opposingPieces = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    opposingPieces.add(piece);
                }
            }
        }
        return opposingPieces;
    }
}
