package m1graphs2025;

/**
 * Classification of edges encountered during graph traversal.
 * {@code TREE} edge to a newly discovered node.
 * {@code BACKWARD} edge that points to an ancestor in a DFS tree.
 * {@code FORWARD} edge that points to a descendant in a DFS tree.
 * {@code CROSS} edge that connects nodes in different DFS branches.
 */
public enum EdgeVisitType { TREE, BACKWARD, FORWARD, CROSS }
