package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

public class PawnMoves {
  public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece.PieceType pieceType, ChessGame.TeamColor teamColor) {
    List<ChessMove> moves = new ArrayList<>();
    // Determine direction and start row based on pawn color
    int direction = teamColor == ChessGame.TeamColor.WHITE ? 1 : -1;
    int startRow = teamColor == ChessGame.TeamColor.WHITE ? 2 : 7;
    int promotionRow = teamColor == ChessGame.TeamColor.WHITE ? 8 : 1;

    // Single step forward
    ChessPosition oneStepForward = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
    if (board.isPositionValid(oneStepForward) && !board.isPieceAt(oneStepForward)) {
      addPromotionMoves(moves, myPosition, oneStepForward, promotionRow);
    }

    // Initial two-step forward move
    if (myPosition.getRow() == startRow) {
      ChessPosition twoStepsForward = new ChessPosition(myPosition.getRow() + (2 * direction), myPosition.getColumn());
      if (!board.isPieceAt(oneStepForward) && !board.isPieceAt(twoStepsForward)) {
        moves.add(new ChessMove(myPosition, twoStepsForward, null));
      }
    }

    // Capturing moves
    int[] captureOffsets = {-1, 1};
    for (int captureOffset : captureOffsets) {
      ChessPosition capturePosition = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + captureOffset);
      if (board.isPositionValid(capturePosition) && board.isPieceAt(capturePosition) &&
              board.getPiece(capturePosition).getTeamColor() != teamColor) {
        addPromotionMoves(moves, myPosition, capturePosition, promotionRow);
      }
    }

    // Convert the list to a HashSet before returning
    return new HashSet<>(moves);
  }

  private static void addPromotionMoves(List<ChessMove> moves, ChessPosition myPosition, ChessPosition newPosition, int promotionRow) {
    if (newPosition.getRow() == promotionRow) {
      // Add all promotion possibilities in the order expected by the test: ROOK, KNIGHT, BISHOP, QUEEN
      moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
      moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
      moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
      moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
    } else {
      moves.add(new ChessMove(myPosition, newPosition, null));
    }
  }
}
