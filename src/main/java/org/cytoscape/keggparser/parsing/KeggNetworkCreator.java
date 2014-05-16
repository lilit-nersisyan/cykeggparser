package org.cytoscape.keggparser.parsing;


import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.com.*;
import org.cytoscape.keggparser.tuning.NetworkManager;
import org.cytoscape.model.*;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.view.presentation.property.values.ArrowShape;
import org.cytoscape.view.presentation.property.values.LineType;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualPropertyDependency;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;

import java.awt.*;
import java.io.File;
import java.util.*;


public class KeggNetworkCreator {

    private CyNetwork network;
    private CyNetworkView networkView;
    private static VisualStyle visualStyle;
    private Graph graph;

    private boolean isForPSA = false;


    private Long networkID;

    CyTable nodeTable;
    CyTable edgeTable;
    TreeMap<Integer, Long> keggNodeIdMap;
    TreeMap<String, Long> keggEdgeIdMap;
    private HashMap<CyNode, Integer> groupCyNodeEntryIdMap = new HashMap<CyNode, Integer>();


    public static VisualPropertyDependency nodeWidthHeightDependency;


    public CyNetwork createNetwork(File kgml) throws Exception {
        networkView = null;
        parseKgml(kgml);
        loadNetworkComponents();
        loadGraphAttributes();
        loadNodeAttributes();
        loadEdgeAttributes();
        performVisualMapping();
        return network;
    }

    private void parseKgml(File kgml) throws Exception {

        Parser parser = new Parser();
        try {
            graph = parser.parse(kgml);
        } catch (Exception e) {
            throw new Exception("xml-parsing problem " + e.getMessage());
        }
        if (graph == null)
            throw new Exception("The parsed network was empty.");
        if (graph.getOrder() == 0)
            throw new Exception("The network contained no nodes");


        try {
            if (EKeggProps.ProcessCompounds.getOldValue()) {
                ParsingReportGenerator.getInstance().appendLine("Compound relation processing ON");
                String result = graph.processCompounds();
                ParsingReportGenerator.getInstance().appendLine(result);
                ParsingReportGenerator.getInstance().appendLine("Finished processing compound relations");
            }
        } catch (Exception e) {
            throw new Exception("Error while processing protein-compound-protein interactions: " + e.getMessage(), e);
        }
        try {

            if (EKeggProps.ProcessBindingDirs.getOldValue()) {
                ParsingReportGenerator.getInstance().appendLine("Correction of edge direactions ON");
                String result = this.graph.correctEdgeDirections();
                ParsingReportGenerator.getInstance().append(result);
                ParsingReportGenerator.getInstance().appendLine("Finished correcting edge directions");
            }
        } catch (Exception e) {
            throw new Exception("Error while processing binding interaction directions: " + e.getMessage(), e);
        }

        try {
            if (EKeggProps.ProcessGroups.getOldValue()) {
                ParsingReportGenerator.getInstance().appendLine("Group node processing ON");
                ArrayList<Integer> processedGroups = this.graph.processGroups();
                if (processedGroups != null && !processedGroups.isEmpty())
                    for (int groupId : processedGroups)
                        ParsingReportGenerator.getInstance().appendLine("Group node " + groupId + " processed and removed");
            }
        } catch (Exception e) {
            throw new Exception("Error while processing group nodes: " + e.getMessage(), e);
        }

    }

    private void loadNetworkComponents() throws Exception {
        try {
            network = KEGGParserPlugin.networkFactory.createNetwork();
            network.getRow(network).set(CyNetwork.NAME, network.getSUID() + "_" + graph.getTitle());
            networkID = network.getSUID();
        } catch (Exception e) {
            throw new Exception("Something went wrong during network initialization: " + e.getMessage(), e);
        }


        //Load nodes
        try {
            keggNodeIdMap = new TreeMap<Integer, Long>();
            for (KeggNode keggNode : graph.getNodes().values()) {
                CyNode node = network.addNode();
                if (!isForPSA)
                    network.getRow(node).set(CyNetwork.NAME, keggNode.getName());
                if (isForPSA)
                    network.getRow(node).set(CyNetwork.NAME, keggNode.getCellName());
                keggNodeIdMap.put(keggNode.getId(), node.getSUID());
                if (keggNode.getType().equals(KeggNode.GROUP))
                    groupCyNodeEntryIdMap.put(node, keggNode.getId());
            }
        } catch (Exception e) {
            throw new Exception("Something went wrong when trying to load the nodes, their names and types", e);
        }

        //Load edges
        try {
            keggEdgeIdMap = new TreeMap<String, Long>();
            for (KeggRelation relation : graph.getRelations()) {
                CyNode node1 = network.getNode(keggNodeIdMap.get(relation.getEntry1().getId()));
                CyNode node2 = network.getNode(keggNodeIdMap.get(relation.getEntry2().getId()));
                CyEdge edge = network.addEdge(node1, node2, true);
                keggEdgeIdMap.put(relation.getEntry1().getId() + "_" + relation.getEntry2().getId(), edge.getSUID());
            }
        } catch (Exception e) {
            throw new Exception("Something went wrong when trying to load the edges, their entryIds and types"
                    + e.getMessage(), e);
        }

        try {
            KEGGParserPlugin.networkManager.addNetwork(network);

            Collection<CyNetworkView> networkViews = KEGGParserPlugin.networkViewManager.getNetworkViews(network);
            if (networkViews.isEmpty()) {
                networkView = KEGGParserPlugin.networkViewFactory.createNetworkView(network);
                KEGGParserPlugin.networkViewManager.addNetworkView(networkView);
            } else
                networkView = networkViews.iterator().next();
        } catch (Exception e) {
            throw new Exception("Something went wrong when trying to add the register the network: "
                    + e.getMessage(), e);
        }
    }

    public static VisualStyle getVisualStyle() {
        return visualStyle;
    }

    private void performVisualMapping() {
        visualStyle = KEGGParserPlugin.visualStyleFactory.createVisualStyle("kegg_vs");
        Set<VisualStyle> visualStyles = KEGGParserPlugin.visualMappingManager.getAllVisualStyles();

        boolean isVisualStylePresent = false;
        for (VisualStyle vs : visualStyles) {
            if (vs.getTitle().equals(visualStyle.getTitle())) {
                isVisualStylePresent = true;
                visualStyle = vs;
            }
        }

        // Node attribute mapping

        VisualMappingFunction<String, Paint> nodeColorMapping = KEGGParserPlugin.vmfFactoryP.createVisualMappingFunction
                (EKeggNodeAttrs.BGCOLOR.getAttrName(), String.class, BasicVisualLexicon.NODE_FILL_COLOR);
        VisualMappingFunction<String, Paint> nodeLabelColorMapping = KEGGParserPlugin.vmfFactoryP.createVisualMappingFunction
                (EKeggNodeAttrs.FGCOLOR.getAttrName(), String.class, BasicVisualLexicon.NODE_LABEL_COLOR);
        VisualMappingFunction<String, String> nodeLabelMapping = KEGGParserPlugin.vmfFactoryP.createVisualMappingFunction
                (EKeggNodeAttrs.LABEL.getAttrName(), String.class, BasicVisualLexicon.NODE_LABEL);
        VisualMappingFunction<String, NodeShape> nodeShapeMapping = KEGGParserPlugin.vmfFactoryP.createVisualMappingFunction
                (EKeggNodeAttrs.SHAPE.getAttrName(), String.class, BasicVisualLexicon.NODE_SHAPE);
        VisualMappingFunction<String, Double> nodeHeightMapping = KEGGParserPlugin.vmfFactoryP.createVisualMappingFunction
                (EKeggNodeAttrs.HEIGHT.getAttrName(), String.class, BasicVisualLexicon.NODE_HEIGHT);
        VisualMappingFunction<String, Double> nodeWidthMapping = KEGGParserPlugin.vmfFactoryP.createVisualMappingFunction
                (EKeggNodeAttrs.WIDTH.getAttrName(), String.class, BasicVisualLexicon.NODE_WIDTH);

        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_LABEL_FONT_SIZE, 7);
        visualStyle.addVisualMappingFunction(nodeLabelMapping);
        visualStyle.addVisualMappingFunction(nodeLabelColorMapping);
        visualStyle.addVisualMappingFunction(nodeColorMapping);
        visualStyle.addVisualMappingFunction(nodeShapeMapping);
        visualStyle.addVisualMappingFunction(nodeHeightMapping);
        visualStyle.addVisualMappingFunction(nodeWidthMapping);

        // Edge attribute mapping
        visualStyle.setDefaultValue(BasicVisualLexicon.EDGE_LABEL_FONT_SIZE, 12);
        DiscreteMapping<String, LineType> edgeLineStyleMapping = (DiscreteMapping<String, LineType>)
                KEGGParserPlugin.vmfFactoryD.createVisualMappingFunction(EKeggEdgeAttrs.LINESTYLE.getAttrName(),
                        String.class, BasicVisualLexicon.EDGE_LINE_TYPE);
        edgeLineStyleMapping.putMapValue(LineTypeVisualProperty.SOLID.getDisplayName(), LineTypeVisualProperty.SOLID);
        edgeLineStyleMapping.putMapValue(LineTypeVisualProperty.LONG_DASH.getDisplayName(), LineTypeVisualProperty.LONG_DASH);
        edgeLineStyleMapping.putMapValue(LineTypeVisualProperty.DOT.getDisplayName(), LineTypeVisualProperty.DOT);
        edgeLineStyleMapping.putMapValue(LineTypeVisualProperty.DASH_DOT.getDisplayName(), LineTypeVisualProperty.DASH_DOT);
        edgeLineStyleMapping.putMapValue(LineTypeVisualProperty.EQUAL_DASH.getDisplayName(), LineTypeVisualProperty.EQUAL_DASH);
        VisualMappingFunction<String, String> edgeLabelMapping = KEGGParserPlugin.vmfFactoryP.createVisualMappingFunction(
                EKeggEdgeAttrs.EDGELABEL.getAttrName(), String.class, BasicVisualLexicon.EDGE_LABEL);
        VisualMappingFunction<String, ArrowShape> arrowShapeMapping = KEGGParserPlugin.vmfFactoryP.createVisualMappingFunction(
                EKeggEdgeAttrs.ARROWSHAPE.getAttrName(), String.class, BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE);
        visualStyle.addVisualMappingFunction(edgeLineStyleMapping);
        visualStyle.addVisualMappingFunction(edgeLabelMapping);
        visualStyle.addVisualMappingFunction(arrowShapeMapping);
        if (!isVisualStylePresent)
            KEGGParserPlugin.visualMappingManager.addVisualStyle(visualStyle);
        Iterator iterator = visualStyle.getAllVisualPropertyDependencies().iterator();
        nodeWidthHeightDependency = null;
        while (iterator.hasNext()) {
            VisualPropertyDependency dep = (VisualPropertyDependency) iterator.next();
            if (dep.getDisplayName().equalsIgnoreCase("Lock \n" +
                    "node width and height"))
                nodeWidthHeightDependency = dep;
        }

//        KEGGParserPlugin.cyEventHelper.flushPayloadEvents();
//        visualStyle.apply(networkView);
//        KEGGParserPlugin.cyEventHelper.flushPayloadEvents();
//        if (nodeWidthHeightDependency != null && nodeWidthHeightDependency.isDependencyEnabled())
//            nodeWidthHeightDependency.setDependency(false);
//        networkView.updateView();
    }

    private void loadEdgeAttributes() {
        edgeTable = network.getDefaultEdgeTable();
        for (EKeggEdgeAttrs attr : EKeggEdgeAttrs.values()) {
            try {
                NetworkManager.createColumn(edgeTable, attr.getAttrName(), String.class);
            } catch (IllegalArgumentException e) {
            }
        }
        if (isForPSA) {
            try {
                edgeTable.createColumn("interaction", String.class, false);
            } catch (Exception e) {
            }
            try {
                edgeTable.createColumn("Weight", Integer.class, false);
            } catch (Exception e) {
            }
        }

        for (KeggRelation relation : graph.getRelations()) {
            CyEdge edge = network.getEdge(keggEdgeIdMap.get(relation.getEntry1().getId() + "_" + relation.getEntry2().getId()));
            CyRow edgeRow = edgeTable.getRow(edge.getSUID());

            edgeRow.set(EKeggEdgeAttrs.ENTRY1.getAttrName(), relation.getEntry1().getId() + "");
            edgeRow.set(EKeggEdgeAttrs.ENTRY2.getAttrName(), relation.getEntry2().getId() + "");
            edgeRow.set(EKeggEdgeAttrs.TYPE.getAttrName(), relation.getType());
            edgeRow.set(EKeggEdgeAttrs.TYPE_MAPPED.getAttrName(), mapRelationType(relation.getType()));
            edgeRow.set(EKeggEdgeAttrs.SUBTYPE1.getAttrName(), relation.getSubtype1());
            edgeRow.set(EKeggEdgeAttrs.SUBTYPE2.getAttrName(), relation.getSubtype2());
            edgeRow.set(EKeggEdgeAttrs.ARROWSHAPE.getAttrName(), relation.getArrowShape().getDisplayName());
            edgeRow.set(EKeggEdgeAttrs.LINESTYLE.getAttrName(), relation.getLineStyle().getDisplayName());
            edgeRow.set(EKeggEdgeAttrs.EDGELABEL.getAttrName(), relation.getEdgeLabel());
            edgeRow.set(EKeggEdgeAttrs.RELATIONVALUE1.getAttrName(), relation.getRelationValue1());
            edgeRow.set(EKeggEdgeAttrs.RELATIONVALUE2.getAttrName(), relation.getRelationValue2());
            edgeRow.set(EKeggEdgeAttrs.COMMENT.getAttrName(), relation.getComment());
            edgeRow.set(EKeggEdgeAttrs.KEGG_TYPE.getAttrName(), relation.getType());

            //Pathway scoring application
            if (isForPSA) {
                edgeRow.set("interaction", relation.getSubtype1().equals(KeggRelation.INHIBITION) ?
                        "inhibition" : (relation.getSubtype2().equals(KeggRelation.INHIBITION) ?
                        "inhibition" : (relation.getSubtype1().equals(KeggRelation.DISSOCIATION) ?
                        "dissociation" : (relation.getSubtype2().equals(KeggRelation.DISSOCIATION) ?
                        "dissociation" : "activation"))));
                edgeRow.set("Weight", 0);
            }

        }

    }

    private void loadNodeAttributes() throws Exception {
        try {
            nodeTable = network.getDefaultNodeTable();
            for (EKeggNodeAttrs attr : EKeggNodeAttrs.values()) {
                NetworkManager.createColumn(nodeTable, attr.getAttrName(), String.class);
            }
            if (isForPSA) {
                nodeTable.createColumn("ENTREZ_ID", String.class, false);
                nodeTable.createColumn("NODE_TYPE", String.class, false);
                nodeTable.createColumn("TARGET_PROCESS", String.class, false);
                nodeTable.createColumn("SCORE", Integer.class, false);
            }
        } catch (Exception e) {
            throw new Exception("Something went wrong while creating columns for node attribute table: " + e.getMessage(), e);
        }


        for (KeggNode keggNode : graph.getNodes().values()) {
            try {
                CyNode node = network.getNode(keggNodeIdMap.get(keggNode.getId()));
                CyRow nodeRow = nodeTable.getRow(node.getSUID());

                nodeRow.set(EKeggNodeAttrs.ENTRY_ID.getAttrName(), keggNode.getId() + "");
                nodeRow.set(EKeggNodeAttrs.LABEL.getAttrName(), keggNode.getCellName());
                nodeRow.set(EKeggNodeAttrs.NAME.getAttrName(), keggNode.getName());
                nodeRow.set(EKeggNodeAttrs.GRAPHICSNAME.getAttrName(), keggNode.getGraphicsName());
                nodeRow.set(EKeggNodeAttrs.EntrezIDs.getAttrName(), keggNode.getEntrezIDsFromName());
                nodeRow.set(EKeggNodeAttrs.TYPE.getAttrName(), keggNode.getType());
                nodeRow.set(EKeggNodeAttrs.LINK.getAttrName(), keggNode.getLink());
                nodeRow.set(EKeggNodeAttrs.SHAPE.getAttrName(), keggNode.getShape());
                nodeRow.set(EKeggNodeAttrs.WIDTH.getAttrName(), keggNode.getWidth() + "");
                nodeRow.set(EKeggNodeAttrs.HEIGHT.getAttrName(), keggNode.getHeight() + "");
                nodeRow.set(EKeggNodeAttrs.COMMENT.getAttrName(), keggNode.getComment());
                nodeRow.set(EKeggNodeAttrs.GROUP.getAttrName(), keggNode.getGroupId() + "");
                nodeRow.set(EKeggNodeAttrs.FGCOLOR.getAttrName(), keggNode.getFgColorAttr());
                nodeRow.set(EKeggNodeAttrs.BGCOLOR.getAttrName(), keggNode.getBgColorAttr());
                nodeRow.set(EKeggNodeAttrs.KEGG_X.getAttrName(), "" + keggNode.getX());
                nodeRow.set(EKeggNodeAttrs.KEGG_Y.getAttrName(), "" + keggNode.getY());

//            //Pathway scoring application
                if (isForPSA) {
                    nodeRow.set("ENTREZ_ID", "" + keggNode.getEntrezIDsFromName().replace(',', ' '));
                    nodeRow.set("NODE_TYPE", "" + keggNode.getType());
                    nodeRow.set("TARGET_PROCESS", keggNode.getType().equals(KeggNode.MAP) ? "yes" : "no");
                    nodeRow.set("SCORE", 0);
                }

                View<CyNode> nodeView = networkView.getNodeView(node);
                nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, (keggNode.getX()));
                nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, (keggNode.getY()));
            } catch (Exception e) {
                throw new Exception("Error occurred while loading attributes for node: " + keggNode.toString() + ": " + e.getMessage());

            }
        }


    }

    private void loadGraphAttributes() throws Exception {
        try {
            CyTable networkTable = network.getDefaultNetworkTable();
            networkTable.setTitle(graph.getTitle());
            CyRow networkRow = networkTable.getRow(networkID);

            for (EKGMLNetworkAttrs attr : EKGMLNetworkAttrs.values()) {
                if (!attr.getAttrName().equals(CyNetwork.NAME))
                    NetworkManager.createColumn(networkTable, attr.getAttrName(), String.class);
            }


            networkRow.set(EKGMLNetworkAttrs.TITLE.getAttrName(), graph.getTitle());
            networkRow.set(EKGMLNetworkAttrs.NUMBER.getAttrName(), graph.getNumber());
            networkRow.set(EKGMLNetworkAttrs.LINK.getAttrName(), graph.getLink());
            networkRow.set(EKGMLNetworkAttrs.IMAGE.getAttrName(), graph.getImage());
            networkRow.set(EKGMLNetworkAttrs.ORGANISM.getAttrName(), graph.getOrganism());
        } catch (Exception e) {
            throw new Exception("Error while loading network attributes: " + e.getMessage(), e);

        }
    }


    private String mapRelationType(String keggType) {
        if (keggType.equals(KeggRelation.PPrel))
            return "pp";
        if (keggType.equals(KeggRelation.PCrel))
            return "pp";
        if (keggType.equals(KeggRelation.ECrel))
            return "rc";
        if (keggType.equals(KeggRelation.GErel))
            return "pd";
        return "";
    }

    public CyNetworkView getNetworkView() {
        return networkView;
    }

    public static void applyKeggVisualStyle(CyNetworkView view) {
        VisualStyle kegg_vs = KeggNetworkCreator.getVisualStyle();
        if (kegg_vs != null) {
            VisualPropertyDependency dependency = null;
            for (VisualPropertyDependency<?> dep : kegg_vs.getAllVisualPropertyDependencies()) {
                if (dep.getDisplayName().equalsIgnoreCase("Lock node width and height"))
                    dependency = dep;
            }
            if (dependency != null && dependency.isDependencyEnabled())
                dependency.setDependency(false);
            kegg_vs.addVisualPropertyDependency(dependency);
            kegg_vs.apply(view);
            KEGGParserPlugin.visualMappingManager.setVisualStyle(kegg_vs, view);
            view.updateView();
            KEGGParserPlugin.cytoscapeDesktopService.getJFrame().repaint();
        }


    }
}
