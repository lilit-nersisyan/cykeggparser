package org.cytoscape.keggparser.tuning;

import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.KEGGParserPluginTest;
import org.cytoscape.model.CyNetwork;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import static org.junit.Assert.assertNotNull;

public class TunerTest {
    private BundleContext bundleContext;
    KEGGParserPluginTest keggParserPluginTest;
    KEGGParserPlugin keggParserPlugin;
    Tuner tuner;

    @Ignore
    @Before
    public void setUp(){
        bundleContext = KEGGParserPluginTest.bundleContext;
        keggParserPlugin = new KEGGParserPlugin();
        keggParserPlugin.start(bundleContext);
        CyNetwork network = KEGGParserPlugin.networkFactory.createNetwork();
        tuner = new Tuner(network);
    }

    @Ignore
    @Test
    public void testConstructor(){
        assertNotNull(tuner);
        assertNotNull(tuner.getNetwork());
    }

    @Ignore
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorForNullArgument(){
        new Tuner(null);
    }
}
