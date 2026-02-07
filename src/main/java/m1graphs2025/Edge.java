package m1graphs2025;

import java.util.Objects;

/**
 * A class Edge, codes edges as objects. 
 * This will allow for physically disposing of the set E of edges of a graph G = (V, E).
 * Class Edge implements interface Comparable<Edge> in order to be able to enumerate the edges in a predictable order.
 * The edges are ordered first by source node number,
 * then by target node number in case of source node equality,
 * and then by increasing weight in case of source and target node equality.
 * An example of ordering can be shown as (1, 2), (1, 4), (3, 6), (4, 2), (4, 3), (4, 5), (4, 8), (6, 4), (6, 7), (7, 3) and finally (8, 7).
 */
public class Edge implements Comparable<Edge> {
  private Node from;
  private Node to;
  private Integer weight;

  /**
   * Constructor for the Edge class, takes a source node (from) and a target node (to) as parameters.
   * For successfully building an edge, both nodes from and to must be non-null and belong to the same graph.
   * When this is not the case, the construction is refused and an {@link IllegalArgumentException} is thrown.
   * @param from Source node.
   * @param to Target node.
   */
  public Edge(Node from, Node to) {
    if (from.getGraph() != to.getGraph())
      throw new IllegalArgumentException("Nodes must belong to the same graph");
    this.from = from;
    this.to = to;
    this.weight = null;
  }

  /**
   * Constructor for the Edge class, takes a source node id, a target node id and a graph holder as parameters.
   * If any of the node ids is not present in the graph, new nodes with those ids will be created.
   * More details can be found here {@link #Edge(Node, Node)}.
   * @param fromId Source node id.
   * @param toId Target node id.
   * @param graphHolder The graph holder. 
   */
  public Edge(int fromId, int toId, Graph graphHolder) {
    this.from = graphHolder.getNodeOrCreate(fromId);
    this.to = graphHolder.getNodeOrCreate(toId);
    this.weight = null;
  }

  /**
   * Constructor for the Edge class, takes a source node (from), a target node (to) and a weight as parameters.
   * More details can be found here {@link #Edge(Node, Node)}.
   * @param from Source node.
   * @param to Target node.
   * @param weight Edge weight, restricted to integers, 
   * but values 0 or negative are allowed. 
   * A null value for the weight indicates that it is not weighted.
   */
  public Edge(Node from, Node to, int weight) {
    if (from.getGraph() != to.getGraph())
      throw new IllegalArgumentException("Nodes must belong to the same graph");
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  /**
   * Constructor for the Edge class, takes a source node id, a target node id, graph holder and a weight as parameters.
   * More details can be found here {@link #Edge(Node, Node)}.
   * @param fromId Source node id.
   * @param toId Target node id.
   * @param graphHolder The graph holder. 
   */
  public Edge(int fromId, int toId, Graph graphHolder, int weight) {
    if (graphHolder.getNode(fromId) == null || graphHolder.getNode(toId) == null)
      throw new IllegalArgumentException("Nodes must belong to the same graph");
    this.from = graphHolder.getNode(fromId);
    this.to = graphHolder.getNode(toId);
    this.weight = weight;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Edge))
      return false;
    return to == ((Edge) obj).to &&
        from == ((Edge) obj).from &&
        weight == ((Edge) obj).weight;
  }

  @Override
  public int hashCode() {
    return Objects.hash(from.getId(), to.getId());
  }

  @Override
  public int compareTo(Edge o) {
    int cmp;
    if (this.weight == null && o.weight == null) return 0;
    if (this.weight == null) return 1;
    if (o.weight == null) return -1;
    if ((cmp = Integer.compare(from.getId(), o.from.getId())) != 0) return cmp;
    if ((cmp = Integer.compare(to.getId(), o.to.getId())) != 0) return cmp;
    return Integer.compare(this.weight, o.weight);
  }

  /* API */

  /**
   * Gets the source node of a directed edge.
   * @return The source node of a directed edge.
   */
  public Node from() {
    return from;
  }

  /**
   * Gets the target node of a directed edge.
   * @return The target node of a directed edge.
   */
  public Node to() {
    return to;
  }

  /**
   * Gets the symmetric of an edge as a new Edge instance.
   * @return The symmetric of an edge as a new Edge instance.
   */
  public Edge getSymmetric() {
    return new Edge(to, from);
  }

  /**
   * Know if edge this is a self-loop or not.
   * @return True if it's an edge, otherwise false.
   */
  public boolean isSelfLoop() {
    return to.equals(from);
  }

  /**
   * Know if edge this is a multi-edge or not.
   * @return True if it's a multi-edge, otherwise false.
   */
  public boolean isMultiEdge() {
    for (Node n : from.getSuccessorsMulti()) {
      if (to.getId() == n.getId())
        return true;
    }
    return false;
  }

  /**
   * Know if edge this weighted or not.
   * @return True if it's weighted, otherwise false.
   */
  public boolean isWeighted() {
    return weight != null;
  }

  /**
   * Gets the weight of edge this.
   * @return The weight of edge this, or null in the unweighted case.
   */
  public Integer getWeight() {
    return weight;
  }

  /**
   * Sets the weight of edge this.
   */
  public void setWeight(int weight) {
    this.weight = weight;
  }

}
