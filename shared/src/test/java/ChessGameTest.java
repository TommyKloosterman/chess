package chess; // This should be the first line of the file, adjust the package name if needed

import java.util.Collection;

public class ChessGameTest {

  public static void main(String[] args) {
    // Create a chessboard
    ChessBoard board = new ChessBoard();

    // Create a bishop and add it to the board at position (5, 4)
    ChessPosition bishopPosition = new ChessPosition(5, 4); // Assuming 0-based indexing
    ChessPiece bishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
    board.addPiece(bishopPosition, bishop);

    // Get the moves for the bishop
    Collection<ChessMove> moves = bishop.pieceMoves(board, bishopPosition);

    // Print out the moves
    for (ChessMove move : moves) {
      System.out.println(move.toString());
    }
  }
}
