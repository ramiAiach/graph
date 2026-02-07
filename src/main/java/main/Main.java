package main;

import flownetworks.FlowNetwork;
import flownetworks.FlowNetworkOperations;

public class Main {
    public static void main(String[] args) {
        final String INPUT_PATH1 = "target/input/dotInput";
        final String INPUT_PATH2 = "target/input/maxFlowTest_STLabels";
        final String INPUT_PATH3 = "target/input/maxFlowTest_STNames";

        FlowNetwork fn = FlowNetwork.fromDotFile(INPUT_PATH2);
        fn.toDotFile("target/output/dotOutput");
        
        FlowNetworkOperations fno = new FlowNetworkOperations(fn);
        fno.getMaximumFlow("target/output/steps/");
    }
}
