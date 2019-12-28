package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Graph {

    public NodeList edges;
    public NodeList nodes;

    public void nodelist(){

        nodes = Main.doc.getElementsByTagName("node");
        for(int i= 0; i < nodes.getLength(); i++) {
            System.out.println("\tVertex " + i + ": 'v" + nodes.item(i).getTextContent().trim() + "'");
        }
    }

    public void edgelist(){
        edges = Main.doc.getElementsByTagName("edge");
        for(int b = 0; b < edges.getLength(); b++) {
            System.out.println("\tEdge " + b + ": 'e" + edges.item(b).getChildNodes().item(1).getTextContent() + "', weight: " + edges.item(b).getChildNodes().item(3).getTextContent());
        }
    }

    public void destination(){
        for(int i = 0; i < edges.getLength(); i++) {
            System.out.println("\tSource: " + edges.item(i).getAttributes().getNamedItem("source").getNodeValue() + ", target: " + edges.item(i).getAttributes().getNamedItem("target").getNodeValue());
        }
    }

}
