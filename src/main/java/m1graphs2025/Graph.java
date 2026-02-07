package m1graphs2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;

/**
 * A class Graph, codes a directed graph.
 * The graph structure is coded as adjacent edge lists, meaning that the list
 * of its out edges is mapped to each of the graph nodes.
 */
public class Graph {
  private Map<Node, List<Edge>> ael; // TreeMap<Node, List<Edge>>
  protected int nodeCount;
  protected int edgeCount;
  private static final String PRINT_PATH = "target/output/printOutput.txt";

  /**
   * Constructor for the class Graph, constructs an empty graph with an adjacent
   * edge lists structure.
   */
  public Graph() {
    this.ael = new TreeMap<>(Comparator.comparingInt(Node::getId));
    this.nodeCount = 0;
  }

  /**
   * Constructor for the class Graph, takes a Successor Arrays as parameter.
   * Only unweighted graphs can be built this way.
   * For example: Graph g = new Graph(2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0,
   * 3, 0, 7, 0);
   * 
   * @param sa Successor Array (see Lectures for an accurate specification of the
   *           SA formalism).
   */
  public Graph(int... sa) {
    ael = new TreeMap<>(Comparator.comparingInt(Node::getId));
    int sourceNodeId = 1;

    Node n = getNodeOrCreate(sourceNodeId);
    for (int saIdx = 0; saIdx < sa.length - 1; saIdx++) {
      if (sa[saIdx] == 0) {
        n = getNodeOrCreate(++sourceNodeId);
      } else {
        Node u = getNodeOrCreate(sa[saIdx]);
        ael.get(n).add(new Edge(n, u));
        edgeCount++;
      }
    }
  }

  /**
   * Gets the graph Adjacency Edge List.
   * 
   * @return This.ael
   */
  public Map<Node, List<Edge>> getAel() {
    return ael;
  }

  /**
   * Sets the edge count.
   * @param edgeCount
   */
  public void setEdgeCount(int edgeCount) {
    this.edgeCount = edgeCount;
  }

  /**
   * Same behavior as {@link #getNode()} but creates a node if it dosen't exist.
   * 
   * @return An existing node of the graph or a new node that belongs to the graph
   *         otherwise.
   */
  public Node getNodeOrCreate(int id) {
    Node n = getNode(id);
    if (n == null) {
      n = new Node(id, this);
      addNode(n);
    }
    return n;
  }

  public Node getNodeOrCreate(String name) {
    Node n = null;
    for (Node node : ael.keySet()) {
      if (node.getName().equals(name)) {
        n = node;
      }
    }
    if (n == null) {
      n = new Node(nodeCount+1, this, name);
      addNode(n);
    }
    return n;
  }

  /**
   * Prints the graph Adjacency Edge List into
   * {@code target/output/printOutput.txt}.
   */
  public void printAel() {
    StringBuilder sb = new StringBuilder("\nAel representation:\n");
    for (Map.Entry<Node, List<Edge>> entry : ael.entrySet()) {
      Node n = entry.getKey();
      List<Edge> edges = entry.getValue();

      sb.append(n.getId()).append(": ");
      for (Edge e : edges) {
        sb.append("( ")
            .append(e.from().getId())
            .append(", ")
            .append(e.to().getId())
            .append(" ) ");
      }
      sb.append("\n");
    }

    try (FileWriter writer = new FileWriter(PRINT_PATH, true)) {
      writer.write(sb.toString());
    } catch (IOException e) {
      throw new GraphExceptions("Failed to write print output");
    }
  }

  /**
   * Prints the graph Successor Array into {@code target/output/printOutput.txt}.
   */
  public void printSuccessorArray() {
    StringBuilder sb = new StringBuilder("\nSuccessor Array:\n");
    int[] sa = toSuccessorArray();
    sb.append("[ ");
    for (int el : sa)
      sb.append(el).append(" ");
    sb.append("]\n");

    try (FileWriter writer = new FileWriter(PRINT_PATH, true)) {
      writer.write(sb.toString());
    } catch (IOException e) {
      throw new GraphExceptions("Failed to write print output");
    }
  }

  /**
   * Prints the graph Adjacency Matrix into {@code target/output/printOutput.txt}.
   */
  public void printAdjMatrix() {
    int[][] am = toAdjMatrix();
    StringBuilder sb = new StringBuilder("\nAdjacency Matrix:\n");

    for (int i = 0; i < am.length; i++) {
      for (int j = 0; j < am[i].length; j++) {
        sb.append(am[i][j]).append(" ");
      }
      sb.append("\n");
    }

    try (FileWriter writer = new FileWriter(PRINT_PATH, true)) {
      writer.write(sb.toString());
    } catch (IOException e) {
      throw new GraphExceptions("Failed to write adjacency matrix output");
    }
  }

  /* API */

  /** Nodes **/

  /**
   * Know the number of nodes in the graph.
   * 
   * @return the number of nodes.
   */
  public int nbNodes() {
    return nodeCount;
  }

  /**
   * Know if node n has an id that is already used in this graph.
   * 
   * @param n Target node.
   * @return True if the node exists, false otherwise.
   */
  public boolean usesNode(Node n) {
    return ael.containsKey(n);
  }

  /**
   * Know if node id is already used in this graph.
   * 
   * @param id Target node id.
   * @return True if the node id exists, false otherwise.
   */
  public boolean usesNode(int id) {
    for (Node v : ael.keySet()) {
      if (id == v.getId())
        return true;
    }
    return false;
  }

  /**
   * Know if node n is a node of this graph,
   * meaning its id is used and the graph holder of n is this.
   * No overloads.
   * 
   * @param n Target node.
   * @return True if it is a node of this graph, false otherwise.
   */
  public boolean holdsNode(Node n) {
    for (Node v : ael.keySet()) {
      if (n.equals(v))
        return true;
    }
    return false;
  }

  /**
   * Gets the node instance held by this graph whose number is id.
   * 
   * @param id Target node id.
   * @return The node instance held by graph whose number is id,
   *         or null in case this does not have a node with number id.
   */
  public Node getNode(int id) {
    for (Node v : ael.keySet()) {
      if (id == v.getId())
        return v;
    }
    return null;
  }

  /**
   * Add a node to the graph.
   * 
   * @param n Target node.
   * @return True if node was added, false otherwise.
   */
  public boolean addNode(Node n) {
    if (n == null || usesNode(n))
      return false;
    ael.put(n, new java.util.ArrayList<>());
    nodeCount++;
    return true;
  }

  /**
   * Add a node to the graph.
   * 
   * @param id Target node id.
   * @return True if node was added, false otherwise.
   */
  public boolean addNode(int id) {
    if (getNode(id) != null)
      return false;
    return addNode(new Node(id, this));
  }

  /**
   * Add a node to the graph.
   * 
   * @param id   Target node id.
   * @param name Target node name.
   * @return True if node was added, false otherwise.
   */
  public boolean addNode(int id, String name) {
    if (getNode(id) != null)
      return false;
    return addNode(new Node(id, this, name));
  }

  /**
   * Remove a node from the graph, if it exists.
   * This consequently removes all edges incident to that node.
   * 
   * @param n Target node.
   * @return True if node was removed, false otherwise.
   */
  public boolean removeNode(Node n) {
    if (!usesNode(n))
      return false;
    // Remove all outgoing edges from node
    List<Edge> outgoingEdges = ael.remove(n);
    if (outgoingEdges != null)
      edgeCount -= outgoingEdges.size();
    // Remove all incoming edges from node
    for (List<Edge> edges : ael.values()) {
      Iterator<Edge> it = edges.iterator();
      while (it.hasNext()) {
        if (it.next().to().equals(n)) {
          it.remove();
          edgeCount--;
        }
      }
    }
    return true;
  }

  /**
   * Remove a node from the graph, if it exists.
   * This consequently removes all edges incident to that node.
   * 
   * @param id Target node id.
   * @return True if node was removed, false otherwise.
   */
  public boolean removeNode(int id) {
    if (getNode(id) == null) return false;
    return removeNode(getNode(id));
  }

  /**
   * Gets a list of all the nodes of the graph.
   * 
   * @return A list of all the nodes of the graph.
   */
  public List<Node> getAllNodes() {
    return new ArrayList<>(ael.keySet());
  }

  /**
   * Know the largest id used by the graph.
   * 
   * @return The largest id used by the graph.
   */
  public int largestNodeId() {
    return ((TreeMap<Node, List<Edge>>) ael).lastKey().getId();
  }

  /**
   * Know the smallest id used by the graph.
   * 
   * @return The smallest id used by the graph.
   */
  public int smallestNodeId() {
    return ((TreeMap<Node, List<Edge>>) ael).firstKey().getId();
  }

  /**
   * Gets a list without duplicates of the successors of node n.
   * “Without duplicates” means that each successor appears uniquely in the list
   * returned,
   * even in the case of a multigraph.
   * 
   * @param n Source node.
   * @return A ist without duplicates of the successors of node n.
   */
  public List<Node> getSuccessors(Node n) {
    Set<Node> successors = new LinkedHashSet<>();
    for (Edge edge : ael.get(n)) {
      successors.add(edge.to());
    }
    return new ArrayList<>(successors);
  }

  /**
   * Gets a list without duplicates of the successors of node with specified id.
   * “Without duplicates” means that each successor appears uniquely in the list
   * returned,
   * even in the case of a multigraph.
   * 
   * @param id Source node id.
   * @return A ist without duplicates of the successors of node with specified id.
   */
  public List<Node> getSuccessors(int id) {
    if (getNode(id) == null) return new ArrayList<>();
    return getSuccessors(getNode(id));
  }

  /**
   * Gets a list with possible duplicates of the successors of node n.
   * “With possible duplicates” means that in the case of a multigraph,
   * each successor appears as many times as it is joined by an edge to node n.
   * 
   * @param n Srouce node.
   * @return A ist with possible duplicates of the successors of node n.
   */
  public List<Node> getSuccessorsMulti(Node n) {
    List<Node> successors = new ArrayList<>();
    for (Edge edge : ael.get(n)) {
      successors.add(edge.to());
    }
    return successors;
  }

  /**
   * Gets a list with possible duplicates of the successors of node with specified
   * id.
   * “With possible duplicates” means that in the case of a multigraph,
   * each successor appears as many times as it is joined by an edge to node with
   * specified id.
   * 
   * @param id Srouce node id.
   * @return A ist with possible duplicates of the successors of node with
   *         specified id.
   */
  public List<Node> getSuccessorsMulti(int id) {
    if (getNode(id) == null) return new ArrayList<>();
    return getSuccessorsMulti(getNode(id));
  }

  /**
   * Know wheter node u is adjacent to node n.
   * 
   * @param n Source node.
   * @param u Target node.
   * @return True if adjacent, false otherwise.
   */
  public boolean adjacent(Node n, Node u) {
    for (Edge edge : ael.get(n)) {
      if (edge.to().equals(u))
        return true;
    }
    return false;
  }

  /**
   * Know wheter node with specified id uId u is adjacent to node with specified
   * id nId.
   * 
   * @param nId Source node id.
   * @param uId Target node id.
   * @return True if adjacent, false otherwise.
   */
  public boolean adjacent(int nId, int uId) {
    if (getNode(nId) == null || getNode(uId) == null) return false;
    return adjacent(getNode(nId), getNode(uId));
  }

  /**
   * Know the in-degree of node n.
   * 
   * @return In-degree of node n.
   */
  public int inDegree(Node n) {
    int inDegree = 0;
    for (List<Edge> edges : ael.values()) {
      for (Edge edge : edges) {
        if (edge.to().equals(n))
          inDegree++;
      }
    }
    return inDegree;
  }

  /**
   * Know the in-degree of node with specified id.
   * 
   * @return In-degree of node with specified id.
   */
  public int inDegree(int id) {
    if (getNode(id) == null) return 0;
    return inDegree(getNode(id));
  }

  /**
   * Know the out-degree of node n.
   * 
   * @return Out-degree of node n.
   */
  public int outDegree(Node n) {
    return ael.get(n).size();
  }

  /**
   * Know the out-degree of node with specified id.
   * 
   * @return Out-degree of node with specified id.
   */
  public int outDegree(int id) {
    if (getNode(id) == null) return 0;
    return outDegree(getNode(id));
  }

  /**
   * Know the degree of node n.
   * 
   * @return Degree of node n.
   */
  public int degree(Node n) {
    return inDegree(n) + outDegree(n);
  }

  /**
   * Know the degree of node with specified id.
   * 
   * @return Degree of node with specified id.
   */
  public int degree(int id) {
    Node n = getNode(id);
    if (n == null) return 0;
    return degree(n);
  }

  /** Edges **/

  /**
   * Know the number of edges in the graph.
   * 
   * @return The number of edges in the graph.
   */
  public int nbEdges() {
    return edgeCount;
  }

  /**
   * Know whether an edge exists in this graph between nodes u and v.
   * 
   * @param u Source node.
   * @param v Target node.
   * @return True if an edge exists between nodes u and v, false otherwise.
   */
  public boolean existsEdge(Node u, Node v) {
    for (Edge edge : ael.get(u)) {
      if (edge.to().equals(v))
        return true;
    }
    return false;
  }

  /**
   * Know whether an edge exists in this graph between nodes with specified id uId
   * and vId.
   * 
   * @param uId Source node id.
   * @param vId Target node id.
   * @return True if an edge exists between nodes u and v, false otherwise.
   */
  public boolean existsEdge(int uId, int vid) {
    if (getNode(uId) == null || getNode(vid) == null) return false;
    return existsEdge(getNode(uId), getNode(vid));
  }

  /**
   * Know whether an reference edge exists in this graph.
   * 
   * @param e Edge reference.
   * @return True if an edge exists, false otherwise.
   */
  public boolean existsEdge(Edge e) {
    return existsEdge(e.from(), e.to());
  }

  /**
   * Know if edge (u, v) is a multi-edge,
   * i.e. there is at least one other edge from u to v in graph this.
   * 
   * @param u Source node.
   * @param v Target node.
   * @return True if there is at least one other edge from u to v in graph this,
   *         false otherwise.
   */
  public boolean isMultiEdge(Node u, Node v) {
    int eCount = 0;
    for (Edge edge : ael.get(u)) {
      if (edge.from().equals(u) && edge.to().equals(v))
        eCount++;
    }
    return eCount > 1;
  }

  /**
   * Know if edge (u, v) is a multi-edge,
   * i.e. there is at least one other edge from node with id uId to node with id
   * vId in graph this.
   * 
   * @param uId Source node id.
   * @param vId Target node id.
   * @return True if there is at least one other edge from u to v in graph this,
   *         false otherwise.
   */
  public boolean isMultiEdge(int uId, int vid) {
    if (getNode(uId) == null || getNode(vid) == null) return false;
    return isMultiEdge(getNode(uId), getNode(vid));
  }

  /**
   * Know if edge e is a multi-edge.
   * 
   * @param e Edge reference.
   * @return True if there is at least one other edge from e.from() to e.to() in
   *         graph this, false otherwise.
   */
  public boolean isMultiEdge(Edge e) {
    return isMultiEdge(e.from(), e.to());
  }

  /**
   * Adds an edge from the node from towards the node to.
   * These nodes are created in case they don’t already belong to the graph.
   * The method is void instead of boolean.
   * 
   * @param from Source node.
   * @param to   Target node.
   */
  public void addEdge(Node from, Node to) {
    if (!ael.containsKey(from) || !ael.containsKey(to))
      return;
    ael.get(from).add(new Edge(from, to));
    edgeCount++;
  }

  /**
   * Adds an edge from the node with id fromId towards the node with id toId.
   * These nodes are created in case they don’t already belong to the graph.
   * 
   * @param fromId Source node id.
   * @param toId   Target node id.
   */
  public void addEdge(int fromId, int toId) {
    addEdge(getNodeOrCreate(fromId), getNodeOrCreate(toId));
  }

  /**
   * Adds an edge from the node from towards the node to with specified weight.
   * These nodes are created in case they don’t already belong to the graph.
   * 
   * @param from   Source node.
   * @param to     Target node.
   * @param weight Edge weigth.
   */
  public void addEdge(Node from, Node to, int weight) {
    if (!ael.containsKey(from) || !ael.containsKey(to))
      return;
    ael.get(from).add(new Edge(from, to, weight));
    edgeCount++;
  }

  /**
   * Adds an edge from the node with id fromId towards the node with id toId with
   * specified weight.
   * These nodes are created in case they don’t already belong to the graph.
   * 
   * @param fromId Source node id.
   * @param toId   Target node id.
   * @param weight Edge weight.
   */
  public void addEdge(int fromId, int toId, int weight) {
    if (getNode(fromId) == null || getNode(toId) == null) return;
    addEdge(getNode(fromId), getNode(toId), weight);
  }

  /**
   * Adds an edge to the graph if both the source node and the target node belong
   * to the graph.
   * 
   * @param edge Edge reference.
   */
  public void addEdge(Edge edge) {
    if (!ael.containsKey(edge.from()) || !ael.containsKey(edge.to()))
      return;
    ael.get(edge.from()).add(edge);
    edgeCount++;
  }

  /**
   * Removes an edge from the node from towards the node to if it exists.
   * 
   * @param from Source node.
   * @param to   Target node.
   * @return True if edge was removed, false otherwise.
   */
  public boolean removeEdge(Node from, Node to) {
    if (!existsEdge(from, to))
      return false;
    boolean isRemoved = ael.get(from).removeIf(edge -> edge.to().equals(to));
    if (isRemoved)
      edgeCount--;
    return isRemoved;
  }

  /**
   * Removes an edge from the node with id fromId towards the node with id toId if
   * it exists.
   * 
   * @param fromId Source node id.
   * @param toId   Target node id.
   * @return True if edge was removed, false otherwise.
   */
  public boolean removeEdge(int fromId, int toId) {
    Node n = getNode(fromId);
    Node u = getNode(toId);
    if (n == null || u == null)
      return false;
    return removeEdge(n, u);
  }

  /**
   * Removes an edge from the node from towards the node to that has the specified
   * weight if it exists.
   * All the edges with the specified weight will be removed in case of
   * multigraph.
   * 
   * @param from   Source node.
   * @param to     Target node.
   * @param weight Edge weigth.
   * @return True if edge was removed, false otherwise.
   */
  public boolean removeEdge(Node from, Node to, int weight) {
    if (!existsEdge(from, to))
      return false;
    boolean isRemoved = ael.get(from).removeIf(edge -> edge.to().equals(to) && edge.getWeight().equals(weight));
    if (isRemoved)
      edgeCount--;
    return isRemoved;
  }

  /**
   * Removes an edge from the node with id fromId towards the node with id toId
   * that has a specified weight if it exists.
   * All the edges with the specified weight will be removed in case of
   * multigraph.
   * 
   * @param fromId Source node id.
   * @param toId   Target node id.
   * @param weight Edge weight.
   * @return True if edge was removed, false otherwise.
   */
  public boolean removeEdge(int fromId, int toId, int weight) {
    if (getNode(fromId) == null || getNode(toId) == null) return false;
    return removeEdge(getNode(fromId), getNode(toId), weight);
  }

  /**
   * Removes an edge remove the graph if it exists.
   * 
   * @param edge Edge reference.
   * @return True if edge was removed, false otherwise.
   */
  public boolean removeEdge(Edge edge) {
    if (edge.isWeighted())
      return removeEdge(edge.from(), edge.to(), edge.getWeight());
    return removeEdge(edge.from(), edge.to());
  }

  /**
   * Gets the list of all edges leaving node n.
   * 
   * @param n Source node.
   * @return A list of all edges leaving node n.
   */
  public List<Edge> getOutEdges(Node n) {
    return ael.get(n);
  }

  /**
   * Gets the list of all edges leaving node with specified id.
   * 
   * @param id Source node id.
   * @return A list of all edges leaving node with specified id.
   */
  public List<Edge> getOutEdges(int id) {
    if (getNode(id) == null) return new ArrayList<>();
    return getOutEdges(getNode(id));
  }

  /**
   * Gets the list of all edges entering node n.
   * 
   * @param n Source node.
   * @return A list of all edges entering node n.
   */
  public List<Edge> getInEdges(Node n) {
    List<Edge> inEdges = new ArrayList<>();
    for (List<Edge> edges : ael.values()) {
      for (Edge edge : edges) {
        if (edge.to().equals(n))
          inEdges.add(edge);
      }
    }
    return inEdges;
  }

  /**
   * Gets the list of all edges entering node with specified id.
   * 
   * @param id Source node id.
   * @return A list of all edges entering node with specified id.
   */
  public List<Edge> getInEdges(int id) {
    if (getNode(id) == null) return new ArrayList<>();
    return getInEdges(getNode(id));
  }

  /**
   * Gets the list of all edges incident to node n.
   * This is the union of the out and in edges.
   * 
   * @return A ist of all edges incident to node n.
   */
  public List<Edge> getIncidentEdges(Node n) {
    List<Edge> incidentEdges = new ArrayList<>();
    incidentEdges.addAll(getOutEdges(n));
    incidentEdges.addAll(getInEdges(n));
    return incidentEdges;
  }

  /**
   * Gets the list of all edges incident to node with specified id.
   * This is the union of the out and in edges.
   * 
   * @return A ist of all edges incident to node with specified id.
   */
  public List<Edge> getIncidentEdges(int id) {
    if (getNode(id) == null) return new ArrayList<>();
    return getIncidentEdges(getNode(id));
  }

  /**
   * Gets the list of all edges going from node u to node v.
   * 
   * @param u Source node.
   * @param v Target node.
   * @return A list of all edges going from node u to node v.
   */
  public List<Edge> getEdges(Node u, Node v) {
    List<Edge> edges = new ArrayList<>();
    for (Edge edge : ael.get(u)) {
      if (edge.to().equals(v))
        edges.add(edge);
    }
    return edges;
  }

  /**
   * Gets the list of all edges going from node with id uId to node with id vId.
   * 
   * @param u Source node id.
   * @param v Target node id.
   * @return A list of all edges going from node with id uId to node with id vId.
   */
  public List<Edge> getEdges(int uId, int vid) {
    if (getNode(uId) == null || getNode(vid) == null) return new ArrayList<>();
    return getEdges(getNode(uId), getNode(vid));
  }

  /**
   * Gets the list of all the edges of the graph.
   * 
   * @return A list of all the edges of the graph.
   */
  public List<Edge> getAllEdges() {
    List<Edge> allEdges = new ArrayList<>();
    for (List<Edge> edges : ael.values()) {
      allEdges.addAll(edges);
    }
    return allEdges;
  }

  /** Graph representation & transformation **/

  /**
   * Obtain a representation of the graph in the SA (successor array) formalism.
   * 
   * @return A representation of the graph in the SA (successor array) formalism.
   */
  public int[] toSuccessorArray() {
    int[] sa = new int[edgeCount + nodeCount];
    int saIdx = 0;
    for (List<Edge> edges : ael.values()) {
      for (int i = 0; i < edges.size(); i++) {
        sa[saIdx++] = edges.get(i).to().getId();
      }
      sa[saIdx++] = 0;
    }
    return sa;
  }

  /**
   * Obtain a representation of the graph as an adjacency matrix.
   * Multigraphs are allowed, so the elements in the matrix may be greater than 1,
   * indicating the number of edges between any two nodes.
   * Also graphs with self-loops are allowed, thus allowing nonzero diagonal
   * elements.
   * 
   * @return A representation of the graph as an adjacency matrix.
   */
  public int[][] toAdjMatrix() {
    int[][] am = new int[nodeCount][nodeCount];
    for (List<Edge> edges : ael.values()) {
      for (Edge edge : edges) {
        am[edge.from().getId() - 1][edge.to().getId() - 1]++;
      }
    }
    return am;
  }

  /**
   * Computes in a new graph the reverse (G^-1) of the graph.
   * 
   * @return The reverse (G^-1) of the graph in a new graph.
   */
  public Graph getReverse() {
    Graph reverseGraph = new Graph();

    for (Node n : getAllNodes()) {
      reverseGraph.addNode(n.getId());
    }

    for (Map.Entry<Node, List<Edge>> entry : ael.entrySet()) {
      for (Edge edge : entry.getValue()) {
        Node from = reverseGraph.getNode(edge.to().getId());
        Node to = reverseGraph.getNode(edge.from().getId());
        reverseGraph.addEdge(from, to);
      }
    }
    return reverseGraph;
  }

  /**
   * Computes in a new graph the transitive closure of the graph.
   * 
   * @return The transitive closure of the graph in a new graph.
   */
  public Graph getTransitiveClosure() {
    Graph closure = new Graph();

    for (Node n : getAllNodes()) {
      closure.addNode(n.getId());
    }

    for (Node n : getAllNodes()) {
      List<Node> reachable = getDFS(n);
      for (Node u : reachable) {
        if (n != u) {
          Node from = closure.getNode(n.getId());
          Node to = closure.getNode(u.getId());
          closure.addEdge(from, to);
        }
      }
    }
    return closure;
  }

  /**
   * Know if this is a multi-graph (i.e. it has at least one multi-edge) or not.
   * 
   * @return True if it is a multi-graph, false otherwise.
   */
  public boolean isMultiGraph() {
    for (List<Edge> edges : ael.values()) {
      for (Edge edge : edges) {
        if (edge.isMultiEdge())
          return true;
      }
    }
    return false;
  }

  /**
   * Know if this is a simple graph (i.e. it has neither self-loop nor multi-edge)
   * or not.
   * 
   * @return
   */
  public boolean isSimpleGraph() {
    return !(isMultiGraph() && hasSelfLoops());
  }

  /**
   * Know if this is has self-loops or not.
   * 
   * @return True if it has self-loops, false otherwise.
   */
  public boolean hasSelfLoops() {
    for (List<Edge> edges : ael.values()) {
      for (Edge edge : edges) {
        if (edge.isSelfLoop())
          return true;
      }
    }
    return false;
  }

  /**
   * Transform the (possibly) multi-graph this into a simple one, by removing its
   * self-loops and multi-edges.
   * 
   * @return A simple graph from this in a new graph.
   */
  public Graph toSimpleGraph() {
    Graph simpleGraph = new Graph();

    for (Node n : getAllNodes()) {
      simpleGraph.addNode(n.getId());
    }

    for (Node u : getAllNodes()) {
      Set<Integer> targets = new HashSet<>();

      for (Edge e : ael.getOrDefault(u, List.of())) {
        int fromId = e.from().getId();
        int toId = e.to().getId();

        if (fromId == toId || !targets.add(toId))
          continue;

        simpleGraph.addEdge(
            simpleGraph.getNode(fromId),
            simpleGraph.getNode(toId));
      }
    }

    return simpleGraph;
  }

  /**
   * Gets a copy of this graph into a new graph.
   * 
   * @return A copy of graph this.
   */
  public Graph copy() {
    Graph g = new Graph();

    for (Node n : getAllNodes()) {
      g.addNode(n.getId(), n.getName());
    }

    for (Node u : getAllNodes()) {
      for (Edge e : ael.getOrDefault(u, List.of())) {
        Node from = g.getNode(e.from().getId());
        Node to = g.getNode(e.to().getId());

        if (e.isWeighted()) {
          g.addEdge(from, to, e.getWeight());
        } else {
          g.addEdge(from, to);
        }
      }
    }

    return g;
  }

  /* Graph traversal */

  /**
   * Gets a Depth-First Search traversal of the graph.
   * 
   * @return A Depth-First Search traversal of the graph.
   */
  public List<Node> getDFS() {
    Node s = ((TreeMap<Node, List<Edge>>) ael).firstKey();
    return getDFS(s);
  }

  /**
   * Gets a Depth-First Search traversal of the graph starting from node s.
   * 
   * @param s The starting node.
   * @return A Depth-First Search traversal of the graph starting from node s.
   */
  public List<Node> getDFS(Node s) {
    List<Node> order = new ArrayList<>();
    HashSet<Node> visited = new HashSet<>();
    ArrayDeque<Node> q = new ArrayDeque<>();

    visited.add(s);
    q.push(s);

    while (!q.isEmpty()) {
      Node n = q.pop();
      order.add(n);

      for (Edge e : n.getIncidentEdges()) {
        if (visited.add(e.to()))
          q.push(e.to()); // Returns true if v is not visited
      }
    }

    return order;
  }

  /**
   * Gets a Depth-First Search traversal of the graph starting from node with
   * specified id.
   * 
   * @param s Starting node id.
   * @return A Depth-First Search traversal of the graph starting from node with
   *         specified id.
   */
  public List<Node> getDFS(int id) {
    if (getNode(id) == null) return new ArrayList<>();
    return getDFS(getNode(id));
  }

  /**
   * Gets a Breadth-First Search traversal of the graph.
   * 
   * @return A Breadth-First Search traversal of the graph.
   */
  public List<Node> getBFS() {
    Node s = ((TreeMap<Node, List<Edge>>) ael).firstKey();
    return getBFS(s);
  }

  /**
   * Gets a Breadth-First Search traversal of the graph starting from node s.
   * 
   * @param s The starting node.
   * @return A Breadth-First Search traversal of the graph starting from node s.
   */
  public List<Node> getBFS(Node s) {
    List<Node> order = new ArrayList<>();
    HashSet<Node> visited = new HashSet<>();
    ArrayDeque<Node> q = new ArrayDeque<>();

    visited.add(s);
    q.add(s);

    while (!q.isEmpty()) {
      Node n = q.remove();
      order.add(n);

      for (Edge e : n.getIncidentEdges()) {
        if (visited.add(e.to()))
          q.add(e.to());
      }
    }
    return order;
  }

  /**
   * Gets a Breadth-First Search traversal of the graph starting from node with
   * specified id.
   * 
   * @param s Starting node id.
   * @return A Breadth-First Search traversal of the graph starting from node with
   *         specified id.
   */
  public List<Node> getBFS(int id) {
    if (getNode(id) == null) return new ArrayList<>();
    return getBFS(getNode(id));
  }

  /**
   * Gets a Depth-First Search traversal of the graph and traversal properties
   * like:
   * The Characterization of the nodes by their colour (white, gray, black ),
   * Their predecessor in the traversal,
   * Their discovery and finish timestamps,
   * And the Characterization of the edges by their type (tree, backward, forward
   * or cross edge).
   * 
   * @param nodeVisit An empty map that contains a {@link NodeVisitInfo} instance
   *                  for each Node of the graph,
   *                  It will be filled upon traversal.
   * @param edgeVisit An empty map that contains an {@link EdgeVisitType} for each
   *                  Node of the graph,
   *                  It will be filled upon traversal.
   * @return A Depth-First Search traversal of the graph.
   */
  public List<Node> getDFSWithVisitInfo(Map<Node, NodeVisitInfo> nodeVisit, Map<Edge, EdgeVisitType> edgeVisit) {
    Node s = ((TreeMap<Node, List<Edge>>) ael).firstKey();
    return getDFSWithVisitInfo(s, nodeVisit, edgeVisit);
  }

  /**
   * Gets a Depth-First Search traversal of the graph starting from node s and
   * traversal properties.
   * More details can be found here {@link #getDFSWithVisitInfo(Map<Node,
   * NodeVisitInfo> nodeVisit, Map<Edge, EdgeVisitType> edgeVisit)}.
   * 
   * @param s         The starting node.
   * @param nodeVisit An empty map that contains a {@link NodeVisitInfo} instance
   *                  for each Node of the graph,
   *                  It will be filled upon traversal.
   * @param edgeVisit An empty map that contains an {@link EdgeVisitType} for each
   *                  Node of the graph,
   *                  It will be filled upon traversal.
   * @return A Depth-First Search traversal of the graph starting from node s.
   */
  @SuppressWarnings("java:S3776") // ***I can use helper functions, but i don't know if i have the right to
  public List<Node> getDFSWithVisitInfo(Node s, Map<Node, NodeVisitInfo> nodeVisit,
      Map<Edge, EdgeVisitType> edgeVisit) {
    List<Node> order = new ArrayList<>();
    ArrayDeque<Node> q = new ArrayDeque<>();

    for (Node node : ael.keySet()) {
      nodeVisit.put(node, new NodeVisitInfo(NodeColour.WHITE, null, null, null));
    }

    NodeVisitInfo.resetTime();

    q.push(s);

    for (Node node : ael.keySet()) {
      if (nodeVisit.get(node).getColour() == NodeColour.WHITE) {
        if (nodeVisit.get(node).getColour() == NodeColour.WHITE && !q.contains(s)) {
          q.push(node);
        }
        while (!q.isEmpty()) {
          Node n = q.pop();
          order.add(n);
          NodeVisitInfo nVisitInfo = nodeVisit.get(n);
          nVisitInfo.setDiscovery(NodeVisitInfo.incrementTime());
          nVisitInfo.setColour(NodeColour.GRAY);

          for (Edge e : n.getIncidentEdges()) {
            NodeVisitInfo uVisitInfo = nodeVisit.get(e.to());
            if (uVisitInfo.getColour() == NodeColour.WHITE) {
              edgeVisit.put(e, EdgeVisitType.TREE);
              uVisitInfo.setPredecessor(n);
              q.push(e.to());
            } else if (uVisitInfo.getColour() == NodeColour.GRAY) {
              edgeVisit.put(e, EdgeVisitType.BACKWARD);
            } else {
              if (nVisitInfo.getDiscovery() < uVisitInfo.getDiscovery()) {
                edgeVisit.put(e, EdgeVisitType.FORWARD); // descendant
              } else {
                edgeVisit.put(e, EdgeVisitType.CROSS);
              }
            }
          }

          nVisitInfo.setColour(NodeColour.BLACK);
          nVisitInfo.setFinished(NodeVisitInfo.incrementTime());
        }
      }
    }

    return order;
  }

  /**
   * Gets a Depth-First Search traversal of the graph starting from node with
   * specified id and traversal properties.
   * More details can be found here {@link #getDFSWithVisitInfo(Map<Node,
   * NodeVisitInfo> nodeVisit, Map<Edge, EdgeVisitType> edgeVisit)}.
   * 
   * @param id        The starting node id.
   * @param nodeVisit An empty map that contains a {@link NodeVisitInfo} instance
   *                  for each Node of the graph,
   *                  It will be filled upon traversal.
   * @param edgeVisit An empty map that contains an {@link EdgeVisitType} for each
   *                  Node of the graph,
   *                  It will be filled upon traversal.
   * @return A Depth-First Search traversal of the graph starting from node with
   *         specified id.
   */
  public List<Node> getDFSWithVisitInfo(int id, Map<Node, NodeVisitInfo> nodeVisit,
      Map<Edge, EdgeVisitType> edgeVisit) {
    return getDFSWithVisitInfo(getNode(id), nodeVisit, edgeVisit);
  }

  /* Graph import & export */

  /**
   * Import a file in a restricted DOT format.
   * 
   * @param filename The absolute path to the DOT file with no extension.
   *                 The extension is assumed to be '.gv'.
   * @return A graph representing the DOT file.
   */
  public static Graph fromDotFile(String filename) {
    return fromDotFile(filename, ".gv");
  }

  /**
   * Import a file in a restricted DOT format.
   * 
   * @param filename  The absolute path to the DOT file with no extension.
   * @param extension File extension such for example as '.dot'.
   * @return A graph representing the DOT file.
   */
  public static Graph fromDotFile(String filename, String extension) {
    Graph graph = new Graph();

    Pattern directed = Pattern.compile("\\s*(\"[^\"]+\"|[A-Za-z0-9_]+)\\s*->\\s*(\"[^\"]+\"|[A-Za-z0-9_]+)");
    Pattern attribute = Pattern.compile("(\\w+)\\s*=\\s*([^,\\]]+)");

    try (BufferedReader br = new BufferedReader(new FileReader(filename + extension))) {
      String line;

      while ((line = br.readLine()) != null) {

        Matcher nodeMatcher = directed.matcher(line);
        Matcher attribuMatcher = attribute.matcher(line);

        if (nodeMatcher.find()) {
          Node n = graph.getNodeOrCreate(nodeMatcher.group(1));
          Node u = graph.getNodeOrCreate(nodeMatcher.group(2));

          graph.addNode(n);
          graph.addNode(u);

          boolean attributeWatcher = false;
          while (attribuMatcher.find()) {
            String key = attribuMatcher.group(1);
            String value = attribuMatcher.group(2);

            if (key.equals("weight") || key.equals("label")) {
              graph.addEdge(n, u, Integer.parseInt(value));
              attributeWatcher = true;
            }
          }
          if (!attributeWatcher)
            graph.addEdge(n, u);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return graph;
  }

  /**
   * Export the graph as a String in the DOT syntax.
   * 
   * @return A String representing the graph this in the DOT format.
   */
  public String toDotString() {
    StringBuilder sb = new StringBuilder();

    sb.append("digraph G {\n");

    for (List<Edge> edges : ael.values()) {
      for (Edge e : edges) {
        sb.append("  ")
            .append(e.from().getName())
            .append(" -> ")
            .append(e.to().getName());
        if (e.isWeighted())
          sb.append(" [weight=")
              .append(e.getWeight())
              .append(", label=")
              .append(e.getWeight())
              .append("]");
        sb.append(";\n");
      }
    }

    sb.append("}\n");
    return sb.toString();
  }

  /**
   * Export the graph as a file in the DOT syntax.
   * 
   * @param fileName The absolute path to the DOT file with no extension.
   *                 The default extension ’.gv’ will be added to the file name.
   */
  public void toDotFile(String fileName) {
    toDotFile(fileName, ".gv");
  }

  /**
   * Export the graph as a file in the DOT syntax.
   * 
   * @param fileName  The absolute path to the DOT file with no extension.
   * @param extension File extension such for example as '.dot'.
   */
  public void toDotFile(String fileName, String extension) {
    try (FileWriter writer = new FileWriter(fileName + extension)) {
      writer.write(toDotString());
    } catch (IOException e) {
      throw new GraphExceptions("Failed to write DOT file: " + fileName, e);
    }
  }
}
