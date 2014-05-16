package org.cytoscape.keggparser;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.service.util.internal.FakeBundleContext;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.swing.DialogTaskManager;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public final class KEGGParserPluginTest {

    public static org.cytoscape.keggparser.KEGGParserPlugin keggParserPlugin;
    //
    // FakeBundleContext is provided by the service-api test-jar and
    // will create mock services for each Class specified in the
    // constructor.  Other services can be added using the registerService
    // method, like you'd use a normal BundleContext!
    //
    public static BundleContext bundleContext = new FakeBundleContext(
            CySwingApplication.class,
            DialogTaskManager.class,
            CySessionManager.class,
            CyNetworkFactory.class,
            CyNetworkManager.class,
            CyNetworkViewFactory.class,
            CyNetworkViewManager.class,
            VisualMappingManager.class,
            VisualMappingFunctionFactory.class,
            VisualStyleFactory.class,
            CyTableFactory.class,
            CyApplicationConfiguration.class,
            CyEventHelper.class,
            CySwingApplication.class,
            CyApplicationManager.class,
            CyTableManager.class
    );

    @Before
    public void setUp() {
        keggParserPlugin = new org.cytoscape.keggparser.KEGGParserPlugin();
        keggParserPlugin.start(bundleContext);
    }

    @Test
    public void testConstructor() {
        assertNotNull(keggParserPlugin);
    }

    @Test(expected = RuntimeException.class)
    public void testStartNullBundleContext() {
        keggParserPlugin.start(null);
    }

    @Test
    public void testStart() {
        keggParserPlugin.start(bundleContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetReportFile() {
        checkReportFile(KEGGParserPlugin.PARSING);
        checkReportFile(KEGGParserPlugin.TUNING);
        checkReportFile(-1);
    }

    private void checkReportFile(int type) {
        File reportFile = KEGGParserPlugin.getReportFile(type);
        assertNotNull(reportFile);
        assertTrue(reportFile.exists());
    }

    @Test
    public void testGetKeggProps() {
        assertNotNull(KEGGParserPlugin.getKeggProps());
    }

    @Test
    public void testGetKEGGParserDir() {
        File keggParserDir = KEGGParserPlugin.getKEGGParserDir();
        assertNotNull(keggParserDir);
        assertTrue(keggParserDir.exists());
        assertTrue(keggParserDir.isDirectory());
    }

    @Test
    public void testGetKeggPropsFile() {
        assertNotNull(KEGGParserPlugin.getKeggPropsFile());
    }

    @Test
    public void testGetKeggTranslatorJar() {

        File jar = null;
        boolean exception = false;
        try {
            jar = KEGGParserPlugin.getKeggTranslatorJar();
        } catch (Exception e) {
            exception = true;
            assert (e instanceof FileNotFoundException);
        }
        if (!exception) {
            assertNotNull(jar);
            assertTrue(jar.exists());
            assertFalse(jar.length() == 0);
        }

    }
}
