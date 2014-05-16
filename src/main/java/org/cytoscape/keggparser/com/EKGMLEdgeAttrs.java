package org.cytoscape.keggparser.com;

public enum EKGMLEdgeAttrs {
    KGML_SUBTYPE("subtype",""),
    KGML_SUBTYPE_VALUE("value","---"),
    KGML_ENTRY1("entry1",""),
    KGML_ENTRY2("entry2",""),
    KGML_TYPE("type","PPrel"),
    KGML_SUBTYPE_NAME("name",KeggRelation.BINDING);

    private String attribute;
    private String defaultValue;

    EKGMLEdgeAttrs(String attribute, String defaultValue){
        this.attribute = attribute;
        this.defaultValue = defaultValue;
    }

    public String getAttrName(){
        return attribute;
    }
    public  String  getDefaultValue() {
        return defaultValue;
    }
}
