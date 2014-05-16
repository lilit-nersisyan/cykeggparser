package org.cytoscape.keggparser.com;

import org.cytoscape.keggparser.KEGGParserPlugin;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class TuningReportGenerator {
    private static int reportType = KEGGParserPlugin.TUNING;
    private static PrintWriter writer;
    private static StringBuffer buffer;
    private static File outputFile;
    private static TuningReportGenerator reportGenerator = null;



    public static TuningReportGenerator getInstance() {
        if (reportGenerator == null)
            reportGenerator = new TuningReportGenerator();
        return reportGenerator;
    }

    private TuningReportGenerator() {
        if (outputFile == null)
            outputFile = KEGGParserPlugin.getReportFile(reportType);
        try {
            writer = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            LoggerFactory.getLogger(TuningReportGenerator.class).error(e.getMessage());
        }
    }

    public void setOutputFile(File outputFile) {
        outputFile = outputFile;
        try {
            writer = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void appendLine(String text){
        append("\n" + text);

    }
    public void append(String text) {
        if (writer == null) {
            if (outputFile == null) {
                LoggerFactory.getLogger(TuningReportGenerator.class).
                        warn("No report file is available for report generation.");
                return;
            } else
                try {
                    writer = new PrintWriter(outputFile);
                } catch (FileNotFoundException e) {
                    LoggerFactory.getLogger(TuningReportGenerator.class).error(e.getMessage());
                }
        }

        writer.append(text);
        writer.flush();

    }


    @Override
    public void finalize() {
        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
                writer = null;
                outputFile = null;
            }
        }
    }

}
