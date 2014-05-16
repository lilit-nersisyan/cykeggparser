package org.cytoscape.keggparser.tuning.string;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

/**
 * A class to parse the xml formatted gene expression data from GeneCards.
 */

public class StringParser {

    private Document document;
    private File stringXml = new File("String/string.xml");
    private Element rootElement;
    private static StringParser parser;

    private StringParser() {
    }

    public static StringParser getParser() {
        if (parser == null)
            parser = new StringParser();
        return parser;
    }

    public void loadDocument() {
        if (document == null)
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                document = builder.parse(stringXml);
                this.rootElement = document.getDocumentElement();
            } catch (Exception e) {
//                System.out.println(e.getMessage());
            }
    }

    public double getScore(String source, String target, ArrayList<String> sources) {
        if (rootElement == null)
            loadDocument();
        rootElement = document.getDocumentElement();
        NodeList elements = rootElement.getElementsByTagName("rel_" + source
                + "_" + target);
        if (elements.getLength() != 0) {
            Element relation = (Element) elements.item(0);
            for (String db : sources)
                if (relation.getAttribute("sources").contains(db))
                    return Double.parseDouble(relation.getAttribute("score"));
        } else {
//            System.out.println("searching for the reverse relation");
            elements = rootElement.getElementsByTagName("rel_" + target
                    + "_" + source);
            if (elements.getLength() != 0) {
//                System.out.println("reversed relation found");
                Element relation = (Element) elements.item(0);
                for (String db : sources)
                    if (relation.getAttribute("sources").contains(db))
                        return Double.parseDouble(relation.getAttribute("score"));
            } else {
//                System.out.println("rel_" + source + "_" + target
//                        + " not found in the xml");
            }
        }
        return Double.NaN;
    }

}
