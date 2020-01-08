import java.util.*;

public class GraphProperties {
    private int numberOfVertices;
    private int numberOfEdges;
    private Graph graph;
    private LinkedList<Integer> adjacencyList[];

    public GraphProperties(Graph graph) {
        this.graph = graph;
        this.numberOfVertices = graph.getAmountOfNodes();
        adjacencyList = new LinkedList[numberOfVertices];
        for (int i = 0; i < numberOfVertices; i++) {
            adjacencyList[i] = new LinkedList<Integer>();
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

    public void printAdjacency() {
        for (int i = 0; i < numberOfVertices; i++) {
            System.out.println("Node n" + i + adjacency(i));
        }
    }

    public boolean bfsPaths(int s) {
        boolean visited[] = new boolean[this.numberOfVertices];
        boolean connected = false;
        LinkedList<Integer> queue = new LinkedList<Integer>();
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
        if (counter == numberOfVertices) {
            return true;
        } else return false;
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

    // predecessor debug -> next milestone
    public void shortestPath(int source, int target){
        List<Integer> distances = new ArrayList<Integer>();
        List<Integer> unvisitedNodes = new ArrayList<Integer>();
        //List<Integer> predecessors = new ArrayList<Integer>();
        HashMap<Integer, Node> predecessors = new HashMap<Integer, Node>();
        //ArrayList<Node> nodeList = graph.getNodeArrayList();
        HashMap<Integer, Node> nodeList = graph.getNodeHashMap();
        for (Node value : nodeList.values()) {
            int id = value.getId();                 //and set their distance to the target node-id to unlimited(-1)
            unvisitedNodes.add(id, id);
            distances.add(id, -1);
        }
        if(!unvisitedNodes.contains(source) || !unvisitedNodes.contains(target)){
            System.out.println("One or both nodes do not exist in the graph. (" + source + ", " + target + ")");
            return;
        }
        distances.set(source, 0);        //set starting/source node distance to zero

        while(!unvisitedNodes.isEmpty()){
            int minDistanceNodeId = getLowestDistanceNode(unvisitedNodes, distances);
            if(minDistanceNodeId < 0){    //Breaks if there is no reachable node left -> no shortest path found.
                System.out.println("No shortest path found from node " + source + " to " + target + ".");
                return;
            } else if(minDistanceNodeId == target){
                List<Integer> path = buildPath(source, target, predecessors);
                System.out.print("Target node found. Shortest path distance: " + distances.get(minDistanceNodeId) +
                        "\nwith path: ");
                System.out.print(+ path.get(0));
                path.remove(0);
                for(int n : path){
                    System.out.print(" -> " + n);
                }
                System.out.println(".");
                break;
            } else{
                unvisitedNodes.remove(unvisitedNodes.indexOf(minDistanceNodeId));
                for(int n : adjacency(minDistanceNodeId)){
                    int weight = getWeightOfEdge(minDistanceNodeId, n);
                    if(weight >= 0 && unvisitedNodes.contains(n))
                        if((weight + distances.get(minDistanceNodeId)) < distances.get(n) || distances.get(n) < 0){
                            distances.set(n, weight + distances.get(minDistanceNodeId));
                            predecessors.put(n, nodeList.get(minDistanceNodeId));
                        }
                }
            }
        }
    }

    public int getLowestDistanceNode(List<Integer> listOfNodesToCheck, List<Integer>listOfNodesDistances){
        int lowest = -1;
        for(int n : listOfNodesToCheck){
            int c = listOfNodesDistances.get(n); //current node's distance
            if((c >= 0 && c < lowest) || (lowest < 0 && c >= 0)){
                lowest = n;
            }
        }
        System.out.println("getLowestDistanceNode: " + lowest);
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
     * Creates a path from the target to source node and returns the reversed path.
     * @param s - id of the source node
     * @param t - id of the target node
     * @param pre - list of each node's predecessor
     * @return
     */
    //public ArrayList<Integer> buildPath(int s, int t, Map<Node, Node> pre){
    public List<Integer> buildPath(int s, int t, HashMap<Integer, Node> pre){
        List<Integer> p = new ArrayList<Integer>();
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
}
