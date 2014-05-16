package org.cytoscape.keggparser.actions;

import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.KEGGParserPluginTest;
import org.cytoscape.keggparser.dialogs.KeggLoadFrame;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.finder.JFileChooserFinder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import java.awt.event.ActionEvent;
import java.io.File;

import static junit.framework.Assert.assertNotNull;

public class KeggLoadActionTest {
    private BundleContext bundleContext;
    private KEGGParserPluginTest keggParserPluginTest;
    private KEGGParserPlugin keggParserPlugin;
    private KeggLoadAction keggLoadAction;

    @Before
    public void setUp() {
        keggParserPluginTest = new KEGGParserPluginTest();
        keggParserPluginTest.setUp();
        bundleContext = KEGGParserPluginTest.bundleContext;
        keggParserPlugin = KEGGParserPluginTest.keggParserPlugin;
        keggLoadAction = KEGGParserPlugin.keggLoadAction;
    }
    @Test
    public void testConstructor(){
        assertNotNull(keggLoadAction);
    }

    @Ignore
    @Test
    public void testActionPerformed(){
        FrameFixture fixture = new FrameFixture(keggLoadAction.getKeggLoadFrame());
        GuiActionRunner.executeInEDT(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                keggLoadAction.actionPerformed(new ActionEvent(bundleContext, ActionEvent.ACTION_PERFORMED, ""));
            }
        }).start();
        JFileChooserFixture fileChooser =JFileChooserFinder.findFileChooser
                (KeggLoadFrame.fileChooserName).using(fixture.robot);
        fileChooser.selectFile(new File("d:\\Workspace\\unit_tests\\src\\test\\testdata\\test_kgml.xml"));
        fixture.robot.waitForIdle();
        fileChooser.approve();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSomething() {
    }
}