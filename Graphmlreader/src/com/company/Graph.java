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

    private HashMap<Integer, Node> nodeHashMap = new HashMap<Integer, Node>();
    private HashMap<Integer, Edge> edgeHashMap = new HashMap<Integer, Edge>();

    public Document doc=null;

    public boolean readFile(String str) throws ParserConfigurationException, IOException, SAXException {
//Creates a object pointing to the file
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
        System.out.println("No or Wrong File provided");
        return false;
    }

    public void printAllNodes(){
    //prints all nodes on the console
        for (Node value : nodeHashMap.values()) {
            System.out.println("\tVertex " + value.getId() + ": 'v" + value.getId() + "'");
        }
    }

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

    private void saveNodesFromFile(){
        for(int n = 0; n < nodesTagList.getLength(); n++){
            int t = Integer.parseInt(nodesTagList.item(n).getChildNodes().item(1).getTextContent());
            nodeHashMap.put(t, new Node(t));
        }
    }

    private void saveEdgesFromFile(){
        for(int n = 0; n < edgesTagList.getLength(); n++){
            int sourceNodeID = Integer.parseInt(edgesTagList.item(n).getAttributes().getNamedItem("source").getNodeValue().substring(1));
            int targetNodeID = Integer.parseInt(edgesTagList.item(n).getAttributes().getNamedItem("target").getNodeValue().substring(1));
            int edgeId = Integer.parseInt(edgesTagList.item(n).getChildNodes().item(1).getTextContent());
            int edgeWeight = Integer.parseInt(edgesTagList.item(n).getChildNodes().item(3).getTextContent());

            edgeHashMap.put(edgeId, new Edge(new Node(sourceNodeID), new Node(targetNodeID), edgeId, edgeWeight));
        }
    }

    public HashMap<Integer, Node> getNodeHashMap(){
        return nodeHashMap;
    }

    public HashMap<Integer, Edge> getEdgeHashMap(){
        return edgeHashMap;
    }

    public int getAmountOfNodes() {
        return nodeHashMap.size();
    }

    public int getAmountOfEdges() {
        return edgeHashMap.size();
    }
}
