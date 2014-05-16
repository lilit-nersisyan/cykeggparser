package org.cytoscape.keggparser.tuning.tse;

import org.cytoscape.keggparser.KEGGParserPlugin;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * A class to generate an xml formatted file for expression data storage.
 */
public class GeneExpXmlCreator {
    private DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;
    private Document document;
    private Element rootElement;
    private String[] tissueList;
    private TreeMap<Integer, double[]> expMap = new TreeMap<Integer, double[]>();


    public boolean loadData(File source) {
        try {
            Scanner scanner = new Scanner(source);
            String line;
            if (!scanner.hasNext()) {
                JOptionPane.showMessageDialog(KEGGParserPlugin.cytoscapeDesktopService.getJFrame(),
                        "The file" + source.getName() + "is empty!");
                return false;
            }
            line = scanner.nextLine();
            if (!line.startsWith("ID\t")) {
                JOptionPane.showMessageDialog(KEGGParserPlugin.cytoscapeDesktopService.getJFrame(),
                        "The gene expression file should be tab delimited and " +
                        "start with \"ID\" column. " +
                        "Please choose a valid file and try again.");
                return false;
            }
            StringTokenizer tokenizer = new StringTokenizer(line, "\t");
            int numOfColumns = tokenizer.countTokens() - 1;
            if (numOfColumns == 0) {
                JOptionPane.showMessageDialog(KEGGParserPlugin.cytoscapeDesktopService.getJFrame(),
                        "The gene expression file should contain at " +
                        "least one tissue header. " +
                        "Please choose a valid file and try again.");
                return false;
            }
            tissueList = new String[numOfColumns];
            tokenizer.nextToken();
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                tissueList[i++] = tokenizer.nextToken();
            }
            if (!scanner.hasNextLine()) {
                JOptionPane.showMessageDialog(KEGGParserPlugin.cytoscapeDesktopService.getJFrame(),
                        "The gene expression file should contain at " +
                        "least one row with gene expression values. " +
                        "Please choose a valid file and try again.");
                return false;
            }
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                tokenizer = new StringTokenizer(line, "\t");
                if (tokenizer.countTokens() != numOfColumns + 1) {
                    JOptionPane.showMessageDialog(KEGGParserPlugin.cytoscapeDesktopService.getJFrame(),
                            "Each row in the gene expression file should contain " +
                            "gene identifiear and tab-delimited expression values for each of the tissues " +
                            "Please choose a valid file and try again.");
                    return false;
                }
                tokenizer = new StringTokenizer(line, "\t");
                int gene = -1;
                try {
                    gene = Integer.parseInt(tokenizer.nextToken());
                } catch (NumberFormatException e) {
                }
                if (gene != -1) {
                    i = 0;
                    double[] expList = new double[tissueList.length];
                    while (tokenizer.hasMoreTokens()) {
                        expList[i++] = Double.parseDouble(tokenizer.nextToken());
                    }
                    expMap.put(gene, expList);
                }
            }


        } catch (FileNotFoundException e) {
            LoggerFactory.getLogger(GeneExpXmlCreator.class).error(e.getMessage());
        }
        return true;
    }

    public String[] getTissueList() {
        return tissueList;
    }

    public void createXml(File outFile) throws Exception{
        try {
            if (expMap.size() == 0)
                return;
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

        } catch (Exception e){
            throw new Exception(e.getMessage());
        }


    }

}
