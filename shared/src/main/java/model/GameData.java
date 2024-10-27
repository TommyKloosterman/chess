package model;

import chess.ChessGame;

public class GameData {
  private int gameID;
  private String whiteUsername;
  private String blackUsername;
  private String gameName;
  private ChessGame game;

  public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    this.gameID = gameID;
    this.whiteUsername = whiteUsername;
    this.blackUsername = blackUsername;
    this.gameName = gameName;
    this.game = game;
  }

  // Getters
  public int gameID() {
    return gameID;
  }

  public String whiteUsername() {
    return whiteUsername;
  }

  public String blackUsername() {
    return blackUsername;
  }

  public String gameName() {
    return gameName;
  }

  public ChessGame game() {
    return game;
  }

  // Setters
  public void setGameID(int gameID) {
    this.gameID = gameID;
  }

  public void setWhiteUsername(String whiteUsername) {
    this.whiteUsername = whiteUsername;
  }

  public void setBlackUsername(String blackUsername) {
    this.blackUsername = blackUsername;
  }

  public void setGameName(String gameName) {
    this.gameName = gameName;
  }

  public void setGame(ChessGame game) {
    this.game = game;
  }
}
