package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
  // The record class automatically provides getters, constructors, equals(), hashCode(), and toString()
}
