package flownetworks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import m1graphs2025.Edge;
import m1graphs2025.Graph;
import m1graphs2025.Node;

public class ResidualNetwork extends Graph {
  private int residualNetworkId = -1;
  private List<Node> augementingPath;
  private int residualCapacity = 0;
  private FlowNetwork flowNetwork;
  private List<Edge> exploredEdges;

  public ResidualNetwork(FlowNetwork flowNetwork) {
    this.flowNetwork = flowNetwork;
    init();
  }

  public void init() {
    this.augementingPath = procAugementingPath();
    this.residualCapacity = getResidualCapacity();
    residualNetworkId++;
  }

  public int getResidualNetworkId() {
    return residualNetworkId;
  }

  public List<Node> getAugementingPath() {
    return augementingPath;
  }

  public int getResidualCapacity() {
    int minFlow = Integer.MAX_VALUE;
    for (Edge edge : exploredEdges) {
      minFlow = Math.min(minFlow, getResidualFlow(edge));
    }
    return minFlow == Integer.MAX_VALUE ? 0 : minFlow;
  }

  public int getResidualFlow(Edge edge) {
    return flowNetwork.getEdgeCapacity(edge) - flowNetwork.getEdgeFlow(edge);
  }

  @Override
  public String toDotString() {
    StringBuilder sb = new StringBuilder();
    final String graphName = "Residual graph";
    for (int i = 0; i < augementingPath.size(); i++) {
      sb.append(augementingPath.get(i).getName());
      if (i != augementingPath.size() - 1) sb.append(", ");
    }
    final String augementingPathString = "[" + sb.toString() + "]";
    sb.setLength(0);

    sb.append("digraph residualNetwork").append(residualNetworkId).append(" {\n");
    sb.append(String.format("  label = \"(%d) %s. Augementing path: %s. Residual capacity: %d.\"%n", residualNetworkId, graphName, augementingPathString, residualCapacity));
    sb.append("  rankdir=LR\n  { rank = source; s; }\n  { rank = sink;   t; }\n");
    for (List<Edge> edges : flowNetwork.getAel().values()) {
      for (Edge e : edges) {
        sb.append("  ")
            .append(e.from().getName())
            .append(" -> ")
            .append(e.to().getName());
        sb.append(" [label=") // Flow
            .append(getResidualFlow(e))
            .append(", len=") // Capacity
            .append(getResidualFlow(e))
            .append("]");
        sb.append(";\n");
      }
    }

    sb.append("}\n");
    return sb.toString();
  }

  public Map<Node, Node> getParentsFromBfs(Node s, Node t) {
    Map<Node, Node> parents = new HashMap<>();
    HashSet<Node> visited = new HashSet<>();
    ArrayDeque<Node> q = new ArrayDeque<>();
    List<Edge> ee = new ArrayList<>();
    exploredEdges = ee;

    visited.add(s);
    q.add(s);

    while (!q.isEmpty()) {
      Node u = q.remove();

      for (Edge e : u.getIncidentEdges()) {
        if (getResidualFlow(e) > 0 && visited.add(e.to())) {
          ee.add(e);
          q.add(e.to());

          parents.put(e.to(), e.from());
          if (e.to().equals(t)) return parents;
        }
      }
    }
    return new HashMap<>();
  }

  public List<Node> procAugementingPath() {
    Map<Node, Node> parents = getParentsFromBfs(flowNetwork.getSource(), flowNetwork.getTarget());
    if (parents.size() == 0) return new ArrayList<>();
    List<Node> reversePath = new ArrayList<>();
    
    Node n = flowNetwork.getTarget();
    reversePath.add(n);
    while(n != flowNetwork.getSource()) {
      n = parents.get(n);
      reversePath.add(n);
    }
    
    return reversePath.reversed();
  }

  public List<Edge> getRouteEdges() {
    List<Edge> routeEdges = new ArrayList<>();
    for (int i = 0; i < augementingPath.size()-1; i++) {
      List<Edge> edges = augementingPath.get(i).getEdgesTo(augementingPath.get(i+1));
      for (Edge exploredEdge : exploredEdges) {
        for (Edge edge : edges) {
          if (exploredEdge == edge) routeEdges.add(exploredEdge);
        }
      }
    }
    return routeEdges;
  }
}
