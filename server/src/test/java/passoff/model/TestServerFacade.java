package passoff.model;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import passoff.server.StandardAPITests;

public class TestServerFacade {
  private String host;
  private String port;
  private int statusCode;
  private Gson gson = new GsonBuilder().create();

  public TestServerFacade(String host, String port) {
    this.host = host;
    this.port = port;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void clear() {
    // Send a DELETE request to /db endpoint
    sendRequest("DELETE", "/db", null, null);
  }

  public String file(String path) {
    return sendRequest("GET", path, null, null);
  }

  public TestAuthResult register(TestUser user) {
    String jsonResponse = sendRequest("POST", "/user", null, user);
    return parseResult(jsonResponse, TestAuthResult.class);
  }

  public TestAuthResult login(TestUser user) {
    String jsonResponse = sendRequest("POST", "/session", null, user);
    return parseResult(jsonResponse, TestAuthResult.class);
  }

  public TestResult logout(String authToken) {
    String jsonResponse = sendRequest("DELETE", "/session", authToken, null);
    return parseResult(jsonResponse, TestResult.class);
  }

  public TestCreateResult createGame(TestCreateRequest request, String authToken) {
    String jsonResponse = sendRequest("POST", "/game", authToken, request);
    return parseResult(jsonResponse, TestCreateResult.class);
  }

  public TestResult joinPlayer(TestJoinRequest request, String authToken) {
    String jsonResponse = sendRequest("PUT", "/game", authToken, request);
    return parseResult(jsonResponse, TestResult.class);
  }

  public TestListResult listGames(String authToken) {
    String jsonResponse = sendRequest("GET", "/game", authToken, null);
    return parseResult(jsonResponse, TestListResult.class);
  }

  // Helper methods

  private String sendRequest(String method, String endpoint, String authToken, Object body) {
    try {
      URL url = new URL("http://" + host + ":" + port + endpoint);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod(method);

      if (authToken != null) {
        connection.setRequestProperty("Authorization", authToken);
      }

      if (body != null) {
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        String jsonBody = gson.toJson(body);
        connection.getOutputStream().write(jsonBody.getBytes());
      }

      statusCode = connection.getResponseCode();

      Scanner scanner;
      if (statusCode >= 200 && statusCode < 300) {
        scanner = new Scanner(connection.getInputStream()).useDelimiter("\\A");
      } else {
        scanner = new Scanner(connection.getErrorStream()).useDelimiter("\\A");
      }

      String response = scanner.hasNext() ? scanner.next() : "";

      scanner.close();
      connection.disconnect();

      return response;
    } catch (Exception e) {
      e.printStackTrace();
      statusCode = 500;
      return null;
    }
  }

  private <T extends TestResult> T parseResult(String jsonResponse, Class<T> resultClass) {
    try {
      T result = gson.fromJson(jsonResponse, resultClass);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      try {
        T result = resultClass.getDeclaredConstructor().newInstance();
        result.setMessage("Error parsing response");
        return result;
      } catch (Exception ex) {
        ex.printStackTrace();
        return null;
      }
    }
  }
}
