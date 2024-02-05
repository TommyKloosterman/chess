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
        if (piece == null || piece.getTeamColor() != currentPlayer) {
            return new ArrayList<>();
        }

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves) {
            if (isMoveLegal(move)) {
                validMoves.add(move);
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING && !piece.hasMoved()) {
            addCastlingMoves(validMoves, piece, startPosition);
        }

        return validMoves;
    }
    private boolean isMoveLegal(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            return false;
        }
        TeamColor teamColor = piece.getTeamColor();

        // Create a temporary board to simulate the move
        ChessBoard tempBoard = new ChessBoard(board);
        tempBoard.addPiece(move.getEndPosition(), tempBoard.getPiece(move.getStartPosition()));
        tempBoard.addPiece(move.getStartPosition(), null);

        // Check if the move results in the king of the moving piece's team being in check
        return !isInCheckOnBoard(teamColor, tempBoard);
    }

    private boolean isInCheckOnBoard(TeamColor teamColor, ChessBoard tempBoard) {
        ChessPosition kingPosition = findKingPositionOnBoard(teamColor, tempBoard);
        if (kingPosition == null) {
            return false;
        }

        for (ChessPiece piece : getAllPiecesOfOpposingTeamOnBoard(teamColor, tempBoard)) {
            Collection<ChessMove> moves = piece.pieceMoves(tempBoard, piece.getCurrentPosition());
            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    private ChessPosition findKingPositionOnBoard(TeamColor teamColor, ChessBoard tempBoard) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = tempBoard.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null;
    }

    private Collection<ChessPiece> getAllPiecesOfOpposingTeamOnBoard(TeamColor teamColor, ChessBoard tempBoard) {
        Collection<ChessPiece> opposingPieces = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = tempBoard.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    opposingPieces.add(piece);
                }
            }
        }
        return opposingPieces;
    }

    private void addCastlingMoves(Collection<ChessMove> validMoves, ChessPiece king, ChessPosition kingPos) {
        // Check for both king-side and queen-side castling
        if (canCastleKingSide(king, kingPos)) {
            validMoves.add(new ChessMove(kingPos, new ChessPosition(kingPos.getRow(), kingPos.getColumn() + 2), null));
        }
        if (canCastleQueenSide(king, kingPos)) {
            validMoves.add(new ChessMove(kingPos, new ChessPosition(kingPos.getRow(), kingPos.getColumn() - 2), null));
        }
    }

    private boolean canCastleKingSide(ChessPiece king, ChessPosition kingPos) {
        if (king.hasMoved()) {
            return false;
        }

        ChessPiece rook = board.getPiece(new ChessPosition(kingPos.getRow(), 8));
        if (rook == null || rook.hasMoved() || rook.getPieceType() != ChessPiece.PieceType.ROOK) {
            return false;
        }

        // Check if the squares between the king and the rook are empty and not under attack
        for (int col = kingPos.getColumn() + 1; col < 8; col++) {
            ChessPosition position = new ChessPosition(kingPos.getRow(), col);
            if (board.isPieceAt(position) || isSquareUnderAttack(position, king.getTeamColor())) {
                return false;
            }
        }
        return true;
    }

    private boolean canCastleQueenSide(ChessPiece king, ChessPosition kingPos) {
        if (king.hasMoved()) {
            return false;
        }

        ChessPiece rook = board.getPiece(new ChessPosition(kingPos.getRow(), 1));
        if (rook == null || rook.hasMoved() || rook.getPieceType() != ChessPiece.PieceType.ROOK) {
            return false;
        }

        // Check if the squares between the king and the rook are empty and not under attack
        for (int col = kingPos.getColumn() - 1; col > 1; col--) {
            ChessPosition position = new ChessPosition(kingPos.getRow(), col);
            if (board.isPieceAt(position) || isSquareUnderAttack(position, king.getTeamColor())) {
                return false;
            }
        }
        return true;
    }

    private boolean isSquareUnderAttack(ChessPosition position, TeamColor kingColor) {
        // Simulate a board where the king is on the position and check if it's in check
        ChessBoard tempBoard = new ChessBoard(board);
        tempBoard.addPiece(position, new ChessPiece(kingColor, ChessPiece.PieceType.KING));
        return isInCheckOnBoard(kingColor, tempBoard);
    }

    private boolean isCastlingMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        return piece != null && piece.getPieceType() == ChessPiece.PieceType.KING &&
                Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) == 2;
    }

    private void performCastling(ChessMove move) {
        ChessPiece king = board.getPiece(move.getStartPosition());
        ChessPosition rookStartPos;
        ChessPosition rookEndPos;

        if (move.getEndPosition().getColumn() > move.getStartPosition().getColumn()) {
            // King-side castling
            rookStartPos = new ChessPosition(move.getStartPosition().getRow(), 8);
            rookEndPos = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn() - 1);
        } else {
            // Queen-side castling
            rookStartPos = new ChessPosition(move.getStartPosition().getRow(), 1);
            rookEndPos = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn() + 1);
        }

        ChessPiece rook = board.getPiece(rookStartPos);
        board.addPiece(move.getEndPosition(), king);
        board.addPiece(rookEndPos, rook);
        board.addPiece(move.getStartPosition(), null);
        board.addPiece(rookStartPos, null);
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!isValidMove(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        // Set hasMoved to true for both the piece being moved and the rook in castling
        if (isCastlingMove(move)) {
            performCastling(move);
            piece.setHasMoved(true);
            // Also set the rook's hasMoved to true
            ChessPosition rookStartPos = (move.getEndPosition().getColumn() > move.getStartPosition().getColumn())
                    ? new ChessPosition(move.getStartPosition().getRow(), 8)
                    : new ChessPosition(move.getStartPosition().getRow(), 1);
            ChessPiece rook = board.getPiece(rookStartPos);
            if (rook != null) {
                rook.setHasMoved(true);
            }
        } else {
            piece.setHasMoved(true);
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

        // Check if the move is a castling move
        if (isCastlingMove(move)) {
            // Validate the castling move
            if (piece.getPieceType() == ChessPiece.PieceType.KING && !piece.hasMoved()) {
                if (move.getEndPosition().getColumn() == move.getStartPosition().getColumn() + 2) {
                    // King-side castling
                    return canCastleKingSide(piece, move.getStartPosition());
                } else if (move.getEndPosition().getColumn() == move.getStartPosition().getColumn() - 2) {
                    // Queen-side castling
                    return canCastleQueenSide(piece, move.getStartPosition());
                }
            }
            return false; // Not a valid castling move
        }

        // Regular move validation
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