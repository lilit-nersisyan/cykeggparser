package org.cytoscape.keggparser.dialogs;


import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.KEGGParserPluginTest;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.finder.JFileChooserFinder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class KeggLoadFrameTest {
    KEGGParserPluginTest keggParserPluginTest;
    KEGGParserPlugin keggParserPlugin;
    BundleContext bundleContext;
    KeggLoadFrame keggLoadFrame;

    @Before
    public void setUp() {
        keggParserPluginTest = new KEGGParserPluginTest();
        keggParserPluginTest.setUp();
        bundleContext = KEGGParserPluginTest.bundleContext;
        keggParserPlugin = KEGGParserPluginTest.keggParserPlugin;
        keggLoadFrame = new KeggLoadFrame();
    }

    @Test
    public void testConstructor() {
        assertNotNull(keggLoadFrame);
    }

    @Test
    public void testgetRecentDirectory(){
        File file = keggLoadFrame.getRecentDirectory();
        assertNotNull(file);
        assertTrue(file.exists());
    }

    @Test
    public void testSetRecentDirectory(){
        if (keggLoadFrame.setRecentDirStoringFile()){
            assertNotNull(keggLoadFrame.getRecentDirStoringFile());
            assertTrue(keggLoadFrame.getRecentDirStoringFile().exists());
        }
    }

    @Ignore
    @Test
    public void testShowFrame() {
        FrameFixture fixture = new FrameFixture(keggLoadFrame);
        GuiActionRunner.executeInEDT(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                keggLoadFrame.showFrame();
            }
        }).start();
        JFileChooserFixture fileChooser = JFileChooserFinder.findFileChooser
                (KeggLoadFrame.fileChooserName).using(fixture.robot);
        File file = new File("d:\\Workspace\\unit_tests\\src\\test\\testdata\\test_kgml.xml");
        fileChooser.selectFile(file);
        fixture.robot.waitForIdle();
        fileChooser.approve();
        assertEquals(file,fileChooser.target.getSelectedFile());

    }
}
