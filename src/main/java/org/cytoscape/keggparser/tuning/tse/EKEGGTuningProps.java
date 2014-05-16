package org.cytoscape.keggparser.tuning.tse;

public enum EKEGGTuningProps {
    Database("tuning.database"),
    TissueType("tuning.tissueType"),
    Tissue("tuning.tissue"),
    GeneExpressionMode("tuning.geneExpression"),
    PPIMode("tuning.ppi"),
    GenerateNewNetwork("tuning.generateNewNetwork"),
    ExpThreshold("tuning.expThreshold"),
    GeneIdAttr("tuning.geneIdAttr"),
    TypeAttr("tuning.typeAttr"),
    TypeValues("tuning.typeValues"),
    TSEConflictMode("tuning.tse.conflictMode"),
    PPITHreshold("tuning.ppi.threshold"),
    PPISourceGRID("tuning.ppi.source.GRID"),
    PPISourceMINT("tuning.ppi.source.MINT"),
    PPISourceKEGG("tuning.ppi.source.KEGG"),
    PPISourceDIP("tuning.ppi.source.DIP"),
    PPISourcePDB("tuning.ppi.source.PDB"),
    TSEKeepAbsentGenes("tuning.tse.keepAbsentGenes"),
    PPIKeepIndirectInteractions("tuning.ppi.keepIndirectInteractions")
    ;


    private String name = "";
    
    EKEGGTuningProps(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
