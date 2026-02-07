package m1graphs2025;

/**
 * A class to handle graph related exceptions.
 */
public class GraphExceptions extends RuntimeException {
  public GraphExceptions(String message) {
    super(message);
  }

  public GraphExceptions(String message, Throwable cause) {
    super(message, cause);
  }
}
