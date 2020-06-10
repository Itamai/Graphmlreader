import CustomExceptions.NodeOutOfGraphException;
import CustomExceptions.WrongPathParameterException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Graph {
    private static Logger graphLogger = Logger.getLogger(Graph.class.getName());

    GraphCalculations graphCalculations;

    private String pathOfGraph;

    private int numberOfNodes;
    private int numberOfEdges;

    private double diameter = 0;

    private boolean connectivity = false;
    private boolean calculatedAllShortestPaths = false;
    private boolean calculatedBetweennessCentrality = false;

    private List<Node> listOfNodes = new ArrayList<>();
    private List<Edge> listOfEdges = new ArrayList<>();

    private HashMap<Node, HashMap<Node, ShortestPaths>> allShortestPaths = new HashMap<>();
    private HashMap<Integer, List<Node>> adjacencyList = new HashMap<>();

    /**
     * Creates an object type Graph setting all attributes if the input file is correct
     * @param str path of the input file
     */
    public Graph(String str) throws Exception {
        pathOfGraph = str;
        Document doc = parseFile(str);

        if (doc == null) {
            try {
                throw new NullPointerException();
            } catch (NullPointerException e) {
                Exception ex = new WrongPathParameterException();
                graphLogger.log(Level.WARNING, "Invalid path to the graphml file", ex);
                throw ex;
            }
        }

        NodeList nodesTagList = doc.getElementsByTagName("node");
        saveNodesFromFile(nodesTagList);

        NodeList edgesTagList = doc.getElementsByTagName("edge");
        saveEdgesFromFile(edgesTagList);

        numberOfNodes = listOfNodes.size();
        numberOfEdges = listOfEdges.size();

        // Sets the adjacency list of the graph and the connectivity
        new GraphProperties(this);

        graphCalculations = new GraphCalculations(this);
    }

    // ********************* Getter ******************** //

    public List<Node> getListOfNodes(){
        return listOfNodes;
    }

    public List<Edge> getListOfEdges(){
        return listOfEdges;
    }

    public double getDiameter(){
        return diameter;
    }

    public HashMap<Node, HashMap<Node, ShortestPaths>> getAllShortestPathsHashMap(){
        return allShortestPaths;
    }

    public HashMap<Integer, List<Node>> getAdjacencyList(){
        return adjacencyList;
    }

    public Logger getGraphLogger(){
        return graphLogger;
    }

    public boolean getConnectivity(){
        return connectivity;
    }

    // ****************** Setter ***************** //

    public void setDiameter(double diameter){
        this.diameter = diameter;
    }

    public void setConnectivity(boolean connectivity){
        this.connectivity = connectivity;
    }

    // ***************** Other methods ************** //

    /**
     * Prints all graph properties in the console
     */
    public void printGraphProperties(){
        System.out.println("############# Graph properties ############");
        System.out.println("Number of nodes: " + numberOfNodes);
        printAllNodes();
        System.out.println("Number of edges: " + numberOfEdges);
        printAllEdges();
        if(connectivity) {
            if (!calculatedAllShortestPaths) {
                graphCalculations.calculateAllShortestPaths();
                calculatedAllShortestPaths = true;
            }
            System.out.println("The graph is connected");
            System.out.println("Diameter: " + diameter);
        } else
            System.out.println("The graph is not connected");
        System.out.println("############################################");
    }

    /**
     * -a argument given. Calculates every graph properties with all shortest paths, betweenness centrality values
     * for each node and the diameter creating a new output.graphml file at the given path in the parameter
     * @param outPutPath path for the new output.grapgmal
     */
    public void calculateEverything(String outPutPath) throws InterruptedException {
        if(!calculatedAllShortestPaths) {
            graphCalculations.calculateAllShortestPaths();
            calculatedAllShortestPaths = true;
        }
        if(!calculatedBetweennessCentrality){
            graphCalculations.calculateBetweennessCentralities();
            calculatedBetweennessCentrality = true;
        }

        //start new Thread to print the calculations of the graph in the console
        PrintConsoleThread printConsoleThread = new PrintConsoleThread(this);
        printConsoleThread.start();

        //start new Thread to write the output file
        WriteOutputFileThread writeOutputFileThread = new WriteOutputFileThread(this, outPutPath);
        writeOutputFileThread.start();

        //wait for both Threads to join
        printConsoleThread.join();
        writeOutputFileThread.join();
    }

    /**
     * Gets a list of nodes and a id of a node returning the occurrence of the given node id in the list
     * @param unvisitedNodes list with nodes to check
     * @param id id of the node to search
     * @return true when the id was found in a node in the list and false otherwise
     */
    public boolean checkListContainsNode(List<Node> unvisitedNodes, int id){
        for(Node node : unvisitedNodes) {
            if (node.getId() == id)
                return true;
        }
        return false;
    }

    /**
     * "-s" argument given. Calculate shortest path and print it out after the graph properties.
     */
    public void getShortestPaths(int source, int target) throws NodeOutOfGraphException {

        Node sourceNode = getNodeFromNodeList(source);
        Node targetNode = getNodeFromNodeList(target);

        if(sourceNode == null || targetNode == null) { // check if both nodes are in the graph
            try {
                throw new NodeOutOfGraphException();
            } catch (NodeOutOfGraphException e) {
                graphLogger.log(Level.SEVERE, "Nodes entered are not in the graph", e);
                throw e;
            }
        }

        printGraphProperties();

        List<List<Node>> paths = graphCalculations.calculateDijkstra(sourceNode, targetNode).get(sourceNode);

        System.out.println("All shortest paths between " + source + " and " + target + ":");
        for (List<Node> list : paths) {
            System.out.print("\t[" + list.get(0).getId());
            list.remove(0);
            for (Node n : list) {
                System.out.print(" -> " + n.getId());
            }
            System.out.println("]");
        }
    }

    /**
     * Calculates the betweenness centrality for all nodes of the graph and returns the betweenness centrality value of the given node
     * @param id id of the node you want to return the betweenness centrality of
     */
    public void getBetweennessCentrality(int id) throws NodeOutOfGraphException {

        Node node = getNodeFromNodeList(id);

        if(node == null){
            try{
                throw new NodeOutOfGraphException();
            }catch (NodeOutOfGraphException e) {
                graphLogger.log(Level.WARNING, "Node entered is not in the graph", e);
                throw e;
            }
        }

        if(!calculatedAllShortestPaths) {
            graphCalculations.calculateAllShortestPaths();
            calculatedAllShortestPaths = true;
        }
        if(!calculatedBetweennessCentrality){
            graphCalculations.calculateBetweennessCentralities();
            calculatedBetweennessCentrality = true;
        }

        printGraphProperties();

        System.out.println("Betweenness centrality of node " + node.getId() + ": " + node.getBetweennessCentrality());
    }

    /**
     * Stores every calculations in a new output.graphml file moving it to the given path
     * @param outPutPath gets the path for the output.graphml file
     */
    public void writeOutput(String outPutPath) {
        graphLogger.info("Write output file thread creating the file has started.");

        String nameOfFile = "output.graphml";

        File t = new File(outPutPath.concat(nameOfFile));
        if(t.exists()) { //delete an existing file when there is one
            //noinspection ResultOfMethodCallIgnored
            t.delete();
        }

        try {
            //BufferedWriter output = new BufferedWriter(new FileWriter(nameOfFile));
            FileWriter output = new FileWriter(new File(outPutPath, nameOfFile));

            //Keys for nodes
            output.write(   "<key id=\"v_id\" for=\"node\" attr.name=\"id\" attr.type=\"double\"/>\n" +
                    "<key id=\"v_bc\" for=\"node\" attr.name=\"bc\" attr.type=\"double\"/>\n");
            //Keys for edges
            output.write( "<key id=\"e_id\" for=\"edge\" attr.name=\"id\" attr.type=\"double\"/>\n" +
                    "<key id=\"e_weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>\n");
            //Keys for graph
            output.write(   "<key id=\"g_non\" for=\"graph\" attr.name=\"non\" attr.type=\"double\"/>\n" +
                    "<key id=\"g_noe\" for=\"graph\" attr.name=\"noe\" attr.type=\"double\"/>\n"+
                    "<key id=\"g_c\" for=\"graph\" attr.name=\"c\" attr.type=\"boolean\"/>\n"+
                    "<key id=\"g_d\" for=\"graph\" attr.name=\"d\" attr.type=\"double\"/>\n\n");

            output.write( "<graph id=\"G\" edgedefault=\"undirected\">\n");
            //Graph-data
            output.write(   "\t<data key=\"g_non\">" + numberOfNodes + "</data>\n" +
                    "\t<data key=\"g_noe\">" + numberOfEdges + "</data>\n" +
                    "\t<data key=\"g_c\">" + connectivity + "</data>\n" +
                    "\t<data key=\"g_d\">" + diameter + "</data>\n\n");
            //All nodes
            for(Node node : listOfNodes){
                output.write(   "\t<node id= \"n" + node.getId() + "\">\n" +
                        "\t\t<data key=\"v_id\">" + node.getId() + "</data>\n" +
                        "\t\t<data key=\"v_bc\">" + node.getBetweennessCentrality() + "</data>\n" +
                        "\t</node>\n");
                output.flush();
            }
            //All edges
            for(Edge edge : listOfEdges){
                output.write(   "\t<edge source=\"n" + edge.getSourceNode().getId() + "\" target=\"n" + edge.getTargetNode().getId() + "\">\n" +
                        "\t\t<data key=\"e_id\">" + edge.getId() + "</data>\n" +
                        "\t\t<data key=\"e_weight\">" + edge.getWeight() + "</data>\n" +
                        "\t</edge>\n");
                output.flush();
            }
            //All shortest paths
            output.write("\t<shortest-paths>\n");
            for(Map.Entry<Node, HashMap<Node, ShortestPaths>> entry : allShortestPaths.entrySet()){
                //here we go through every shortest paths for each source node (Source -> nodeX)
                int sourceID = entry.getKey().getId();
                for(ShortestPaths shortestPathsEntry : entry.getValue().values()){
                    //here we go through every shortest paths for each pair of node with
                    List<Node> firstPath = shortestPathsEntry.getPaths().get(0);
                    int targetID = firstPath.get(firstPath.size() - 1).getId();
                    output.write("\t\t<shortest-paths-between-two-nodes from= \"n" + sourceID + "\" to \"n" + targetID + "\">\n");
                    int paths = 1;
                    for(List<Node> path : shortestPathsEntry.getPaths()){
                        //here we go through every shortest path between 2 nodes
                        output.write("\t\t\t<path_" + paths++ + ">");
                        int i;
                        for(i = 0; i < path.size() - 1; i++){
                            output.write("n" + path.get(i).getId() + ",");
                        }
                        output.write("n" + path.get(i).getId() + "</path>\n");
                    }
                    output.write("\t\t\t<distance>" + shortestPathsEntry.getDistance() + "</distance>\n");
                    output.write("\t\t</shortest-paths-between-two-nodes>\n");
                    output.flush();
                }
            }
            output.write("\t</shortest-paths>\n");
            output.write("</graph>");

            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets 2 nodes and their list of shortestPath between them saving them both ways in the HashMap for all shortest paths
     * @param source source node
     * @param target target node
     * @param listOfPaths list of shortest paths between their nodes
     */
    public void setNewPathsShortestPaths(Node source, Node target, List<List<Node>> listOfPaths, int distance){
        List<List<Node>> paths = new ArrayList<>();
        ShortestPaths shortestPaths = new ShortestPaths(paths, distance);
        for(List<Node> list : listOfPaths)
            paths.add(new ArrayList<>(list));

        if(allShortestPaths.containsKey(source))
            allShortestPaths.get(source).put(target, shortestPaths);
        else{
            HashMap<Node, ShortestPaths> tempHashMap = new HashMap<>();
            tempHashMap.put(target, shortestPaths);
            allShortestPaths.put(source, tempHashMap);
        }
        // saved e.g. 1->2, 1->3, 1->4,..., so far, now save 2->1, 3->1, 4->1,... (save reversed paths)
        List<List<Node>> pathsReversed = new ArrayList<>();
        ShortestPaths shortestPathsReversed = new ShortestPaths(pathsReversed, distance);
        for(List<Node> list : listOfPaths) {
            Collections.reverse(list);
            pathsReversed.add(new ArrayList<>(list));
        }

        if(allShortestPaths.containsKey(target))
            allShortestPaths.get(target).put(source, shortestPathsReversed);
        else{
            HashMap<Node, ShortestPaths> tempHashMap = new HashMap<>();
            tempHashMap.put(source, shortestPathsReversed);
            allShortestPaths.put(target, tempHashMap);
        }
    }

    /**
     * prints all calculations
     */
    public void printAllCalculations(){
        graphLogger.info("Print all calculations on the console thread has started.");

        System.out.println("Read in file '" + pathOfGraph + "'");
        System.out.println("############## Graph-Information ##############");
        System.out.println("\tNumber of nodes: " + numberOfNodes);
        System.out.println("\tNumber of edges: " + numberOfEdges);

        printAllNodes();

        printAllEdges();

        if(!connectivity)
            System.out.println("\tThe graph is not connected");
        else{
            System.out.println("\tThe graph is connected");
            System.out.println("\tGraph diameter: " + diameter);
            printAllShortestPaths();
            printAllBetweennessCentralities();
        }
    }

    /**
     * Gets a path to the graph file and returns the parsed Document
     * @param str path to the graph input file
     * @return returns the parsed Document or null when the path is incorrect
     * @throws ParserConfigurationException ParserConfigurationException
     * @throws IOException IOException
     * @throws SAXException SAXException
     */
    private Document parseFile(String str) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(str);
        if(file.exists() && !file.isDirectory()) {
//        	creates a object tree from xml document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(file);
        }
        return null;
    }

    /**
     * Stores all nodes from the edgeTagList of type w3c.dom.NodeList to a List<Node> listOfNodes
     * @param nodesTagList the current data storage NodeList
     */
    private void saveNodesFromFile(NodeList nodesTagList){
        for(int n = 0; n < nodesTagList.getLength(); n++){
            int t = Integer.parseInt(nodesTagList.item(n).getChildNodes().item(1).getTextContent());
            listOfNodes.add(new Node(t));
        }
    }

    /**
     * Stores all nodes from the edgeTagList of type w3c.dom.NodeList to a List<Edge> listOfEdges
     * @param edgesTagList the current data storage NodeList
     */
    private void saveEdgesFromFile(NodeList edgesTagList){
        for(int n = 0; n < edgesTagList.getLength(); n++){
            Node sourceNode = getNodeFromNodeList(Integer.parseInt(edgesTagList.item(n).getAttributes().getNamedItem("source").getNodeValue().substring(1)));
            Node targetNode = getNodeFromNodeList(Integer.parseInt(edgesTagList.item(n).getAttributes().getNamedItem("target").getNodeValue().substring(1)));
            int edgeId = Integer.parseInt(edgesTagList.item(n).getChildNodes().item(1).getTextContent());
            int edgeWeight = Integer.parseInt(edgesTagList.item(n).getChildNodes().item(3).getTextContent());
            Edge temp = new Edge(sourceNode, targetNode, edgeId, edgeWeight);
            listOfEdges.add(temp);
        }
    }

    /**
     * gets an id of a node and searching and returning the fitting node in the listOfNodes
     * @param id id of the searched node
     * @return returns the node if possible, otherwise return null
     */
    private Node getNodeFromNodeList(int id){
        for(Node node : listOfNodes){
            if(node.getId() == id)
                return node;
        }
        return null;
    }

    /**
     * Prints all nodes that are in the nodeHashMap
     */
    private void printAllNodes(){
        System.out.print("\tNode ID's: [");
        for(int i = 0; i < listOfNodes.size() - 1; i++){
            if(i % 10 == 0 && i > 0)
                System.out.print("\n\t\t\t\t");
            System.out.print("'" + listOfNodes.get(i).getId() + "', ");
        }
        System.out.println("'" + listOfNodes.get(listOfNodes.size() - 1).getId() + "']");
    }

    /**
     * Prints all edges that are in the edgeHashMap
     */
    private void printAllEdges(){
        System.out.print("\tEdge ID's: [");
        for(int i = 0; i < listOfEdges.size() - 1; i++){
            if(i % 10 == 0 && i > 0)
                System.out.print("\n\t\t\t\t");
            System.out.print("'" + listOfEdges.get(i).getId() + "', ");
        }
        System.out.println("'" + listOfEdges.get(listOfEdges.size() - 1).getId() + "']");
    }

    /**
     * Prints the betweenness centrality of every node in the graph
     */
    private void printAllBetweennessCentralities(){
        System.out.println("######## Betweenness centralities ##########");
        for(Node node : listOfNodes){
            System.out.println("\tNode: '" + node.getId() + "': " + node.getBetweennessCentrality());
        }
    }

    /**
     * Prints every shortest path calculated in this graph from every pair of node
     */
    private void printAllShortestPaths() {
        System.out.println("############# All shortest paths in this graph #############");

        for(Map.Entry<Node, HashMap<Node, ShortestPaths>> entry : allShortestPaths.entrySet()){
            System.out.println("Source node '" + entry.getKey().getId() + "': ");
            for(ShortestPaths shortestPathsEntry : entry.getValue().values()){
                List<List<Node>> paths = new ArrayList<>(shortestPathsEntry.getPaths());
                List<Node> firstPath = new ArrayList<>(paths.get(0));
                System.out.print("\tTo node '" + firstPath.get(firstPath.size() - 1).getId() + "' with path: ");
                System.out.print("[" + firstPath.get(0).getId());
                firstPath.remove(0);
                for(Node node : firstPath){
                    System.out.print(" -> " + node.getId());
                }
                System.out.println("]");
                paths.remove(0);
                for(List<Node> path : paths){
                    System.out.print("\t\t\t\t\t\tor ");
                    System.out.print("[" + path.get(0).getId());
                    path.remove(0);
                    for(Node node : path){
                        System.out.print(" -> " + node.getId());
                    }
                    System.out.println("]");
                }
            }
        }
    }
}
