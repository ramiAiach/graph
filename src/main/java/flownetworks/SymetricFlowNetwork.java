package flownetworks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m1graphs2025.Edge;
import m1graphs2025.Graph;
import m1graphs2025.Node;

public class SymetricFlowNetwork extends Graph {
  private Map<Node, List<Edge>> aelRef;
  private Map<Edge, Integer> flowMapRef;
  private Map<Edge, Integer> flowMap = new HashMap<>();

  public SymetricFlowNetwork(Map<Node, List<Edge>> aelRef, Map<Edge, Integer> flowMapRef) {
    super();
    this.aelRef = aelRef;
    this.flowMapRef = flowMapRef;
  }

  public void init() {
    for (Node n : aelRef.keySet()) {
      addNode(n.getId(), n.getName());
    }

    for (Node u : aelRef.keySet()) {
      for (Edge e : aelRef.getOrDefault(u, List.of())) {
        Node from = getNode(e.from().getId());
        Node to = getNode(e.to().getId());

        Edge newEdge;
        if (e.isWeighted()) {
          newEdge = new Edge(from, to, e.getWeight());
        } else {
          newEdge = new Edge(from, to);
        }
        addEdge(newEdge);
        flowMap.putIfAbsent(newEdge, flowMapRef.get(e));
      }
    }
  }

  public void applySymetry() {
    init();
    List<Edge> symetricEdges = new ArrayList<>();
    for (List<Edge> edges : getAel().values()) {
      for (Edge edge : edges) {
        Edge symetricEdge = edge.getSymmetric();
        symetricEdges.add(symetricEdge);
        setEdgeFlow(symetricEdge, getEdgeFlow(edge) * -1);
      }
    }
    for (Edge edge : symetricEdges) {
      if (this.existsEdge(edge))
        continue;
      this.addEdge(edge);
    }
  }

  public int getEdgeCapacity(Edge edge) {
    Integer capacity = edge.getWeight();
    return capacity == null ? 0 : capacity;
  }

  public int getEdgeFlow(Edge edge) {
    if (edge == null) return 0;
    return flowMap.get(edge);
  }

  public boolean setEdgeFlow(Edge edge, int flow) {
    if (flow > getEdgeCapacity(edge)) {
      flowMap.putIfAbsent(edge, getEdgeCapacity(edge));
      return false;
    }
    flowMap.putIfAbsent(edge, flow);
    return true;
  }

  @Override
  public String toDotString() {
    StringBuilder sb = new StringBuilder();

    sb.append("digraph flowNetworkSymetric").append(" {\n");
    sb.append("  rankdir=LR\n  { rank = source; s; }\n  { rank = sink;   t; }\n");
    for (List<Edge> edges : getAel().values()) {
      for (Edge e : edges) {
        sb.append("  ")
            .append(e.from().getName())
            .append(" -> ")
            .append(e.to().getName());
        sb.append(" [label=") // Flow
            .append(getEdgeFlow(e))
            .append(", len=") // Capacity
            .append(getEdgeCapacity(e))
            .append("]");
        sb.append(";\n");
      }
    }

    sb.append("}\n");
    return sb.toString();
  }
}