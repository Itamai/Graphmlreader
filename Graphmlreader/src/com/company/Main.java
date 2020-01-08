package com.company;

import javax.xml.parsers.*;
import org.xml.sax.SAXException;
import java.io.*;



public class Main
{
    public static void main(String argv[]) throws SAXException, ParserConfigurationException, IOException {

        Graph graphml = new Graph();
        graphml.readFile("C:\\Users\\ati\\Documents\\small_graph.graphml");
        GraphProperties test = new GraphProperties(graphml);
        test.showStatus();
        /*graphml.printAllNodes();
        graphml.printAllEdges();*/
        test.shortestPath(Integer.parseInt(String.valueOf(1)),Integer.parseInt(String.valueOf(14)));

    }
}        
