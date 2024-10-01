package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class BishopMoves {
  public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece.PieceType pieceType, ChessGame.TeamColor teamColor) {
    List<ChessMove> moves = new ArrayList<>();
    int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}; // The four diagonal directions


    for (int[] direction : directions) {
      int deltaX = direction[0];
      int deltaY = direction[1];
      ChessPosition position = new ChessPosition(myPosition.getRow() + deltaX, myPosition.getColumn() + deltaY);

      while (board.isPositionValid(position)) {
        if (!board.isPieceAt(position)) {
          moves.add(new ChessMove(myPosition, position, null));
        } else {
          if (board.getPiece(position).getTeamColor() != teamColor) {
            moves.add(new ChessMove(myPosition, position, null));
          }
          break;
        }
        position = new ChessPosition(position.getRow() + deltaX, position.getColumn() + deltaY);
      }
    }
    return moves;
  }
}

