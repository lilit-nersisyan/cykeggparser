package org.cytoscape.keggparser.tuning.string;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBManager {

    public static Map<String, Map<String,
            ArrayList<JsonInteractions.InteractionParams>>> getInteractionsMap(String output) {
        Gson gson = new Gson();
        JsonInteractions interactions = null;
        Map<String, Map<String, ArrayList<JsonInteractions.InteractionParams>>> map =
                new HashMap<String, Map<String, ArrayList<JsonInteractions.InteractionParams>>>();

        TypeToken typeToken = TypeToken.get(map.getClass());
        map = gson.fromJson(output, typeToken.getType());

        return map;
    }

    public static String sendPost(String json, double score, String sources) throws Exception {

        String urlParameters = "?score=" + score + "&sources=" + sources;
        String targetURL = "http://www.molbiol.sci.am/big/apps/cy_kp/gene/interaction.php";
//        System.out.println("Sending request to " + targetURL + urlParameters);
//        System.out.println("Requested json: " + json);
        URL wsurl = new URL(targetURL + urlParameters);
        URLConnection conn = wsurl.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(json);
        wr.flush();
        // Get the response
        StringBuilder output = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        System.out.println("Request processed");
        String line;
        while ((line = rd.readLine()) != null) {
            output.append(line);
        }
        wr.close();
        rd.close();

        return output.toString();

    }

    public static String sendPost(String json, double score, String org, String sources)
            throws Exception {

        String urlParameters = "?score=" + score + "&sources=" + sources + "&org=" + org;
        String targetURL = "http://www.molbiol.sci.am/big/apps/cy_kp/gene/interaction3.php";
//        System.out.println("Sending request to " + targetURL + urlParameters);
//        System.out.println("Requested json: " + json);
        URL wsurl = new URL(targetURL + urlParameters);
        URLConnection conn = wsurl.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(json);
        wr.flush();
        // Get the response
        StringBuilder output = new StringBuilder();
        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (Exception e) {
            LoggerFactory.getLogger(DBManager.class).error(e.getMessage());
        }
        if (rd != null) {
//            System.out.println("Request processed");
            String line;
            while ((line = rd.readLine()) != null) {
                output.append(line);
            }
            wr.close();
            rd.close();
        }

        return output.toString();

    }

    public static Map<String, Map<String,
            ArrayList<JsonInteractions.InteractionParams>>> getInteractionsMap(String jsonString,
                                                                               int threshold, String sourceString) {
        try {
            return getInteractionsMap(sendPost(jsonString, threshold, sourceString));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    public static Map<String, Map<String,
            ArrayList<JsonInteractions.InteractionParams>>> getInteractionsMap(
            String jsonString, int threshold, String org, String sourceString) {
        try {
            return getInteractionsMap(sendPost(jsonString, threshold, org, sourceString));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    public static void addOrgNameIntoTableFromFile(File file){
        try {
            StringBuffer output = new StringBuffer();
            BufferedReader rd = new BufferedReader(new FileReader(file));
            String line;
            while ((line = rd.readLine()) != null) {
                output.append(line + "\n");
            }
            rd.close();

            PrintWriter writer = new PrintWriter(new File("D:/String/php/query.txt"));
            String[] lines = output.toString().split("\n");
            String tNumber, orgName;
            String[] tokens;
            String id;
            for (int i = 0; i < lines.length; i++){
                line = lines[i];
                tokens = line.split(" ");

                orgName = tokens[0];
                id = tokens[1];

                if (id != null) {
                    String query = "UPDATE organizm_gene set org_name='" + orgName +
                            "' WHERE organizm_id=" + id ;
                    writer.append(query + ";\n");
                    System.out.println(orgName + " " + id);
                }
            }
            writer.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }



    public static void main(String[] args) {
        JsonNode node1 = new JsonNode("106");
        JsonNode node2 = new JsonNode("103");
        JsonNode node3 = new JsonNode("104");
        JsonNode node4 = new JsonNode("105");

        node1.addGeneId("973");
        node2.addGeneId("695");
        node3.addGeneId("4067");
        node4.addGeneId("974");

        ArrayList<JsonNode> jsonNodes = new ArrayList<JsonNode>();
        jsonNodes.add(node1);
        jsonNodes.add(node2);
        jsonNodes.add(node3);
        jsonNodes.add(node4);
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonNodes);

//        try {
//            System.out.println(sendPost(jsonString, 0, "grid,hprd,mint,intact,kegg_pathways,dip," +
//                    "PID,reactome,pdb"));
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
            addOrgNameIntoTableFromFile(new File("D:/String/php/kegg2taxonomy.txt"));
    }



}
