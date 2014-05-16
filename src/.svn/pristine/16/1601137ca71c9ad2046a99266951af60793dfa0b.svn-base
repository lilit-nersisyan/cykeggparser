package org.cytoscape.keggparser.tuning.tse;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

public class FilterGeneCards {
    private File biogpsData = new File("d:\\Workspace\\cytoscape_app_fix\\GeneCards\\BiogpsData.txt");
    private File biogpsMeanData = new File("d:\\Workspace\\cytoscape_app_fix\\GeneCards\\BiogpsMeanData.xls");
    private TreeMap<String, Double[]> pureValues = new TreeMap<String, Double[]>();
    private TreeMap<String, Double[]> meanValues = new TreeMap<String, Double[]>();
    private String tissues;
    private int tissueNum = 84;

    public void readData(){
        try {
            java.util.Scanner scanner = new java.util.Scanner(biogpsData);
            if (scanner.hasNext())
                tissues = scanner.nextLine();
            StringTokenizer tokenizer;
            String geneID;
            while(scanner.hasNext()){
                tokenizer = new StringTokenizer(scanner.nextLine(), "\t");
                geneID = tokenizer.nextToken();                
                Double[] values = new Double[tissueNum];
                int i = 0;
                while (tokenizer.hasMoreTokens())
                    values[i++] = Double.parseDouble(tokenizer.nextToken());
                pureValues.put(geneID, values);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void averageData(){
        TreeSet<String> meanGeneSet = new TreeSet<String>();
        for (String gene : pureValues.keySet())
            if (!gene.contains("#"))
                meanGeneSet.add(gene);
            else
                meanGeneSet.add(gene.substring(0, gene.indexOf("#")));
        for (String meanGene : meanGeneSet){
            Double[] values = new Double[tissueNum];
            for (int i = 0; i < tissueNum; i++)
                values[i] = 0d;
            meanValues.put(meanGene, values);
            int count = 0;
            for (String pureGene : pureValues.keySet()){
                if(pureGene.contains(meanGene)){
                    for (int i = 0; i < tissueNum; i++)
                        values[i] += pureValues.get(pureGene)[i]; 
                    count++;                    
                }
            }
            for (int i = 0; i < tissueNum; i++)
                values[i] /= count;
        }
    }
    
    public void printMeanData(){
        try {
            PrintWriter writer = new PrintWriter(biogpsMeanData);
            writer.append(tissues + "\n");
            for(String gene : meanValues.keySet()){
                writer.append(gene + "\t");
                for (Double value : meanValues.get(gene)){
                    writer.append(value + "\t");
                }
                writer.append("\n");
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args) {
        FilterGeneCards filter = new FilterGeneCards();
        filter.readData();
        filter.averageData();
        filter.printMeanData();
    }
}
