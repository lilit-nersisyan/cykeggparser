package org.cytoscape.keggparser.com;


public enum EKeggRelationType {


    ACTIVATION (KeggRelation.ACTIVATION, KeggRelation.ACTIVATION_VALUE, KeggRelation.PPrel),
    INHIBITION (KeggRelation.INHIBITION, KeggRelation.INHIBITION_VALUE, KeggRelation.PPrel),
    BINDING (KeggRelation.BINDING, KeggRelation.BINDING_VALUE, KeggRelation.PPrel),
    INDIRECT_EFFECT_PP (KeggRelation.INDIRECT_EFFECT, KeggRelation.INDIRECT_EFFECT_VALUE, KeggRelation.PPrel),
    INDIRECT_EFFECT_GE (KeggRelation.INDIRECT_EFFECT, KeggRelation.INDIRECT_EFFECT_VALUE, KeggRelation.GErel),
    DISSOCIATION (KeggRelation.DISSOCIATION, KeggRelation.DISSOCIATION_VALUE, KeggRelation.PPrel),
    PHOSPHORYLATION (KeggRelation.PHOSPHORYLATION, KeggRelation.PHOSPHORYLATION_VALUE, KeggRelation.PPrel),
    DEPHOSPHORYLATION (KeggRelation.DEPHOSPHORYLATION, KeggRelation.DEPHOSPHORYLATION_VALUE, KeggRelation.PPrel),
    UBIQUITINATION (KeggRelation.UBIQUITINATION, KeggRelation.UBIQUITINATION_VALUE, KeggRelation.PPrel),
    GLYCOSYLATION (KeggRelation.GLYCOSYLATION, KeggRelation.GLYCOSYLATION_VALUE, KeggRelation.PPrel),
    METHYLATION (KeggRelation.METHYLATION, KeggRelation.METHYLATION_VALUE, KeggRelation.PPrel),
    COMPOUND_PP (KeggRelation.COMPOUND, "", KeggRelation.PPrel),
    COMPOUND_EC (KeggRelation.COMPOUND, "", KeggRelation.ECrel),
    HIDDEN_COMPOUND (KeggRelation.HIDDEN_COMPOUND, "", KeggRelation.ECrel),
    EXPRESSION (KeggRelation.EXPRESSION, KeggRelation.EXPRESSION_VALUE, KeggRelation.GErel),
    REPRESSION (KeggRelation.REPRESSION, KeggRelation.REPRESSION_VALUE, KeggRelation.GErel),
    STATE_CHANGE (KeggRelation.STATE_CHANGE, KeggRelation.STATE_CHANGE_VALUE, KeggRelation.PPrel),
    MISSING_INTERACTION_PP (KeggRelation.MISSING_INTERACTION, KeggRelation.MISSING_INTERACTION_VALUE, KeggRelation.PPrel),
    MISSING_INTERACTION_GE (KeggRelation.MISSING_INTERACTION, KeggRelation.MISSING_INTERACTION_VALUE, KeggRelation.GErel)
    ;


    private String relationSubType;
    private String relationValue;
    private String relationType;

    private EKeggRelationType(String relationSubType, String relationValue, String relationType) {

        this.relationSubType = relationSubType;
        this.relationValue = relationValue;
        this.relationType = relationType;
    }

    public String getRelationSubType() {
        return relationSubType;
    }

    public String getRelationValue() {
        return relationValue;
    }

    public String getRelationType() {
        return relationType;
    }

    public static String getRelationValueFromSubType(String subtype){
        for (EKeggRelationType relationType : EKeggRelationType.values()){
            if (relationType.getRelationSubType().equals(subtype))
                return relationType.getRelationValue();
        }
        return "";
    }



    public static boolean isTypeValid(String type){
        if (type.equals(KeggRelation.PPrel))
            return true;
        if (type.equals(KeggRelation.ECrel))
            return true;
        if (type.equals(KeggRelation.GErel))
            return true;
        if (type.equals(KeggRelation.PCrel))
            return true;
        if (type.equals(KeggRelation.Maplink))
            return true;
        return false;
    }

    public static boolean isSubTypeValid(String subtype){
        for (EKeggRelationType relationType : EKeggRelationType.values()){
            if (subtype.equals(relationType.getRelationSubType()))
                return true;
        }
        return false;
    }

    public static boolean isSubTypeValueValid(String subtypeValue){
        for (EKeggRelationType relationType : EKeggRelationType.values()){
            if (subtypeValue.equals(relationType.getRelationValue()))
                return true;
        }
        return false;
    }

}
