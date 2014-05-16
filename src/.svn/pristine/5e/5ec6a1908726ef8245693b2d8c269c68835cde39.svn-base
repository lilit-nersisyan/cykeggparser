package org.cytoscape.keggparser.dialogs;


import org.cytoscape.keggparser.KEGGParserPlugin;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;


public class KeggLoadFrame extends JFrame {
    private JFileChooser fileChooser;
    private File selectedFile;
    private File recentDirStoringFile;
    public static final String fileChooserName = "KeggLoadFrameFileChooser";
    private int response;

    public KeggLoadFrame() {
        super("KGML load window");
        setLocation(400, 250);
        setSize(400, 200);
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setName(fileChooserName);
        }
    }

    public void showFrame() {
        fileChooser.setCurrentDirectory(getRecentDirectory());
        fileChooser.setFileFilter(getFileFilter());
        fileChooser.setDialogTitle("Load local kgml");
        response = fileChooser.showOpenDialog(this);

        if (fileChooser.getSelectedFile() != null) {
            selectedFile = fileChooser.getSelectedFile();
            if (response != JFileChooser.CANCEL_OPTION)
                writeSelectedFile(selectedFile);
        }
    }

    public File getRecentDirectory() {
        String recentDir = System.getProperty("user.home");
        String userHome = recentDir;

        if (setRecentDirStoringFile()) {
            try {
                java.util.Scanner scanner = new java.util.Scanner(recentDirStoringFile);
                while (scanner.hasNext()) {
                    recentDir = scanner.nextLine();
                }
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }
        File recentDirFile = new File(recentDir);
        if (recentDirFile.exists())
            return recentDirFile;
        return new File(userHome);
    }

    public File getRecentDirStoringFile() {
        return recentDirStoringFile;
    }

    public boolean setRecentDirStoringFile() {
        File cyDir = KEGGParserPlugin.cyAppConfig.getConfigurationDirectoryLocation();
        File keggDir;
        if (cyDir == null || !cyDir.exists()) {
            return false;
        } else
            keggDir = new File(cyDir, "app-data/CyKEGGParser/kgml");

        if (!keggDir.exists())
            if (!keggDir.mkdir())
                return false;

        if (keggDir.exists()) {
            if (recentDirStoringFile == null)
                recentDirStoringFile = new File(keggDir, "recentKeggDir.txt");
            if (!recentDirStoringFile.exists()) {
                try {
                    if (!recentDirStoringFile.createNewFile())
                        return false;
                } catch (IOException e) {
                    LoggerFactory.getLogger(KeggLoadFrame.class).error(e.getMessage());
                }
            }
        }
        return (recentDirStoringFile != null && recentDirStoringFile.exists());
    }

    private FileFilter getFileFilter() {
        return new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
            }

            @Override
            public String getDescription() {
                return "xml";
            }
        };
    }


    private void writeSelectedFile(File file) {
        try {
            if (setRecentDirStoringFile()) {
                PrintWriter recentDirWriter = new PrintWriter(recentDirStoringFile);
                recentDirWriter.write(file.getParent());
                recentDirWriter.close();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    public File getSelectedFile() {
        if (response == JFileChooser.CANCEL_OPTION)
            return null;
        return selectedFile;
    }
}
