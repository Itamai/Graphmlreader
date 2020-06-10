package com.company;

import javax.xml.parsers.*;
import org.xml.sax.SAXException;
import java.io.*;

// C:\Users\Torben\Desktop\java-project\small_graph.graphml
// C:\Users\Torben\Desktop\java-project\small_graph_v2.graphml
// C:\Users\Torben\Desktop\java-project\small_graph_v3.graphml
// C:\Users\Torben\Desktop\java-project\small_graph_v4.graphml
// C:\Users\Torben\Desktop\java-project\large_graph.graphml

public class Main
{
    public static void main(String[] argv) throws IOException, SAXException, ParserConfigurationException {

        Graph graphml = new Graph();
        if(graphml.readFile(argv[0])){
            GraphProperties test = new GraphProperties(graphml);
            //test.showStatus();
            /*graphml.printAllNodes();
            graphml.printAllEdges();*/
            test.shortestPathDijkstra(Integer.parseInt(argv[1]),Integer.parseInt(argv[2]));
            test.calculateBetweennessCentrality();
        }
    }
}
