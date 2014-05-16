package org.cytoscape.keggparser.com;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Graph {
    private String name = "";
    private String organism = "";
    private String number = "";
    private String title = "";
    private String image = "";
    private String link = "";
    private TreeMap<Integer, KeggNode> nodes;
    private List<KeggNode> groupNodes;
    private ArrayList<KeggRelation> relations;


    public Graph() {
        nodes = new TreeMap<Integer, KeggNode>();
        relations = new ArrayList<KeggRelation>();
    }

    public void setPathwayName(String name) {
        if (name != null)
            this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        if (organism != null)
            this.organism = organism;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        if (number != null)
            this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        if (image != null)
            this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        if (link != null)
            this.link = link;
    }

    public TreeMap<Integer, KeggNode> getNodes() {
        return nodes;
    }

    public ArrayList<KeggRelation> getRelations() {
        return relations;
    }


    public void addNode(KeggNode node) throws IllegalArgumentException {
        if (node == null)
            throw new IllegalArgumentException("Null node cannot be added to graph");
        if (!nodes.containsKey(node.getId()))
            nodes.put(node.getId(), node);
    }

    public KeggNode getNode(int id) {
        if (nodes.containsKey(id))
            return nodes.get(id);
        return null;
    }

    public void addRelation(KeggRelation relation) throws IllegalArgumentException {
        if (relation == null)
            throw new IllegalArgumentException("Null relations cannot be added to graph");
        if (!relations.contains(relation))
            relations.add(relation);
    }

    public List<KeggNode> getNeighbors(KeggNode node) {
        List<KeggNode> neiggbors = new ArrayList<KeggNode>();
        for (KeggRelation relation : relations) {
            if (relation.getEntry1().equals(node))
                neiggbors.add(relation.getEntry2());
            else if (relation.getEntry2().equals(node))
                neiggbors.add(relation.getEntry1());
        }
        return neiggbors;
    }

    public List<KeggRelation> getIncidentEdges(KeggNode node) {
        List<KeggRelation> edges = new ArrayList<KeggRelation>();
        for (KeggRelation relation : relations)
            if (relation.getEntry1().equals(node) || relation.getEntry2().equals(node))
                edges.add(relation);
        return edges;
    }

    public boolean edgeExists(KeggNode node1, KeggNode node2) {
        if (node1 == null || node2 == null)
            return false;
        for (KeggRelation relation : relations)
            if (relation.getEntry1().equals(node1) && relation.getEntry2().equals(node2))
                return true;
        return false;
    }

    public KeggRelation getRelation(KeggNode node1, KeggNode node2) {
        if (node1 == null || node2 == null)
            return null;
        for (KeggRelation relation : relations)
            if (relation.getEntry1().equals(node1) && relation.getEntry2().equals(node2))
                return relation;
        return null;
    }

    public ArrayList<KeggRelation> getNodeRelations(KeggNode node) {
        if (node == null)
            return null;
        ArrayList<KeggRelation> nodeRelations = new ArrayList<KeggRelation>();
        for (KeggRelation relation : relations)
            if (relation.getEntry1().equals(node) || relation.getEntry2().equals(node))
                nodeRelations.add(relation);
        return nodeRelations;
    }


    private void flagGroupNodes() {
        for (Map.Entry<Integer, KeggNode> nodeEntry : nodes.entrySet()) {
            KeggNode node = nodeEntry.getValue();
            if (node.getType().equals(KeggNode.GROUP)) {
                if (node.getComponentIds() != null)
                    for (int compId : node.getComponentIds()) {
                        getNode(compId).setGroupFlag(node.getId());
                    }
            }
        }
    }

    public ArrayList<Integer> processGroups(){
        ArrayList<Integer> processedGroupIds = new ArrayList<Integer>();
        flagGroupNodes();
        String groupNodes = "";
        ArrayList<KeggNode> groupNodesList = new ArrayList<KeggNode>();
        for (Map.Entry<Integer, KeggNode> keggNodeEntry : nodes.entrySet()) {
            if (keggNodeEntry.getValue().getType().equals(KeggNode.GROUP)) {
                KeggNode groupNode = keggNodeEntry.getValue();
                ArrayList<KeggNode> components = new ArrayList<KeggNode>();
                groupNodesList.add(groupNode);
                groupNodes += groupNode.getId() + ":";
                for (int id : groupNode.getComponentIds()) {
                    components.add(getNode(id));
                    groupNodes += id + ",";
                }
                groupNodes += ";";

                //Copy group edges to the
                KeggNode inNode = null;
                KeggNode outNode;
                ArrayList<KeggNode> inComponents = new ArrayList<KeggNode>();
                ArrayList<KeggNode> outComponents = new ArrayList<KeggNode>();
                ArrayList<KeggNode> biComponents = new ArrayList<KeggNode>();
                ArrayList<KeggNode> incomingNodes = new ArrayList<KeggNode>();
                ArrayList<KeggNode> outgoingNodes = new ArrayList<KeggNode>();
                ArrayList<KeggNode> incomingGroupNodes = new ArrayList<KeggNode>();
                ArrayList<KeggNode> outgoingGroupNodes = new ArrayList<KeggNode>();

                //Sort nodes according to indegree and outdegree composition
                for (KeggNode node : nodes.values()) {

                    if (edgeExists(node, groupNode)) {
                        if (!incomingGroupNodes.contains(node))
                            incomingGroupNodes.add(node);
                    } else if (edgeExists(groupNode, node)) {
                        if (!outgoingGroupNodes.contains(node))
                            outgoingGroupNodes.add(node);
                    }
                    int size = components.size();
                    for (KeggNode component : components) {
                        if (edgeExists(node, component)) {
                            if (!incomingNodes.contains(node))
                                incomingNodes.add(node);
                            if (outComponents.contains(component)) {
                                if (!biComponents.contains(component))
                                    biComponents.add(component);
                                outComponents.remove(component);
                            } else if (!inComponents.contains(component))
                                inComponents.add(component);
                        } else if (edgeExists(component, node)) {
                            if (inComponents.contains(component)) {
                                if (!biComponents.contains(component))
                                    biComponents.add(component);
                                inComponents.remove(component);
                            } else if (!outComponents.contains(component))
                                outComponents.add(component);
                            if (!outgoingNodes.contains(node))
                                outgoingNodes.add(node);
                        }
                    }

                }

                for (KeggNode component : inComponents)
                    components.remove(component);
                for (KeggNode component : outComponents)
                    components.remove(component);
                for (KeggNode component : biComponents)
                    components.remove(component);

                KeggNode prevNode = null;

                if (biComponents.size() > 0) {
                    if (inComponents.size() > 0) {
                        if (outComponents.isEmpty())
                            for (KeggNode biNode : biComponents) {
                                if (!outComponents.contains(biNode))
                                    outComponents.add(biNode);
                            }
                    } else for (KeggNode biNode : biComponents) {
                        if (!inComponents.contains(biNode))
                            inComponents.add(biNode);
                    }
                }



                    /*
                    Make the first node the innode. Remove all other incomming edges and move them to
                    the innode.
                     */
                if (inComponents.size() > 0) {
                    inNode = inComponents.get(0);
                    prevNode = inNode;
                    inComponents.remove(0);
                    if (inComponents.size() > 0)
                        for (KeggNode componentNode : inComponents) {
                            for (KeggNode incomingNode : incomingNodes)
                                if (edgeExists(incomingNode, componentNode)) {
                                    redirectEdge(incomingNode, componentNode, incomingNode, inNode);
                                }
                            connectNodes(prevNode, componentNode);
                            prevNode = componentNode;
                        }

                } else if (components.size() > 0) {
                    inNode = components.get(0);
                    components.remove(0);
                    prevNode = inNode;
                } else if (outComponents.size() > 0) {
                    inNode = outComponents.get(0);
                }


                while (components.size() > 0) {
                    if (prevNode != null)
                        connectNodes(prevNode, components.get(0));
                    prevNode = components.get(0);
                    components.remove(0);
                }
                    /*
                    Make the first node the outNode. Remove all the edges from other
                    components in outComponents and redirect them from the outNode.
                     */
                if (outComponents.size() > 0) {
                    outNode = outComponents.get(0);
                    outComponents.remove(0);
                    if (outComponents.size() > 0)
                        for (KeggNode componentNode : outComponents) {
                            for (KeggNode outgoingNode : outgoingNodes)
                                if (edgeExists(componentNode, outgoingNode)) {
                                    redirectEdge(componentNode, outgoingNode, outNode, outgoingNode);
                                }
                            if (prevNode != null)
                                connectNodes(prevNode, componentNode);
                            prevNode = componentNode;
                        }
                    connectNodes(prevNode, outNode);
                } else
                    outNode = prevNode;

                /*
                Add the edges to the group node to the inNode and outNode.
                Remove group node.
                 */
                if (!incomingGroupNodes.isEmpty())
                    for (KeggNode incomingGroupNode : incomingGroupNodes) {
                        redirectEdge(incomingGroupNode, groupNode, incomingGroupNode, inNode);
                    }
                if (!outgoingGroupNodes.isEmpty())
                    for (KeggNode outgoingGroupNode : outgoingGroupNodes) {
                        redirectEdge(groupNode, outgoingGroupNode, outNode, outgoingGroupNode);
                    }
                for (KeggNode outGoingNode : outgoingNodes) {
                    if (edgeExists(inNode, outGoingNode)) {
                        redirectEdge(inNode, outGoingNode, outNode, outGoingNode);
                    }
                }

                for (KeggNode inComingNode : incomingNodes) {
                    if (edgeExists(inComingNode, outNode)) {
                        redirectEdge(inComingNode, outNode, inComingNode, inNode);
                    }
                }

                processedGroupIds.add(groupNode.getId());
            }
        }
        removeGroupNodes(groupNodesList);
        return processedGroupIds;

    }

    private void connectNodes(KeggNode source, KeggNode target) {
        if (!edgeExists(source, target))
            if (!edgeExists(target, source)) {
                KeggRelation relation = new KeggRelation(source, target, KeggRelation.PPrel);
                addRelation(relation);
            }
    }


    private KeggRelation redirectEdge(KeggNode prevSource, KeggNode prevTarget,
                                      KeggNode newSource, KeggNode newTarget) {
        KeggRelation prevEdge = getRelation(prevSource, prevTarget);

        if (prevEdge != null) {
            removeRelation(prevEdge);
            KeggRelation newEdge = new KeggRelation(newSource,
                    newTarget, KeggRelation.PPrel);
            addRelation(newEdge);
            return newEdge;
        } else {
           ParsingReportGenerator.getInstance().appendLine("No interaction exists between " + prevSource.getId() + ": " +
                   prevTarget.getId());
            return null;
        }
    }

    private void removeGroupNodes(ArrayList<KeggNode> groupNodes) {
        for (KeggNode node : groupNodes) {
            removeNode(node);
        }
    }

    private void removeRelation(KeggRelation relation) {
        if (relation == null)
            return;
        if (relations.contains(relation))
            relations.remove(relation);
    }

    private void removeNode(KeggNode node) {
        if (node == null)
            return;
        if (nodes.containsValue(node)) {
            int key = 0;
            for (Map.Entry<Integer, KeggNode> entry : nodes.entrySet())
                if (entry.getValue().equals(node)) {
                    key = entry.getKey();
                }
            nodes.remove(key);
        }
    }

    public String processCompounds() {
        String result = "";
        List<KeggRelation> compoundRelations = new ArrayList<KeggRelation>(); //Relations to be removed
        List<KeggRelation> newRelations = new ArrayList<KeggRelation>(); //Relations to be added
        for (KeggRelation relation : relations) {
            if (relation.getSubtype1().equals(KeggRelation.COMPOUND) ||
                    relation.getSubtype2().equals(KeggRelation.COMPOUND)) {
                int id;
                KeggNode compound = null;
                try {
                    id = relation.getSubtype1().equals(KeggRelation.COMPOUND) ?
                            Integer.parseInt(relation.getRelationValue1()) :
                            Integer.parseInt(relation.getRelationValue2());
                    compound = getNode(id);
                } catch (NumberFormatException e) {
                    result += "\nThe compound value for relation " + relation.toString() + " could not be found";
                }


                if (compound != null) {
                    KeggRelation relation1 = new KeggRelation(relation.getEntry1(), compound, relation.getType());
                    KeggRelation relation2 = new KeggRelation(compound, relation.getEntry2(), relation.getType());
                    relation1.addComment(KeggRelation.COMPOUND_PROCESSED);
                    relation2.addComment(KeggRelation.COMPOUND_PROCESSED);
                    if (relation.getSubtype1().equals(KeggRelation.COMPOUND)) {
                        if (relation.getSubtype2() != null &&
                                !relation.getSubtype2().isEmpty() &&
                                !relation.getSubtype2().equals(KeggRelation.COMPOUND)) {
                            relation1.setSubtype(relation.getSubtype2());
                            relation2.setSubtype(relation.getSubtype2());
                            if (relation.getRelationValue2() != null) {
                                relation1.setRelationValue(relation.getRelationValue2());
                                relation2.setRelationValue(relation.getRelationValue2());
                            }
                        } else {
                            relation1.setSubtype(KeggRelation.STATE_CHANGE);
                            relation2.setSubtype(KeggRelation.STATE_CHANGE);
                            relation1.setRelationValue(EKeggRelationType.getRelationValueFromSubType(KeggRelation.STATE_CHANGE));
                            relation2.setRelationValue(EKeggRelationType.getRelationValueFromSubType(KeggRelation.STATE_CHANGE));
                        }
                    } else if (!relation.getSubtype2().equals(KeggRelation.COMPOUND)) {
                        if (relation.getSubtype1() != null && !relation.getSubtype1().isEmpty()) {
                            relation1.setSubtype(relation.getSubtype1());
                            relation2.setSubtype(relation.getSubtype1());
                            if (relation.getRelationValue1() != null) {
                                relation1.setRelationValue(relation.getRelationValue1());
                                relation2.setRelationValue(relation.getRelationValue1());
                            }
                        } else {
                            relation1.setSubtype(KeggRelation.STATE_CHANGE);
                            relation2.setSubtype(KeggRelation.STATE_CHANGE);
                            relation1.setRelationValue(EKeggRelationType.getRelationValueFromSubType(KeggRelation.STATE_CHANGE));
                            relation2.setRelationValue(EKeggRelationType.getRelationValueFromSubType(KeggRelation.STATE_CHANGE));
                        }
                    }

                    newRelations.add(relation1);
                    newRelations.add(relation2);
                    compoundRelations.add(relation);

                    result += "\n" + relation.toString() + " removed";
                    result += "\n" + relation1.toString() + " added";
                    result += "\n" + relation2.toString() + " added";
                }
            }
        }
        if (compoundRelations.size() > 0)
            for (KeggRelation relation : compoundRelations) {
                removeRelation(relation);
            }
        if (newRelations.size() > 0)
            for (KeggRelation relation : newRelations) {
                addRelation(relation);
            }
        return result;

    }

    public String correctEdgeDirections() {
        /*If relations are from higher Id to lower, leave it as it is.
        * Otherwise, check, if the second node lies lefter of higher than the first one, reverse the edge.
        * */
        String result = "";

        List<KeggRelation> reverseEdges = new ArrayList<KeggRelation>();
        for (KeggRelation relation : relations) {
            if (relation.getSubtype1().equals(KeggRelation.BINDING) ||
                    relation.getSubtype2().equals(KeggRelation.BINDING))
                if (relation.getEntry1().getId() < relation.getEntry2().getId()) {
                    if (isReverseDirection(relation)) {
                        reverseEdges.add(relation);
                    }
                }
        }

        for (KeggRelation relation : reverseEdges) {
            reverseEdge(relation);
            result += "\n" + relation.toString() + " reversed";
        }

        return result;

    }

    private boolean isReverseDirection(KeggRelation relation) {
        KeggNode node1 = relation.getEntry1();
        KeggNode node2 = relation.getEntry2();
        if (node1.getX() - node2.getX() != 0)
            return node1.getX() > node2.getX();
        else
            return node1.getY() > node2.getY();
    }

    private void reverseEdge(KeggRelation relation) {
        KeggRelation reversed = relation.clone();
        reversed.setEntry1(relation.getEntry2());
        reversed.setEntry2(relation.getEntry1());
        reversed.addComment(KeggRelation.DIRECTION_REVERSED);
        removeRelation(relation);
        addRelation(reversed);
    }

    @Override
    public String toString() {
        return "Graph{" +
                "name='" + (name != null ? name : "null") + '\'' +
                ", organism='" + (organism != null ? organism : "null") + '\'' +
                ", number='" + (number != null ? number : "null") + '\'' +
                ", title='" + (title != null ? title : "null") + '\'' +
                ", image='" + (image != null ? image : "image") + '\'' +
                ", link='" + (link != null ? link : "link") + '\'' +
                ", \nnodes=" + nodes +
                ", \nrelations=" + relations +
                '}';
    }

    /**
     * Returns the number of edges in the graph
     * @return int
     */
    public int getSize(){
        return relations.size();
    }

    /**
     * Returns the number of nodes in the graph
     * @return
     */
    public int getOrder(){
        return nodes.size();
    }
}
