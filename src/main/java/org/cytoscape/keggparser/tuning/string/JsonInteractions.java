package org.cytoscape.keggparser.tuning.string;


import java.util.HashMap;

public class JsonInteractions {
    private HashMap<String, HashMap<String, InteractionParams>> interactions;


    public class InteractionParams {
        private String action;
        private String mode;
        private String is_acting;
        private double score;
        private String sources;
        private String transferred_sources;

    }
}