package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class KingMoves {
  public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece.PieceType pieceType, ChessGame.TeamColor teamColor) {
    List<ChessMove> moves = new ArrayList<>();
    // The king can move one square in any direction
    int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 1}};

    for (int[] direction : directions) {
      int deltaX = direction[0];
      int deltaY = direction[1];
      ChessPosition position = new ChessPosition(myPosition.getRow() + deltaX, myPosition.getColumn() + deltaY);

      if (board.isPositionValid(position)) {
        if (!board.isPieceAt(position) || board.getPiece(position).getTeamColor() != teamColor) {
          moves.add(new ChessMove(myPosition, position, null)); // No promotion for king
        }
      }
    }

    return moves;
  }
}
