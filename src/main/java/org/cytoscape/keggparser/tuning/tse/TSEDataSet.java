package org.cytoscape.keggparser.tuning.tse;

import org.cytoscape.keggparser.KEGGParserPlugin;

import java.util.Arrays;
import java.util.HashMap;

public class TSEDataSet {
    public static final int MIN = 0;
    public static final int MEAN = 1;
    public static final int MAX = 2;
    private int threshold;
    private double nThreshold;
    private String tissue;
    private int mode = MEAN;
    private Double[] values;

    private HashMap<String, Double> dataSet = new HashMap<String, Double>();
    private HashMap<String, Integer> geneCounts = new HashMap<String, Integer>();
    private Double max;
    private Double min;


    public TSEDataSet(String tissue) throws IllegalArgumentException{
        if (tissue == null)
            throw  new IllegalArgumentException("Tissue cannot be null");
        this.tissue = tissue;
    }

    public void setMode(int mode) {
        if (mode != MIN && mode != MAX && mode != MEAN)
            throw new IllegalArgumentException("Invalid mode: should be from {MIN, MAX, MEAN} fiealds in TSEDataSet");
        this.mode = mode;
    }

    public void addExp(String geneId, Double exp) {
        if (dataSet.containsKey(geneId)) {
            switch (mode) {
                case MEAN: {
                    int count = geneCounts.get(geneId) + 1;
                    geneCounts.remove(geneId);
                    geneCounts.put(geneId, count);
                    double meanExp = (dataSet.get(geneId) * (count - 1) + exp) / count;
                    dataSet.remove(geneId);
                    dataSet.put(geneId, meanExp);
                    break;
                }
                case MIN: {
                    if (dataSet.get(geneId) > exp) {
                        dataSet.remove(geneId);
                        dataSet.put(geneId, exp);
                    }
                    break;
                }
                case MAX: {
                    if (dataSet.get(geneId) < exp) {
                        dataSet.remove(geneId);
                        dataSet.put(geneId, exp);
                    }
                    break;
                }
                default:
                    break;
            }
        } else {
            switch (mode) {
                case MEAN: {
                    geneCounts.put(geneId, 1);
                    dataSet.put(geneId, exp);
                    break;
                }
                case MIN:
                case MAX: {
                    dataSet.put(geneId, exp);
                    break;
                }
                default:
                    break;
            }
        }


        dataSet.put(geneId, exp);
    }

    public boolean isGeneExpressed(String geneId) {
        if (dataSet.containsKey(geneId))
            return dataSet.get(geneId) >= nThreshold;
        else {
            if (KEGGParserPlugin.getKeggProps().getProperty(EKEGGTuningProps.TSEKeepAbsentGenes.getName()).equals("true"))
                return true;
            else
                return false;
        }
    }

    public boolean containsGene(String geneId) {
        return dataSet.containsKey(geneId);
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
        nThreshold = getAbsValue(threshold);
    }

    public String getTissue() {
        return tissue;
    }

    public Double getMax() {
        return max;
    }

    public Double getMin() {
        return min;
    }

    public void setTissue(String tissue) {
        this.tissue = tissue;
    }

    public void sort() {
        values = new Double[dataSet.size()];
        dataSet.values().toArray(values);
        Arrays.sort(values);

        min = values[0];
        max = values[values.length-1];

    }

    public double getAbsValue(int percentile){
        int index = Math.round(percentile* values.length/100);
        if (index < 0)
            index = 0;
        if (index > values.length - 1)
            index = values.length - 1;
        return values[index];
    }

    public int size() {
        return dataSet.size();
    }
}
