package m1graphs2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class UndirectedGraph, codes an undirected graph.
 * The graph structure is coded as adjacent edge lists, meaning that the list
 * of its out edges is mapped to each of the graph nodes.
 */
public class UndirectedGraph extends Graph {
  private Graph reverseGraph;

  /**
   * Constructor for the class UndirectedGraph, constructs an empty undirected
   * graph with an adjacent edge lists structure.
   */
  public UndirectedGraph() {
    super();
    this.reverseGraph = super.getReverse();
  }

  /**
   * Constructor for the class UndirectedGraph, takes a Successor Arrays as
   * parameter.
   * Only unweighted graphs can be built this way.
   * For example: UndirectedGraph g = new UndirectedGraph(2, 4, 0, 0, 6, 0, 2, 3,
   * 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0);
   * 
   * @param sa Successor Array (see Lectures for an accurate specification of the
   *           SA formalism).
   */
  public UndirectedGraph(int... sa) {
    super(sa);
    this.reverseGraph = super.getReverse();
  }

  /**
   * Gets a list without duplicates of the neighbors of node n.
   * “Without duplicates” means that each neighbor appears uniquely in the list
   * returned,
   * even in the case of a multigraph.
   * 
   * @param n Source node.
   * @return A ist without duplicates of the neighbors of node n.
   */
  @Override
  public List<Node> getSuccessors(Node n) {
    Set<Node> result = new LinkedHashSet<>();

    result.addAll(super.getSuccessors(n));

    for (Node v : reverseGraph.getSuccessors(n)) {
      if (!v.equals(n)) { // skip self-loops to avoid double-counting
        result.add(v);
      }
    }

    return new ArrayList<>(result);
  }

  /**
   * Gets a list with possible duplicates of the neighbors of node n.
   * “With possible duplicates” means that in the case of a multigraph,
   * each neighbor appears as many times as it is joined by an edge to node n.
   * 
   * @param n Srouce node.
   * @return A ist with possible duplicates of the neighbors of node n.
   */
  @Override
  public List<Node> getSuccessorsMulti(Node n) {
    List<Node> result = new ArrayList<>();

    result.addAll(super.getSuccessorsMulti(n));

    for (Node v : reverseGraph.getSuccessorsMulti(n)) {
      if (!v.equals(n)) {
        result.add(v); // skip self-loops to avoid double-counting
      }
    }

    return new ArrayList<>(result);
  }

  @Override
  public boolean adjacent(Node n, Node u) {
    return super.adjacent(n, u) || reverseGraph.adjacent(n, u);
  }

  @Override
  public boolean addNode(Node n) {
    if (reverseGraph != null) {
      reverseGraph.addNode(n);
    }
    return super.addNode(n);
  }

  @Override
  public boolean removeNode(Node n) {
    if (reverseGraph != null) {
      reverseGraph.removeNode(n);
    }
    return super.removeNode(n);
  }

  @Override
  public int inDegree(Node n) {
    return degree(n);
  }

  @Override
  public int outDegree(Node n) {
    return degree(n);
  }

  @Override
  public int degree(Node n) {
    return getSuccessorsMulti(n).size();
  }

  @Override
  public boolean existsEdge(Node u, Node v) {
    return super.existsEdge(u, v) || reverseGraph.existsEdge(u, v);
  }

  @Override
  public boolean isMultiEdge(Node u, Node v) {
    return super.isMultiEdge(u, v) || reverseGraph.isMultiEdge(u, v);
  }

  @Override
  public void addEdge(Node u, Node v) {
    super.addEdge(u, v);
    if (reverseGraph != null) {
      reverseGraph.addEdge(v, u);
    }
  }

  @Override
  public boolean removeEdge(Node u, Node v) {
    if (reverseGraph != null) {
      reverseGraph.removeEdge(v, u);
    }
    return super.removeEdge(u, v);
  }

  /**
   * Gets the list of all edges leaving node n.
   * Notice that in the undirected case, all incident edges to a node are both in
   * and out edges.
   * 
   * @param n Source node.
   * @return A list of all edges leaving node n.
   */
  @Override
  public List<Edge> getOutEdges(Node n) {
    return getIncidentEdges(n);
  }

  /**
   * Gets the list of all edges entering node n.
   * Notice that in the undirected case, all incident edges to a node are both in
   * and out edges.
   * 
   * @param n Source node.
   * @return A list of all edges entering node n.
   */
  @Override
  public List<Edge> getInEdges(Node n) {
    return getIncidentEdges(n);
  }

  /**
   * Gets the list of all edges incident to node n.
   * This is the union of the out and in edges.
   * Notice that in the undirected case, all incident edges to a node are both in
   * and out edges.
   * 
   * @return A ist of all edges incident to node n.
   */
  @Override
  public List<Edge> getIncidentEdges(Node n) {
    List<Edge> result = new ArrayList<>();

    result.addAll(super.getOutEdges(n));
    result.addAll(reverseGraph.getOutEdges(n));

    return result;
  }

  @Override
  public List<Edge> getEdges(Node u, Node v) {
    List<Edge> result = new ArrayList<>();

    result.addAll(super.getEdges(u, v));
    result.addAll(reverseGraph.getEdges(u, v));

    return result;
  }

  @Override
  public int[] toSuccessorArray() {
    int totalEdges = 0;
    for (Node n : getAllNodes()) {
      totalEdges += getSuccessorsMulti(n).size();
    }

    int[] sa = new int[totalEdges + nbNodes()];
    int saIdx = 0;

    for (Node n : getAllNodes()) {
      List<Node> neighbors = getSuccessorsMulti(n);
      for (Node v : neighbors) {
        sa[saIdx++] = v.getId();
      }
      sa[saIdx++] = 0;
    }

    return sa;
  }

  @Override
  public int[][] toAdjMatrix() {
    int n = nbNodes();
    int[][] am = new int[n][n];

    for (Map.Entry<Node, List<Edge>> entry : getAel().entrySet()) {
      Node u = entry.getKey();
      for (Edge e : entry.getValue()) {
        Node v = e.to();
        int i = u.getId() - 1;
        int j = v.getId() - 1;

        if (i == j) {
          am[i][j]++; // self-loop counted once
        } else {
          am[i][j]++;
          am[j][i]++; // symmetric for undirected
        }
      }
    }

    return am;
  }

  /**
   * Computes in a new undirected graph the reverse (G^-1) of the undirected
   * graph.
   * 
   * @return The reverse (G^-1) of the undirected graph in a new undirected graph.
   */
  @Override
  public UndirectedGraph getReverse() {
    return this.copy();
  }

  /**
   * Computes in a new graph the transitive closure of the graph.
   * Simple cast from Graph.
   * 
   * @return The transitive closure of the graph in a new graph.
   */
  @Override
  public UndirectedGraph getTransitiveClosure() {
    UndirectedGraph closure = new UndirectedGraph();

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
   * Transform the (possibly) multi-graph this into a simple one, by removing its
   * self-loops and multi-edges.
   * Simple cast from Graph.
   * 
   * @return A simple graph from this in a new graph.
   */
  @Override
  public UndirectedGraph toSimpleGraph() {
    UndirectedGraph simpleGraph = new UndirectedGraph();

    for (Node n : getAllNodes()) {
      simpleGraph.addNode(n.getId());
    }

    for (Node u : getAllNodes()) {
      Set<Integer> targets = new HashSet<>();

      for (Edge e : getAel().getOrDefault(u, List.of())) {
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
   * Simple cast from Graph.
   * 
   * @return A copy of graph this.
   */
  @Override
  public UndirectedGraph copy() {
    UndirectedGraph g = new UndirectedGraph();

    // 1. Copy all nodes (deep copy)
    for (Node n : getAllNodes()) {
      g.addNode(n.getId(), n.getName());
    }

    for (Node u : getAllNodes()) {
      for (Edge e : getAel().getOrDefault(u, List.of())) {
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

  /**
   * Import a file in a restricted DOT format.
   * 
   * @param filename The absolute path to the DOT file with no extension.
   *                 The extension is assumed to be '.gv'.
   * @return A graph representing the DOT file.
   */
  public static UndirectedGraph fromDotFile(String filename) {
    return fromDotFile(filename, ".gv");
  }

  /**
   * Import a file in a restricted DOT format.
   * 
   * @param filename  The absolute path to the DOT file with no extension.
   * @param extension File extension such for example as '.dot'.
   * @return A graph representing the DOT file.
   */
  public static UndirectedGraph fromDotFile(String filename, String extension) {
    UndirectedGraph graph = new UndirectedGraph();

    Pattern undirected = Pattern.compile("\\s*(\"[^\"]+\"|[A-Za-z0-9_]+)\\s*--\\s*(\"[^\"]+\"|[A-Za-z0-9_]+)");
    Pattern attribute = Pattern.compile("(\\w+)\\s*=\\s*([^,\\]]+)");

    try (BufferedReader br = new BufferedReader(new FileReader(filename + extension))) {
      String line;

      while ((line = br.readLine()) != null) {

        Matcher nodeMatcher = undirected.matcher(line);
        Matcher attributeMatcher = attribute.matcher(line);

        if (nodeMatcher.find()) {
          Node n = graph.getNodeOrCreate(nodeMatcher.group(1));
          Node u = graph.getNodeOrCreate(nodeMatcher.group(2));

          graph.addNode(n);
          graph.addNode(u);

          boolean attributeWatcher = false;
          while (attributeMatcher.find()) {
            String key = attributeMatcher.group(1);
            String value = attributeMatcher.group(2);

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

  @Override
  public String toDotString() {
    StringBuilder sb = new StringBuilder();

    sb.append("graph G {\n");

    for (List<Edge> edges : getAel().values()) {

      for (Edge e : edges) {
        sb.append("  ")
            .append(e.from().getName())
            .append(" -- ")
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

}
