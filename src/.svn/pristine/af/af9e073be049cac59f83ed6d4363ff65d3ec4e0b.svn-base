package org.cytoscape.keggparser.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.com.ParsingReportGenerator;
import org.cytoscape.keggparser.dialogs.KeggSaveDialog;
import org.cytoscape.keggparser.parsing.KGMLConverter;
import org.cytoscape.keggparser.parsing.KGMLCreator;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.io.*;


public class KeggSaveAsBioPAXAction_twoTask extends AbstractCyAction {
    private int bioPaxLevel;
    private String bioPaxLevelString;
    private KeggSaveDialog saveDialog;
    private File outFile;
    private String menuName;
    File keggTranslatorJarFile;
    private String suffix = "";
    public static final String BioPAX_level2 = "BioPAX_level2";
    public static final String BioPAX_level3 = "BioPAX_level3";
    private File kgmlFile;


    public KeggSaveAsBioPAXAction_twoTask(int bioPaxLevel, String menuName) {
        super(menuName == null ? "" : menuName);
        setPreferredMenu("Apps.KEGGParser.Save network");

        if (menuName == null)
            menuName = "";
        this.menuName = menuName;
        this.bioPaxLevel = bioPaxLevel;

        if (bioPaxLevel == KGMLConverter.BioPAX2) {
            suffix = ".bp2";
            bioPaxLevelString = BioPAX_level2;
        } else if (bioPaxLevel == KGMLConverter.BioPAX3) {
            suffix = ".bp3";
            bioPaxLevelString = BioPAX_level3;
        }

        try {
            keggTranslatorJarFile = KEGGParserPlugin.getKeggTranslatorJar();
        } catch (FileNotFoundException e) {
            keggTranslatorJarFile = null;
        }
    }


    public void actionPerformed(ActionEvent e) {
        outFile = getSelectedFileFromSaveDialog();
        if (outFile != null) {
            writeOutFileDirectory();

            // Create Task
            final CreateKGMLTask createKGMLTask = new CreateKGMLTask(outFile);
            final SaveBioPAXTask task = new SaveBioPAXTask(outFile);
            KEGGParserPlugin.taskManager.execute(new TaskIterator(2,createKGMLTask,task));

        }
    }

    public class CreateKGMLTask extends AbstractTask {
        private File outFile;
        private TaskMonitor taskMonitor;

        public CreateKGMLTask(File outFile) {
            this.outFile = outFile;
            cancelled = false;
        }

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            this.taskMonitor = taskMonitor;
            taskMonitor.setTitle("Kegg saving task");
            taskMonitor.setStatusMessage("Saving KGML file.\n\nIt may take a while.\nPlease wait...");
            ParsingReportGenerator.getInstance().appendLine("Saving the network as BioPAX_level2 to " +
                    outFile.getAbsolutePath());
            taskMonitor.setProgress(-1);
            if (outFile.exists())
                outFile.delete();

            try {
                if (keggTranslatorJarFile == null || !keggTranslatorJarFile.exists()) {
                    String message = "Unable to translate the kgml, since " +
                            "KeggTranslator jar file could not be found";
                    ParsingReportGenerator.getInstance().appendLine(message);
                    throw new Exception(message);
                }

                kgmlFile = createKGMLFile(outFile);

                if (kgmlFile != null && kgmlFile.length() != 0) {
                    taskMonitor.setStatusMessage("KGML file " + kgmlFile.getAbsolutePath() + " successfully generated.");
                    ParsingReportGenerator.getInstance().appendLine("KGML file saved to " + kgmlFile.getAbsolutePath());
                } else {
                    throw new Exception("KGML file could not be generated ");
                }
            } catch (Exception e) {
                ParsingReportGenerator.getInstance().appendLine(e.getMessage());
                throw e;
            } finally {
                System.gc();
            }
        }

        private File createKGMLFile(File outFile) throws Exception {
            File kgmlFile = getKGMLFile(outFile);
            KGMLCreator kgmlCreator = new KGMLCreator();
            kgmlCreator.setFilterForConversion(bioPaxLevel);
            kgmlCreator.createKGML(KEGGParserPlugin.cyApplicationManager.getCurrentNetwork(), kgmlFile);
            return kgmlFile;
        }

    }


    public class SaveBioPAXTask extends AbstractTask {
        private File outFile;
        private TaskMonitor taskMonitor;
        private String command;
        Process process;
        int maxTime = 15000;
        private Thread translationThread;
        private Runtime runtime;
        private InputStream inputStream;
        private InputStream errorStream;

        public SaveBioPAXTask(File outFile) {
            this.outFile = outFile;
            cancelled = false;
        }

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            this.taskMonitor = taskMonitor;
            taskMonitor.setTitle("Kegg saving task");

            if (outFile.exists())
                outFile.delete();

            try {
                if (keggTranslatorJarFile == null || !keggTranslatorJarFile.exists()) {
                    String message = "Unable to translate the kgml, since " +
                            "KeggTranslator jar file could not be found";
                    ParsingReportGenerator.getInstance().appendLine(message);
                    throw new Exception(message);
                }

                taskMonitor.setStatusMessage("Converting KGML file " + kgmlFile.getAbsolutePath()
                        + " to " + suffix + " file");

                command = String.format("java -jar %s --input %s --output %s --format %s --gene-names %s",
                        keggTranslatorJarFile.getAbsolutePath(),
                        "\"" + kgmlFile.getAbsolutePath() + "\"",
                        "\"" + outFile.getAbsolutePath() + "\"",
                        bioPaxLevelString,
                        "FIRST_NAME_FROM_KGML");

                ParsingReportGenerator.getInstance().appendLine("Calling KeggTranslator with the command: \n" + command);

                translationThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        translateFromCmd();
                    }
                });
                long maxExecutionTime = System.currentTimeMillis() + maxTime;
                translationThread.start();
                while (!cancelled && System.currentTimeMillis() < maxExecutionTime){
                    Thread.sleep(1000);
                }
                if (translationThread.isAlive())
                    translationThread.interrupt();
                destroy();


                if (outFile.exists() && outFile.length() != 0) {
                    taskMonitor.setStatusMessage("BioPAX file " + outFile.getAbsolutePath() + " successfully generated.");
                    taskMonitor.setProgress(1);
//                    destroy();
                } else {
//                    destroy();
                    Thread.sleep(1000);
                    ParsingReportGenerator.getInstance().appendLine("Problem saving network as " + suffix);
                    throw new Exception("Problem saving network as " + suffix);
                }
            } catch (Exception e) {
                destroy();
                ParsingReportGenerator.getInstance().appendLine(e.getMessage());
                throw e;
            } finally {
                taskMonitor.setProgress(1);
                System.gc();
            }
        }

        private void destroy() {
            if (process != null) {
                process.destroy();
            }
            if (translationThread.isAlive())
                translationThread.interrupt();
        }

        public void translateFromCmd() {
            try {
                runtime = Runtime.getRuntime();
                process = runtime.exec(command);
                inputStream = process.getInputStream();
                errorStream = process.getErrorStream();
                String line;
                if (inputStream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    while ((line = reader.readLine()) != null) {
                        ParsingReportGenerator.getInstance().appendLine(line);
                    }
                }
                if (errorStream != null) {
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                    while ((line = errorReader.readLine()) != null) {
                        ParsingReportGenerator.getInstance().appendLine(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean isCancelled() {
            return cancelled;
        }
    }

    private File getKGMLFile(File outFile) {
        String kgmlFileName;
        if (outFile.getName().contains(suffix))
            kgmlFileName = outFile.getName().replace(suffix, ".xml");
        else
            kgmlFileName = outFile.getName() + ".xml";
        File kgmlDir = new File(KEGGParserPlugin.getKEGGParserDir(), "/kgml");
        if (!kgmlDir.exists())
            if (!kgmlDir.mkdir()) {
                LoggerFactory.getLogger(KeggSaveAsBioPAXAction_twoTask.class).
                        error("Error creating directory " + kgmlDir.getAbsolutePath());
                kgmlDir = KEGGParserPlugin.getKEGGParserDir();
            }
        return new File(kgmlDir, kgmlFileName);
    }

    private void writeOutFileDirectory() {
        if (outFile != null) {
            try {
                PrintWriter recentDirWriter = new PrintWriter(saveDialog.getRecentDir());
                recentDirWriter.write(outFile.getParent());
                recentDirWriter.close();
            } catch (FileNotFoundException e1) {
                LoggerFactory.getLogger(KeggSaveAsBioPAXAction_twoTask.class).error(e1.getMessage());
            }
        }
    }

    private File getSelectedFileFromSaveDialog() {
        if (bioPaxLevel == KGMLConverter.BioPAX2)
            saveDialog = new KeggSaveDialog(".bp2");
        else if (bioPaxLevel == KGMLConverter.BioPAX3)
            saveDialog = new KeggSaveDialog(".bp3");
        else {
            try {
                throw new Exception("Ivalid bioPax level has been specified.");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return null;
        }
        int response = saveDialog.showSaveDialog(KEGGParserPlugin.cytoscapeDesktopService.getJFrame());
        if (response == KeggSaveDialog.CANCEL_OPTION)
            return null;
        return saveDialog.getSelectedFile();
    }


}

