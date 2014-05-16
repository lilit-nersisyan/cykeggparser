package org.cytoscape.keggparser.tuning.string;

import com.google.gson.Gson;

import java.util.ArrayList;

public class JsonNode {
    private String nodeId;
    private ArrayList<String> geneIds;

    public JsonNode(String nodeId){
        this.nodeId = nodeId;
        this.geneIds = new ArrayList<String>();
    }

    public JsonNode(String nodeId, ArrayList<String> geneIds){
        this.nodeId = nodeId;
        this.geneIds = geneIds;
    }
    public void addGeneId(String geneId){
        this.geneIds.add(geneId);
    }

    public static void main(String[] args) {
        Gson gson = new Gson();
        ArrayList<JsonNode> nodes = new ArrayList<JsonNode>();
        JsonNode node1 = new JsonNode("6");
        node1.addGeneId("3706");
        node1.addGeneId("3707");
        node1.addGeneId("80271");
        JsonNode node2 = new JsonNode("7");
        node2.addGeneId("107");
        node2.addGeneId("109");
        node2.addGeneId("114");
        JsonNode node3 = new JsonNode("8");
        node3.addGeneId("5136");
        node3.addGeneId("5137");
        node3.addGeneId("5153");
        JsonNode node4 = new JsonNode("53");
        node4.addGeneId("163688");
        node4.addGeneId("51806");
        node4.addGeneId("801");
        node4.addGeneId("805");
        node4.addGeneId("808");
        node4.addGeneId("810");

        nodes.add(node1);
        nodes.add(node2);
        nodes.add(node3);
        nodes.add(node4);

        String jsonString = gson.toJson(nodes);
        try {
            System.out.println(DBManager.sendPost(jsonString, 1000, "grid,hprd,mint,intact,kegg_pathways,dip," +
                    "PID,reactome,pdb"));
            String jsonOutput = DBManager.sendPost(jsonString, 0, "grid,hprd,mint,intact,kegg_pathways,dip," +
                    "PID,reactome,pdb");
            System.out.println(DBManager.getInteractionsMap(jsonOutput).toString());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
