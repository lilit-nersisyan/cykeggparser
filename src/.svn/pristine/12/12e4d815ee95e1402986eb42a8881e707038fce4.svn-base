package org.cytoscape.keggparser.parsing;

import org.cytoscape.keggparser.com.Graph;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Parser {
    private static final String EXTERNAL_DTD_LOADING_FEATURE =
            "http://apache.org/xml/features/nonvalidating/load-external-dtd";

    public Graph parse(File kgmlFile) throws Exception {
        if (!kgmlFile.exists() || kgmlFile.length() == 0)
            throw new Exception("Empty or non-existing file");
        Graph graph = new Graph();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            InputStream xmlInput = new FileInputStream(kgmlFile);
            factory.setNamespaceAware(true);
            factory.setValidating(false);

            SAXParser saxParser = factory.newSAXParser();
            saxParser.getXMLReader().setFeature(EXTERNAL_DTD_LOADING_FEATURE, false);

            SaxHandler handler = new SaxHandler(graph);
            saxParser.parse(xmlInput, handler);
            return graph;
        } catch (Throwable err) {
            throw new Exception(err.getMessage());
        }

    }


}
