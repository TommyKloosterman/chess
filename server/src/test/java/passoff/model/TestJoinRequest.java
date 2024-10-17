package passoff.model;

import chess.ChessGame;

public class TestJoinRequest {
  private String playerColor;
  private Integer gameID;

  public TestJoinRequest(ChessGame.TeamColor playerColor, Integer gameID) {
    this.playerColor = (playerColor != null) ? playerColor.name() : null;
    this.gameID = gameID;
  }

  // Getters
  public String getPlayerColor() {
    return playerColor;
  }

  public Integer getGameID() {
    return gameID;
  }
}
