package m1graphs2025;

import java.util.List;

/**
 * A class Node, codes a node of a graph.
 */
public class Node implements Comparable<Node> {
  private int id;
  private String name;
  private Graph graphHolder;

  /**
   * Constructor for the class Node, takes both the id and the graph holder as parameters. 
   * The node will be automatically assigned a default name "node{id}".
   * @param id The node number, which must act as an identifier.
   * @param graphHolder Is a reference to the graph to which the node belongs.
   */
  public Node(int id, Graph graphHolder) {
    this.id = id;
    this.graphHolder = graphHolder;
    this.name = String.valueOf(id);
  }

  /**
   * Constructor for the class Node, takes the id, the graph holder and a name as parameters. 
   * @param id The node number, which must act as an identifier.
   * @param graphHolder Is a reference to the graph to which the node belongs.
   * @param name The name of the node.
   */
  public Node(int id, Graph graphHolder, String name) {
    this.id = id;
    this.graphHolder = graphHolder;
    this.name = name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Node))
      return false;
    return this.id == ((Node) obj).id;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(id);
  }

  @Override
  public int compareTo(Node n) {
    return Integer.compare(this.id, n.id);
  }

  /* API */

  /**
   * Gets the node id.
   * @return This.id
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the node graph holder.
   * @return This.graphHolder
   */
  public Graph getGraph() {
    return graphHolder;
  }

  /**
   * Gets the node name.
   * @return This.name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the node name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets a list without duplicates of the successors of node this. 
   * “Without duplicates” means that each successor appears uniquely in the list returned, 
   * even in the case of a multigraph.
   * @return A ist without duplicates of the successors of node this.
   */
  public List<Node> getSuccessors() {
    return graphHolder.getSuccessors(this);
  }

  /**
   * Gets a list with possible duplicates of the successors of node this. 
   * “With possible duplicates” means that in the case of a multigraph, 
   * each successor appears as many times as it is joined by an edge to node this.
   * @return A ist with possible duplicates of the successors of node this.
   */
  public List<Node> getSuccessorsMulti() {
    return graphHolder.getSuccessorsMulti(this);
  }

  /**
   * Know wheter node u is adjacent to node this.
   * @param u Target node.
   * @return True if adjacent, false otherwise.
   */
  public boolean adjacent(Node u) {
    return graphHolder.adjacent(this, u);
  }

  /**
   * Know wheter node with specified id is adjacent to node this.
   * @param id Target node id.
   * @return True if adjacent, false otherwise.
   */
  public boolean adjacent(int id) {
    return graphHolder.adjacent(this.id, id);
  }

  /**
   * Know the in-degree of node this.
   * @return In-degree of node this.
   */
  public int inDegree() {
    return graphHolder.inDegree(this);
  }

  /**
   * Know the out-degree of node this.
   * @return Out-degree of node this.
   */
  public int outDegree() {
    return graphHolder.outDegree(this);
  }

  /**
   * Know the degree of node this.
   * @return Degree of node this.
   */
  public int degree() {
    return graphHolder.degree(this);
  }

  /**
   * Gets the list of all edges leaving node this.
   * Notice that in the undirected case, all incident edges to a node are both in and out edges.
   * @return A ist of all edges leaving node this.
   */
  public List<Edge> getOutEdges() {
    return graphHolder.getOutEdges(this);
  }

  /**
   * Gets the list of all edges entering node this.
   * Notice that in the undirected case, all incident edges to a node are both in and out edges.
   * @return A ist of all edges entering node this.
   */
  public List<Edge> getInEdges() {
    return graphHolder.getInEdges(this);
  }

  /**
   * Gets the list of all edges incident to node this. 
   * This is the union of the out and in edges.
   * Notice that in the undirected case, all incident edges to a node are both in and out edges.
   * @return A ist of all edges incident to node this.
   */
  public List<Edge> getIncidentEdges() {
    return graphHolder.getIncidentEdges(this);
  }

  /**
   * Gets the list of all edges going from node this to node u.
   * @param u Target node.
   * @return A ist of all edges going from node this to node u.
   */
  public List<Edge> getEdgesTo(Node u) {
    return graphHolder.getEdges(this, u);
  }

  /**
   * Gets the list of all edges going from node this to node with specified id.
   * @param id Target node id.
   * @return A ist of all edges going from node this to node u.
   */
  public List<Edge> getEdgesTo(int id) {
    return graphHolder.getEdges(this.id, id);
  }
}
