package com.company;

import java.util.*;

public class GraphProperties {
    private int numberOfVertices;
    private int numberOfEdges;
    private Graph graph;
    private LinkedList<Integer>[] adjacencyList;
    private HashMap<Integer, List<Integer>> peter;

    public GraphProperties(Graph graph) {
        this.graph = graph;
        this.numberOfVertices = graph.getAmountOfNodes();
        adjacencyList = new LinkedList[numberOfVertices];
        for (int i = 0; i < numberOfVertices; i++) {
            adjacencyList[i] = new LinkedList<>();
        }
        addEdge(graph);
    }

    public void addEdge(Graph graph) {
        for (Edge value : graph.getEdgeHashMap().values()) {
            adjacencyList[value.getSourceNode().getId()].add(value.getTargetNode().getId());
            adjacencyList[value.getTargetNode().getId()].add(value.getSourceNode().getId());
        }
    }

    public LinkedList<Integer>[] getAdjacencyList() {
        return adjacencyList;
    }

    public Iterable<Integer> adjacency(int v) {
        return adjacencyList[v];
    }

    public boolean bfsPaths(int s) {
        boolean[] visited = new boolean[this.numberOfVertices];
        boolean connected = false;
        LinkedList<Integer> queue = new LinkedList<>();
        int counter = 0;

        visited[s] = true;
        queue.add(s);

        while (queue.size() != 0) {
            // Dequeue a vertex from queue and print it
            s = queue.poll();

            // System.out.print(s + " ");
            counter++;
            // Get all adjacent vertices of the dequeued vertex s
            // If a adjacent has not been visited, then mark it
            // visited and enqueue it
            Iterator<Integer> i = adjacencyList[s].listIterator();
            while (i.hasNext()) {
                int n = i.next();
                if (!visited[n]) {
                    visited[n] = true;
                    queue.add(n);
                }
            }
        }
        return counter == numberOfVertices;
    }

    public boolean isConnected(){
        int counter = 0;
        boolean connected = false;
        for (int i = 0; i < numberOfVertices; i++) {
            connected = bfsPaths(i);
            if (connected) {
                counter++;
            }
        }
        return connected;
    }

    public void showStatus(){
        System.out.println("Number of nodes: " + graph.getAmountOfNodes());
        System.out.println("Number of edges: " + graph.getAmountOfEdges());

        System.out.print("Is Connected ? ");
        if (isConnected()) {
            System.out.println("Yes");
        } else System.out.println("No");
        System.out.println("### Vertices ###");
        graph.printAllNodes();
        System.out.println("### Edges ###");
        graph.printAllEdges();
    }

    /**
     * Gets the keys of the source node and target node to calculate the shortest path between them if possible
     * @param source
     * @param target
     */
    public void shortestPathDijkstra(int source, int target){
        List<Integer> p = shortestPath(source, target);
        if(p != null){
            System.out.println("Shortest path found from " + source + " to " + target + ".");
            System.out.println("Distance: " + p.get(p.size() - 1));
            p.remove(p.size() - 1);
            System.out.print("Path: ");
            printPath(p);
        } else
            System.out.println("No shortest path found from " + source + " to " + target + ".");
    }

    /**
     * Gets two nodes (source and target) and returns the shortest path from the source node to the target node
     * plus the total distance in the last ArrayList entry
     * @param source - source node
     * @param target - target node
     */
    public List<Integer> shortestPath(int source, int target){
        List<Integer> unvisitedNodes = new ArrayList<Integer>();    // saves the key's of nodes that are unvisited

        HashMap<Integer, Integer> distances = new HashMap<Integer, Integer>();     // saves the distances of every node
        HashMap<Integer, Node> predecessors = new HashMap<Integer, Node>();
        HashMap<Integer, Node> nodeList = graph.getNodeHashMap();

        int counter = 0;
        for(Map.Entry<Integer, Node> value : nodeList.entrySet()) {
            Integer key = value.getKey();
            //Node currNode = value.getValue();

            unvisitedNodes.add(counter++, key);
            distances.put(key, -1);
        }
        if(!unvisitedNodes.contains(source) || !unvisitedNodes.contains(target))
            return null;

        distances.put(source, 0);      //set starting/source node distance to zero

        while(!unvisitedNodes.isEmpty()){
            int minDistanceNodeKey = getLowestDistanceNode(unvisitedNodes, distances);
            if(minDistanceNodeKey < 0){    //Breaks if there is no reachable node left -> no shortest path found.
                return null;
            } else if(minDistanceNodeKey == target){
                List<Integer> path = buildPath(source, target, predecessors); // path from source node to target node
                path.add(path.size(), distances.get(minDistanceNodeKey));
                return path;
            } else{
                unvisitedNodes.remove((Integer) minDistanceNodeKey);
                for(int n : adjacency(minDistanceNodeKey)){
                    int weight = getWeightOfEdge(minDistanceNodeKey, n);
                    if(weight >= 0 && unvisitedNodes.contains(n))
                        if((weight + distances.get(minDistanceNodeKey)) < distances.get(n) || distances.get(n) < 0){
                            distances.put(n, weight + distances.get(minDistanceNodeKey));
                            predecessors.put(n, nodeList.get(minDistanceNodeKey));
                        }
                }
            }
        }
        return null;
    }

    /**
     * gets a list of nodes to check and a list of their distances returning the node that has the lowest distance
     * @param listOfNodesToCheck
     * @param listOfNodesDistances
     * @return lowest distance node's id
     */
    public int getLowestDistanceNode(List<Integer> listOfNodesToCheck, HashMap<Integer, Integer> listOfNodesDistances){
        int lowest = -1;
        for(int n : listOfNodesToCheck){
            int c = listOfNodesDistances.get(n); //current node's distance
            if((c >= 0 && c < lowest) || (lowest < 0 && c >= 0)){
                lowest = n;
            }
        }
        //System.out.println("getLowestDistanceNode: " + lowest);
        return lowest;
    }

    public int getWeightOfEdge(int source, int target){
        HashMap<Integer, Edge> edges = graph.getEdgeHashMap();
        for (Edge value : edges.values()) {
            int s = value.getSourceNode().getId(); //save source node id of current edge
            int t = value.getTargetNode().getId(); //save target node id of current edge
            if ((s == source && t == target) || (s == target && t == source)) {  // edges are both ways but saved only 1 way
                //System.out.println("getWeightOfEdge " + e.getSourceNode().getNodeId() + " to " + e.getTargetNode().getNodeId() + ": " + e.getEdgeWeight());
                return value.getEdgeWeight();
            }
        }
        System.out.println("getWeightOfEdge: -1");
        return -1;
    }

    /**
     *
     * @param path
     */
    public void printPath(List<Integer> path){
        System.out.print(+ path.get(0));
        path.remove(0);
        for(int n : path){
            System.out.print(" -> " + n);
        }
        System.out.println(".");
    }

    /**
     * Creates a path from the target to source node and returns the reversed path.
     * @param s - id of the source node
     * @param t - id of the target node
     * @param pre - list of each node's predecessor
     * @return the reversed path
     */
    public List<Integer> buildPath(int s, int t, HashMap<Integer, Node> pre){
        List<Integer> p = new ArrayList<>();
        int currNode = t;
        while(currNode != s){
            p.add(currNode);
            //currNode = pre.get(currNode);
            currNode = pre.get(currNode).getId();
        }
        p.add(s);
        Collections.reverse(p);
        return p;
    }

    /**
     * Calculates the betweenness centrality of each node of the calling graph
     */
    public void calculateBetweennessCentrality(){
        if(isConnected()){
            HashMap<Integer, Node> nodeList = graph.getNodeHashMap();
            for(Map.Entry<Integer, Node> value : nodeList.entrySet()) {
                Node source = value.getValue();
                calculateDijkstra(source);
            }
            int amountOfShortestPaths = nodeList.size() * (nodeList.size() - 1);
            for(Map.Entry<Integer, Node> value : nodeList.entrySet()){
                Node node = value.getValue();
                node.adjustBetweennessCentrality(amountOfShortestPaths);
                System.out.println("Node: " + node.getId() + ", inb. cen. : " + node.getBetweennessCentrality());
            }
        } else{
            System.out.println("Graph is not connected. Betweenness centrality is not calculatable.");
            return;
        }

    }

    /**
     * Gets a source node calculating all shortest paths to each other node with the dijkstra algorithm
     * Then adding up the counter for each used node for each shortest path except the source and target node
     * @param source - source node of the graph to calculate the Dijkstra from
     */
    public void calculateDijkstra(Node source){
        List<Integer> unvisitedNodes = new ArrayList<Integer>();    // saves the key's of nodes that are unvisited

        HashMap<Integer, Integer> distances = new HashMap<Integer, Integer>();     // saves the distances of every node
        HashMap<Integer, Node> predecessors = new HashMap<Integer, Node>();
        HashMap<Integer, Node> nodeList = graph.getNodeHashMap();

        int counter = 0;
        for(Map.Entry<Integer, Node> value : nodeList.entrySet()) {
            Integer key = value.getKey();

            unvisitedNodes.add(counter++, key);
            distances.put(key, -1);
        }
        if(!unvisitedNodes.contains(source.getId())){
            System.out.println("Fehler 1535");
            return;
        }

        distances.put(source.getId(), 0);      //set starting/source node distance to zero

        while(!unvisitedNodes.isEmpty()){
            int minDistanceNodeKey = getLowestDistanceNode(unvisitedNodes, distances);
            if(minDistanceNodeKey < 0){    //Breaks if there is no reachable node left -> no shortest path found.
                System.out.println("Fehler 2512");
                return;
            } else{
                unvisitedNodes.remove((Integer) minDistanceNodeKey);
                for(int n : adjacency(minDistanceNodeKey)){
                    int weight = getWeightOfEdge(minDistanceNodeKey, n);
                    if(weight >= 0 && unvisitedNodes.contains(n))
                        if((weight + distances.get(minDistanceNodeKey)) < distances.get(n) || distances.get(n) < 0){
                            distances.put(n, weight + distances.get(minDistanceNodeKey));
                            predecessors.put(n, nodeList.get(minDistanceNodeKey));
                        }
                }
            }
        }
        for(Map.Entry<Integer, Node> value : nodeList.entrySet()) {
            Node target = value.getValue();
            if(source != target){
                List<Integer> path = buildPath(source.getId(), target.getId(), predecessors);
                path.remove(0);
                for(int key : path){
                    nodeList.get(key).incrementBetweennessCentrality();
                }
            }
        }
    }
}
