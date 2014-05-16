package org.cytoscape.keggparser.actions;


import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.KEGGParserPluginTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import java.awt.event.ActionEvent;

import static org.junit.Assert.assertNotNull;


public class KeggHelpActionTest {
    KeggHelpAction keggHelpAction;
    private BundleContext bundleContext;
    KEGGParserPluginTest keggParserPluginTest;
    KEGGParserPlugin keggParserPlugin;


    @Before
    public void setUp() {
        bundleContext = KEGGParserPluginTest.bundleContext;
        keggParserPlugin = new KEGGParserPlugin();
        keggParserPlugin.start(bundleContext);
        keggHelpAction = new KeggHelpAction();
    }

    @Test
    public void testConstructor(){
        keggHelpAction = new KeggHelpAction();
        assertNotNull(keggHelpAction);
    }


    @Test
    public void testActionPerformed(){
        keggHelpAction.actionPerformed(new ActionEvent(bundleContext, ActionEvent.ACTION_PERFORMED, ""));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSomething() {
    }
}