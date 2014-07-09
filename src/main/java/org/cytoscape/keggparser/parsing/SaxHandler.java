package org.cytoscape.keggparser.parsing;

import org.cytoscape.keggparser.com.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.awt.*;

public class SaxHandler extends DefaultHandler {
    private Graph graph;
    private KeggNode node;
    private KeggRelation relation;

    public SaxHandler(Graph graph) {
        this.graph = graph;
    }

    public void startDocument() throws SAXException {

    }

    public void endDocument() throws SAXException {

    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (qName.equals("entry")){
            node = new KeggNode(Integer.parseInt(attributes.getValue(EKGMLNodeAttrs.KGML_ID.getAttrName())),
                    attributes.getValue(EKGMLNodeAttrs.KGML_NAME.getAttrName()),
                    attributes.getValue(EKGMLNodeAttrs.KGML_TYPE.getAttrName()));
            node.setLink(attributes.getValue(EKGMLNodeAttrs.KGML_LINK.getAttrName()));
        }
        else if (qName.equals("graphics")){
            String value;
            if ((value = attributes.getValue(EKGMLNodeAttrs.KGML_NAME.getAttrName()))!=null)
                node.setGraphicsName(value);
            else {
                ParsingReportGenerator.getInstance()
                        .append("Attribute " + EKGMLNodeAttrs.KGML_NAME.getAttrName()
                                + " name does not exist for node " + node.toString());
                node.setGraphicsName(EKGMLNodeAttrs.KGML_NAME.getDefaultValue());
            }
            if((value = attributes.getValue(EKGMLNodeAttrs.KGML_FGCOLOR.getAttrName()))!=null)
                node.setFgColorAttr(value);
            else {
                ParsingReportGenerator.getInstance()
                        .appendLine("Attribute "
                                + EKGMLNodeAttrs.KGML_FGCOLOR.getAttrName()
                                + " does not exist for node " + node.toString());
                node.setFgColorAttr(EKGMLNodeAttrs.KGML_FGCOLOR.getDefaultValue());
            }
            try {
                node.setFgColor(Color.decode
                        (attributes.getValue(EKGMLNodeAttrs.KGML_FGCOLOR.getAttrName())));
            } catch (Exception e){
                ParsingReportGenerator.getInstance()
                        .appendLine("Could not set "
                                + EKGMLNodeAttrs.KGML_FGCOLOR.getAttrName()
                                + " for node " + node.toString());
                node.setFgColor(Color.decode(EKGMLNodeAttrs.KGML_FGCOLOR.getDefaultValue()));
            }

            if ((value = attributes.getValue(EKGMLNodeAttrs.KGML_BGCOLOR.getAttrName()))!=null)
                node.setBgColorAttr(value);
            else {
                ParsingReportGenerator.getInstance().appendLine("Attribute "
                        + EKGMLNodeAttrs.KGML_BGCOLOR.getAttrName()
                        + "name does not exist for node " + node.toString());
                node.setBgColorAttr(EKGMLNodeAttrs.KGML_BGCOLOR.getDefaultValue());
            }
            try{
                node.setBgColor(Color.decode(attributes.getValue(EKGMLNodeAttrs.KGML_BGCOLOR.getAttrName())));
            } catch (Exception e){
                ParsingReportGenerator.getInstance().appendLine("Could not set "
                        + EKGMLNodeAttrs.KGML_BGCOLOR.getAttrName()
                        + " for node " + node.toString());
                node.setBgColor(Color.decode(EKGMLNodeAttrs.KGML_BGCOLOR.getDefaultValue()));
            }

            if ((value = attributes.getValue(EKGMLNodeAttrs.KGML_TYPE.getAttrName()))!=null)
                node.setShape(value);
            else {
                ParsingReportGenerator.getInstance().appendLine("Attribute "
                        + EKGMLNodeAttrs.KGML_TYPE.getAttrName()
                        + " does not exist for node " + node.toString());
                node.setShape(EKGMLNodeAttrs.KGML_TYPE.getDefaultValue());
            }

            int x = (int) Double.parseDouble(EKGMLNodeAttrs.KGML_X.getDefaultValue());
            int y = (int) Double.parseDouble(EKGMLNodeAttrs.KGML_Y.getDefaultValue());
            int width = (int) Double.parseDouble(EKGMLNodeAttrs.KGML_WIDTH.getDefaultValue());
            int height = (int) Double.parseDouble(EKGMLNodeAttrs.KGML_HEIGHT.getDefaultValue());

            if ((value = attributes.getValue(EKGMLNodeAttrs.KGML_COORDS.getAttrName())) != null){
                String[] coords = value.split(",");
                if (coords.length != 4) {
                    ParsingReportGenerator.getInstance()
                            .appendLine(EKGMLNodeAttrs.KGML_COORDS.getAttrName()
                                    + " attribute contains "
                                    + coords.length + " elements, instead of 4");
                } else{
                    try{
                        x = (int) Double.parseDouble(coords[0]);
                        y = (int) Double.parseDouble(coords[1]);
                        int x2 = (int) Double.parseDouble(coords[2]);
                        int y2 = (int) Double.parseDouble(coords[3]);
                        if(x2 - x > 1)
                            width = x2 - x;
                        else
                            width = 5;
                        if (y2 - y >  1)
                            height = y2 - y;
                        else
                            height = 5;
                    } catch (Exception e){
                    }
                }
            } else{
                if ((value = attributes.getValue(EKGMLNodeAttrs.KGML_X.getAttrName()))!=null) {
                    try {
                        x = (int) Double.parseDouble(value);
                    } catch (Exception e) {
                    }
                }
                if ((value = attributes.getValue(EKGMLNodeAttrs.KGML_Y.getAttrName()))!=null) {
                    try {
                        y = (int) Double.parseDouble(value);
                    } catch (Exception e) {
                    }
                }
                if ((value = attributes.getValue(EKGMLNodeAttrs.KGML_WIDTH.getAttrName()))!=null) {
                    try {
                        width = (int) Double.parseDouble(value);
                    } catch (Exception e) {
                    }
                }
                if ((value = attributes.getValue(EKGMLNodeAttrs.KGML_HEIGHT.getAttrName()))!=null) {
                    try {
                        height = (int) Double.parseDouble(value);
                    } catch (Exception e) {
                    }
                }
            }

            node.setX(x);
            node.setY(y);
            node.setWidth(width);
            node.setHeight(height);
        }
        else if (qName.equals("component")){
            node.addComponentId(Integer.parseInt(attributes.getValue(EKGMLNodeAttrs.KGML_ID.getAttrName())));
        }

        else if(qName.equals("relation")){
            relation = new KeggRelation(graph.getNode(Integer.parseInt(attributes.getValue(EKGMLEdgeAttrs.KGML_ENTRY1.getAttrName()))),
                    graph.getNode(Integer.parseInt(attributes.getValue(EKGMLEdgeAttrs.KGML_ENTRY2.getAttrName()))),
                    attributes.getValue(EKGMLEdgeAttrs.KGML_TYPE.getAttrName()));
        }

        else if (qName.equals("subtype")){
            relation.setSubtype(attributes.getValue(EKGMLEdgeAttrs.KGML_SUBTYPE_NAME.getAttrName()));
            relation.setRelationValue(attributes.getValue(EKGMLEdgeAttrs.KGML_SUBTYPE_VALUE.getAttrName()));
        }

        else if (qName.equals("pathway")){
            graph.setPathwayName(attributes.getValue(EKGMLNetworkAttrs.NAME.getAttrName()));
            graph.setOrganism(attributes.getValue(EKGMLNetworkAttrs.ORGANISM.getAttrName()));
            graph.setNumber(attributes.getValue(EKGMLNetworkAttrs.NUMBER.getAttrName()));
            graph.setTitle(attributes.getValue(EKGMLNetworkAttrs.TITLE.getAttrName()));
            graph.setImage(attributes.getValue(EKGMLNetworkAttrs.IMAGE.getAttrName()));
            graph.setLink(attributes.getValue(EKGMLNetworkAttrs.LINK.getAttrName()));
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("entry")){
            graph.addNode(node);
        }
        else if (qName.equals("relation")){
            graph.addRelation(relation);
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException {
    }

    public void ignorableWhitespace(char ch[], int start, int length)
            throws SAXException {
    }

}