package org.cytoscape.keggparser.tuning.tse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * A class to generate an xml formatted file for expression data storage.
 */
public class GeneCardsXMLCreator {
    private DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;
    private Document document;
    private Element rootElement;
    private String[] tissueList;
    private TreeMap<Integer, double[]> expMap = new TreeMap<Integer, double[]>();


    private void loadBioGpsData(File source) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(source));
            String line, tissues;
            tissues = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(tissues);
            tissueList = new String[tokenizer.countTokens() - 1];
            tokenizer.nextToken();
            int index = 0;
            while (tokenizer.hasMoreTokens())
                tissueList[index++] = tokenizer.nextToken();


            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#N/A")) {
                    tokenizer = new StringTokenizer(line, "\t");
                    int gene = Integer.parseInt(tokenizer.nextToken());
                    int i = 0;
                    double[] expList = new double[tissueList.length];
                    while (tokenizer.hasMoreTokens()) {
                        expList[i++] = Double.parseDouble(tokenizer.nextToken());
                    }
                    expMap.put(gene, expList);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void createGeneCardsXML(File sourceFile, File outFile) {
        try {
            loadBioGpsData(sourceFile);
//            System.out.println(tissueList);
//            System.out.println(expMap.toString());
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements  (genes)
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("root");
            doc.appendChild(rootElement);

            for (int geneId : expMap.keySet()) {
                Element gene = doc.createElement("geneId_" + geneId);
                rootElement.appendChild(gene);
//                gene.setAttribute("ID", "" + geneId);
                double[] exps = expMap.get(geneId);
//                Element expression = doc.createElement("expression");
//                gene.appendChild(expression);
                for (int i = 0; i < tissueList.length; i++) {
                    gene.setAttribute(tissueList[i], "" + exps[i]);
                }
            }


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outFile);

            // Output to console for testing
//            result = new StreamResult(System.out);

            transformer.transform(source, result);

//            System.out.println("File saved!");

        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public static void main(String[] args) {
        GeneCardsXMLCreator creator = new GeneCardsXMLCreator();
        File outFile = new File("GeneCards/bioGps.xml");
        File source = new File("GeneCards/bioGpsFinal.txt");
        creator.createGeneCardsXML(source, outFile);

    }
}
