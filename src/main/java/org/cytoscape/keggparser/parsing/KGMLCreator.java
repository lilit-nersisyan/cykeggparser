package org.cytoscape.keggparser.parsing;


import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.com.*;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.util.Collection;

public class KGMLCreator {
    boolean biopax2 = false;
    boolean biopax3 = false;
    boolean kgml = false;

    public void setFilterForConversion(int filterForConversion) {
        switch (filterForConversion) {
            case KGMLConverter.BioPAX2: {
                this.biopax2 = true;
                this.biopax3 = false;
                this.kgml = false;
                break;
            }
            case KGMLConverter.BioPAX3: {
                this.biopax2 = false;
                this.biopax3 = true;
                this.kgml = false;
                break;
            }
            default: {
                this.biopax2 = false;
                this.biopax3 = false;
                this.kgml = true;
                break;
            }
        }
    }

    public void createKGML(CyNetwork network, File outFile) throws Exception {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element pathway = doc.createElement("pathway");
            doc.appendChild(pathway);

            String attrValue = "", attr = "";

            setPathwayElements(pathway, network);
            setNodeElements(doc, pathway, network);
            setEdgeElements(doc, pathway, network);


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.kegg.jp/kegg/xml/KGML_v0.7.1_.dtd");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outFile);
            transformer.transform(source, result);
            LoggerFactory.getLogger(KGMLCreator.class).debug("File saved!");

        } catch (ParserConfigurationException e){
            throw new Exception(e.getMessage());
        } catch (TransformerConfigurationException e) {
            throw new Exception(e.getMessage());
        }

    }

    private void setEdgeElements(Document doc, Element pathway, CyNetwork network) throws Exception {
        String attrValue = "", attr = "";
        for (Object edge : network.getEdgeList()) {
            try {
                CyEdge cyEdge = (CyEdge) edge;
                CyTable edgeTable = network.getDefaultEdgeTable();
                Element relation = doc.createElement("relation");

                String entry1 = edgeTable.getRow(cyEdge.getSUID()).
                        get(EKeggEdgeAttrs.ENTRY1.getAttrName(), String.class);
                if (entry1 == null || entry1.equals("")) {
                    String source_entryId = network.getDefaultNodeTable().getRow(cyEdge.getSource().getSUID())
                            .get(EKeggNodeAttrs.ENTRY_ID.getAttrName(), String.class);
                    if (source_entryId != null && !source_entryId.equals("")) {
                        entry1 = source_entryId;
                    } else {
                        entry1 = cyEdge.getSource().getSUID().toString();
                    }

                }

                String entry2 = edgeTable.getRow(cyEdge.getSUID()).
                        get(EKeggEdgeAttrs.ENTRY2.getAttrName(), String.class);
                if (entry2 == null || entry2.equals("")) {
                    String target_entryId = network.getDefaultNodeTable().getRow(cyEdge.getTarget().getSUID())
                            .get(EKeggNodeAttrs.ENTRY_ID.getAttrName(), String.class);
                    if (target_entryId != null && !target_entryId.equals("")) {
                        entry2 = target_entryId;
                    } else {
                        entry2 = cyEdge.getTarget().getSUID().toString();
                    }
                }


                relation.setAttribute(EKGMLEdgeAttrs.KGML_ENTRY1.getAttrName(), entry1);
                relation.setAttribute(EKGMLEdgeAttrs.KGML_ENTRY2.getAttrName(), entry2);

                attr = EKeggEdgeAttrs.KEGG_TYPE.getAttrName();
                String type = edgeTable.getRow(cyEdge.getSUID()).get(attr, String.class);
                if (type == null || type.equals("")) {
                    String sourceType = network.getDefaultNodeTable().getRow(cyEdge.getSource().getSUID())
                            .get(EKeggNodeAttrs.TYPE.getAttrName(), String.class);
                    String targetType = network.getDefaultNodeTable().getRow(cyEdge.getTarget().getSUID())
                            .get(EKeggNodeAttrs.TYPE.getAttrName(), String.class);
                    type = getRelationTypeFromEntryTypes(sourceType, targetType);
                }

                if (type == null || !EKeggRelationType.isTypeValid(type)) {
                    ParsingReportGenerator.getInstance().appendLine("The relation type for edge " + cyEdge.getSUID() +
                            " could not be detected. It was removed from the kgml file ");
                } else {

                    relation.setAttribute(EKGMLEdgeAttrs.KGML_TYPE.getAttrName(), type);

                    Element subtype1 = doc.createElement("subtype");
                    attr = EKeggEdgeAttrs.SUBTYPE1.getAttrName();
                    String subtype = getNotNullAttribute(edgeTable.getRow(cyEdge.getSUID()).get(attr, String.class));
                    if (subtype == null || subtype.equals("") || !EKeggRelationType.isSubTypeValid(subtype)) {
                        if (type.equals(KeggRelation.Maplink) && biopax3)
                            subtype = EKeggRelationType.INDIRECT_EFFECT_PP.getRelationSubType();
                        else
                            subtype = EKeggRelationType.BINDING.getRelationSubType();
                        subtype1.setAttribute(EKGMLEdgeAttrs.KGML_SUBTYPE_NAME.getAttrName(), subtype);
                        String subtypeValue = EKeggRelationType.getRelationValueFromSubType(subtype);
                        subtype1.setAttribute(EKGMLEdgeAttrs.KGML_SUBTYPE_VALUE.getAttrName(), subtypeValue);
                        ParsingReportGenerator.getInstance().appendLine("The relation subtype for edge " + cyEdge.getSUID() +
                                " was missing. The default value of " + subtype
                                + " was assigned.");
                    } else {
                        subtype1.setAttribute(EKGMLEdgeAttrs.KGML_SUBTYPE_NAME.getAttrName(), subtype);
                        String subtypeValue = EKeggRelationType.getRelationValueFromSubType(subtype);
                        subtype1.setAttribute(EKGMLEdgeAttrs.KGML_SUBTYPE_VALUE.getAttrName(), subtypeValue);
                    }

                    relation.appendChild(subtype1);

                    String subtype2Name = edgeTable.getRow(cyEdge.getSUID()).
                            get(EKeggEdgeAttrs.SUBTYPE2.getAttrName(), String.class);
                    if (subtype2Name != null && !subtype2Name.equals("")) {
                        if (EKeggRelationType.isSubTypeValid(subtype2Name)) {
                            Element subtype2 = doc.createElement("subtype");
                            subtype2.setAttribute(EKGMLEdgeAttrs.KGML_SUBTYPE_NAME.getAttrName(), subtype2Name);
                            subtype2.setAttribute(EKGMLEdgeAttrs.KGML_SUBTYPE_VALUE.getAttrName(),
                                    EKeggRelationType.getRelationValueFromSubType(subtype2Name));
                            relation.appendChild(subtype2);
                        } else {
                            ParsingReportGenerator.getInstance().appendLine("The attribute subtype2"
                                    + subtype2Name + " for edge " +
                                    cyEdge.getSUID() + " was invalid. It was not included in the KGML file.");
                        }
                    }
                    pathway.appendChild(relation);
                }
            } catch (DOMException e) {
                throw new Exception("Error occurred while processing edge elements for edge " +
                        edge.toString() + ": " + e.getMessage());
            }
        }
    }

    private void setNodeElements(Document doc, Element pathway, CyNetwork network) throws Exception {
        String attrValue = "", attr = "";
        CyTable nodeTable = null;
        int maxId = 0;
        try {
            nodeTable = network.getDefaultNodeTable();
            maxId = 0;
            for (Object node : network.getNodeList()) {
                CyNode cyNode = (CyNode) node;

                String entryId = getNotNullAttribute(nodeTable.getRow(cyNode.getSUID()).
                        get(EKeggNodeAttrs.ENTRY_ID.getAttrName(), String.class));
                int id = 0;
                try {
                    id = Integer.parseInt(entryId);
                } catch (Exception e) {
                }

                if (id > maxId)
                    maxId = id;

            }
            maxId++;
        } catch (Exception e) {
            throw new Exception("Error occurred while processing node entry ids: " + e.getMessage());
        }


        for (Object node : network.getNodeList()) {
            try {
                CyNode cyNode = (CyNode) node;
                Element entry = doc.createElement("entry");
                attr = EKeggNodeAttrs.ENTRY_ID.getAttrName();
                String entryId = getNotNullAttribute(nodeTable.getRow(cyNode.getSUID()).
                        get(attr, String.class));
                if (entryId.equals("")) {
                    entryId = "" + maxId;
                    maxId++;
                    nodeTable.getRow(cyNode.getSUID()).
                            set(EKeggNodeAttrs.ENTRY_ID.getAttrName(),
                                    entryId + "");
                }
                entry.setAttribute(EKGMLNodeAttrs.KGML_ID.getAttrName(), entryId);

                attr = EKeggNodeAttrs.NAME.getAttrName();
                setNodeEntry(attr, EKGMLNodeAttrs.KGML_NAME.getAttrName(),
                        nodeTable, cyNode, entryId, entry);
                attr = EKeggNodeAttrs.TYPE.getAttrName();

                setNodeEntry(attr, EKGMLNodeAttrs.KGML_TYPE.getAttrName(),
                        nodeTable, cyNode, entryId, entry,
                        EKeggNodeAttrs.getKeggNodeTypes(),
                        KeggNode.GENE);
                attr = EKeggNodeAttrs.LINK.getAttrName();
                setNodeEntry(attr, EKGMLNodeAttrs.KGML_LINK.getAttrName(),
                        nodeTable, cyNode, entryId, entry);

                Element graphics = doc.createElement("graphics");
                attr = EKeggNodeAttrs.GRAPHICSNAME.getAttrName();
                setNodeEntry(attr, EKGMLNodeAttrs.KGML_NAME.getAttrName(),
                        nodeTable, cyNode, entryId, graphics);
                attr = EKeggNodeAttrs.BGCOLOR.getAttrName();
                attrValue = getBgColorAttribute(network, cyNode);
                setNodeEntry(attrValue, attr, EKGMLNodeAttrs.KGML_BGCOLOR.getAttrName(),
                        nodeTable, cyNode, entryId, graphics);

                attr = EKeggNodeAttrs.FGCOLOR.getAttrName();
                attrValue = getFgColorAttribute(network, cyNode);
                setNodeEntry(attrValue, attr, EKGMLNodeAttrs.KGML_FGCOLOR.getAttrName(),
                        nodeTable, cyNode, entryId, graphics);


                attr = EKeggNodeAttrs.SHAPE.getAttrName();
                setNodeEntry(attr, EKGMLNodeAttrs.KGML_TYPE.getAttrName(),
                        nodeTable, cyNode, entryId, graphics,
                        EKeggNodeAttrs.getKeggNodeGraphicsTypes(),
                        KeggNode.RECTANGLE);

                Collection<CyNetworkView> networkViews = KEGGParserPlugin.networkViewManager.getNetworkViews(network);
                CyNetworkView networkView = null;
                if (networkViews.iterator().hasNext()) {
                    networkView = networkViews.iterator().next();
                    graphics.setAttribute(EKGMLNodeAttrs.KGML_X.getAttrName(),
                            "" + (int) Math.round(networkView
                                    .getNodeView(cyNode).getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION)));
                    graphics.setAttribute(EKGMLNodeAttrs.KGML_Y.getAttrName(),
                            "" + (int) Math.round(networkView
                                    .getNodeView(cyNode).getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION)));
                }

                String width = String.valueOf(
                        nodeTable.getRow(cyNode.getSUID()).
                                get(EKeggNodeAttrs.WIDTH.getAttrName(), String.class));
                if (width == null || width.equals("null")) {
                    if (networkView != null) {
                        width = KEGGParserPlugin.visualMappingManager.getVisualStyle(networkView).
                                getVisualMappingFunction(BasicVisualLexicon.NODE_WIDTH).
                                getVisualProperty().getDefault().toString();
                        if (width.contains("."))
                            width = width.substring(0, width.indexOf('.'));
                    }
                }
                graphics.setAttribute(EKGMLNodeAttrs.KGML_WIDTH.getAttrName(), width);
                String height = String.valueOf(nodeTable.getRow(cyNode.getSUID()).
                        get(EKeggNodeAttrs.HEIGHT.getAttrName(), String.class));
                if (height == null || height.equals("null")) {
                    height = KEGGParserPlugin.visualMappingManager.getVisualStyle(networkView).
                            getVisualMappingFunction(BasicVisualLexicon.NODE_HEIGHT).getVisualProperty().
                            getDefault().toString();
                    if (height.contains("."))
                        height = height.substring(0, height.indexOf('.'));
                }
                graphics.setAttribute(EKGMLNodeAttrs.KGML_HEIGHT.getAttrName(), height);

                entry.appendChild(graphics);
                pathway.appendChild(entry);
            } catch (DOMException e) {
                throw new Exception("Error occurred while processing node element for node " + node.toString() +
                        ": " + e.getMessage());
            }
        }
    }

    private void setPathwayElements(Element pathway, CyNetwork network) throws Exception {
        try {
            String attrValue = "", attr = "";
            for (EKGMLNetworkAttrs attribute : EKGMLNetworkAttrs.values()) {
                attr = attribute.getAttrName();
                if (attr.equals(EKGMLNetworkAttrs.NAME.getAttrName()))
                    attrValue = network.getRow(network).get(CyNetwork.NAME, String.class);
                else
                    attrValue = network.getRow(network).get(attr, String.class);
//                if (attrValue == null || attrValue.equals(""))
//                    attrValue = JOptionPane.showInputDialog(attr,
//                            "Insert the missing value for network attribute \"" + attr + "\"");
                if (attrValue == null || attrValue.equals(""))
                    ParsingReportGenerator.getInstance().appendLine("Pathway attribute " + attr + "is missing");
                pathway.setAttribute(attr, attrValue);

            }
        } catch (DOMException e) {
            throw new Exception("Error occurred while processing pathway attributes: " + e.getMessage());
        }
    }

    private String getRelationTypeFromEntryTypes(String sourceType, String targetType) {
        if (sourceType == null || targetType == null)
            return null;

        if ((sourceType.equals(KeggNode.COMPOUND) || targetType.equals(KeggNode.COMPOUND)))
            return KeggRelation.PCrel;
        if (sourceType.equals(KeggNode.GENE) && targetType.equals(KeggNode.GENE))
            return KeggRelation.PPrel;
        else if (!biopax3) {
            if (sourceType.equals(KeggNode.MAP) || targetType.equals(KeggNode.MAP))
                return KeggRelation.Maplink;
            else
                return KeggRelation.PPrel;
        } else
            return null;
    }

    private void setNodeEntry(String attrValue, String attr, String kgmlAttrName,
                              CyTable nodeTable, CyNode cyNode, String entryId,
                              Element entry) {
        if (attrValue == null || attrValue.equals(""))
            setNodeEntry(attr, kgmlAttrName, nodeTable, cyNode, entryId, entry, EKeggNodeAttrs.getKeggNodeTypes(), EKGMLNodeAttrs.KGML_TYPE.getDefaultValue());
        else {
            entry.setAttribute(kgmlAttrName, attrValue);
        }
    }

    private void setNodeEntry(String attr, String kgmlAttrName,
                              CyTable nodeTable, CyNode cyNode, String entryId,
                              Element entry) {
        String attrValue = nodeTable.getRow(cyNode.getSUID()).get(attr, String.class);
//        if (attrValue == null || attrValue.equals(""))
//            attrValue = JOptionPane.showInputDialog(KEGGParserPlugin.cytoscapeDesktopService.getJFrame(),
//                    "Please, fill in the missing attribute " + attr +
//                            " for node with id: " + entryId,
//                    "Attribute specification", JOptionPane.QUESTION_MESSAGE);
        if (attrValue == null || attrValue.equals(""))
            ParsingReportGenerator.getInstance().appendLine("Node attribute " + attr
                    + " is missing for node with id: " + entryId);
        entry.setAttribute(kgmlAttrName, attrValue);
    }

    private void setNodeEntry(String attr, String kgmlAttrName,
                              CyTable nodeTable, CyNode cyNode, String entryId,
                              Element entry, String[] values, String defaultValue) {
        String attrValue = nodeTable.getRow(cyNode.getSUID()).get(attr, String.class);
//        if (attrValue == null || attrValue.equals(""))
//            attrValue = (String) JOptionPane.showInputDialog(KEGGParserPlugin.cytoscapeDesktopService.getJFrame(),
//                    "Please, fill in the missing attribute " + attr +
//                            "for node with id: " + entryId,
//                    "Attribute specification", JOptionPane.QUESTION_MESSAGE,
//                    null, values, defaultValue);

        if (attrValue == null || attrValue.equals("")) {
            ParsingReportGenerator.getInstance().appendLine("Node attribute " + attr
                    + " is missing for node with id: " + entryId
                    + ". It's been set to default value " + defaultValue);
            attrValue = defaultValue;
        }
        entry.setAttribute(kgmlAttrName, attrValue);
    }


    private String getBgColorAttribute(CyNetwork network, CyNode cyNode) {
        String colorAttr = getNotNullAttribute(
                network.getDefaultNodeTable().getRow(cyNode.getSUID()).
                        get(EKeggNodeAttrs.BGCOLOR.getAttrName(), String.class));
        Collection<CyNetworkView> networkViews = KEGGParserPlugin.networkViewManager.getNetworkViews(network);
        if (networkViews.iterator().hasNext()) {
            CyNetworkView networkView = networkViews.iterator().next();
            if (colorAttr.equals("")) {
                Color color = (Color) KEGGParserPlugin.visualMappingManager.getVisualStyle(networkView).
                        getVisualMappingFunction(BasicVisualLexicon.NODE_FILL_COLOR).
                        getVisualProperty().getDefault();
                colorAttr = "#" + String.format("%06x", color.getRGB() & 0x00FFFFFF);
            }
        }
        return colorAttr;
    }

    private String getFgColorAttribute(CyNetwork network, CyNode cyNode) {
        String colorAttr = getNotNullAttribute(
                network.getDefaultNodeTable().getRow(cyNode.getSUID()).
                        get(EKeggNodeAttrs.FGCOLOR.getAttrName(), String.class));
        Collection<CyNetworkView> networkViews = KEGGParserPlugin.networkViewManager.getNetworkViews(network);
        if (networkViews.iterator().hasNext()) {
            CyNetworkView networkView = networkViews.iterator().next();
            if (colorAttr.equals("")) {
                Color color = (Color) KEGGParserPlugin.visualMappingManager.getVisualStyle(networkView).
                        getVisualMappingFunction(BasicVisualLexicon.NODE_LABEL_COLOR).
                        getVisualProperty().getDefault();
                colorAttr = "#" + String.format("%06x", color.getRGB() & 0x00FFFFFF);
            }
        }
        return colorAttr;
    }

    private String getNotNullAttribute(String attr) {
        if (attr == null)
            return "";
        return attr;
    }

    public void createKGML(Graph graph, File outFile) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element pathway = doc.createElement("pathway");
            doc.appendChild(pathway);
            pathway.setAttribute("name", graph.getName());
            pathway.setAttribute("title", graph.getTitle());
            pathway.setAttribute("image", graph.getImage());
            pathway.setAttribute("link", graph.getLink());

            for (KeggNode node : graph.getNodes().values()) {
                Element entry = doc.createElement("entry");
                entry.setAttribute("id", node.getId() + "");
                entry.setAttribute("name", node.getName());
                entry.setAttribute("type", node.getType());
                entry.setAttribute("link", node.getLink());
                Element graphics = doc.createElement("graphics");
                graphics.setAttribute("name", node.getGraphicsName());
                graphics.setAttribute("fgcolor", node.getFgColorAttr());
                graphics.setAttribute("bgcolor", node.getBgColorAttr());
                graphics.setAttribute("type", node.getShape());
                graphics.setAttribute("x", node.getX() + "");
                graphics.setAttribute("y", node.getY() + "");
                graphics.setAttribute("width", node.getWidth() + "");
                graphics.setAttribute("height", node.getHeight() + "");
                entry.appendChild(graphics);
                pathway.appendChild(entry);
            }

            for (KeggRelation rel : graph.getRelations()) {
                Element relation = doc.createElement("relation");
                relation.setAttribute("entry1", rel.getEntry1().getId() + "");
                relation.setAttribute("entry2", rel.getEntry2().getId() + "");
                relation.setAttribute("type", rel.getType());
                Element subtype1 = doc.createElement("subtype");
                subtype1.setAttribute("name", rel.getSubtype1());
                subtype1.setAttribute("value", rel.getRelationValue1());
                relation.appendChild(subtype1);
                if (rel.getSubtype2() != null && !rel.getSubtype2().equals("")) {
                    Element subtype2 = doc.createElement("subtype");
                    subtype2.setAttribute("name", rel.getSubtype2());
                    subtype2.setAttribute("value", rel.getRelationValue2());
                    relation.appendChild(subtype2);
                }
                pathway.appendChild(relation);
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

        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

}
