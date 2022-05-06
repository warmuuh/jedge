package com.github.warmuuh.jedge;

public class DatabaseProtocolException extends Exception {

  public DatabaseProtocolException() {
  }

  public DatabaseProtocolException(String message) {
    super(message);
  }

  public DatabaseProtocolException(String message, Throwable cause) {
    super(message, cause);
  }

  public DatabaseProtocolException(Throwable cause) {
    super(cause);
  }
}
