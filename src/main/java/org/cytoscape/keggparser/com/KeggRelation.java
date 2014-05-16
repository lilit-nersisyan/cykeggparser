package org.cytoscape.keggparser.com;

import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.view.presentation.property.values.ArrowShape;
import org.cytoscape.view.presentation.property.values.LineType;


public class KeggRelation {
    public static final String ACTIVATION = "activation";
    public static final String INHIBITION = "inhibition";
    public static final String BINDING = "binding/association";
    public static final String INDIRECT_EFFECT = "indirect effect";
    public static final String DISSOCIATION = "dissociation";
    public static final String PHOSPHORYLATION = "phosphorylation";
    public static final String DEPHOSPHORYLATION = "dephosphorylation";
    public static final String UBIQUITINATION = "ubiquitination";
    public static final String GLYCOSYLATION = "glycosylation";
    public static final String METHYLATION = "methylation";
    public static final String COMPOUND = "compound";
    public static final String HIDDEN_COMPOUND = "hidden compound";
    public static final String EXPRESSION = "expression";
    public static final String REPRESSION = "repression";
    public static final String STATE_CHANGE = "state change";
    public static final String MISSING_INTERACTION = "missing interaction";

    public static final String ACTIVATION_VALUE = "-->";
    public static final String INHIBITION_VALUE = "--|";
    public static final String EXPRESSION_VALUE = "-->";
    public static final String REPRESSION_VALUE = "--|";
    public static final String INDIRECT_EFFECT_VALUE = "..>";
    public static final String STATE_CHANGE_VALUE = "...";
    public static final String BINDING_VALUE = "---";
    public static final String DISSOCIATION_VALUE = "-+-";
    public static final String MISSING_INTERACTION_VALUE = "-/-";
    public static final String PHOSPHORYLATION_VALUE = "+p";
    public static final String DEPHOSPHORYLATION_VALUE = "-p";
    public static final String GLYCOSYLATION_VALUE = "+g";
    public static final String UBIQUITINATION_VALUE = "+u";
    public static final String METHYLATION_VALUE = "+m";

    public static final String PPrel = "PPrel";
    public static final String PCrel = "PCrel";
    public static final String GErel = "GErel";
    public static final String ECrel = "ECrel";
    public static final String Maplink = "maplink";

    public static String COMPOUND_PROCESSED = "compound processed";
    public static String DIRECTION_REVERSED = "direction reversed";


    private KeggNode entry1;
    private KeggNode entry2;
    private String type;
    private String subtype;
    private String subtype2;
    private String relationValue;
    private String relationValue2;
    private String comment = "";

    public KeggRelation(KeggNode entry1, KeggNode entry2, String type)
            throws IllegalArgumentException {
        if (entry1 == null || entry2 == null)
            throw new IllegalArgumentException("Null entries are not allowed in KeggRelation");
        if (type == null || !EKeggRelationType.isTypeValid(type))
            throw new IllegalArgumentException("Invalid or null type is not allowed in KeggRelation");
        this.entry1 = entry1;
        this.entry2 = entry2;
        this.type = type;
    }

    public KeggNode getEntry1() {
        return entry1;
    }

    public void setEntry1(KeggNode entry1) {
        this.entry1 = entry1;
    }

    public KeggNode getEntry2() {
        return entry2;
    }

    public void setEntry2(KeggNode entry2) {
        this.entry2 = entry2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype1() {
        if (subtype == null)
            return "";
        return subtype;
    }

    public String getSubtype2() {
        if (subtype2 == null)
            return "";
        return subtype2;
    }

    public void setSubtype(String subtype) {
        if (subtype != null)
            if (this.subtype == null)
                this.subtype = subtype;
            else
                this.subtype2 = subtype;
    }

    public String getRelationValue1() {
        return relationValue == null ? "" : relationValue;
    }

    public String getRelationValue2() {
        return relationValue2 == null ? "" : relationValue2;
    }

    public void setRelationValue(String relationValue) {
        if (relationValue != null)
            if (this.subtype2 == null)
                this.relationValue = relationValue;
            else
                this.relationValue2 = relationValue;
    }

    public String getComment() {
        return comment;
    }

    public void addComment(String comment) {
        if (comment != null)
            this.comment += comment + "; ";
    }

    public ArrowShape getArrowShape() {
        if (subtype == null)
            subtype = "";
        if (subtype2 == null)
            subtype2 = "";
        if (subtype.equals(ACTIVATION) || subtype2.equals(ACTIVATION))
            return ArrowShapeVisualProperty.DELTA;
        if (subtype.equals(INHIBITION) || subtype2.equals(INHIBITION))
            return ArrowShapeVisualProperty.T;
        return ArrowShapeVisualProperty.NONE;
    }

    public LineType getLineStyle() {
        if (subtype2 == null)
            subtype2 = "";
        if (subtype != null) {
            if (subtype.equals(INDIRECT_EFFECT) || subtype2.equals(INDIRECT_EFFECT))
                return LineTypeVisualProperty.LONG_DASH;
            if (subtype.equals(DISSOCIATION) || subtype2.equals(DISSOCIATION))
                return LineTypeVisualProperty.DOT;
            if (subtype.equals(EXPRESSION) || subtype2.equals(EXPRESSION))
                return LineTypeVisualProperty.LONG_DASH;
            if (subtype.equals(REPRESSION) || subtype2.equals(REPRESSION))
                return LineTypeVisualProperty.LONG_DASH;
            if (subtype.equals(STATE_CHANGE) || subtype2.equals(STATE_CHANGE))
                return LineTypeVisualProperty.DASH_DOT;
            if (subtype.equals(MISSING_INTERACTION) || subtype2.equals(MISSING_INTERACTION))
                return LineTypeVisualProperty.DASH_DOT;
        }
        return LineTypeVisualProperty.SOLID;
    }

    public String getEdgeLabel() {
        if (subtype2 == null)
            subtype2 = "";
        if (subtype != null) {
            if (subtype.equals(PHOSPHORYLATION) || subtype2.equals(PHOSPHORYLATION))
                return "\n+p\n";
            if (subtype.equals(DEPHOSPHORYLATION) || subtype2.equals(DEPHOSPHORYLATION))
                return "\n-p\n";
            if (subtype.equals(GLYCOSYLATION) || subtype2.equals(GLYCOSYLATION))
                return "\n+g\n";
            if (subtype.equals(UBIQUITINATION) || subtype2.equals(UBIQUITINATION))
                return "\n+u\n";
            if (subtype.equals(METHYLATION) || subtype2.equals(METHYLATION))
                return "\n+m\n";
        }
        return "";
    }

    @Override
    public String toString() {
        return "Relation{" +
                "entry1=" + entry1.getId() +
                ", entry2=" + entry2.getId() +
                ", type='" + type + '\'' +
                ", subtype='" + (subtype == null ? "null" : subtype) + '\'' +
                ", subtype2='" + (subtype2 != null ? subtype2 : "null") + '\'' +
                ", relationValue='" + (relationValue == null ? "null" : relationValue) + '\'' +
                '}';
    }

    @Override
    public KeggRelation clone() {
        KeggRelation relation = new KeggRelation(entry1, entry2, type);
        setSubtype(subtype);
        setRelationValue(relationValue);
        if (subtype2 != null)
            setSubtype(subtype2);
        if (relationValue2 != null)
            setRelationValue(relationValue2);
        return relation;
    }

    @Override
    public boolean equals(Object o) {
        KeggRelation relation;
        if (o instanceof KeggRelation) {
            relation = (KeggRelation) o;
            if (relation.getEntry1().equals(entry1))
                if (relation.getEntry2().equals(entry2))
                    return true;
            return false;
        } else
            return false;
    }
}
