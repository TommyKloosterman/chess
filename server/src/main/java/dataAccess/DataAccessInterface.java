package dataAccess;

import model.UserData;
import model.GameData;
import model.AuthData;

public interface IDataAccess {

  // Methods for UserData
  void insertUser(UserData user) throws DataAccessException;
  UserData getUser(String username) throws DataAccessException;
  // ... other user methods

  // Methods for GameData
  void createGame(GameData game) throws DataAccessException;
  GameData getGame(int gameID) throws DataAccessException;
  // ... other game methods

  // Methods for AuthData
  void createAuth(AuthData auth) throws DataAccessException;
  AuthData getAuth(String authToken) throws DataAccessException;
  // ... other auth methods
}
