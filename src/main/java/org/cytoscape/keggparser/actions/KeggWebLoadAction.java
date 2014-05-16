package org.cytoscape.keggparser.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.dialogs.KeggWebLoadFrame;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class KeggWebLoadAction extends AbstractCyAction {
    private KeggWebLoadFrame keggWebLoadFrame;
//    protected boolean isTaskRunning = false;
    protected static Logger keggLoadActionLogger = LoggerFactory.getLogger(KeggWebLoadAction.class);

    /**
     * Creates a new HelpContentsAction object.
     */
    public KeggWebLoadAction() {
        super("Load KGML from web");
        setMenuGravity(1);
        setPreferredMenu("Apps.KEGGParser.Load KGML");
    }


    public void actionPerformed(ActionEvent e) {
//        try {
//            keggWebLoadFrame = KeggWebLoadFrame.getInstance();
//        } catch (Exception e1) {
//            JOptionPane.showMessageDialog(KEGGParserPlugin.cytoscapeDesktopService.getJFrame(),
//                    e1.getMessage());
//        }
        if (keggWebLoadFrame != null)
            keggWebLoadFrame.setVisible(true);
        else {
            final KeggWebLoadTask task = new KeggWebLoadTask();
            KEGGParserPlugin.taskManager.execute(new TaskIterator(task));

//            while (keggWebLoadFrame == null) {
//                try {
//                    Thread.sleep(200);
//                    System.out.println(System.currentTimeMillis());
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                }
//            }

            if (keggWebLoadFrame != null) {
                keggWebLoadFrame.setVisible(true);
                keggWebLoadFrame.setState(JFrame.NORMAL);
            }
        }


    }

    private class KeggWebLoadTask extends AbstractTask {

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
//            isTaskRunning = true;
            taskMonitor.setProgress(-1);
            setName("KEGG web load task");
            taskMonitor.setStatusMessage("Loading KEGG pathway and organism lists");
            try {
                keggWebLoadFrame = KeggWebLoadFrame.getInstance();
//                isTaskRunning = false;
            } catch (Exception e) {
//                isTaskRunning = false;
                throw new Exception(e.getMessage());
            } finally {
                taskMonitor.setProgress(1);
                System.gc();
            }
        }

    }


}

