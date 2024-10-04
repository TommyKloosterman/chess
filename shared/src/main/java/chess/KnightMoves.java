package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class KnightMoves {
  public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece.PieceType pieceType, ChessGame.TeamColor teamColor) {
    List<ChessMove> moves = new ArrayList<>();
    // The knight can move to 8 possible positions
    int[][] movesOffset = {
            {-2, -1}, {-1, -2}, {1, -2}, {2, -1},
            {2, 1}, {1, 2}, {-1, 2}, {-2, 1}
    };

    for (int[] offset : movesOffset) {
      int row = myPosition.getRow() + offset[0];
      int column = myPosition.getColumn() + offset[1];
      ChessPosition newPosition = new ChessPosition(row, column);

      if (board.isPositionValid(newPosition) && (!board.isPieceAt(newPosition) ||
              board.getPiece(newPosition).getTeamColor() != teamColor)) {
        moves.add(new ChessMove(myPosition, newPosition, null)); // No promotion for knight
      }
    }

    return moves;
  }
}
