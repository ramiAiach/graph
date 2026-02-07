package flownetworks;

public class FlowNetworkOperations {
  private FlowNetwork flowNetwork;
  private ResidualNetwork residualNetwork;
  
  public FlowNetworkOperations(FlowNetwork flowNetwork) {
    this.flowNetwork = flowNetwork;
  }

  public void getMaximumFlow(String path) {
    flowNetwork.toDotFile(path + "initialFlowNetwork");
    flowNetwork.setNullValuatedInitialFlow();
    residualNetwork = new ResidualNetwork(flowNetwork);
    residualNetwork.toDotFile(path + "initialResidualNetwork");

    int i = 1;
    while (residualNetwork.getAugementingPath().size() != 0) {
      flowNetwork.applyResidualCapacity(residualNetwork.getRouteEdges(), residualNetwork.getResidualCapacity());
      flowNetwork.toDotFile(path + "flowNetwork" + i);
      residualNetwork.init();
      residualNetwork.toDotFile(path + "residualNetwork" + i);
      i++;
    }
  }
}
