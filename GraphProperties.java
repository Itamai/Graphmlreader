import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Builds the properties of the graph to prepare the calculations that probably need to be done in future
 */
public class GraphProperties {

    private Graph graph;

    private HashMap<Integer, List<Node>> adjacencyList;

    /**
     * Creates a GraphProperties object with the node and edge list
     * @param graph The graph
     */
    public GraphProperties(Graph graph) {
        this.graph = graph;

        adjacencyList = graph.getAdjacencyList();

        calculateAdjacencyList();
        calculateConnectivity();
    }

    /**
     * Calculates the adjacency of every node and saves them in the HashMap attribute of the graph
     */
    private void calculateAdjacencyList(){
        List<Edge> listOfEdges = graph.getListOfEdges();

        for(Edge edge : listOfEdges){   //go through every edge to update every adjacent correctly
            Node source = edge.getSourceNode();
            Node target = edge.getTargetNode();
            int sourceId = source.getId();
            int targetId = target.getId();
            //add way source->target and the way back target->source
            //source->target
            if(adjacencyList.containsKey(sourceId)) //check if there is already a entry for this node, otherwise create new List to add
                adjacencyList.get(sourceId).add(target);
            else{   //create new List to add
                List<Node> t = new ArrayList<>();
                t.add(target);
                adjacencyList.put(sourceId, t);
            }
            //target->source
            if(adjacencyList.containsKey(targetId)) //check if there is already a entry for this node, otherwise create new List to add
                adjacencyList.get(targetId).add(source);
            else{   //create new List to add
                List<Node> s = new ArrayList<>();
                s.add(source);
                adjacencyList.put(targetId, s);
            }
        }
    }

    /**
     * Checks if the calling graph is connected or not and sets his attribute to the result of the calculation(true/false)
     */
    public void calculateConnectivity(){
        //https://en.wikipedia.org/wiki/Breadth-first_search - Explanation
        List<Node> unvisitedNodes = new ArrayList<>(graph.getListOfNodes());
        List<Node> queue = new ArrayList<>();
        Node currentNode;

        try{
            currentNode = unvisitedNodes.get(0);
        } catch(NullPointerException e){
            Logger l = graph.getGraphLogger();
            l.log(Level.SEVERE, "Graph has no nodes", e);
            throw e;
        }

        unvisitedNodes.remove(currentNode);
        queue.add(currentNode);

        while(!queue.isEmpty() && !unvisitedNodes.isEmpty()){
            currentNode = queue.get(0);
            for(Node adjacentNode : adjacencyList.get(currentNode.getId())){
                if(unvisitedNodes.contains(adjacentNode)){
                    unvisitedNodes.remove(adjacentNode);
                    queue.add(adjacentNode);
                }
            }
            queue.remove(0);
        }
        graph.setConnectivity(unvisitedNodes.isEmpty());
    }


}