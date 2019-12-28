package com.company;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import java.io.*;
import java.util.Scanner;

public class Main
{
public static Document doc;
public static String filepath;

    public static void main(String argv[]) throws IOException, SAXException, ParserConfigurationException {


        Scanner scan = new Scanner(System.in);
        System.out.print("Enter input file path: ");
        filepath = scan.next();
        scan.close();
        File file = new File("C:\\Users\\ati\\Documents\\small_graph.graphml");
        if(file.exists() && !file.isDirectory()) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(file);
        }
        else
            System.out.println("No or Wrong File provided");

        Graph test = new Graph();
        test.nodelist();
        test.edgelist();
        test.destination();

    }
}