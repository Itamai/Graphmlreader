package com.company;

public class Edge {

    private Node sourceNode;
    private Node targetNode;

    private int edgeId;
    private int edgeWeight;

    public Edge(Node sourceNode, Node targetNode, int edgeId, int edgeWeight) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.edgeId = edgeId;
        this.edgeWeight = edgeWeight;
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public int getEdgeId() {
        return edgeId;
    }

    public int getEdgeWeight() {
        return edgeWeight;
    }

}
