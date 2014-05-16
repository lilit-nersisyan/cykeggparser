package org.cytoscape.keggparser.com;

public enum EKGMLNodeAttrs {
    KGML_ID("id", ""),
    KGML_NAME("name", ""),
    KGML_TYPE("type", ""),
    KGML_LINK("link", ""),
    KGML_WIDTH("width", ""),
    KGML_HEIGHT("height", ""),
    KGML_FGCOLOR("fgcolor", ""),
    KGML_BGCOLOR("bgcolor", ""),
    KGML_X("x", ""),
    KGML_Y("y", "");

    private String attribute;
    private String defaultValue;

    EKGMLNodeAttrs(String attribute, String defaultValue){
        this.attribute = attribute;
        this.defaultValue = defaultValue;
    }

    public String getAttrName(){
        return attribute;
    }
    public  String getDefaultValue() {
        return defaultValue;
    }


}
