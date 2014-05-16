package org.cytoscape.keggparser.tuning;


import com.google.gson.Gson;
import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.com.*;
import org.cytoscape.keggparser.dialogs.KeggPrefsDialog;
import org.cytoscape.keggparser.dialogs.KeggWebLoadFrame;
import org.cytoscape.keggparser.parsing.KeggNetworkCreator;
import org.cytoscape.keggparser.tuning.string.DBManager;
import org.cytoscape.keggparser.tuning.string.JsonInteractions;
import org.cytoscape.keggparser.tuning.string.JsonNode;
import org.cytoscape.keggparser.tuning.string.StringParser;
import org.cytoscape.keggparser.tuning.tse.EKEGGTuningProps;
import org.cytoscape.keggparser.tuning.tse.GeneExpXmlParser;
import org.cytoscape.keggparser.tuning.tse.TSEDataSet;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tuner {
    private CyNetwork network;
    private CyNetwork tunedNetwork;
    private Logger logger = LoggerFactory.getLogger(Tuner.class);
    private String geneIdAttr;
    private String typeAttr;
    private ArrayList<String> typeAttrValues;
    private int threshold;
    private File xmlFile;
    private TSEDataSet dataSet;
    private CyNetworkView tunedNetworkView;
    private ArrayList<String> notFoundGeneIds = new ArrayList<String>();

    public Tuner(CyNetwork network) throws IllegalArgumentException {
        if (network == null)
            throw new IllegalArgumentException("The input network was null");
        this.network = network;
    }

    public Tuner(CyNetwork network, String geneIdAttr,
                 String typeAttr, ArrayList<String> typeAttrValues, int threshold) {
        this.network = network;
        this.geneIdAttr = geneIdAttr;
        this.threshold = threshold;
        this.typeAttr = typeAttr;
        this.typeAttrValues = typeAttrValues;
        this.threshold = threshold;
    }

    public void performTSETuning(CyNetwork network,
                                 String tissue, String geneIdAttr, String typeAttr,
                                 ArrayList<String> selectedTypes, int threshold,
                                 boolean generateNewNetwork) throws IllegalArgumentException {
        if (network == null || tissue == null || geneIdAttr == null ||
                typeAttr == null || selectedTypes == null)
            throw new IllegalArgumentException("Null arguments are not supported");

        final KEGGTuningTask task = new KEGGTuningTask(tissue, geneIdAttr,
                typeAttr, selectedTypes, threshold, generateNewNetwork, true);
        KEGGParserPlugin.taskManager.execute(new TaskIterator(task));
    }

    public void performPPITuning(ArrayList<String> sources, String geneIdAttr, String typeAttr,
                                 ArrayList<String> selectedTypes, int threshold,
                                 boolean generateNewNetwork) throws IllegalArgumentException {
        if (sources == null || geneIdAttr == null ||
                typeAttr == null || selectedTypes == null)
            throw new IllegalArgumentException("Null arguments are not supported");

        final KEGGTuningTask task = new KEGGTuningTask(sources, geneIdAttr,
                typeAttr, selectedTypes, threshold, generateNewNetwork, false);

        KEGGParserPlugin.taskManager.execute(new TaskIterator(task));
    }

    public boolean tuneByTSE(String tissue, boolean generateNewNetwork, TaskMonitor taskMonitor, int threshold) {
        if (xmlFile == null || !xmlFile.exists())
            return false;

        TuningReportGenerator.getInstance().appendLine("Expression file: " + xmlFile.getPath());
        TuningReportGenerator.getInstance().appendLine("Tissue: " + tissue);
        TuningReportGenerator.getInstance().appendLine("Expression threshold: " + threshold);

        if (dataSet == null)
            try {
                dataSet = loadExpressionDataSet(tissue);
            } catch (Exception e) {
                return false;
            }
        dataSet.setThreshold(threshold);
        taskMonitor.setProgress(0.5);


        ArrayList<CyNode> removedNodes = new ArrayList<CyNode>();
        HashMap<CyNode, ArrayList<String>> removedGenes = new HashMap<CyNode, ArrayList<String>>();

        String netTitle = network.getRow(network).get(CyNetwork.NAME, String.class) + "_" + tissue + "_" + threshold;
        if (generateNewNetwork) {
            tunedNetwork = NetworkManager.copyNetwork(network,
                    netTitle);
        } else {
            tunedNetwork = network;
            network.getRow(network).set(CyNetwork.NAME, netTitle);
        }


        taskMonitor.setProgress(0.7);
        HashMap<CyNode, CyNode> sourceDistNodeMap = NetworkManager.getSourceDestNodeMap();
        for (CyNode sourceNode : network.getNodeList()) {
            CyNode tunedNode = sourceDistNodeMap.get(sourceNode);
            if (!isNodePresent(tunedNode, dataSet, tunedNetwork)) {
                removedNodes.add(sourceNode);
                tunedNetwork.removeNodes(Collections.singletonList(tunedNode));
            } else {
                filterNode(tunedNode, sourceNode, dataSet, removedNodes, removedGenes, tunedNetwork);
            }
        }

        if (!notFoundGeneIds.isEmpty()) {
            boolean keepAbsentGenes;
            if (KEGGParserPlugin.getKeggProps().getProperty(EKEGGTuningProps.TSEKeepAbsentGenes.getName()).equals("true"))
                keepAbsentGenes = true;
            else keepAbsentGenes = false;

            TuningReportGenerator.getInstance().appendLine("The following genes were not found in the expression data file: ");

            String genes = "{";
            for (String geneId : notFoundGeneIds) {
                genes += geneId + ", ";
            }
            genes = genes.substring(0, genes.lastIndexOf(", ")) + "}";
            TuningReportGenerator.getInstance().appendLine(genes);
            TuningReportGenerator.getInstance().appendLine("Absent genes are kept in the network: " + keepAbsentGenes);
        }

        try {
            if (!removedNodes.isEmpty()) {
                TuningReportGenerator.getInstance().appendLine("Removed nodes (source SUID, geneID):");
                for (CyNode cyNode : removedNodes) {
                    TuningReportGenerator.getInstance().appendLine(cyNode.getSUID() + ":\t" +
                            network.getDefaultNodeTable().getRow(cyNode.getSUID()).get(geneIdAttr, String.class));
                }
            } else {
                TuningReportGenerator.getInstance().appendLine("No node was removed from the network");
            }
            if (!removedGenes.isEmpty()) {
                TuningReportGenerator.getInstance().appendLine("Removed genes from the remaining nodes (source SUID, [gene Entrez IDs]):");
                for (Map.Entry<CyNode, ArrayList<String>> entry : removedGenes.entrySet()) {
                    TuningReportGenerator.getInstance().appendLine(entry.getKey().getSUID() + ":\t" +
                            entry.getValue().toString());
                }
            } else {
                TuningReportGenerator.getInstance().appendLine("No gene was removed from the network");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            tunedNetworkView = KEGGParserPlugin.networkViewManager.getNetworkViews(tunedNetwork).iterator().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void filterNode(CyNode tunedNode, CyNode sourceNode, TSEDataSet dataSet,
                            ArrayList<CyNode> removedNodes, HashMap<CyNode, ArrayList<String>> removedGenes,
                            CyNetwork cyNetwork) {
        if (typeAttr != null && typeAttrValues.size() != 0)
            for (String typeValue : typeAttrValues)
                if (cyNetwork.getDefaultNodeTable().getRow(tunedNode.getSUID()).get(typeAttr, String.class).equals(typeValue)) {
                    String name = tunedNetwork.getDefaultNodeTable().getRow(tunedNode.getSUID()).
                            get(EKeggNodeAttrs.NAME.getAttrName(), String.class);
                    String filteredName = "", filteredIds = "";
                    if (name != null) {
                        StringTokenizer tokenizer = new StringTokenizer(name);
                        while (tokenizer.hasMoreTokens()) {
                            String token = tokenizer.nextToken();
                            String geneId = "";
                            for (int i = 0; i < token.length(); i++)
                                if (Character.isDigit(token.charAt(i)))
                                    geneId += token.charAt(i);
                            if (!geneId.equals("") && dataSet.isGeneExpressed(geneId)) {
                                filteredName += token + " ";
                                filteredIds += (filteredIds.length() == 0 ? "" : ",\t") + geneId;
                            } else {
                                if (removedGenes.containsKey(sourceNode))
                                    removedGenes.get(sourceNode).add(geneId);
                                else {
                                    ArrayList<String> list = new ArrayList<String>();
                                    list.add(geneId);
                                    removedGenes.put(sourceNode, list);
                                }
                            }
                        }
                    }
                    if (filteredName.equals("")) {
                        removedNodes.add(sourceNode);
                    } else {
                        cyNetwork.getDefaultNodeTable().getRow(tunedNode.getSUID()).
                                set(EKeggNodeAttrs.NAME.getAttrName(), filteredName);
                        cyNetwork.getDefaultNodeTable().getRow(tunedNode.getSUID())
                                .set(EKeggNodeAttrs.EntrezIDs.getAttrName(), filteredIds);
                    }
                }
    }

    private boolean isNodePresent(CyNode node, TSEDataSet dataSet, CyNetwork cyNetwork) {
        CyRow cyRow = cyNetwork.getDefaultNodeTable().getRow(node.getSUID());
        String typeAttrValue = null, entrezIds = null;
        try {
            typeAttrValue = cyRow.get(typeAttr, String.class);
            entrezIds = cyRow.get(geneIdAttr, String.class);
        } catch (NullPointerException e) {
            LoggerFactory.getLogger(Tuner.class).error(e.getMessage());
        }
        if (typeAttrValue != null) {
            if (typeAttr != null && typeAttrValues.size() != 0)
                for (String typeValue : typeAttrValues)
                    if (typeAttrValue.equals(typeValue)) {
                        if (entrezIds != null) {
                            StringTokenizer tokenizer = new StringTokenizer(entrezIds, "\t,; abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                            while (tokenizer.hasMoreTokens()) {
                                String geneId = tokenizer.nextToken();
                                if (!dataSet.containsGene(geneId))
                                    notFoundGeneIds.add(geneId);
                                if (dataSet.isGeneExpressed(geneId))
                                    return true;
                            }
                            return false;
                        }
                    }
            return true;
        }
        return false;

    }


    public TSEDataSet loadExpressionDataSet(String tissue) throws Exception {
        if (tissue == null)
            throw new IllegalArgumentException("Tissue cannot be null");
        TuningReportGenerator.getInstance().appendLine("Gene expression values for tissue " + tissue + " loaded.");
        dataSet = new TSEDataSet(tissue);
        String conflictMode = KEGGParserPlugin.getKeggProps().getProperty(EKEGGTuningProps.TSEConflictMode.getName());
        if (conflictMode != null)
            if (conflictMode.equals(KeggPrefsDialog.MEAN))
                dataSet.setMode(TSEDataSet.MEAN);
            else if (conflictMode.equals(KeggPrefsDialog.MAX))
                dataSet.setMode(TSEDataSet.MAX);
            else
                dataSet.setMode(TSEDataSet.MIN);
        /*TreeSet<String> geneIds = new TreeSet<String>();
        for (Object node : network.getNodeList()) {
            CyNode cyNode = (CyNode) node;
            String entrezIds = network.getDefaultNodeTable().getRow(cyNode.getSUID()).get(geneIdAttr, String.class);
            if (entrezIds != null && !entrezIds.equals("")) {
                StringTokenizer tokenizer = new StringTokenizer(entrezIds, "\"\t,; abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                while (tokenizer.hasMoreTokens())
                    geneIds.add(tokenizer.nextToken());
            }
        }*/
        ArrayList<String> notFoundGenes = new ArrayList<String>();
        try {
            GeneExpXmlParser parser = new GeneExpXmlParser(xmlFile);
//            for (String geneId : geneIds) {
            for (Map.Entry<String, Double> expEntry : parser.getAllExpValues(tissue).entrySet()) {
                String geneId = expEntry.getKey();
                double exp = expEntry.getValue();
//                double exp = parser.getExpValue(geneId, tissue);
                if (!Double.isNaN(exp))
                    dataSet.addExp(geneId, exp);
//                else
//                    notFoundGenes.add(geneId);
            }

            if (dataSet.size() == 0)
                return null;
            return dataSet;
        } catch (Exception e) {
            throw new Exception("Problem loading TSE dataset", e);
        }
    }


    public boolean drillDownNetwork(String newTitle, int threshold,
                                    ArrayList<String> sources, TaskMonitor taskMonitor) {

        TuningReportGenerator.getInstance().appendLine("PPI confidence threshold: " + threshold);
        String sourceString = "";
        for (String source : sources) {
            sourceString += source + ",";
        }
        sourceString = sourceString.substring(0, sourceString.lastIndexOf(','));
        TuningReportGenerator.getInstance().appendLine("Database sources: " + sourceString);
        CyNetworkView networkView = null;
        Collection<CyNetworkView> networkViews = KEGGParserPlugin.networkViewManager.getNetworkViews(network);
        if (networkViews.iterator().hasNext())
            networkView = networkViews.iterator().next();

        tunedNetwork = KEGGParserPlugin.networkFactory.createNetwork();
        newTitle = newTitle.replace(network.getSUID().toString(), tunedNetwork.getSUID().toString());
        tunedNetwork.getRow(tunedNetwork).set(CyNetwork.NAME, newTitle);

        taskMonitor.setProgress(0.2);


        ArrayList<JsonNode> jsonNodesArray = new ArrayList<JsonNode>();
        TreeMap<Long, ArrayList<Long>> memberMap = new TreeMap<Long, ArrayList<Long>>();
        TreeMap<String, Long> uniqueIdNodeMap = new TreeMap<String, Long>();
        ArrayList<CyNode> singleNodes = new ArrayList<CyNode>();

        int maxId = retrieveMaxId(network);

        //Set columns
        NetworkManager.createColumns(network.getDefaultNetworkTable(), tunedNetwork.getDefaultNetworkTable());
        NetworkManager.createColumns(network.getDefaultNodeTable(), tunedNetwork.getDefaultNodeTable());
        NetworkManager.createColumn(tunedNetwork.getDefaultNodeTable(), EKeggNodeAttrs.ENTREZ_ID.getAttrName(), String.class);
        NetworkManager.createColumn(tunedNetwork.getDefaultNodeTable(), EKeggNodeAttrs.UNIQUEID.getAttrName(), String.class);
        NetworkManager.createColumns(network.getDefaultEdgeTable(), tunedNetwork.getDefaultEdgeTable());

        NetworkManager.copyNetworkAttributes(network, tunedNetwork);

        HashMap<CyNode, ArrayList<CyNode>> parentChildrenNodesMap = new HashMap<CyNode, ArrayList<CyNode>>();
        // Drill the nodes
        for (Object node : network.getNodeList()) {
            CyNode cyNode = (CyNode) node;
            CyRow row = network.getDefaultNodeTable().getRow(cyNode.getSUID());
            String type = row.get(EKeggNodeAttrs.TYPE.getAttrName(), String.class);
            String entrezIds = row.get(EKeggNodeAttrs.EntrezIDs.getAttrName(), String.class);
            String uniqueId = row.get(EKeggNodeAttrs.UNIQUEID.getAttrName(), String.class);
            if (type != null && type.equals(KeggNode.GENE)) {
                JsonNode jsonNode = new JsonNode(cyNode.getSUID().toString());
                ArrayList<Long> membersList = new ArrayList<Long>();
                ArrayList<String> entrezIdlist = new ArrayList<String>();
                StringTokenizer tokenizer = new StringTokenizer(entrezIds,
                        "\t,; abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                while (tokenizer.hasMoreTokens())
                    entrezIdlist.add(tokenizer.nextToken());
                if (entrezIdlist.isEmpty())
                    TuningReportGenerator.getInstance().appendLine("Node " + cyNode.getSUID() + " does not have entrez ids");
                else {
                    for (String entrezId : entrezIdlist) {
                        if (!uniqueIdNodeMap.containsKey(entrezId)) {
                            row = network.getDefaultNodeTable().getRow(cyNode.getSUID());
                            addDrilledNode(cyNode, row, entrezId, uniqueIdNodeMap, parentChildrenNodesMap, true, maxId);
                            maxId++;
                        }
                        try {
                            int geneId = Integer.parseInt(entrezId);
                            jsonNode.addGeneId(geneId + "");
                            membersList.add(uniqueIdNodeMap.get(entrezId));
                        } catch (NumberFormatException e) {
                        }
                    }
                    memberMap.put(cyNode.getSUID(), membersList);
                    jsonNodesArray.add(jsonNode);
                }
            } else {
                if (uniqueId == null || !uniqueId.equals("")) {
                    String name = row.get(EKeggNodeAttrs.NAME.getAttrName(), String.class);
                    if (name != null)
                        uniqueId = name;
                    else
                        uniqueId = "arsen_" + cyNode.getSUID().toString();
                }
                if (!uniqueIdNodeMap.containsKey(uniqueId)) {
                    addDrilledNode(cyNode, row, uniqueId, uniqueIdNodeMap, parentChildrenNodesMap, false, maxId);
                    maxId++;
                }
            }

        }
        taskMonitor.setStatusMessage("Retrieving interactions from the database...");
        //Set interactions
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonNodesArray);
        if (jsonString == null || jsonString.isEmpty()) {
            TuningReportGenerator.getInstance().appendLine("Problems generating json");
            LoggerFactory.getLogger(Tuner.class).error("Problems generating json");
            return false;
        }
        String org = network.getDefaultNetworkTable().getRow(network.getSUID())
                .get(EKGMLNetworkAttrs.ORGANISM.getAttrName(), String.class);

        Map<String, Map<String,
                ArrayList<JsonInteractions.InteractionParams>>> interactions = null;
        try {
            if (org == null)
                org = askFromUserAboutOrganism();
            if (org != null && !org.isEmpty())
                interactions = DBManager.getInteractionsMap(jsonString, threshold, org, sourceString);
            else {
                taskMonitor.setStatusMessage("No organisms were specified, " +
                        "therefore data retrieval will take long (usually 1 min), please be patient :)");
                interactions = DBManager.getInteractionsMap(jsonString, threshold, sourceString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (interactions == null || interactions.isEmpty()) {
            LoggerFactory.getLogger(Tuner.class).warn("No interactions retrieved for the json " + jsonString);
            TuningReportGenerator.getInstance().appendLine("No interactions were retrieved for the network.\nCancelled tuning.");
            JOptionPane.showMessageDialog(KEGGParserPlugin.cytoscapeDesktopService.getJFrame(),
                    "No interactions were retrieved for the network! Exiting.");
            return false;
        } else {
            TuningReportGenerator.getInstance().appendLine("Interactions successfully retrieved from String database. ");
            taskMonitor.setProgress(0.8);
            taskMonitor.setStatusMessage("Interactions successfully retrieved");

            //Set interactions
            ArrayList<CyNode> connectedNodes = new ArrayList<CyNode>();
            boolean keepIndirectInteractions = KEGGParserPlugin.getKeggProps().
                    getProperty(EKEGGTuningProps.PPIKeepIndirectInteractions.getName()).equals("true");
            TuningReportGenerator.getInstance().appendLine("Option \"Keep indirect interactions in the network\": "
                    + keepIndirectInteractions);
            for (Object edge : network.getEdgeList()) {
                CyEdge cyEdge = (CyEdge) edge;
                CyNode source = cyEdge.getSource();
                CyNode target = cyEdge.getTarget();
                ArrayList<CyNode> sourceNodes = null;
                ArrayList<CyNode> targetNodes = null;
                if (memberMap.containsKey(source.getSUID())) {
                    if (memberMap.get(source.getSUID()) != null) {
                        sourceNodes = new ArrayList<CyNode>();
                        for (Long memberID : memberMap.get(source.getSUID()))
                            sourceNodes.add(tunedNetwork.getNode(memberID));
                    } else
                        sourceNodes = null;
                } else
                    sourceNodes = null;

                if (memberMap.containsKey(target.getSUID())) {
                    if (memberMap.get(target.getSUID()) != null) {
                        targetNodes = new ArrayList<CyNode>();
                        for (Long memberID : memberMap.get(target.getSUID()))
                            targetNodes.add(tunedNetwork.getNode(memberID));
                    } else
                        targetNodes = null;
                } else
                    targetNodes = null;
                if (sourceNodes != null && !sourceNodes.isEmpty() &&
                        targetNodes != null && !targetNodes.isEmpty()) {
                    for (CyNode sourceNode : sourceNodes)
                        for (CyNode targetNode : targetNodes) {
                            try {
                                int source_id = Integer.parseInt(tunedNetwork.getDefaultNodeTable().getRow(sourceNode.getSUID()).
                                        get(EKeggNodeAttrs.ENTREZ_ID.getAttrName(), String.class));
                                int target_id = Integer.parseInt(tunedNetwork.getDefaultNodeTable().getRow(targetNode.getSUID()).
                                        get(EKeggNodeAttrs.ENTREZ_ID.getAttrName(), String.class));
                                String sourceEntrezID = source_id + "";
                                String targetEntrezID = target_id + "";
                                boolean addEdge = false;
                                if (interactionExists(sourceEntrezID, targetEntrezID, interactions))
                                    addEdge = true;
                                else if (keepIndirectInteractions) {
                                    String subtype1 = network.getDefaultEdgeTable().getRow(cyEdge.getSUID()).
                                            get(EKeggEdgeAttrs.SUBTYPE1.getAttrName(), String.class);
                                    String subtype2 = network.getDefaultEdgeTable().getRow(cyEdge.getSUID()).
                                            get(EKeggEdgeAttrs.SUBTYPE2.getAttrName(), String.class);
                                    if (subtype1 != null && !subtype1.isEmpty())
                                        if (subtype1.equals(KeggRelation.EXPRESSION) ||
                                                subtype1.equals(KeggRelation.REPRESSION) ||
                                                subtype1.equals(KeggRelation.INDIRECT_EFFECT) ||
                                                subtype1.equals(KeggRelation.Maplink))
                                            addEdge = true;
                                    if (!addEdge)
                                        if (subtype2 != null && !subtype2.isEmpty())
                                            if (subtype2.equals(KeggRelation.EXPRESSION) ||
                                                    subtype2.equals(KeggRelation.REPRESSION) ||
                                                    subtype2.equals(KeggRelation.INDIRECT_EFFECT) ||
                                                    subtype2.equals(KeggRelation.Maplink))
                                                addEdge = true;
                                }
                                if (addEdge)
                                    if (!tunedNetwork.containsEdge(sourceNode, targetNode)) {
                                        try {
                                            setMemberEdge(sourceNode, targetNode, cyEdge, tunedNetwork);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        connectedNodes.add(sourceNode);
                                        connectedNodes.add(targetNode);
                                    }
                            } catch (NumberFormatException e) {
                                LoggerFactory.getLogger(Tuner.class).error(e.getMessage());
                            }
                        }
                } else if (sourceNodes != null && !sourceNodes.isEmpty()) {
                    Long id;
                    String unique_id = network.getDefaultNodeTable().getRow(target.getSUID()).
                            get(EKeggNodeAttrs.UNIQUEID.getAttrName(), String.class);
                    if (unique_id == null || unique_id.equals(""))
                        unique_id = network.getDefaultNodeTable().getRow(target.getSUID()).
                                get(EKeggNodeAttrs.NAME.getAttrName(), String.class);
                    if (unique_id == null || unique_id.equals("") || !uniqueIdNodeMap.containsKey(unique_id)) {
                        id = target.getSUID();
                    } else {
                        id = uniqueIdNodeMap.get(unique_id);
                    }
                    CyNode targetNode = tunedNetwork.getNode(id);
                    if (targetNode != null) {
                        for (CyNode sourceNode : sourceNodes) {
                            if (!tunedNetwork.containsEdge(sourceNode, targetNode)) {
                                try {
                                    setMemberEdge(sourceNode, targetNode, cyEdge, tunedNetwork);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                connectedNodes.add(sourceNode);
                                connectedNodes.add(targetNode);
                            }
                        }
                    } else
                        TuningReportGenerator.getInstance().appendLine("target " + target.getSUID().toString() + " resulted in null node ");

                } else if (targetNodes != null && !targetNodes.isEmpty()) {
                    Long id;
                    String unique_id = network.getDefaultNodeTable().getRow(source.getSUID()).
                            get(EKeggNodeAttrs.UNIQUEID.getAttrName(), String.class);
                    if (unique_id == null || unique_id.equals(""))
                        unique_id = network.getDefaultNodeTable().getRow(source.getSUID()).
                                get(EKeggNodeAttrs.NAME.getAttrName(), String.class);
                    if (unique_id == null || unique_id.equals("") || !uniqueIdNodeMap.containsKey(unique_id)) {
                        id = source.getSUID();
                    } else {
                        id = uniqueIdNodeMap.get(unique_id);
                    }
                    CyNode sourceNode = null;
                    try {
                        sourceNode = tunedNetwork.getNode(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (sourceNode != null) {
                        for (CyNode targetNode : targetNodes) {
                            if (!tunedNetwork.containsEdge(sourceNode, targetNode)) {
                                try {
                                    setMemberEdge(sourceNode, targetNode, cyEdge, tunedNetwork);
                                } catch (Exception e) {
                                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                }
                                connectedNodes.add(sourceNode);
                                connectedNodes.add(targetNode);
                            }
                        }
                    } else {
                        TuningReportGenerator.getInstance().appendLine("source " + source.getSUID().toString() + " resulted in null node ");
                    }
                } else {
                    Long id;
                    String unique_id = network.getDefaultNodeTable().getRow(target.getSUID()).
                            get(EKeggNodeAttrs.UNIQUEID.getAttrName(), String.class);
                    if (unique_id == null || unique_id.equals(""))
                        unique_id = network.getDefaultNodeTable().getRow(target.getSUID()).
                                get(EKeggNodeAttrs.NAME.getAttrName(), String.class);
                    if (unique_id == null || unique_id.equals("") || !uniqueIdNodeMap.containsKey(unique_id)) {
                        id = target.getSUID();
                    } else {
                        id = uniqueIdNodeMap.get(unique_id);
                    }
                    CyNode targetNode = null;
                    try {
                        targetNode = tunedNetwork.getNode(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    unique_id = network.getDefaultNodeTable().getRow(source.getSUID()).
                            get(EKeggNodeAttrs.UNIQUEID.getAttrName(), String.class);
                    if (unique_id == null || unique_id.equals(""))
                        unique_id = network.getDefaultNodeTable().getRow(source.getSUID()).
                                get(EKeggNodeAttrs.NAME.getAttrName(), String.class);
                    if (unique_id == null || unique_id.equals("") || !uniqueIdNodeMap.containsKey(unique_id)) {
                        id = source.getSUID();
                    } else {
                        id = uniqueIdNodeMap.get(unique_id);
                    }

                    CyNode sourceNode = tunedNetwork.getNode(id);
                    if (sourceNode == null)
                        TuningReportGenerator.getInstance().appendLine("source " + source.getSUID().toString() + " resulted in null node ");
                    else if (targetNode == null)
                        TuningReportGenerator.getInstance().appendLine("target " + source.getSUID().toString() + " resulted in null node ");
                    else {
                        if (!tunedNetwork.containsEdge(sourceNode, targetNode)) {
                            try {
                                setMemberEdge(sourceNode, targetNode, cyEdge, tunedNetwork);
                            } catch (Exception e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                            connectedNodes.add(sourceNode);
                            connectedNodes.add(targetNode);
                        }
                    }
                }
            }
            taskMonitor.setProgress(0.9);

            for (Object node : tunedNetwork.getNodeList()) {
                CyNode cyNode = (CyNode) node;
                if (!connectedNodes.contains(cyNode)) {
                    singleNodes.add(cyNode);
                }
            }
            tunedNetwork.removeNodes(singleNodes);
            if (!singleNodes.isEmpty()) {

                String singleNodeEntryIds = "{";
                for (CyNode cyNode : singleNodes) {
                    singleNodeEntryIds += cyNode.getSUID().toString() + ", ";
                }
                singleNodeEntryIds = singleNodeEntryIds.substring(0, singleNodeEntryIds.lastIndexOf(", "));
                singleNodeEntryIds += "}";
                TuningReportGenerator.getInstance().appendLine("Removed disconnected node entry IDs: " + singleNodeEntryIds);
            }
        }


        TuningReportGenerator.getInstance().appendLine("Finished the drill down.");
        TuningReportGenerator.getInstance().appendLine("Resulted network title: " + tunedNetwork.getRow(tunedNetwork).get("Name", String.class));
        TuningReportGenerator.getInstance().appendLine("Number of nodes: " + tunedNetwork.getNodeList().size());
        TuningReportGenerator.getInstance().appendLine("Number of edges: " + tunedNetwork.getEdgeList().size());
        TuningReportGenerator.getInstance().appendLine("Number of disconnected nodes removed from the network: " + singleNodes.size());

        KEGGParserPlugin.networkManager.addNetwork(tunedNetwork);
        tunedNetworkView = KEGGParserPlugin.networkViewFactory.createNetworkView(tunedNetwork);
        KEGGParserPlugin.networkViewManager.addNetworkView(tunedNetworkView);
        for (CyNode cyNode : network.getNodeList()) {
            View<CyNode> nodeView = networkView.getNodeView(cyNode);
            if (parentChildrenNodesMap.containsKey(cyNode))
                for (CyNode childNode : parentChildrenNodesMap.get(cyNode)) {
                    View<CyNode> childNodeView = tunedNetworkView.getNodeView(childNode);
                    NetworkManager.copyNodeCoordinates(nodeView, childNodeView);
                }
        }

        return true;
    }

    private String askFromUserAboutOrganism() {
        String org = null;
        try {
            String[] orgValues, lines, tokens;
            String url = "http://rest.kegg.jp/list/organism";
            String result = KeggWebLoadFrame.sendRestRequest(url).toString();
            lines = result.split("\n");
            orgValues = new String[lines.length];
            for (int i = 0; i < orgValues.length; i++) {
                tokens = lines[i].split("\t");
                orgValues[i] = tokens[1];
            }

            org = (String) JOptionPane.showInputDialog(KEGGParserPlugin.cytoscapeDesktopService.getJFrame(),
                    "Organism specification",
                    "Please, specify the organism to speed up PPI data retrieval",
                    JOptionPane.QUESTION_MESSAGE,
                    null, orgValues, "hsa");
        } catch (Exception e) {
            LoggerFactory.getLogger(Tuner.class).warn(e.getMessage());
        }
        return org;
    }

    private void addDrilledNode(CyNode parentNode, CyRow row, String uniqueId,
                                TreeMap<String, Long> uniqueIdNodeMap,
                                HashMap<CyNode, ArrayList<CyNode>> parentChildrenNodesMap,
                                boolean isGene, int maxId) {
        String parentId = row.get(EKeggNodeAttrs.ENTRY_ID.getAttrName(), String.class);
        CyNode geneNode = tunedNetwork.addNode();
        if (parentChildrenNodesMap.containsKey(parentNode)) {
            parentChildrenNodesMap.get(parentNode).add(geneNode);
        } else {
            ArrayList<CyNode> childrenList = new ArrayList<CyNode>();
            childrenList.add(geneNode);
            parentChildrenNodesMap.put(parentNode, childrenList);
        }

        if (!uniqueId.equals(""))
            uniqueIdNodeMap.put(uniqueId, geneNode.getSUID());

        CyRow tunedRow = tunedNetwork.getDefaultNodeTable().getRow(geneNode.getSUID());

        NetworkManager.copyNodeAttributes(parentNode, geneNode, network, tunedNetwork);
        NetworkManager.setAttribute(EKeggNodeAttrs.LABEL.getAttrName(), uniqueId, tunedRow, tunedNetwork);
        NetworkManager.setAttribute(EKeggNodeAttrs.PARENT_ENTRY_ID.getAttrName(), parentId, tunedRow, tunedNetwork);
        if (!isGene)
            NetworkManager.setAttribute(EKeggNodeAttrs.ENTRY_ID.getAttrName(), parentId, tunedRow, tunedNetwork);
        if (isGene) {
            NetworkManager.setAttribute(EKeggNodeAttrs.ENTREZ_ID.getAttrName(), uniqueId, tunedRow, tunedNetwork);
            NetworkManager.setAttribute(EKeggNodeAttrs.EntrezIDs.getAttrName(), uniqueId, tunedRow, tunedNetwork);
            NetworkManager.setAttribute(EKeggNodeAttrs.ENTRY_ID.getAttrName(), maxId + "", tunedRow, tunedNetwork);
            NetworkManager.setAttribute(EKeggNodeAttrs.NAME.getAttrName(), "hsa:" + uniqueId, tunedRow, tunedNetwork);
            NetworkManager.setAttribute(EKeggNodeAttrs.GRAPHICSNAME.getAttrName(), "" + uniqueId, tunedRow, tunedNetwork);
            NetworkManager.setAttribute(EKeggNodeAttrs.LINK.getAttrName(), "http://www.kegg.jp/dbget-bin/www_bget?hsa:" + uniqueId, tunedRow, tunedNetwork);

        }
    }


    private void setMemberEdge(CyNode sourceNode, CyNode targetNode, CyEdge cyEdge, CyNetwork newNetwork) throws Exception {
        try {
            CyEdge memberEdge = tunedNetwork.addEdge(sourceNode, targetNode, true);
            NetworkManager.copyEdgeAttributes(cyEdge, memberEdge, network, tunedNetwork);
            NetworkManager.setAttribute(EKeggEdgeAttrs.ENTRY1.getAttrName(),
                    tunedNetwork.getDefaultNodeTable().getRow(sourceNode.getSUID()).
                            get(EKeggNodeAttrs.ENTRY_ID.getAttrName(), String.class),
                    tunedNetwork.getDefaultEdgeTable().getRow(memberEdge.getSUID()),
                    tunedNetwork);
            NetworkManager.setAttribute(EKeggEdgeAttrs.ENTRY2.getAttrName(),
                    tunedNetwork.getDefaultNodeTable().getRow(targetNode.getSUID()).
                            get(EKeggNodeAttrs.ENTRY_ID.getAttrName(), String.class),
                    tunedNetwork.getDefaultEdgeTable().getRow(memberEdge.getSUID()),
                    tunedNetwork);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private boolean interactionExists(String sourceEntrezID, String targetEntrezID,
                                      Map<String, Map<String,
                                              ArrayList<JsonInteractions.InteractionParams>>> interactions) {
        if (sourceEntrezID == null || targetEntrezID == null || interactions == null)
            return false;
        Map<String, ArrayList<JsonInteractions.InteractionParams>> interactors;

        if ((interactors = interactions.get(sourceEntrezID)) != null)
            if (interactors.get(targetEntrezID) != null)
                return true;
        return false;
    }

    private int retrieveMaxId(CyNetwork network) {
        int maxId = 0;
        for (Object node : network.getNodeList()) {
            CyNode cyNode = (CyNode) node;

            String entryId = network.getDefaultNodeTable().getRow(cyNode.getSUID()).
                    get(EKeggNodeAttrs.ENTRY_ID.getAttrName(), String.class);
            if (entryId != null && !entryId.equals("")) {
                int id = 0;
                try {
                    id = Integer.parseInt(entryId);
                } catch (Exception e) {
//                    LoggerFactory.getLogger(Tuner.class).error(e.getMessage());
                }

                if (id > maxId)
                    maxId = id;
            }
        }
        return maxId;
    }

    public void tuneByPPI(ArrayList<String> sources, boolean generateNewNetwork,
                          TaskMonitor taskMonitor) {

        taskMonitor.setProgress(50);
        StringParser parser = StringParser.getParser();

        if (!sources.isEmpty()) {
            String netTitle = network.getRow(network).get(CyNetwork.NAME, String.class) + "_";
            for (String source : sources)
                netTitle += (source.charAt(0) + "").toUpperCase();
            netTitle += threshold;
            if (generateNewNetwork) {
                tunedNetwork = NetworkManager.copyNetwork(network,
                        netTitle);
            } else {
                tunedNetwork = network;
                network.getRow(network).set(CyNetwork.NAME, netTitle);
            }

            taskMonitor.setProgress(30);

            ArrayList<CyEdge> removedEdges = new ArrayList<CyEdge>();
            for (Object edge : tunedNetwork.getEdgeList()) {
                CyEdge cyEdge = (CyEdge) edge;
                CyNode source = cyEdge.getSource();
                CyNode target = cyEdge.getTarget();
                if (tunedNetwork.getDefaultNodeTable().getRow(source.getSUID()).
                        get(EKeggNodeAttrs.TYPE.getAttrName(), String.class).
                        equals(KeggNode.GENE)) {
                    if (tunedNetwork.getDefaultNodeTable().getRow(target.getSUID()).
                            get(EKeggNodeAttrs.TYPE.getAttrName(), String.class).
                            equals(KeggNode.GENE))
                        if (tunedNetwork.getDefaultEdgeTable().getRow(cyEdge).
                                get(EKeggEdgeAttrs.LINESTYLE.getAttrName(), String.class).
                                equals(LineTypeVisualProperty.SOLID.getDisplayName()))
                            if (!isEdgePresent(source, target, sources, parser)) {
                                removedEdges.add(cyEdge);
                            }
                }

            }
            tunedNetwork.removeEdges(removedEdges);
        } else {
            TuningReportGenerator.getInstance().appendLine("No sources chosen for PPI tuning");
        }
    }

    private boolean isEdgePresent(CyNode source, CyNode target, ArrayList<String> sources,
                                  StringParser parser) {
        if (typeAttr != null && typeAttrValues.size() != 0)
            for (String typeValue : typeAttrValues)
                if (tunedNetwork.getDefaultNodeTable().getRow(source.getSUID())
                        .get(typeAttr, String.class).equals(typeValue))
                    if (tunedNetwork.getDefaultNodeTable().getRow(target.getSUID())
                            .get(typeAttr, String.class).equals(typeValue)) {
                        String sEntrezIds = tunedNetwork.getDefaultNodeTable().getRow(source.getSUID())
                                .get(geneIdAttr, String.class);
                        String tEntrezIds = tunedNetwork.getDefaultNodeTable().getRow(target.getSUID())
                                .get(geneIdAttr, String.class);
                        if (sEntrezIds != null && tEntrezIds != null) {
                            StringTokenizer sTokenizer = new StringTokenizer(sEntrezIds,
                                    "\t,; abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                            StringTokenizer tTokenizer;
                            double maxScore = 0;
                            while (sTokenizer.hasMoreTokens()) {
                                String sourceId = sTokenizer.nextToken();
                                tTokenizer = new StringTokenizer(tEntrezIds,
                                        "\t,; abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                                while (tTokenizer.hasMoreTokens()) {
                                    String targetId = tTokenizer.nextToken();
                                    double score = parser.getScore(sourceId,
                                            targetId, sources);
                                    TuningReportGenerator.getInstance().appendLine(sourceId + " - " + targetId + ": " + score);
                                    if (score > maxScore)
                                        maxScore = score;
                                }
                            }
                            return maxScore > threshold;
                        }
                    }
        return false;
    }

    public void setXmlFile(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public CyNetwork getNetwork() {
        return network;
    }

    class KEGGTuningTask extends AbstractTask {
        private String tissue;
        private boolean generateNewNetwork;
        private TaskMonitor taskMonitor;
        private String geneIdAttr;
        private String typeAttr;
        private ArrayList<String> typeAttrValues;
        private ArrayList<String> sources;
        private int threshold;
        private boolean isTSE;

        public KEGGTuningTask(String tissue, String geneIdAttr,
                              String typeAttr, ArrayList<String> typeAttrValues,
                              int threshold, boolean generateNewNetwork, boolean isTSE) {
            this.tissue = tissue;
            this.generateNewNetwork = generateNewNetwork;
            this.geneIdAttr = geneIdAttr;
            this.threshold = threshold;
            this.typeAttr = typeAttr;
            this.typeAttrValues = typeAttrValues;
            this.isTSE = isTSE;
        }

        public KEGGTuningTask(ArrayList<String> sources, String geneIdAttr,
                              String typeAttr, ArrayList<String> typeAttrValues,
                              int threshold, boolean generateNewNetwork, boolean isTSE) {
            this.sources = sources;
            this.generateNewNetwork = generateNewNetwork;
            this.geneIdAttr = geneIdAttr;
            this.threshold = threshold;
            this.typeAttr = typeAttr;
            this.typeAttrValues = typeAttrValues;
            this.isTSE = isTSE;
        }

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            TuningReportGenerator.getInstance().appendLine("\n" + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
            TuningReportGenerator.getInstance().appendLine("Tuning the network: " + network.getRow(network).get(CyNetwork.NAME, String.class));
            TuningReportGenerator.getInstance().appendLine("Pathway name: " + network.getRow(network).get(EKGMLNetworkAttrs.NAME.getAttrName(), String.class));
            TuningReportGenerator.getInstance().appendLine("Pathway title: " + network.getRow(network).get(EKGMLNetworkAttrs.TITLE.getAttrName(), String.class));
            TuningReportGenerator.getInstance().appendLine("Pathway link: " + network.getRow(network).get(EKGMLNetworkAttrs.LINK.getAttrName(), String.class));
            TuningReportGenerator.getInstance().appendLine("Pathway organism: " + network.getRow(network).get(EKGMLNetworkAttrs.ORGANISM.getAttrName(), String.class));

            taskMonitor.setTitle("KEGG tuning task");
            taskMonitor.setStatusMessage("Tuning the network " + network.getRow(network).get("Name", String.class) +
                    ".\n\nIt may take a while.\nPlease wait...");
            taskMonitor.setProgress(0.1);

            boolean success = true;
            try {
                tunedNetworkView = null;

                if (isTSE) {
                    TuningReportGenerator.getInstance().appendLine("Tuning mode: TSE");
                    success = tuneByTSE(tissue, generateNewNetwork, taskMonitor, threshold);
                } else {
                    TuningReportGenerator.getInstance().appendLine("Tuning mode: PPI");
                    success = drillDownNetwork(network.getRow(network).get(CyNetwork.NAME,
                            String.class) + "_drilled",
                            threshold, sources, taskMonitor);
                }
                if (!success)
                    throw new Exception("Tuning the network somewhere went wrong.");
                else {

                    taskMonitor.setStatusMessage("The network " + network.getRow(network).get("Name", String.class) + " successfully tuned.");
                    TuningReportGenerator.getInstance().appendLine("The network " + network.getRow(network).get("Name", String.class) + " successfully tuned.");

                    if (tunedNetworkView != null) {
                        taskMonitor.setStatusMessage("Applying kegg_vs visual style to the network");
                        KeggNetworkCreator.applyKeggVisualStyle(tunedNetworkView);
                    }
                }
            } catch (Exception e) {
                TuningReportGenerator.getInstance().appendLine("Error while tuning the network: " + e.getMessage());
                throw new Exception("Error while tuning the network" + e.getMessage());
            } finally {
                taskMonitor.setProgress(1);
                System.gc();
            }
        }

        @Override
        public void cancel() {
            if (tunedNetwork != null) {
                Collection<CyNetworkView> networkViews =
                        KEGGParserPlugin.networkViewManager.getNetworkViews(tunedNetwork);
                for (CyNetworkView networkView : networkViews)
                    KEGGParserPlugin.networkViewManager.destroyNetworkView(networkView);

                KEGGParserPlugin.networkManager.destroyNetwork(tunedNetwork);
            }
            super.cancelled = true;

        }

    }

}
