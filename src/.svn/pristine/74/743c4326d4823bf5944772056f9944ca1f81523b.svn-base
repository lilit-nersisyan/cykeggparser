package org.cytoscape.keggparser.com;

import org.fest.util.Strings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class KeggNodeTest {
    KeggNode keggNode;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        keggNode = new KeggNode(1, "node", KeggNode.GENE);
    }

    @Test
    public void testConstructor() {
        checkKeggNodeState(1, "node", KeggNode.GENE);
        checkKeggNodeState(1, null, KeggNode.GENE);
        thrown.expect(IllegalArgumentException.class);
        checkKeggNodeState(-1, "node", KeggNode.GENE);
        checkKeggNodeState(1, "node", null);
        checkKeggNodeState(1, "node", "arbitrary");
        checkKeggNodeState(0, "node", KeggNode.GENE);
        thrown = ExpectedException.none();
    }

    private void checkKeggNodeState(int id, String name, String type) {
        keggNode = new KeggNode(id, name, type);
        assertNotNull(keggNode);
        assertNotNull(keggNode.getId());
        assertEquals(id, keggNode.getId());
        assertNotNull(keggNode.getName());
        if (name != null)
            assertEquals(name, keggNode.getName());
        else
            assertEquals("", keggNode.getName());
        assertNotNull(keggNode.getType());
        if (EKeggNodeAttrs.isNodeTypeValid(type))
            assertEquals(type, keggNode.getType());
        else
            assertTrue(EKeggNodeAttrs.isNodeTypeValid(keggNode.getType()));
    }

    @Test
    public void testSetName() {
        keggNode.setName(null);
        assertNotNull(keggNode.getName());
        keggNode.setName("node");
        assertNotNull(keggNode.getName());
    }


    @Test
    public void testAddComponentId() {
        int prevSize = (keggNode.getComponentIds() == null ? 0 : keggNode.getComponentIds().size());
        keggNode.addComponentId(1);
        assertNotNull(keggNode.getComponentIds());
        assertEquals(prevSize + 1, keggNode.getComponentIds().size());
        thrown.expect(IllegalArgumentException.class);
        keggNode.addComponentId(0);
        keggNode.addComponentId(-1);
        thrown = ExpectedException.none();
    }

    @Test
    public void testSetId(){
        thrown.expect(IllegalArgumentException.class);
        keggNode.setId(0);
        keggNode.setId(-1);
        thrown = ExpectedException.none();
        keggNode.setId(1);
        assertEquals(1, keggNode.getId());
    }

    @Test
    public void testSetType(){
        thrown.expect(IllegalArgumentException.class);
        keggNode.setType(null);
        keggNode.setType("");
        thrown = ExpectedException.none();

        checkKeggNodeType(KeggNode.GENE);
        checkKeggNodeType(KeggNode.MAP);
        checkKeggNodeType(KeggNode.COMPOUND);
        checkKeggNodeType(KeggNode.ENZYME);
        checkKeggNodeType(KeggNode.ORTHOLOG);
        checkKeggNodeType(KeggNode.GROUP);
    }

    private void checkKeggNodeType(String type){
        keggNode.setType(type);
        assertNotNull(keggNode.getType());
        assertTrue(EKeggNodeAttrs.isNodeTypeValid(keggNode.getType()));
        assertEquals(type, keggNode.getName());
    }

    @Test
    public void testSetLink(){
        keggNode.setLink(null);
        assertNotNull(keggNode.getLink());
        keggNode.setLink("link");
        assertNotNull(keggNode.getLink());
    }

    @Test
    public void testSetGraphicsName(){
        keggNode.setGraphicsName(null);
        assertNotNull(keggNode.getGraphicsName());
        assertNotNull(keggNode.getCellName());

        String graphicsName = "graphicsName";
        keggNode.setGraphicsName(graphicsName);
        assertEquals(graphicsName, keggNode.getGraphicsName());
        assertEquals(graphicsName, keggNode.getCellName());

        graphicsName = "graphics, name";
        String subString = "graphics";
        keggNode.setGraphicsName(graphicsName);
        assertEquals(graphicsName, keggNode.getGraphicsName());
        assertEquals(subString, keggNode.getCellName());

    }

    @Test
    public void testGetEntrezIDsFromName(){
        keggNode.setName("");
        checkKeggNodeEntrezIds(keggNode.getEntrezIDsFromName());
        keggNode.setName(null);
        checkKeggNodeEntrezIds(keggNode.getEntrezIDsFromName());
        keggNode.setName("1, 2, 3");
        checkKeggNodeEntrezIds(keggNode.getEntrezIDsFromName());
        keggNode.setName("name");
        checkKeggNodeEntrezIds(keggNode.getEntrezIDsFromName());

    }

    private void checkKeggNodeEntrezIds(String entrezIds) {
        assertNotNull(entrezIds);
        if (Strings.isEmpty(entrezIds))
            return;
        String[] ids = entrezIds.split(", ");
        int id;
        for (int i = 0; i < ids.length; i++)
            try{
                id = Integer.parseInt(ids[i]);
                assert id > 0;
            } catch (NumberFormatException e) {
                fail();
            }
    }

    @Test
    public void testToString(){
        assertNotNull(keggNode.toString());
    }

}
