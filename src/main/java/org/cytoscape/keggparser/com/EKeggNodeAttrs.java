package org.cytoscape.keggparser.com;

import org.cytoscape.model.CyNetwork;

public enum EKeggNodeAttrs {
    ENTRY_ID("Entry_id"),
    NAME(CyNetwork.NAME),
    GRAPHICSNAME("GraphicsName"),
    LABEL("Label"),
    TYPE("Type"),
    COLOR("Color"),
    LINK("Link"),
    SHAPE("Shape"),
    WIDTH("Width"),
    HEIGHT("Height"),
    COMMENT("Comment"),
    GROUP("Group"),
    EntrezIDs("EntrezIDs"),
    FGCOLOR("fgColor"),
    BGCOLOR("bgColor"),
    ENTREZ_ID("entrezId"),
    PARENT_ENTRY_ID("parentEntryId"),
    UNIQUEID("Unique_id"),
    KEGG_X("kegg.x"),
    KEGG_Y("kegg.y");


    private String attribute;

    EKeggNodeAttrs(String attribute) {
        this.attribute = attribute;
    }

    public String getAttrName() {
        return attribute;
    }

    public static boolean isNodeTypeValid(String type) {
        if (type == null)
            return false;
        if (type.equals(KeggNode.GENE))
            return true;
        if (type.equals(KeggNode.COMPOUND))
            return true;
        if (type.equals(KeggNode.ENZYME))
            return true;
        if (type.equals(KeggNode.GROUP))
            return true;
        if (type.equals(KeggNode.MAP))
            return true;
        if (type.equals(KeggNode.ORTHOLOG))
            return true;
        return false;
    }

    public static String[] getKeggNodeTypes() {
        return new String[]{KeggNode.GENE, KeggNode.COMPOUND, KeggNode.GROUP,
                KeggNode.MAP, KeggNode.ENZYME, KeggNode.ORTHOLOG};
    }

    public static String[] getKeggNodeGraphicsTypes() {
        return new String[]{KeggNode.RECTANGLE, KeggNode.CIRCLE, KeggNode.ROUND_RECTANGLE,
                KeggNode.LINE};
    }

}
