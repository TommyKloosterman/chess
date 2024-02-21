package model;

public record UserData(String username, String password, String email) {
  // The record class automatically provides getters, constructors, equals(), hashCode(), and toString()
}
