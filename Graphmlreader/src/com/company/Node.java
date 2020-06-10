package com.company;

public class Node {
    private int nodeId;
    private float betweennessCentrality;

    public Node(int nodeId) {
        this.nodeId = nodeId;
        this.betweennessCentrality = 0;
    }

    public int getId() {
        return nodeId;
    }

    public float getBetweennessCentrality(){
        return betweennessCentrality;
    }

    public void incrementBetweennessCentrality(){
        betweennessCentrality++;
    }

    public void adjustBetweennessCentrality(int amountOfShortestPaths){
        betweennessCentrality = betweennessCentrality / (float) amountOfShortestPaths;
    }
}
