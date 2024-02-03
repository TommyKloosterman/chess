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

        ChessPiece piece = board.getPiece(move.getStartPosition());

        // Check for pawn promotion
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int promotionRow = piece.getTeamColor() == TeamColor.WHITE ? 8 : 1;
            if (move.getEndPosition().getRow() == promotionRow) {
                // Replace the pawn with the promoted piece
                ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), promotedPiece);
                board.addPiece(move.getStartPosition(), null);
            } else {
                // Execute the move for non-promotion cases
                board.addPiece(move.getEndPosition(), piece);
                board.addPiece(move.getStartPosition(), null);
            }
        } else {
            // Execute the move for non-pawn pieces
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
        }

        // Toggle currentPlayer to the next player
        currentPlayer = (currentPlayer == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
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
        if (kingPosition == null) {
            // Handle the case where the King is not found (e.g., return false or handle appropriately)
            return false;
        }
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
        // First, check if the team is currently in check.
        if (!isInCheck(teamColor)) {
            return false;
        }

        // Get all pieces for the team in check.
        Collection<ChessPiece> teamPieces = getAllPiecesOfTeam(teamColor);

        // Iterate over all pieces and check if any move can escape the check.
        for (ChessPiece piece : teamPieces) {
            Collection<ChessMove> moves = piece.pieceMoves(board, piece.getCurrentPosition());
            for (ChessMove move : moves) {
                if (isValidMove(move)) {
                    return false; // There's at least one valid move that can potentially escape the check.
                }
            }
        }

        // If no valid moves are available, the team is in checkmate.
        return true;
    }

    private Collection<ChessPiece> getAllPiecesOfTeam(TeamColor teamColor) {
        Collection<ChessPiece> teamPieces = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    teamPieces.add(piece);
                }
            }
        }
        return teamPieces;
    }


    public boolean isInStalemate(TeamColor teamColor) {
        // If the team is in check, it's not a stalemate.
        if (isInCheck(teamColor)) {
            return false;
        }

        // Remember the original current player
        TeamColor originalCurrentPlayer = currentPlayer;

        // Set the current player to the team being checked for stalemate
        setTeamTurn(teamColor);

        // Get all pieces for the team.
        Collection<ChessPiece> teamPieces = getAllPiecesOfTeam(teamColor);

        // Iterate over all pieces and check if any move is possible.
        for (ChessPiece piece : teamPieces) {
            Collection<ChessMove> moves = piece.pieceMoves(board, piece.getCurrentPosition());
            for (ChessMove move : moves) {
                if (isValidMove(move)) {
                    // Reset the current player to the original
                    setTeamTurn(originalCurrentPlayer);
                    return false; // There's at least one valid move, so it's not a stalemate.
                }
            }
        }

        // Reset the current player to the original
        setTeamTurn(originalCurrentPlayer);

        // If no valid moves are available for any piece, it's a stalemate.
        return true;
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
        // Return null if the King is not found
        return null;
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
