package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class QueenMoves {
  public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece.PieceType pieceType, ChessGame.TeamColor teamColor) {
    List<ChessMove> moves = new ArrayList<>();
    // Queen combines the moves of a rook and a bishop
    int[][] directions = {
            {1, 0}, {0, 1}, {-1, 0}, {0, -1}, // Rook moves (straight)
            {1, 1}, {1, -1}, {-1, -1}, {-1, 1} // Bishop moves (diagonal)
    };

    for (int[] direction : directions) {
      int row = myPosition.getRow();
      int column = myPosition.getColumn();
      while (true) {
        row += direction[0];
        column += direction[1];
        ChessPosition newPosition = new ChessPosition(row, column);
        if (!board.isPositionValid(newPosition)) {
          break;
        }
        if (board.isPieceAt(newPosition)) {
          if (board.getPiece(newPosition).getTeamColor() != teamColor) {
            moves.add(new ChessMove(myPosition, newPosition, null)); // Capturing move
          }
          break; // Blocked by a piece
        } else {
          moves.add(new ChessMove(myPosition, newPosition, null)); // Free move
        }
      }
    }

    return moves;
  }
}

