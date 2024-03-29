package org.cytoscape.keggparser.tuning;

import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.com.EKeggEdgeAttrs;
import org.cytoscape.model.*;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class NetworkManager {


    private static HashMap<CyNode, CyNode> sourceDestNodeMap;
    private static HashMap<CyEdge, CyEdge> sourceDestEdgeMap;

    public static HashMap<CyNode, CyNode> getSourceDestNodeMap() {
        return sourceDestNodeMap;
    }

    public static HashMap<CyEdge, CyEdge> getSourceDestEdgeMap() {
        return sourceDestEdgeMap;
    }

    public static CyNetwork copyNetwork(CyNetwork network, String newTitle) {
        Collection<CyNetworkView> networkViews = KEGGParserPlugin.networkViewManager.getNetworkViews(network);
        CyNetworkView networkView = null;
        if (networkViews.iterator().hasNext())
            networkView = networkViews.iterator().next();
        CyNetwork newNetwork = KEGGParserPlugin.networkFactory.createNetwork();
        newTitle = newTitle.replace(network.getSUID().toString(), newNetwork.getSUID().toString());
        newNetwork.getDefaultNetworkTable().getRow(newNetwork.getSUID()).set(CyNetwork.NAME, newTitle);

        //Copy network attributes

        copyNetworkAttributes(network, newNetwork);

        //Set columns
        createColumns(network.getDefaultNodeTable(), newNetwork.getDefaultNodeTable());
        createColumns(network.getDefaultEdgeTable(), newNetwork.getDefaultEdgeTable());
        createColumns(network.getDefaultNetworkTable(), newNetwork.getDefaultNetworkTable());


        // Copy nodes
        sourceDestNodeMap = new HashMap<CyNode, CyNode>();
        for (Object node : network.getNodeList()) {
            CyNode cyNode = (CyNode) node;
            copyCyNode(cyNode, network, newNetwork, sourceDestNodeMap);
        }
        // Copy edges
        sourceDestEdgeMap = new HashMap<CyEdge, CyEdge>();
        for (Object edge : network.getEdgeList()) {
            CyEdge cyEdge = (CyEdge) edge;
            CyNode source = cyEdge.getSource();
            CyNode target = cyEdge.getTarget();

            CyNode newSource = sourceDestNodeMap.get(source);
            CyNode newTarget = sourceDestNodeMap.get(target);
            String relationType = getRelationType(cyEdge, network);
            if (relationType == null)
                relationType = "pp";
            if (newSource != null && newTarget != null) {
                CyEdge newCyEdge = newNetwork.addEdge(newSource, newTarget, true);
                sourceDestEdgeMap.put(cyEdge, newCyEdge);

            } else {
//                System.out.println(source.getSUID().toString() + target.getSUID().toString());
            }
        }

        KEGGParserPlugin.networkManager.addNetwork(newNetwork);
        CyNetworkView newNetworkView = KEGGParserPlugin.networkViewFactory.createNetworkView(newNetwork);
        KEGGParserPlugin.networkViewManager.addNetworkView(newNetworkView);
        networkViews = KEGGParserPlugin.networkViewManager.getNetworkViews(network);

        if (networkViews.iterator().hasNext())
            networkView = networkViews.iterator().next();
        for (Object node : network.getNodeList()) {
            CyNode cyNode = (CyNode) node;

            copyNodeAttributes(cyNode, sourceDestNodeMap.get(cyNode), network, newNetwork);

            View<CyNode> nodeView = networkView.getNodeView(cyNode);
            View<CyNode> newNodeView = newNetworkView.getNodeView(sourceDestNodeMap.get(cyNode));

            if (nodeView != null && newNodeView != null) {
                newNodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION,
                        nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION));
                newNodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION,
                        nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION));
            }
        }
        for (Object edge : network.getEdgeList()) {
            CyEdge cyEdge = (CyEdge) edge;
            CyEdge newCyEdge = sourceDestEdgeMap.get(cyEdge);
            copyEdgeAttributes(cyEdge, newCyEdge, network, newNetwork);
        }
//        VisualStyle visualStyle = KEGGParserPlugin.visualMappingManager.getVisualStyle(networkView);
//        visualStyle.apply(newNetworkView);
//        newNetworkView.updateView();
        return newNetwork;
    }


    private static CyNode copyCyNode(CyNode cyNode, CyNetwork network, CyNetwork newNetwork, HashMap<CyNode, CyNode> sourceDestNodeMap) {
        CyNode newCyNode = newNetwork.addNode();
//        Cytoscape.getVisualMappingManager().vizmapNode(newNetworkView.getNodeView(newCyNode.getRootGraphIndex()),
//                newNetworkView);
        sourceDestNodeMap.put(cyNode, newCyNode);
        return newCyNode;
    }

    private static String getRelationType(CyEdge cyEdge, CyNetwork network) {
        return network.getDefaultEdgeTable().getRow(cyEdge.getSUID())
                .get(EKeggEdgeAttrs.TYPE_MAPPED.getAttrName(), String.class);
    }

    public static void copyNetworkAttributes(CyNetwork sourceNetwork, CyNetwork destNetwork) {
        Iterator<CyColumn> iterator = sourceNetwork.getDefaultNetworkTable().getColumns().iterator();
        CyRow destRow = destNetwork.getDefaultNetworkTable().getRow(destNetwork.getSUID());
        while (iterator.hasNext()) {
            String attrName = iterator.next().getName();
            Class attrClass = sourceNetwork.getDefaultNetworkTable().getColumn(attrName).getType();
            Object attribute = sourceNetwork.getDefaultNetworkTable()
                    .getRow(sourceNetwork.getSUID()).get(attrName, attrClass);
            Object destAttr = destRow.get(attrName, attrClass);
            if (attribute != null && destAttr == null)
                setAttribute(attrName, attribute, destRow, destNetwork);
        }


    }

    public static void copyNodeAttributes(CyNode source, CyNode dest,
                                          CyNetwork sourceNetwork, CyNetwork destNetwork) {
        Iterator<CyColumn> iterator = sourceNetwork.getDefaultNodeTable().getColumns().iterator();
        CyRow destRow = destNetwork.getDefaultNodeTable().getRow(dest.getSUID());
        while (iterator.hasNext()) {
            String attrName = iterator.next().getName();
            Class attrClass = sourceNetwork.getDefaultNodeTable().getColumn(attrName).getType();
            Object attribute = sourceNetwork.getDefaultNodeTable()
                    .getRow(source.getSUID()).get(attrName, attrClass);
            Object destAttr = destRow.get(attrName, attrClass);
            if (attribute != null && destAttr == null)
                setAttribute(attrName, attribute, destRow, destNetwork);
        }


    }

    public static void copyEdgeAttributes(CyEdge source, CyEdge dest,
                                          CyNetwork sourceNetwork, CyNetwork destNetwork) {
        Iterator<CyColumn> iterator = sourceNetwork.getDefaultEdgeTable().getColumns().iterator();
        CyRow destRow = destNetwork.getDefaultEdgeTable().getRow(dest.getSUID());
        while (iterator.hasNext()) {
            String attrName = iterator.next().getName();
            Class attrClass = sourceNetwork.getDefaultEdgeTable().getColumn(attrName).getType();
            Object attribute = sourceNetwork.getDefaultEdgeTable()
                    .getRow(source.getSUID()).get(attrName, attrClass);
            Object destAttr = destRow.get(attrName, attrClass);
            if (attribute != null && destAttr == null)
                setAttribute(attrName, attribute, destRow, destNetwork);
        }

    }


    public static void setAttribute(String attrName, Object attrValue, CyRow row,
                                    CyNetwork network) {
        if (attrValue != null && row != null)
            try {
                Object existingValue = row.get(attrName, attrValue.getClass());
                if (existingValue != null)
                    row.set(attrName, null);

                row.set(attrName, attrValue);
            } catch (IllegalArgumentException e) {
                try {
                    CyTable table = null;
                    if (network.getDefaultNodeTable().getAllRows().contains(row))
                        table = network.getDefaultNodeTable();
                    else if (network.getDefaultEdgeTable().getAllRows().contains(row))
                        table = network.getDefaultEdgeTable();
                    else if (network.getDefaultNetworkTable().getAllRows().contains(row))
                        table = network.getDefaultNetworkTable();
                    if (table != null) {
                        table.createColumn(attrName, attrValue.getClass(), false);
                        row = table.getRow(row.get(CyNetwork.SUID, Long.class));
                        row.set(attrName, attrValue);
                    }
                } catch (Exception e1) {
//                LoggerFactory.getLogger(NetworkManager.class).error(e1.getMessage());
                }
            }
    }

    public static void createColumn(CyTable destTable, String columnName, Class classType) {
        boolean containsColumn = false;

        Iterator<CyColumn> destIterator = destTable.getColumns().iterator();
        while (destIterator.hasNext()) {
            CyColumn destColumn = destIterator.next();
            if (destColumn.getName().equals(columnName)) {
                containsColumn = true;
                break;
            }
        }
        if (!containsColumn) {
            destTable.createColumn(columnName, classType, true);
        }
    }

    public static void createColumns(CyTable sourceTable, CyTable destTable) {
        //Set columns
        Iterator<CyColumn> iterator = sourceTable.getColumns().iterator();

        while (iterator.hasNext()) {
            boolean containsColumn = false;
            CyColumn column = iterator.next();
            Iterator<CyColumn> destIterator = destTable.getColumns().iterator();
            while (destIterator.hasNext()) {
                CyColumn destColumn = destIterator.next();
                if (destColumn.getName().equals(column.getName())) {
                    containsColumn = true;
                    break;
                }
            }
            if (!containsColumn)
                try {
                    destTable.createColumn(column.getName(), column.getType(), false);
                } catch (Exception e) {
//                    LoggerFactory.getLogger(NetworkManager.class).error(e.getMessage());
//                    KEGGParserPlugin.getTuningLog().error("Hechelis " + e.getMessage());
                }
        }
    }

    public static void copyNodeCoordinates(View<CyNode> sourceNodeView, View<CyNode> destNodeView) {
        if (sourceNodeView != null && destNodeView != null) {
            destNodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION,
                    sourceNodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION));
            destNodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION,
                    sourceNodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION));
        }
    }
}
