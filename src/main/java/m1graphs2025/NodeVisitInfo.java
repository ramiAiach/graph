package m1graphs2025;

/**
 * A class that encapsulates the colour of a node, its predecessor, its discovery and finished timestamps and a static time.
 */
public class NodeVisitInfo {
  private NodeColour colour;
  private Node predecessor;
  private Integer discovery;
  private Integer finished;
  private static int time = 0;

  /**
   * NodeVisitInfo constructor.
   * @param colour NodeColour {WHITE, GRAY, BLACK}.
   * @param predecessor Node predecessor.
   * @param discovery Node discovery time.
   * @param finished Node finish time.
   */
  public NodeVisitInfo(NodeColour colour, Node predecessor, Integer discovery, Integer finished) {
    this.colour = colour;
    this.predecessor = predecessor;
    this.discovery = discovery;
    this.finished = finished;
  }

  /**
   * Gets the NodeVisitInfo color attribute. 
   * @return this.color
   */
  public NodeColour getColour() {
    return colour;
  }

  /**
   * Gets the NodeVisitInfo predecessor attribute.
   * @return this.predecessor
   */
  public Node getPredecessor() {
    return predecessor;
  }

  /**
   * Gets the NodeVisitInfo discovery attribute.
   * @return this.discovery
   */
  public Integer getDiscovery() {
    return discovery;
  }

  /**
   * Gets the NodeVisitInfo finished attribute.
   * @return this.finished
   */
  public Integer getFinished() {
    return finished;
  }

  /**
   * Sets the NodeVisitInfo color attribute. 
   */
  public void setColour(NodeColour colour) {
    this.colour = colour;
  }

  /**
   * Sets the NodeVisitInfo predecessor attribute. 
   */
  public void setPredecessor(Node predecessor) {
    this.predecessor = predecessor;
  }

  /**
   * Sets the NodeVisitInfo discovery attribute. 
   */
  public void setDiscovery(Integer discovery) {
    this.discovery = discovery;
  }

  /**
   * Sets the NodeVisitInfo finished attribute. 
   */
  public void setFinished(Integer finished) {
    this.finished = finished;
  }

  /**
   * Reset static time value to 0.
   */
  public static void resetTime() {
    time = 0;
  }

  /**
   * Gets the NodeVisitInfo static time attribute. 
   * @return
   */
  public static int getTime() {
    return time;
  }

  /**
   * Increments static time value by 1 and returns it.
   * @return Incremented static time.
   */
  public static int incrementTime() {
    return ++time;
  }
}
