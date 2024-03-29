package org.cytoscape.keggparser.dialogs;

import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.com.EKeggWebProps;
import org.cytoscape.keggparser.com.ParsingReportGenerator;
import org.cytoscape.keggparser.parsing.KeggNetworkCreator;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class KeggWebLoadFrame extends JFrame {


    private JButton jb_close;
    private JButton jb_load;
    private JComboBox jcb_organism;
    private JComboBox jcb_pathway;
    private JLabel jl_organism;
    private JLabel jl_pathwayType;
    private JLabel jl_typeKeggID;
    private JTextField jtxt_organismID;
    private JTextField jtxt_pathwayID;
    private JLabel jl_info;
    private String kgmlFileName;
    private String selectPathway = "Select a pathway";
    private String selectOrganism = "Select an organism";
    private String orgID = "org. ID";
    private String pathID = "path. ID";
    private boolean isOrganismValid = false;
    private boolean isPathwayValid = false;
    private String currentOrganism;
    private String currentPathway;
    private HashMap<String, String> organismMap = new HashMap<String, String>();
    private HashMap<String, String> pathwayMap = new HashMap<String, String>();
    private boolean isFrameClosed = false;
    protected File kgmlFile = null;
    protected boolean isDownloadTaskRunning;

    private static KeggWebLoadFrame keggWebLoadFrame = null;

    public static KeggWebLoadFrame getInstance() throws Exception {
        if (keggWebLoadFrame == null)
            keggWebLoadFrame = new KeggWebLoadFrame();
        keggWebLoadFrame.setVisible(true);
        return keggWebLoadFrame;
    }

    private KeggWebLoadFrame() throws Exception {
        setTitle("Kegg web import");
        try {
            if (checkConnection()) {
                loadProps();
                loadLists();
                initComponents();
                setSelectedOrganism();
                setSelectedPathway();
                enableLoad();
            } else {
                keggWebLoadFrame = null;
                throw new Exception("Problems connecting to http://rest.kegg.jp\n" +
                        "Please, check your internet connection and try again");
            }
        } catch (Exception e) {
            throw new Exception("Problems initializing KeggWebLoadFrame " + e.getMessage());
        }

    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jtxt_organismID = new JTextField();
        jl_typeKeggID = new JLabel();
        jcb_pathway = new JComboBox();
        jb_load = new JButton();
        jb_close = new JButton();
        jtxt_pathwayID = new JTextField();
        jcb_organism = new JComboBox();
        jl_organism = new JLabel();
        jl_pathwayType = new JLabel();
        jl_info = new JLabel();

        jb_close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_closeActionPerformed();
            }
        });

        jb_load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_loadActionPerformed();
            }
        });

        jtxt_organismID.setText(orgID);
        jtxt_organismID.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jtxt_organismIDActionPerformed();
            }
        });

        jl_typeKeggID.setFont(new Font("Tahoma", 0, 12)); // NOI18N
        jl_typeKeggID.setText("Type in KEGG identifier or choose from list ");

        jcb_pathway.setModel(new DefaultComboBoxModel(getPathwayList()));

        jcb_pathway.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jcb_pathwayActionPerformed();
            }
        });

        jb_load.setText("Load");

        jb_close.setText("Close");

        jtxt_pathwayID.setText(pathID);
        jtxt_pathwayID.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jtxt_pathwayIDActionPerformed(evt);
            }
        });


        jcb_organism.setModel(new DefaultComboBoxModel(getOrganismList()));
        jcb_organism.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jcb_organismActionPerformed();
            }
        });

        jl_organism.setText("Organism");

        jl_pathwayType.setText("Pathway");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jl_organism)
                                                        .addComponent(jl_pathwayType))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jtxt_organismID, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jtxt_pathwayID, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jcb_organism, GroupLayout.PREFERRED_SIZE, 296, GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(1, 1, 1)
                                                                .addComponent(jcb_pathway, GroupLayout.PREFERRED_SIZE, 296, GroupLayout.PREFERRED_SIZE))))
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(15, 15, 15)
                                                .addComponent(jl_info, GroupLayout.PREFERRED_SIZE, 329, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jb_load)
                                                .addGap(15, 15, 15)
                                                .addComponent(jb_close))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jl_typeKeggID)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(jl_typeKeggID)
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jcb_organism, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jcb_pathway, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jtxt_organismID, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jl_organism))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jl_pathwayType)
                                                        .addComponent(jtxt_pathwayID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jb_load)
                                        .addComponent(jb_close)
                                        .addComponent(jl_info, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>

    private void jb_loadActionPerformed() {
        kgmlFile = null;

        final DownloadAndParseKgmlTask task = new DownloadAndParseKgmlTask();
        KEGGParserPlugin.taskManager.execute(new TaskIterator(task));
        /*isDownloadTaskRunning = true;
        while (isDownloadTaskRunning)
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        if (kgmlFile != null) {
            kgmlFileName = kgmlFile.getAbsolutePath();
            final ParseKgmlTask parseKgmlTask = new ParseKgmlTask(kgmlFileName);
            KEGGParserPlugin.taskManager.execute(new TaskIterator(parseKgmlTask));
        }*/
    }

    private void saveSelection() {
        KEGGParserPlugin.getKeggProps().setProperty(EKeggWebProps.WebImportDefaultOrganism.getName(),
                jcb_organism.getSelectedItem().toString());
        KEGGParserPlugin.getKeggProps().setProperty(EKeggWebProps.WebImportDefaultPathway.getName(),
                jcb_pathway.getSelectedItem().toString());
        try {
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(KEGGParserPlugin.getKeggPropsFile());
                KEGGParserPlugin.getKeggProps().store(output, "Cytoscape Property File");
                LoggerFactory.getLogger(KeggWebLoadFrame.class).info("wrote KEGG properties file to: " + KEGGParserPlugin.getKeggPropsFile().getAbsolutePath());
            } finally {
                if (output != null) {
                    output.close();
                }
            }
        } catch (Exception ex) {
            LoggerFactory.getLogger(KeggWebLoadFrame.class).error("Could not write cytoscape.props file!", ex);
        }
    }

    private void jb_closeActionPerformed() {
        isFrameClosed = true;
        this.setVisible(false);
    }

    private void loadLists() throws Exception {
        try {
            retrievePathwayList();
            retrieveOrganismlist();
        } catch (Exception e) {
            throw e;
        }
    }

    private void retrievePathwayList() throws Exception {
        String url = "http://rest.kegg.jp/list/pathway";
        String result = null;
        try {
            result = sendRestRequest(url).toString();
        } catch (Exception e) {
            throw e;
        }
        StringTokenizer tokenizer = new StringTokenizer(result, "\n");
        while (tokenizer.hasMoreTokens()) {
            StringTokenizer lineTokenizer = new StringTokenizer(tokenizer.nextToken(), "\t");
            if (lineTokenizer.hasMoreTokens())
                pathwayMap.put(lineTokenizer.nextToken(), lineTokenizer.nextToken());
        }
    }

    private void retrieveOrganismlist() throws Exception {
        String url = "http://rest.kegg.jp/list/organism";
        String result = null;
        try {
            result = sendRestRequest(url).toString();
        } catch (Exception e) {
            throw e;
        }
        organismMap.put("map", "generic map");
        StringTokenizer tokenizer = new StringTokenizer(result, "\n");
        while (tokenizer.hasMoreTokens()) {
            StringTokenizer lineTokenizer = new StringTokenizer(tokenizer.nextToken(), "\t");
            lineTokenizer.nextToken();
            if (lineTokenizer.hasMoreTokens())
                organismMap.put(lineTokenizer.nextToken(), lineTokenizer.nextToken());
        }

    }


    private void loadProps() {
//        if (KEGGParserPlugin.getKeggProps() == null)
//            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Unable to load properties.\n");
        if (KEGGParserPlugin.getKeggProps() != null)
            for (EKeggWebProps property : EKeggWebProps.values()) {
                property.setDefaultValue((String) KEGGParserPlugin.getKeggProps().get(property.getName()));
            }
    }


    public static boolean checkConnection() {
        String url = "http://rest.kegg.jp";
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");

            con.setConnectTimeout(3000); //set timeout to 5 seconds

            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (MalformedURLException e) {
            LoggerFactory.getLogger(KeggWebLoadFrame.class).info(e.getMessage());
            e.printStackTrace();
        } catch (ProtocolException e) {
            LoggerFactory.getLogger(KeggWebLoadFrame.class).info(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LoggerFactory.getLogger(KeggWebLoadFrame.class).info(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    private void jcb_pathwayActionPerformed() {
        String pathway = jcb_pathway.getSelectedItem().toString();
        if (pathway.equals(selectPathway))
            jtxt_pathwayID.setText(pathID);
        else
            for (Map.Entry<String, String> entry : pathwayMap.entrySet()) {
                if (entry.getValue().equals(pathway))
                    jtxt_pathwayID.setText(entry.getKey().replace("path:map", ""));
            }
        enableLoad();
    }

    private void setSelectedPathway() {
        String defaultPathway = EKeggWebProps.WebImportDefaultPathway.getDefaultValue();
        if (defaultPathway == null || defaultPathway.equals(""))
            defaultPathway = selectPathway;
        for (int i = 0; i < jcb_pathway.getModel().getSize(); i++) {
            if ((jcb_pathway.getModel().getElementAt(i)).equals(defaultPathway))
                jcb_pathway.setSelectedItem(jcb_pathway.getModel().getElementAt(i));
        }
        jcb_pathwayActionPerformed();
    }


    private void jcb_organismActionPerformed() {
        jl_info.setText("");
        String organism = jcb_organism.getSelectedItem().toString();
        if (organism.equals(selectOrganism))
            jtxt_organismID.setText(orgID);
        else
            for (Map.Entry<String, String> entry : organismMap.entrySet()) {
                if (entry.getValue().equals(organism))
                    jtxt_organismID.setText(entry.getKey());
            }
        enableLoad();
    }

    private void setSelectedOrganism() {
        String defaultOrganism = EKeggWebProps.WebImportDefaultOrganism.getDefaultValue();
        if (defaultOrganism == null || defaultOrganism.equals("")) {
            defaultOrganism = selectOrganism;
        }
        for (int i = 0; i < jcb_organism.getModel().getSize(); i++) {
            if ((jcb_organism.getModel().getElementAt(i)).equals(defaultOrganism))
                jcb_organism.setSelectedItem(jcb_organism.getModel().getElementAt(i));
        }
        jcb_organismActionPerformed();
    }


    private void jtxt_organismIDActionPerformed() {
        jl_info.setText("");
        boolean foundMatchingOrganism = false;
        String organismKey = jtxt_organismID.getText();
        String organismValue;
        for (int i = 0; i < jcb_organism.getModel().getSize(); i++) {
            organismValue = jcb_organism.getModel().getElementAt(i).toString();
            if (organismValue.equals(organismMap.get(organismKey))) {
                jcb_organism.setSelectedItem(jcb_organism.getModel().getElementAt(i));
                foundMatchingOrganism = true;
            }
        }
        if (!foundMatchingOrganism) {
            jl_info.setText("The identifiers are not valid");
            for (int i = 0; i < jcb_organism.getModel().getSize(); i++) {
                if (jcb_organism.getModel().getElementAt(i).equals(selectOrganism)) {
                    jcb_organism.setSelectedItem(jcb_organism.getModel().getElementAt(i));
                }
            }
        }
        enableLoad();
    }

    private void enableLoad() {
        currentPathway = jcb_pathway.getSelectedItem().toString();
        currentOrganism = jcb_organism.getSelectedItem().toString();
        if (currentPathway.equals(selectPathway))
            isPathwayValid = false;
        else
            isPathwayValid = true;
        if (currentOrganism.equals(selectOrganism))
            isOrganismValid = false;
        else
            isOrganismValid = true;
        if (isPathwayValid && isOrganismValid) {
            jb_load.setEnabled(true);
            jl_info.setForeground(Color.green);
            jl_info.setText(currentOrganism + " : " + currentPathway);
        } else {
            jb_load.setEnabled(false);
            jl_info.setForeground(Color.red);
            jl_info.setText("Select valid identifier(s)");
        }
    }

    private void jtxt_pathwayIDActionPerformed(ActionEvent evt) {
        jl_info.setText("");
        boolean isMatchingPathwayFound = false;
        String pathwayKey = jtxt_pathwayID.getText();
        String pathwayValue;
        for (int i = 0; i < jcb_pathway.getModel().getSize(); i++) {
            pathwayValue = (String) jcb_pathway.getModel().getElementAt(i);
            if (pathwayValue.equals(pathwayMap.get("path:map" + pathwayKey))) {
                jcb_pathway.setSelectedItem(jcb_pathway.getModel().getElementAt(i));
                isMatchingPathwayFound = true;
            }
        }
        if (!isMatchingPathwayFound) {
            jl_info.setText("The identifiers are not valid");
            for (int i = 0; i < jcb_pathway.getModel().getSize(); i++) {
                if (jcb_pathway.getModel().getElementAt(i).equals(selectPathway)) {
                    jcb_pathway.setSelectedItem(jcb_pathway.getModel().getElementAt(i));
                }
            }
        }
        enableLoad();
    }


    public static StringBuffer sendRestRequest(String url) throws Exception {

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            LoggerFactory.getLogger(KeggWebLoadFrame.class).debug("Sending url request " + url + "\nResponse Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine + "\n");
            }
            in.close();

            //print result
            return response;
        } catch (IOException e) {
            throw new Exception("Error while sending request: " + e.getMessage());
        }


    }

    public String getKgmlFileName() {
        return kgmlFileName;
    }

    private String[] getOrganismList() {
        String[] list = new String[organismMap.size() + 1];
        list[0] = selectOrganism;
        int index = 1;
        for (Map.Entry entry : organismMap.entrySet()) {
            list[index++] = (String) entry.getValue();
        }
        return list;
    }

    public String[] getPathwayList() {
        String[] list = new String[pathwayMap.size() + 1];
        list[0] = selectPathway;
        int index = 1;
        for (Map.Entry entry : pathwayMap.entrySet()) {
            list[index++] = (String) entry.getValue();
        }
        return list;
    }

    public boolean isPathwayChosen() {
        return kgmlFileName != null;

    }

    public boolean isFrameClosed() {
        return isFrameClosed;
    }

    class DownloadAndParseKgmlTask extends AbstractTask {

        @Override
        public void run(TaskMonitor taskMonitor) throws Exception {
            taskMonitor.setTitle("KGML web-load task");
            taskMonitor.setProgress(-1);
            try {
                downloadKgml(taskMonitor);
                if (kgmlFile != null)
                    kgmlFileName = kgmlFile.getAbsolutePath();
                CyNetworkView networkView = parseKgml(kgmlFileName, taskMonitor);
                if (networkView != null) {
                    KeggNetworkCreator.applyKeggVisualStyle(networkView);
                }
            } catch (Exception e) {
                throw new Exception("Error loading KGML file: " + e.getMessage());
            } finally {
                taskMonitor.setProgress(1);
                System.gc();
            }
        }

        private CyNetworkView parseKgml(String fileName, TaskMonitor taskMonitor) throws Exception {
            ParsingReportGenerator.getInstance().append("\n" + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()) +
                    "\nParsing " + fileName);
            taskMonitor.setStatusMessage("Parsing " + fileName);
            taskMonitor.setProgress(-1);

            try {
                File kgmlFile = new File(fileName);
                if (!kgmlFile.exists()) {
                    taskMonitor.setStatusMessage("The file " + kgmlFileName + " does not exist.\n Exiting.");
                    return null;
                } else {
                    KeggNetworkCreator networkCreator = new KeggNetworkCreator();
                    networkCreator.createNetwork(new File(fileName));
                    ParsingReportGenerator.getInstance().appendLine("KGML file " + fileName + " successfully parsed.");
                    taskMonitor.setStatusMessage("KGML file " + fileName + " successfully parsed.");
                    return networkCreator.getNetworkView();
                }
            } catch (Exception e) {
                ParsingReportGenerator.getInstance().appendLine("Error while parsing KGML: " + e.getMessage());
                throw new Exception("Error while parsing KGML " + e.getMessage());
            }

        }

        private void downloadKgml(TaskMonitor taskMonitor) throws Exception {
            String organism = jtxt_organismID.getText();
            String pathway = jtxt_pathwayID.getText();
            String kgmlName = organism + pathway;
            ParsingReportGenerator.getInstance().append("\n" + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()) +
                    "\nLoading " + kgmlName + ".xml from web.");
            String url = "http://rest.kegg.jp/get/" + kgmlName + "/kgml";
            StringBuffer buffer = null;
            try {
                taskMonitor.setStatusMessage("Sending request to " + url);
                buffer = sendRestRequest(url);
            } catch (Exception e) {
                throw new Exception("The chosen pathway does not exist or " +
                        "there are problems with the connection. " + e.getMessage());
            }
            try {
                if (buffer == null || buffer.length() == 0)
                    throw new Exception("The chosen pathway does not exist or " +
                            "there are problems with the connection. ");
                else {
                    File kgmlDir = new File(KEGGParserPlugin.getKEGGParserDir(), "kgml");
                    if (!kgmlDir.exists())
                        kgmlDir.mkdir();
                    kgmlFile = new File(kgmlDir, kgmlName + ".xml");
                    taskMonitor.setStatusMessage("Saving KGML to " + kgmlFile.getAbsolutePath());
                    PrintWriter writer = new PrintWriter(kgmlFile);
                    writer.write(String.valueOf(buffer));
                    writer.close();
                    ParsingReportGenerator.getInstance().appendLine("Successfully downloaded file " + kgmlFile.getName());
                    taskMonitor.setStatusMessage("Successfully downloaded file " + kgmlFile.getName());
                }

            } catch (Exception e) {
                throw new Exception("Error occurred while downloading the KGML file " + kgmlName + ".xml" +
                        ": " + e.getMessage());
            }
        }
    }
}
