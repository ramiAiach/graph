package flownetworks;

import m1graphs2025.Graph;
import m1graphs2025.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import m1graphs2025.Edge;

public class FlowNetwork extends Graph {
  private SymetricFlowNetwork symetricFlowNetwork;
  private Map<Edge, Integer> flowMap = new HashMap<>();
  private int flowNetworkId = 0;
  private int flowValue = 0;
  private Integer isInducedBy = null;

  public FlowNetwork() {
    super();
    symetricFlowNetwork = new SymetricFlowNetwork(getAel(), flowMap);
    initFlows();
  }

  public SymetricFlowNetwork getSymetricFlowNetwork() {
    return symetricFlowNetwork;
  }

  public int getFlowValue() {
    return flowValue;
  }

  public int getFlowNetworkId() {
    return flowNetworkId;
  }

  public Integer getIsInducedBy() {
    return isInducedBy;
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

  public boolean updateEdgeFlow(Edge edge, int flow) {
    if (flow > getEdgeCapacity(edge)) {
      flowMap.replace(edge, getEdgeCapacity(edge));
      return false;
    }
    flowMap.replace(edge, flow);
    return true;
  }

  public boolean addEdgeFlow(Edge edge, int flow) {
    int newFlow = flow + getEdgeFlow(edge);
    if (newFlow > getEdgeCapacity(edge)) {
      flowMap.replace(edge, getEdgeCapacity(edge));
      return false;
    }
    flowMap.replace(edge, newFlow);
    return true;
  }

  private void initFlows() {
    for (List<Edge> edges : getAel().values()) {
      for (Edge edge : edges) {
        setEdgeFlow(edge, 0);
      }
    }
  }

  private boolean hasSourceAndTarget() {
    boolean hasS = false;
    boolean hasT = false;
    for (Node node : getAel().keySet()) {
      if (node.getName().equals("s"))
        hasS = true;
      else if (node.getName().equals("t"))
        hasT = true;
    }
    return hasS && hasT;
  }

  private boolean hasValidNodeNames() {
    boolean hasS = false;
    boolean hasT = false;
    final Pattern digits = Pattern.compile("^(\\d+)$");
    for (Node node : getAel().keySet()) {
      if (node.getName().equals("s"))
        hasS = true;
      else if (node.getName().equals("t"))
        hasT = true;

      Matcher m = digits.matcher(node.getName());
      if (m.matches()) {
        int id = Integer.parseInt(m.group(1));
        if (id != node.getId() - 1)
          return false;
      }
    }
    return hasS && hasT;
  }

  private boolean validateNodeNames() {
    if (getSource() == null || getTarget() == null)
      return false;
    final Pattern digits = Pattern.compile("^(\\d+)$");
    int newNodeName = 1;
    for (Node node : getAel().keySet()) {
      Matcher m = digits.matcher(node.getName());
      if (m.matches()) {
        node.setName(String.valueOf(newNodeName++));
      }
    }
    return true;
  }

  private void applyFlowRestriction() {
    for (List<Edge> edges : getAel().values()) {
      for (Edge edge : edges) {
        int edgeFlow = flowMap.get(edge);
        if (edgeFlow > edge.getWeight()) {
          flowMap.replace(edge, edge.getWeight());
        }
      }
    }
  }

  public static FlowNetwork fromDotFile(String filename) {
    return fromDotFile(filename, ".gv");
  }

  public static FlowNetwork fromDotFile(String filename, String extension) {
    FlowNetwork flowNetwork = new FlowNetwork();

    Pattern directed = Pattern.compile("\\s*(\"[^\"]+\"|[A-Za-z0-9_]+)\\s*->\\s*(\"[^\"]+\"|[A-Za-z0-9_]+)");
    Pattern attribute = Pattern.compile("(\\w+)\\s*=\\s*([^,\\]]+)");
    Pattern nodeDecl = Pattern.compile("\\s*(\"[^\\\"]+\"|[A-Za-z0-9_]+)\\s*\\[");

    Map<String, Node> tokenNodeMap = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filename + extension))) {
      String line;

      while ((line = br.readLine()) != null) {

        Matcher nodeDeclMatcher = nodeDecl.matcher(line);
        Matcher nodeMatcher = directed.matcher(line);
        Matcher attribuMatcher = attribute.matcher(line);

        if (nodeDeclMatcher.find() && !line.contains("->")) {
          String nodeToken = nodeDeclMatcher.group(1);
          Node n = flowNetwork.getNodeOrCreate(nodeToken);
          flowNetwork.addNode(n);
          tokenNodeMap.put(nodeToken, n);
          while (attribuMatcher.find()) {
            String key = attribuMatcher.group(1).trim();
            String value = attribuMatcher.group(2).trim().replaceAll("^\"|\"$", "");
            if (key.equals("label")) {
              if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
              }
              if (value.equals("s")) {
                flowNetwork.setSource(n);
              } else if (value.equals("t")) {
                flowNetwork.setTarget(n);
              }
            }
          }
          continue;
        }

        if (nodeMatcher.find()) {
          Node n;
          Node u;

          if (tokenNodeMap.containsKey(nodeMatcher.group(1))) {
            n = tokenNodeMap.get(nodeMatcher.group(1));
          } else {
            n = flowNetwork.getNodeOrCreate(nodeMatcher.group(1));
          }

          if (tokenNodeMap.containsKey(nodeMatcher.group(2))) {
            u = tokenNodeMap.get(nodeMatcher.group(2));
          } else {
            u = flowNetwork.getNodeOrCreate(nodeMatcher.group(2));
          }

          flowNetwork.addNode(n);
          flowNetwork.addNode(u);

          boolean attributeWatcher = false;
          while (attribuMatcher.find()) {
            String key = attribuMatcher.group(1).trim();
            String value = attribuMatcher.group(2).trim();

            if (key.equals("weight") || key.equals("len")) {
              Edge e = flowNetwork.getEdges(n, u).getLast();
              e.setWeight(Integer.parseInt(value));
              attributeWatcher = true;
            }
            if (key.equals("label")) {
              flowNetwork.addEdge(n, u, Integer.parseInt(value));
              flowNetwork.setEdgeFlow(flowNetwork.getEdges(n, u).getLast(), Integer.parseInt(value));
              attributeWatcher = true;
            }
            
          }
          if (!attributeWatcher)
            flowNetwork.addEdge(n, u);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    flowNetwork.initFlows();
    flowNetwork.applyFlowRestriction();
    if (!flowNetwork.hasSourceAndTarget()) throw new InvalidParameterException("Flow network must have source and target !");
    if (!flowNetwork.hasValidNodeNames()) flowNetwork.validateNodeNames();
    return flowNetwork;
  }

  @Override
  public String toDotString() {
    StringBuilder sb = new StringBuilder();
    final String graphName = (isInducedBy == null) ? "Flow initial" : "Flow induced from residual graph " + isInducedBy;

    sb.append("digraph flowNetwork").append(flowNetworkId).append(" {\n");
    sb.append(String.format("  label = \"(%d) %s. Value: %d.\"%n", flowNetworkId, graphName, flowValue));
    sb.append("  rankdir=LR\n  { rank = source; s; }\n  { rank = sink;   t; }\n");
    for (List<Edge> edges : getAel().values()) {
      for (Edge e : edges) {
        sb.append("  ")
            .append(e.from().getName())
            .append(" -> ")
            .append(e.to().getName());
        sb.append(" [label=\"") // Flow
            .append(getEdgeFlow(e)).append("/").append(getEdgeCapacity(e))
            .append("\", len=") // Capacity
            .append(getEdgeCapacity(e))
            .append("]");
        sb.append(";\n");
      }
    }

    sb.append("}\n");
    return sb.toString();
  }

  public Node getSource() {
    for (Node node : getAel().keySet()) {
      if (node.getName().equals("s"))
        return node;
    }
    return null;
  }

  public Node getTarget() {
    for (Node node : getAel().keySet()) {
      if (node.getName().equals("t"))
        return node;
    }
    return null;
  }

  public void setSource(Node n) {
    if (this.holdsNode(n))
      n.setName("s");
  }

  public void setSource(int id) {
    Node n = getNode(id);
    if (n == null)
      return;
    setSource(n);
  }

  public void setTarget(Node n) {
    if (this.holdsNode(n))
      n.setName("t");
  }

  public void setTarget(int id) {
    Node n = getNode(id);
    if (n == null)
      return;
    setTarget(n);
  }

  public void setNullValuatedInitialFlow() {
    for (List<Edge> edges : getAel().values()) {
      for (Edge edge : edges) {
        updateEdgeFlow(edge, 0);
      }
    }
  }

  public void applyResidualCapacity(List<Edge> routeEdges, int residualCapacity) {
    isInducedBy = isInducedBy == null ? 0 : isInducedBy + 1;
    flowNetworkId++;
    flowValue += residualCapacity;
    for (Edge edge : getAllEdges()) {
      for (Edge exploredEdge : routeEdges) {
        if (edge == exploredEdge) {
          addEdgeFlow(edge, residualCapacity);
        }
      }
    }
  }
}
