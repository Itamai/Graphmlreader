import java.util.*;
import java.util.logging.Level;

public class GraphCalculations {

    private Graph graph;
    //counts the amount of shortest paths between 2 nodes - 1 to add the paths with proper indices
    private int countRowsForAllShortestPathsBetweenTwoNodes = 0;

    private List<Node> listOfNodes;
    private List<Edge> listOfEdges;
    private List<List<Node>> allShortestPathsBetweenTwoNodes = new ArrayList<>();

    private HashMap<Integer, List<Node>> adjacencyList;
    private HashMap<Node, Integer> distances = new HashMap<>(); // saves the distances of every node

    public GraphCalculations(Graph graph) {
        this.graph = graph;
        listOfNodes = graph.getListOfNodes();
        listOfEdges = graph.getListOfEdges();
        adjacencyList = graph.getAdjacencyList();
    }

    /**
     * Calculates the betweenness centrality of each node of the graph
     */
    public void calculateAllShortestPaths() {
        for(Node source : listOfNodes){
            //for the calculation it's necessary to do the Dijkstra algorithm to each node in the graph
            //to get all possible shortest paths
            HashMap<Node, List<List<Node>>> paths = calculateDijkstra(source, null);
            //save all shortest paths to the HashMap of the graph for each pair of nodes (with constant source here)
            for(Map.Entry<Node, List<List<Node>>> entry : paths.entrySet()){
                Node target = entry.getKey();
                if(source.getId() < target.getId()){
                    graph.setNewPathsShortestPaths(source, target, entry.getValue(), distances.get(target));
                }
            }
        }
    }

    /**
     * Calculates the betweenness centrality of each node and updates it
     */
    public void calculateBetweennessCentralities(){
        graph.getGraphLogger().log(Level.FINE, "Start calculating all betweenness centralities");
        HashMap<Node, HashMap<Node, ShortestPaths>> allShortestPaths = graph.getAllShortestPathsHashMap();
        for(Node node : listOfNodes)    //set the betweenness centrality of each node to 0.
            node.adjustBetweennessCentrality(node.getBetweennessCentrality() * (- 1));

        int amountShortestPathOfCurrentPair = 0;
        //go through every pair of nodes
        for(Node source : allShortestPaths.keySet()) {   //set source of pair
            for (Node target : allShortestPaths.get(source).keySet()) {   //set target of pair
                //only calculate when the sourceId is less the targetId since the graph is undirected and it's
                //unnecessary to calculate if both ways. source->target + target->source == 2x(source->target)
                if (source.getId() < target.getId()) {
                    HashMap<Node, Integer> betweennesses = new HashMap<>(); //count occurrences of nodes visited in path
                    for (List<Node> path : allShortestPaths.get(source).get(target).getPaths()) {
                        amountShortestPathOfCurrentPair++;  //count amount of shortest paths needed for the calc below
                        for (int i = 1; i < path.size() - 1; i++) {  //don't mention first and last entry
                            Node currNodeInPath = path.get(i);
                            if (betweennesses.containsKey(currNodeInPath))
                                betweennesses.put(currNodeInPath, betweennesses.get(currNodeInPath) + 1);
                            else
                                betweennesses.put(currNodeInPath, 1);
                        }
                    }
                    //adjust betweenness centrality for every node that need's to be adjusted
                    for (Map.Entry<Node, Integer> entry : betweennesses.entrySet()) {
                        entry.getKey().adjustBetweennessCentrality((double) entry.getValue() / (double) amountShortestPathOfCurrentPair);
                    }
                    amountShortestPathOfCurrentPair = 0;
                }
            }
        }
        graph.getGraphLogger().log(Level.FINE, "End calculating all betweenness centralities");
    }

    /**
     * Gets a source node calculating all shortest paths from the source node to each other node with the dijkstra algorithm
     * @param source - source node of the graph to calculate the Dijkstra from
     * @return the HashMap with all shortest paths of the given source node to each other node in the graph
     */
    public HashMap<Node, List<List<Node>>> calculateDijkstra(Node source, Node endNode) {
        //https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-in-java-using-priorityqueue/ + Algorithmen und Datenstrukturen Skript
        //combined and modified
        List<Node> unvisitedNodes = new ArrayList<>();
        HashMap<Node, List<Node>> predecessors = new HashMap<>();  //to save more than 1 predecessor

        //set up for dijkstra
        for(Node node : listOfNodes){
            unvisitedNodes.add(node);
            distances.put(node, -1);
        }
        distances.put(source, 0);      //set starting/source node distance to zero

        while (!unvisitedNodes.isEmpty()) { //continue as long as you have visited every node (got all shortest path for every node)
            Node minDistanceNode = getLowestDistanceNode(unvisitedNodes, distances);
            unvisitedNodes.remove(minDistanceNode);
            for (Node adjacentNode : adjacencyList.get( minDistanceNode.getId())) {
                //get all adjacent nodes of the current node and update their weight if needed
                //save their predecessor(s) when the weight is equal or less the previous calculated one
                int adjacentNodeId = adjacentNode.getId();
                int weight = getWeightOfEdge(minDistanceNode, adjacentNode);
                if (weight >= 0 && graph.checkListContainsNode(unvisitedNodes, adjacentNodeId)) {
                    int possibleNewWeight = (weight + distances.get(minDistanceNode));
                    int currentWeight = distances.get(adjacentNode);
                    if (currentWeight >= 0) {
                        if (possibleNewWeight < currentWeight) {
                            distances.put(adjacentNode, possibleNewWeight);
                            predecessors.remove(adjacentNode);
                            List<Node> temp = new ArrayList<>();
                            temp.add(minDistanceNode);
                            predecessors.put(adjacentNode, temp);
                        } else if (possibleNewWeight == distances.get(adjacentNode)) {
                            predecessors.get(adjacentNode).add(minDistanceNode);
                        }
                    } else {
                        distances.put(adjacentNode, possibleNewWeight);
                        List<Node> temp = new ArrayList<>();
                        temp.add(minDistanceNode);
                        predecessors.put(adjacentNode, temp);
                    }
                }
            }
        }
        // get possible new diameter
        for(Integer value : distances.values()){
            if(value > graph.getDiameter())
                graph.setDiameter(value);
        }
        //so far there are only all predecessors in the predecessor list, now the paths need to get reproduced by backtracking
        //and being saved in the allShortestPath HashMap of the graph
        HashMap<Node, List<List<Node>>> allShortestPathsFromThisNode = new HashMap<>();
        if(endNode == null){ //getting all shortest paths between the source node and all other nodes
            for (Node target : listOfNodes) {
                if(source.getId() != target.getId()){
                    if(source.getId() < target.getId()){
                        getAllShortestPathsBetweenTwoNodes(source, target, predecessors);
                        int counter = 0;
                        List<List<Node>> temp = new ArrayList<>();
                        for(List<Node> ignored : allShortestPathsBetweenTwoNodes){
                            temp.add(counter, new ArrayList<>(allShortestPathsBetweenTwoNodes.get(counter)));
                            Collections.reverse(temp.get(counter));
                            counter++;
                            allShortestPathsFromThisNode.put(target, temp);
                        }
                    }
                }
            }
        } else{ //getting just the paths back between the 2 nodes that are given in the parameters
            getAllShortestPathsBetweenTwoNodes(source, endNode, predecessors);
            for(List<Node> list : allShortestPathsBetweenTwoNodes)
                Collections.reverse(list);
            allShortestPathsFromThisNode.put(source, allShortestPathsBetweenTwoNodes);
        }
        return allShortestPathsFromThisNode;
    }

    /**
     * gets a list of nodes to check and a list of their distances returning the node that has the lowest distance
     * @param listOfNodesToCheck has every node to check in it
     * @param listOfNodesDistances has every distances of each node in value, get by node in key
     * @return lowest distance node
     */
    private Node getLowestDistanceNode(List<Node> listOfNodesToCheck, HashMap<Node, Integer> listOfNodesDistances){
        Node lowestNode = null;
        int lowestDistance = -1;
        for (Node node : listOfNodesToCheck) {
            int c = listOfNodesDistances.get(node); //current node's distance
            if((c >= 0 && c < lowestDistance) || (lowestDistance < 0 && c >= 0)){
                lowestDistance = c; //update lowest node distance
                lowestNode = node;  //update lowest node
            }
        }
        return lowestNode;
    }

    /**
     * Gets they key's of the source and target node finding the edge between them returning it's weight
     * @param source - source node key
     * @param target - target node key
     * @return weight of the edge between the source node and target node
     */
    private int getWeightOfEdge(Node source, Node target){
        for (Edge edge : listOfEdges) {
            Node s = edge.getSourceNode(); //save source node id of current edge
            Node t = edge.getTargetNode(); //save target node id of current edge
            if ((s == source && t == target) || (s == target && t == source)) {  // edges are both ways but saved only 1 way
                return edge.getWeight();
            }
        }
        return -1;
    }

    /**
     * Saves all shortest paths between the source and target node in the allShortestPathsBetweenTwoNodes
     * @param source source node
     * @param target target node
     * @param pre list of each nodes predecessor(s)
     */
    private void getAllShortestPathsBetweenTwoNodes(Node source, Node target, HashMap<Node, List<Node>> pre){
        countRowsForAllShortestPathsBetweenTwoNodes = 0;
        allShortestPathsBetweenTwoNodes.clear();    //clear the paths of the previous pair of nodes

        trackBackAllShortestPathsBetweenTwoNodes(source, target, pre, 0);
    }

    /**
     * Recursive method that tracks the path back with the given predecessor list pre
     * Saves all shortest paths between the source and target node in the allShortestPathsBetweenTwoNodes
     * @param source source node
     * @param target target node
     * @param pre list of each nodes predecessor(s)
     * @param listRow describes the index of list to add nodes in for the shortest paths
     */
    private void trackBackAllShortestPathsBetweenTwoNodes(Node source, Node target, HashMap<Node, List<Node>> pre, int listRow){

        if(source.getId() != target.getId()){   //when true, found the end of the path
            List<Node> predecessors = pre.get(target);  //get all predecessors of the current target node (last node of path)
            int amountOfPredecessors = predecessors.size();
            if(amountOfPredecessors > 1){
                //when there are more than one predecessors continue with the first predecessor in the same row
                //but create for each other predecessor another row and continue recursively
                int i = 0;
                if(allShortestPathsBetweenTwoNodes.isEmpty()){
                    List<Node> t = new ArrayList<>();
                    t.add(target);
                    allShortestPathsBetweenTwoNodes.add(listRow, t);
                } else
                    allShortestPathsBetweenTwoNodes.get(listRow).add(target);
                List<Node> temp = new ArrayList<>(allShortestPathsBetweenTwoNodes.get(listRow));
                for (Node node : predecessors) {    //go through every predecessor, look above for more information
                    if(i == 0){
                        trackBackAllShortestPathsBetweenTwoNodes(source, node, pre, listRow);
                        i += 1;
                    } else{
                        allShortestPathsBetweenTwoNodes.add(listRow, new ArrayList<>(temp));
                        trackBackAllShortestPathsBetweenTwoNodes(source, node, pre, countRowsForAllShortestPathsBetweenTwoNodes);
                    }
                }
            } else{
                //when there is just one predecessor of the current target node -> easily continue with next node
                if(allShortestPathsBetweenTwoNodes.isEmpty()){
                    List<Node> t = new ArrayList<>();
                    t.add(target);
                    allShortestPathsBetweenTwoNodes.add(listRow, t);
                } else{
                    allShortestPathsBetweenTwoNodes.get(listRow).add(target);
                }
                //there is just one node left in the predecessor list for this node due the requirements to get here
                for (Node node : predecessors) {
                    trackBackAllShortestPathsBetweenTwoNodes(source, node, pre, listRow);
                }
            }
        } else{ //add the last node
            allShortestPathsBetweenTwoNodes.get(listRow).add(target);
        }
    }
}