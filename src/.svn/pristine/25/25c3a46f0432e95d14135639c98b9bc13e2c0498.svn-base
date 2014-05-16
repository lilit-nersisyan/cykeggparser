package org.cytoscape.keggparser.com;

import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.ArrowShape;
import org.fest.util.Strings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.junit.Assert.*;

public class KeggRelationTest {
    KeggRelation keggRelation;
    KeggNode keggNode1;
    KeggNode keggNode2;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        keggNode1 = new KeggNode(1, "node1", KeggNode.GENE);
        keggNode2 = new KeggNode(2, "node2", KeggNode.COMPOUND);
        keggRelation = new KeggRelation(keggNode1, keggNode2, KeggRelation.PPrel);
    }


    @Test
    public void testConstructor() {
        thrown.expect(IllegalArgumentException.class);
        new KeggRelation(null, keggNode2, KeggRelation.PPrel);
        new KeggRelation(keggNode1, null, KeggRelation.PPrel);
        new KeggRelation(keggNode1, keggNode2, null);
        thrown = ExpectedException.none();

        assertNotNull(keggRelation);
        assertNotNull(keggRelation.getEntry1());
        assertNotNull(keggRelation.getEntry2());
        assertTrue(EKeggRelationType.isTypeValid(keggRelation.getType()));
    }

    @Test
    public void testGetSubtype1() {
        assertNotNull(keggRelation.getSubtype1());
    }

    @Test
    public void testGetSubType2() {
        assertNotNull(keggRelation.getSubtype2());
    }

    @Test
    public void testSetSuptype() {
        String subtype1 = "subtype1";
        String subtype2 = "subtype2";
        checkSubType(subtype1);
        checkSubType(subtype2);
    }

    private void checkSubType(String subtype) {
        keggRelation.setSubtype(subtype);
        if (Strings.isEmpty(keggRelation.getSubtype2()))
            assertEquals(subtype, keggRelation.getSubtype1());
        else
            assertEquals(subtype, keggRelation.getSubtype2());
    }

    @Test
    public void testGetRelationValue1() {
        assertNotNull(keggRelation.getRelationValue1());
    }

    @Test
    public void testGetRelationValue2() {
        assertNotNull(keggRelation.getRelationValue2());
    }

    @Test
    public void testSetRelationValue() {
        String relationValue1 = "relationValue1";
        String relationValue2 = "relationValue2";
        checkRelationValue(relationValue1);
        checkRelationValue(relationValue2);
    }

    private void checkRelationValue(String relationValue) {
        keggRelation.setRelationValue(relationValue);
        if (Strings.isEmpty(keggRelation.getRelationValue2()))
            assertEquals(relationValue, keggRelation.getRelationValue1());
        else
            assertEquals(relationValue, keggRelation.getRelationValue2());
    }

    @Test
    public void testGetArrowShape() {
        ArrowShape arrowShape = keggRelation.getArrowShape();
        assertThat(arrowShape, anyOf(equalTo(ArrowShapeVisualProperty.DELTA),
                equalTo(ArrowShapeVisualProperty.T), equalTo(ArrowShapeVisualProperty.NONE)));
    }

    @Test
    public void testGetLineStyle(){
        assertNotNull(keggRelation.getLineStyle());
    }

    @Test
    public void testGetEdgeLabel(){
        assertNotNull(keggRelation.getEdgeLabel());
    }

    @Test
    public void testToString() {
        assertNotNull(keggRelation.toString());
    }

    @Test
    public void testClone(){
        assertEquals(keggRelation, keggRelation.clone());
    }




}
