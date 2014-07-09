package org.cytoscape.keggparser.com;

public enum EKGMLNodeAttrs {
    KGML_ID("id", ""),
    KGML_NAME("name", ""),
    KGML_TYPE("type", ""),
    KGML_LINK("link", ""),
    KGML_WIDTH("width", "30"),
    KGML_HEIGHT("height", "10"),
    KGML_FGCOLOR("fgcolor", "#FFFFFF"),
    KGML_BGCOLOR("bgcolor", "#FFFFFF"),
    KGML_X("x", "0"),
    KGML_Y("y", "0"),
    KGML_COORDS("coords", "");

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
