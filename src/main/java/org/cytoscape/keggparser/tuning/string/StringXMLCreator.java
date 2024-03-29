package org.cytoscape.keggparser.tuning.string;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * A class to generate an xml formatted file for expression data storage.
 */
public class StringXMLCreator {
    private DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;
    private Document document;
    private Element rootElement;
    private String[] tissueList;
    private ArrayList<Interaction> interactions = new ArrayList<Interaction>();
    private HashMap<String, TreeSet<String>> idMap = new HashMap<String, TreeSet<String>>();

    private class Interaction {
        String ensembl1;
        String ensembl2;
        String entrez1;
        String entrez2;
        String mode;
        String action;
        String isActing;
        String score;
        String sources;
        String transferredSources;

        private Interaction(String ensembl1, String ensembl2, String entrez1,
                            String entrez2, String mode, String action,
                            String isActing, String score, String sources,
                            String transferredSources) {
            this.ensembl1 = ensembl1;
            this.ensembl2 = ensembl2;
            this.entrez1 = entrez1;
            this.entrez2 = entrez2;
            this.mode = mode;
            this.action = action;
            this.isActing = isActing;
            this.score = score;
            this.sources = sources;
            this.transferredSources = transferredSources;
        }
    }

    private void loadIdMap(File idMapFile) {
        try {
            BufferedReader mapReader = new BufferedReader(new FileReader(idMapFile));
            String line, ensembl = "", entrez = "";
            StringTokenizer tokenizer;
            TreeSet<String> entrezList;
            while ((line = mapReader.readLine()) != null) {
                tokenizer = new StringTokenizer(line);
                if (tokenizer.hasMoreTokens())
                    ensembl = tokenizer.nextToken();
//                System.out.println(ensembl);
                if (tokenizer.hasMoreTokens())
                    entrez = tokenizer.nextToken();
                if (!idMap.containsKey(ensembl)) {
                    entrezList = new TreeSet<String>();
                    entrezList.add(entrez);
                    idMap.put(ensembl, entrezList);
                } else
                    idMap.get(ensembl).add(entrez);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void loadStringData(File stringFile) {
        try {
            BufferedReader stringReader = new BufferedReader(new FileReader(stringFile));

            String ensembl1 = "";
            String ensembl2 = "";
            String mode = "";
            String action = "";
            String isActing = "";
            String score = "";
            String sources = "";
            String transferredSources = "";
            TreeSet<String> entrez1List;
            TreeSet<String> entrez2List;
            StringTokenizer tokenizer;
            String line;
            Interaction interaction;
            while ((line = stringReader.readLine()) != null) {
                tokenizer = new StringTokenizer(line, "\t", true);
                ensembl1 = setToken(tokenizer, ensembl1).substring(5);
                ensembl2 = setToken(tokenizer, ensembl2).substring(5);
//                System.out.println(ensembl1 + " " + ensembl2);
                mode = setToken(tokenizer, mode);
                action = setToken(tokenizer, action);
                isActing = setToken(tokenizer, isActing);
                score = setToken(tokenizer, score);
                sources = setToken(tokenizer, sources);
                transferredSources = setToken(tokenizer, transferredSources);
                entrez1List = idMap.get(ensembl1);
                entrez2List = idMap.get(ensembl2);
                if (entrez1List != null && entrez2List != null)
                    for (String entrez1 : entrez1List)
                        for (String entrez2 : entrez2List) {
                            interaction = new Interaction(ensembl1, ensembl2,
                                    entrez1, entrez2, mode, action, isActing, score,
                                    sources, transferredSources);
                            interactions.add(interaction);
                        }
//                else if (entrez1List == null)
//                    System.out.println(ensembl1 + "\tentrez not found");
//                else
//                    System.out.println(ensembl2 + "\tentrez not found");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private String setToken(StringTokenizer tokenizer, String variable) {
        if (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!token.equals("\t")) {
                variable = token;
                if (tokenizer.hasMoreTokens())
                    tokenizer.nextToken();
            }
        } else
            variable = "";
        return variable;
    }

    public void createStringXML(File stringFile, File idMapFile, File outFile) {
        try {
            loadIdMap(idMapFile);
            loadStringData(stringFile);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("root");
            doc.appendChild(rootElement);

            for (Interaction interaction : interactions) {
                Element relation = doc.createElement("rel_" + interaction.entrez1 + "_" +
                        interaction.entrez2);
                relation.setAttribute("ensembleId1", interaction.ensembl1);
                relation.setAttribute("ensembleId2", interaction.ensembl2);
                relation.setAttribute("entrezId1", interaction.entrez1);
                relation.setAttribute("entrezId2", interaction.entrez2);
                relation.setAttribute("mode", interaction.mode);
                relation.setAttribute("action", interaction.action);
                relation.setAttribute("isActing", interaction.isActing);
                relation.setAttribute("score", interaction.score);
                relation.setAttribute("sources", interaction.sources);
                relation.setAttribute("transferredSources", interaction.transferredSources);

                rootElement.appendChild(relation);
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

        } catch (ParserConfigurationException e){
            e.printStackTrace();
        }
         catch (TransformerConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public static void main(String[] args) {
        StringXMLCreator creator = new StringXMLCreator();
        File outFile = new File("String/string.xml");
        File stringFile = new File("d:\\String\\protein.actions.detailed.v9.05_human.txt");
        File idMapFile = new File("d:\\Workspace\\cytoscape_app_fix\\String\\ensemble_entrez_map.txt");
        creator.createStringXML(stringFile, idMapFile, outFile);

    }
}
