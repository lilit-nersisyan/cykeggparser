package org.cytoscape.keggparser.parsing;


import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.actions.KeggSaveAsBioPAXAction;
import org.cytoscape.keggparser.com.ParsingReportGenerator;
import org.cytoscape.work.TaskMonitor;

import java.io.*;

public class KGMLConverter {

    public static final int BioPAX2 = 0;
    public static final int BioPAX3 = 1;
    public static final String BioPAX_level2 = "BioPAX_level2";
    public static final String BioPAX_level3 = "BioPAX_level3";
    File keggTranslatorJarFile;
    private Thread executeCommandThread;
    private String command;
    private KeggSaveAsBioPAXAction.SaveBioPAXTask parentTask;

    public KGMLConverter() {
        try {
            keggTranslatorJarFile = KEGGParserPlugin.getKeggTranslatorJar();
        } catch (FileNotFoundException e) {
            keggTranslatorJarFile = null;
        }
    }

    public boolean translateFromCmd(File kgmlFile, String outFilePath, int bioPaxLevel, TaskMonitor taskMonitor,
                                    KeggSaveAsBioPAXAction.SaveBioPAXTask saveBioPAXTask) {
        parentTask = saveBioPAXTask;
        if (keggTranslatorJarFile == null || !keggTranslatorJarFile.exists()) {
            ParsingReportGenerator.getInstance().appendLine("Unable to translate the kgml, since " +
                    "KeggTranslator jar file could not be found");
        }

        String bioPaxLevelString = "";
        switch (bioPaxLevel) {
            case BioPAX2:
                bioPaxLevelString = BioPAX_level2;
                break;
            case BioPAX3:
                bioPaxLevelString = BioPAX_level3;
                break;
            default:
                bioPaxLevelString = BioPAX_level2;
        }

        command = String.format("java -jar %s --input %s --output %s --format %s",
                keggTranslatorJarFile.getAbsolutePath(),
                "\"" + kgmlFile.getAbsolutePath() + "\"",
                "\"" + outFilePath + "\"",
                bioPaxLevelString);
        ParsingReportGenerator.getInstance().appendLine("Calling KeggTranslator with the command: \n" + command);


        ExecuteCommandTask executeCommandTask = new ExecuteCommandTask(command);
        executeCommandThread = new Thread(executeCommandTask);
        boolean success = true;
        int maxTime = 15000;
        long initTime = System.currentTimeMillis();
        long maxExecutionTime = initTime + maxTime;
        executeCommandThread.start();
        while (!parentTask.isCancelled() && executeCommandThread.isAlive()) {
            if (System.currentTimeMillis() > maxExecutionTime) {
                String message = "The converter took more than "
                        + maxTime / 1000 + " s to execute. " +
                        "Try saving the network in another format and/or convert the KGML file manually ";
                ParsingReportGenerator.getInstance().appendLine(message);
                taskMonitor.setStatusMessage(message);
                executeCommandThread.interrupt();
                destroyProcess(executeCommandTask);
                success = false;
                break;
            }

            try {
                Thread.yield();
                Thread.sleep(2000);
            } catch (InterruptedException t) {
                t.printStackTrace();
            }
        }

        if (executeCommandThread.isAlive()) {
            executeCommandThread.interrupt();
            destroyProcess(executeCommandTask);
            success = false;
        }
//        File outFile = new File(outFilePath);
//        success = outFile.exists() && outFile.length() >0 ;

        return success;

    }

    private void destroyProcess(ExecuteCommandTask executeCommandTask) {
        executeCommandTask.destroyProcess();
    }

    public void stopTranslationTask() {
        if (executeCommandThread.isAlive())
            executeCommandThread.interrupt();
    }

    private class ExecuteCommandTask implements Runnable {
        private String command;
        private Process process = null;
        private InputStream inputStream;
        private InputStream errorStream;
        private BufferedReader reader;

        ExecuteCommandTask(String commmand) {
            this.command = commmand;
        }

        @Override
        public void run() {

            Runtime runtime = Runtime.getRuntime();
            try {
                process = runtime.exec(command);
            } catch (IOException e) {
                ParsingReportGenerator.getInstance().appendLine(e.getMessage());
            }

            try {
                inputStream = process.getInputStream();
                if (inputStream != null && inputStream.available() > 0) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    if (reader.ready())
                        while ((line = reader.readLine()) != null) {
                            ParsingReportGenerator.getInstance().appendLine(line);
                        }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                errorStream = process.getErrorStream();
                if (errorStream != null) {
                    String line = "";
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                    if (errorReader.ready())
                        while ((line = errorReader.readLine()) != null) {
                            ParsingReportGenerator.getInstance().appendLine(line);
                        }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void destroyProcess() {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (errorStream != null)
                    errorStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null)
                process.destroy();
        }


    }


}
