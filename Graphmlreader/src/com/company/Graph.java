package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Graph {

    private NodeList edgesTagList;
    private NodeList nodesTagList;

    private HashMap<Integer, Node> nodeHashMap = new HashMap<>();
    private HashMap<Integer, Edge> edgeHashMap = new HashMap<>();

    public Document doc = null;

    /**
     * Gets the path of the file to read where the graph is in
     * @param str - the path of the file to read
     * @return returns true when the given file exists, false if not
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public boolean readFile(String str) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(str);
//        check if file exists and if its not a directory
        if(file.exists() && !file.isDirectory()) {
//        	creates a object tree from xml document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(file);
            nodesTagList = doc.getElementsByTagName("node");
            edgesTagList = doc.getElementsByTagName("edge");
            saveNodesFromFile();
            saveEdgesFromFile();
            return true;
        }
        System.out.println("No or wrong file provided.");
        return false;
    }

    /**
     * Stores all nodes from the nodeTagList of type w3c.dom.NodeList to a HashMap<Integer, Node> nodeHashMap
     */
    private void saveNodesFromFile(){
        for(int n = 0; n < nodesTagList.getLength(); n++){
            int t = Integer.parseInt(nodesTagList.item(n).getChildNodes().item(1).getTextContent());
            nodeHashMap.put(t, new Node(t));
        }
    }

    /**
     * Stores all nodes from the edgeTagList of type w3c.dom.NodeList to a HashMap<Integer, Edge> edgeHashMap
     */
    private void saveEdgesFromFile(){
        for(int n = 0; n < edgesTagList.getLength(); n++){
            int sourceNodeID = Integer.parseInt(edgesTagList.item(n).getAttributes().getNamedItem("source").getNodeValue().substring(1));
            int targetNodeID = Integer.parseInt(edgesTagList.item(n).getAttributes().getNamedItem("target").getNodeValue().substring(1));
            int edgeId = Integer.parseInt(edgesTagList.item(n).getChildNodes().item(1).getTextContent());
            int edgeWeight = Integer.parseInt(edgesTagList.item(n).getChildNodes().item(3).getTextContent());

            edgeHashMap.put(edgeId, new Edge(new Node(sourceNodeID), new Node(targetNodeID), edgeId, edgeWeight));
        }
    }

    /**
     * Prints all nodes that are in the nodeHashMap
     */
    public void printAllNodes(){
        for (Node value : nodeHashMap.values()) {
            System.out.println("\tVertex " + value.getId() + ": 'v" + value.getId() + "'");
        }
    }

    /**
     * Prints all edges that are in the edgeHashMap
     */
    public void printAllEdges(){
        //prints all edges on the console
        for (Edge value : edgeHashMap.values()) {
            System.out.println(
                    "\tEdge " + value.getEdgeId() + ": 'e" + value.getEdgeId()
                            + "':\t " + value.getSourceNode().getId()
                            + " to " + value.getTargetNode().getId()
                            + " \twith weight " + value.getEdgeWeight()
            );
        }
    }


    /**
     * Getter for the nodeHashMap
     * @return - nodeHashMap
     */
    public HashMap<Integer, Node> getNodeHashMap(){
        return nodeHashMap;
    }

    /**
     * Getter for the edgeHashMap
     * @return - edgeHashMap
     */
    public HashMap<Integer, Edge> getEdgeHashMap(){
        return edgeHashMap;
    }

    /**
     * Getter for the amount of nodes in the nodeHashMap
     * @return - amount of nodes in the nodeHashMap
     */
    public int getAmountOfNodes() {
        return nodeHashMap.size();
    }

    /**
     * Getter for the amount of edges in the edgeHashMap
     * @return - amount of edges in the edgeHashMap
     */
    public int getAmountOfEdges() {
        return edgeHashMap.size();
    }
}
