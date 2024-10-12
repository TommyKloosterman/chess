import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        // Initialize a ChessPiece (from your chess logic)
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        // Create and run the server (this starts the HTTP server on port 8080)
        Server server = new Server();
        int port = server.run(8080);

        System.out.println("Server is running on port " + port + "...");
    }
}
