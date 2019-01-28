package com.symbolScraper.TrueBox;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import scala.xml.Elem;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class TrainingGTParser {

    File inputFile;
    TrainingGTParser(File inputFile){
        this.inputFile=inputFile;
    }

    public ArrayList<GT> readGT() throws JDOMException, IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        NodeList nList = doc.getElementsByTagName("Match");
        //System.out.println("----------------------------");
        ArrayList<GT> GroundTruth = new ArrayList<>();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            //System.out.println("\nCurrent Element :" + nNode);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;


                //System.out.println("Page id : "
                //        + eElement.getAttribute("pageid").toString());
                ArrayList<Integer> chararray = new ArrayList<>();
                GroundTruth.add(new GT(Integer.parseInt(eElement.getAttribute("pageid").toString()),chararray));
                NodeList childNodes = eElement.getChildNodes();

                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        //System.out.println("Char : "
                        //        + node.getTextContent());
                        Element nattr = (Element) node;

                        //System.out.println("\t id:"+nattr.getAttribute("id").toString());
                        chararray.add(Integer.parseInt(nattr.getAttribute("id").toString()));
                    }
                }
            }
        }

        return GroundTruth;

    }


    public static void main(String args[]) throws JDOMException, IOException, ParserConfigurationException, SAXException {
        File file =  new File("C:\\Users\\ritvi\\PycharmProjects\\CapstoneProject\\MATHXML");
        TrainingGTParser gtParser = new TrainingGTParser(file);

        ArrayList<GT> gt=        gtParser.readGT();

        for(GT g :gt){
            System.out.println("Page:"+g.pageid);
            for(int i:g.charid)
            System.out.println(("\tChar id:"+i));
        }
    }

}

class GT{
    int pageid;
    ArrayList<Integer> charid;

    GT(int pageid, ArrayList<Integer> charid ){
        this.pageid=pageid;
        this.charid=charid;
    }

}