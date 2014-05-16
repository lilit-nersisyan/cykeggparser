package org.cytoscape.keggparser.tuning.string;

import java.io.*;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class FileManager {
    public static void retrieveEnsembleIds(File inFile, File outFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            PrintWriter writer = new PrintWriter(outFile);
            String line;
            StringTokenizer tokenizer;
            TreeSet<String> idSet = new TreeSet<String>();
            int index = 0;
            while ((line = reader.readLine()) != null) {
                while ((line = reader.readLine()) != null && line.startsWith("9606.")) {
                    tokenizer = new StringTokenizer(line);
                    idSet.add(tokenizer.nextToken().substring(5));
                    idSet.add(tokenizer.nextToken().substring(5));
                    System.out.println(index++ + line);
                }
                if (index > 0) break;

            }
            for (String id : idSet) {
                writer.append(id + "\n");
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void filterHuman(File inFile, File outFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            PrintWriter writer = new PrintWriter(outFile);
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                while ((line = reader.readLine()) != null && line.startsWith("9606.")) {
                    System.out.println(index++);
                    writer.append(line + "\n");
                }
                if (index > 0) break;

            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args) {
        File inFile = new File("d:\\String\\protein.actions.detailed.v9.05.txt");
        File outFile = new File("d:\\String\\protein.actions.detailed.v9.05_human.txt");
        filterHuman(inFile, outFile);
    }
}
