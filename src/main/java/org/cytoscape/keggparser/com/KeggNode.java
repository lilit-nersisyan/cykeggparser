package org.cytoscape.keggparser.com;

import java.awt.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class KeggNode implements Comparable<KeggNode> {

    public static final String RECTANGLE = "rectangle";
    public static final String CIRCLE = "circle";
    public static final String ROUND_RECTANGLE = "roundrectangle";
    public static final String LINE = "line";

    public static final String GENE = "gene";
    public static final String ORTHOLOG = "ortholog";
    public static final String REACTION = "reaction";
    public static final String GROUP = "group";
    public static final String COMPOUND = "compound";
    public static final String MAP = "map";
    public static final String ENZYME = "enzyme";

    private String NA = "n/a";
    private String name = NA;
    private int id = 0;
    private String type;
    private String link = NA;
    private String graphicsName = NA;
    private Color fgColor;
    private Color bgColor;
    private String fgColorAttr;
    private String bgColorAttr;
    private String shape = NA;
    private int x;
    private int y;
    private int width;
    private int height;
    private String cellName = NA;
    private ArrayList<Integer> componentIds;
    private String comment = "";
    private int belongsToGroup = 0;

    public KeggNode(int id, String name, String type) throws IllegalArgumentException {
        if (id <= 0)
            throw new IllegalArgumentException("Node id values should be greater than 0");
        this.id = id;
        if (name == null)
            name = "";
        this.name = name;
        if (type == null)
            throw new IllegalArgumentException("Node type should not be null");
        if (!EKeggNodeAttrs.isNodeTypeValid(type))
            throw new IllegalArgumentException("Invalid node type");
        this.type = type;
    }


    public void setGroupFlag(int groupId) {
        this.belongsToGroup = groupId;
    }

    public int getGroupId() {
        return belongsToGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null)
            name = "";
        this.name = name;
    }

    public void addComponentId(int id) throws IllegalArgumentException {
        if (id <= 0)
            throw new IllegalArgumentException("Component ids should be greater than 0");
        if (componentIds == null)
            componentIds = new ArrayList<Integer>();
        componentIds.add(id);
    }

    public ArrayList<Integer> getComponentIds() {
        return componentIds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) throws IllegalArgumentException {
        if (id <= 0)
            throw new IllegalArgumentException("Node ids should be greater than 0");
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) throws IllegalArgumentException {
        if (type == null)
            throw new IllegalArgumentException("Node type should not be null");
        if (!EKeggNodeAttrs.isNodeTypeValid(type))
            throw new IllegalArgumentException("Invalid node type");
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        if (link != null)
            this.link = link;
    }

    public String getGraphicsName() {
        return graphicsName;
    }

    public void setGraphicsName(String graphicsName) {
        if (graphicsName != null)
            this.graphicsName = graphicsName;
        if (graphicsName != null && graphicsName.contains(","))
            this.cellName = graphicsName.substring(0, graphicsName.indexOf(","));
        else if (graphicsName != null)
            this.cellName = graphicsName;
    }

    public String getCellName() {
        return cellName;
    }

    public String getEntrezIDsFromName() {
        String entrezIds = "";
        String delimiter = "abcdefghijklmnopqrstuvwxyz: ,\t";
        if (name != null) {
            StringTokenizer tokenizer = new StringTokenizer(name, delimiter);
            while (tokenizer.hasMoreTokens())
                entrezIds += tokenizer.nextToken() + ", ";
        }
        return entrezIds;
    }

    public void setFgColor(Color fgColor) {
        this.fgColor = fgColor;
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getComment() {
        return comment;
    }

    public void addComment(String comment) {
        this.comment += comment + "; ";
    }

    @Override
    public String toString() {

        return "\nNode{" +
                "name='" + (name == null ? "null" : name) + '\'' +
                ", id=" + id +
                ", EntrezIDs=" + getEntrezIDsFromName() +
                ", type='" + type + '\'' +
                ", link='" + link + '\'' +
                ", graphicsName=" + graphicsName +
                ", fgColor=" + (fgColor == null ? "null" : fgColor) +
                ", bgColor=" + (bgColor == null ? "null" : bgColor) +
                ", shape='" + (shape == null ? "null" : shape) + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    public void setBgColorAttr(String bgColorAttr) {
        this.bgColorAttr = bgColorAttr;
    }

    public void setFgColorAttr(String fgColorAttr) {
        this.fgColorAttr = fgColorAttr;
    }

    public String getBgColorAttr() {
        return bgColorAttr;
    }

    public String getFgColorAttr() {
        return fgColorAttr;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof KeggNode) {
            KeggNode keggNode = (KeggNode) object;
            return (this.getId() == keggNode.getId()
//                    && this.getName().equals(keggNode.getName())
//                    && this.getType().equals(keggNode.getType())
            );
        }
        return false;
    }

    @Override
    public int compareTo(KeggNode o) {
        return equals(o) ? 0 : 1;
    }
}
