package org.cytoscape.keggparser.dialogs;


import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.tuning.Tuner;
import org.cytoscape.keggparser.tuning.tse.EKEGGTuningProps;
import org.cytoscape.keggparser.tuning.tse.GeneExpXmlCreator;
import org.cytoscape.keggparser.tuning.tse.TSEDataSet;
import org.cytoscape.keggparser.tuning.tse.Tissue;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class KEGGTuningDialog extends JFrame {
    private ArrayList<String> databases;
    private ArrayList<String> tissueTypes;
    private String[] bioGPSTissues;
    private Properties keggProps;
    private static Logger logger = LoggerFactory.getLogger(KEGGTuningDialog.class);

    private CyNetwork tunableNetwork;
    private String expFileName;
    private String[] customTissuesList;
    private File xmlFile;
    private String noTissuesAvailable = "No tissues available";
    private TSEDataSet tseDataSet;
    private Tuner tuner;
    private String tunableNetworkTitle;


    public KEGGTuningDialog() {
        setTitle("Pathway tuning settings");
        setTunableNetwork();
        keggProps = KEGGParserPlugin.getKeggProps();
        initLists();
        initComponents();
        this.setVisible(true);
    }

    private void setTunableNetwork() {
        try {
            if (jcb_network != null)
            if (jcb_network.getSelectedItem() != null) {
                String selectedNetwork = jcb_network.getSelectedItem().toString();
                for (CyNetwork network : KEGGParserPlugin.networkManager.getNetworkSet()) {
                    String title = network.getRow(network).get(CyNetwork.NAME, String.class);
                    if (selectedNetwork.equals(title)) {
                        tunableNetwork = network;
                        tunableNetworkTitle = title;
                        break;
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (tunableNetwork == null) {
            tunableNetwork = KEGGParserPlugin.cyApplicationManager.getCurrentNetwork();
            if (tunableNetwork != null)
                tunableNetworkTitle = tunableNetwork.getRow(tunableNetwork).
                        get(CyNetwork.NAME, String.class);
        }

    }

    private void initLists() {
        databases = new ArrayList<String>();
        databases.add("BioGPS");
        databases.add("GeneNote");

        tissueTypes = new ArrayList<String>();
        tissueTypes.add("Normal");
        tissueTypes.add("Cancer");

        bioGPSTissues = new String[Tissue.values().length];
        int i = 0;
        for (Tissue tissue : Tissue.values())
            bioGPSTissues[i++] = tissue.getTissue();
    }

    private void initComponents() {
        jl_title = new JLabel();
        jbg_tuneBy = new ButtonGroup();
        jbg_tuningMode = new ButtonGroup();

        jbg_databaseGroup = new ButtonGroup();
        tp_tuningSettings = new JTabbedPane();
        jp_expSettings = new JPanel();
        jp_dataSource = new JPanel();
        jl_dataSource = new JLabel();
        jrb_BioGPS = new JRadioButton();
        jb_chooseFile = new JButton();
        jrb_userSuppliedData = new JRadioButton();
        jl_selectedFile = new JLabel();
        jp_tissue = new JPanel();
        jl_selectTissue = new JLabel();
        jcb_selectTissue = new JComboBox();
        jb_loadDataset = new JButton();
        jp_threshold = new JPanel();
        jsl_threshold = new JSlider();
        jl_threshold = new JLabel();
        jtxt_threshold = new JTextField();
        jl_absValue = new JLabel();
        jl_percentile = new JLabel();
        jtxt_absValue = new JTextField();
        jp_ppiSettings = new JPanel();
        jp_source = new JPanel();
        jl_source = new JLabel();
        jchb_source_grid = new JCheckBox();
        jchb_source_mint = new JCheckBox();
        jchb_source_kegg = new JCheckBox();
        jchb_source_pdb = new JCheckBox();
        jchb_source_dip = new JCheckBox();
        jp_ppiThreshold = new JPanel();
        jsl_ppiThreshold = new JSlider();
        jl_ppiThreshold = new JLabel();
        jtxt_ppiThreshold = new JTextField();
        jp_general = new JPanel();
        jl_tune = new JLabel();
        jb_tune = new JButton();
        jb_cancel = new JButton();
        jrb_generateNetwork = new JRadioButton();
        jrb_changeNetwork = new JRadioButton();
        jb_saveSettings = new JButton();
        jl_tuningMode = new JLabel();
        jrb_geneExp = new JRadioButton();
        jrb_ppi = new JRadioButton();
        jl_selectNetwork = new JLabel();
        jcb_network = new JComboBox();
        jp_attrs = new JPanel();
        jl_selectGeneIdAttr = new JLabel();
        jcb_geneIdAttr = new JComboBox();
        jl_selectTypeAttr = new JLabel();
        jcb_typeAttr = new JComboBox();
        jsp_type = new JScrollPane();
        jlist_type = new JList();


        jl_dataSource.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        jl_dataSource.setText("Data source");

        jrb_BioGPS.setText("BioGPS (only for Human (hsa) genes) ");
        jrb_BioGPS.setFont(new Font("Tahoma", Font.PLAIN, 8));
        jrb_BioGPS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jrb_BioGPSActionPerformed();
            }
        });

        jb_chooseFile.setText("Choose file");

        jrb_userSuppliedData.setText("User supplied data");

        jl_selectedFile.setText("No file selected");

        jl_selectTissue.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        jl_selectTissue.setText("Select the tissue");

        jcb_selectTissue.setModel(jcb_tissueModel());
        jcb_selectTissue.setAutoscrolls(true);
        jcb_selectTissue.setFocusable(true);
        jb_loadDataset.setFont(new Font("Tahoma", 0, 12)); // NOI18N
        jb_loadDataset.setText("Load dataset");

        GroupLayout jp_tissueLayout = new GroupLayout(jp_tissue);
        jp_tissue.setLayout(jp_tissueLayout);
        jp_tissueLayout.setHorizontalGroup(
                jp_tissueLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, jp_tissueLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_tissueLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(jb_loadDataset, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.LEADING, jp_tissueLayout.createSequentialGroup()
                                                .addComponent(jl_selectTissue, GroupLayout.PREFERRED_SIZE, 143, GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(jcb_selectTissue, GroupLayout.Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(6, 6, 6))
        );
        jp_tissueLayout.setVerticalGroup(
                jp_tissueLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_tissueLayout.createSequentialGroup()
                                .addComponent(jl_selectTissue)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcb_selectTissue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                                .addComponent(jb_loadDataset, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        GroupLayout jp_dataSourceLayout = new GroupLayout(jp_dataSource);
        jp_dataSource.setLayout(jp_dataSourceLayout);
        jp_dataSourceLayout.setHorizontalGroup(
                jp_dataSourceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_dataSourceLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_dataSourceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_dataSourceLayout.createSequentialGroup()
                                                .addComponent(jrb_userSuppliedData)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jb_chooseFile)
                                                .addGap(18, 18, 18)
                                                .addComponent(jl_selectedFile))
                                        .addComponent(jl_dataSource, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jrb_BioGPS))
                                .addContainerGap(21, Short.MAX_VALUE))
                        .addComponent(jp_tissue, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jp_dataSourceLayout.setVerticalGroup(
                jp_dataSourceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_dataSourceLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_dataSourceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_dataSourceLayout.createSequentialGroup()
                                                .addGap(21, 21, 21)
                                                .addComponent(jrb_BioGPS))
                                        .addComponent(jl_dataSource))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                                .addGroup(jp_dataSourceLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jb_chooseFile)
                                        .addComponent(jrb_userSuppliedData)
                                        .addComponent(jl_selectedFile))
                                .addGap(18, 18, 18)
                                .addComponent(jp_tissue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        jl_selectedFile.getAccessibleContext().setAccessibleName("jl_selectedFile");

        jsl_threshold.setMajorTickSpacing(20);
        jsl_threshold.setMinorTickSpacing(5);
        jsl_threshold.setPaintLabels(true);
        jsl_threshold.setPaintTicks(true);

        jl_threshold.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        jl_threshold.setText("Set expression threshold");

        jl_absValue.setBackground(new Color(255, 255, 255));
        jl_absValue.setText("abs value\n");

        jl_percentile.setText("percentile");

        GroupLayout jp_thresholdLayout = new GroupLayout(jp_threshold);
        jp_threshold.setLayout(jp_thresholdLayout);
        jp_thresholdLayout.setHorizontalGroup(
                jp_thresholdLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_thresholdLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_thresholdLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jsl_threshold, GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jl_threshold))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jp_thresholdLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_percentile)
                                        .addComponent(jtxt_threshold, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_thresholdLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_thresholdLayout.createSequentialGroup()
                                                .addComponent(jl_absValue)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(jp_thresholdLayout.createSequentialGroup()
                                                .addComponent(jtxt_absValue)
                                                .addContainerGap())))
        );
        jp_thresholdLayout.setVerticalGroup(
                jp_thresholdLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_thresholdLayout.createSequentialGroup()
                                .addContainerGap(12, Short.MAX_VALUE)
                                .addGroup(jp_thresholdLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(GroupLayout.Alignment.TRAILING, jp_thresholdLayout.createSequentialGroup()
                                                .addComponent(jl_threshold)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jsl_threshold, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(GroupLayout.Alignment.TRAILING, jp_thresholdLayout.createSequentialGroup()
                                                .addGroup(jp_thresholdLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jl_absValue)
                                                        .addComponent(jl_percentile))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jp_thresholdLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jtxt_threshold, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jtxt_absValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                .addGap(34, 34, 34))))
        );

        GroupLayout jp_expSettingsLayout = new GroupLayout(jp_expSettings);
        jp_expSettings.setLayout(jp_expSettingsLayout);
        jp_expSettingsLayout.setHorizontalGroup(
                jp_expSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_expSettingsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_expSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jp_threshold, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jp_dataSource, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jp_expSettingsLayout.setVerticalGroup(
                jp_expSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_expSettingsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jp_dataSource, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jp_threshold, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        tp_tuningSettings.addTab("Gene Expression Settings", jp_expSettings);

        jl_source.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        jl_source.setText("Source");

        jchb_source_grid.setText("GRID");

        jchb_source_mint.setText("MINT");

        jchb_source_kegg.setText("KEGG");

        jchb_source_pdb.setText("PDB");

        jchb_source_dip.setText("DIP");

        GroupLayout jp_sourceLayout = new GroupLayout(jp_source);
        jp_source.setLayout(jp_sourceLayout);
        jp_sourceLayout.setHorizontalGroup(
                jp_sourceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_sourceLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_sourceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_source)
                                        .addGroup(jp_sourceLayout.createSequentialGroup()
                                                .addGroup(jp_sourceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jchb_source_mint)
                                                        .addComponent(jchb_source_grid))
                                                .addGap(35, 35, 35)
                                                .addGroup(jp_sourceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jchb_source_pdb)
                                                        .addComponent(jchb_source_dip))
                                                .addGap(39, 39, 39)
                                                .addComponent(jchb_source_kegg)))
                                .addContainerGap(85, Short.MAX_VALUE))
        );
        jp_sourceLayout.setVerticalGroup(
                jp_sourceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_sourceLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_sourceLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(jp_sourceLayout.createSequentialGroup()
                                                .addGroup(jp_sourceLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jchb_source_dip)
                                                        .addComponent(jchb_source_kegg))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jchb_source_pdb))
                                        .addGroup(jp_sourceLayout.createSequentialGroup()
                                                .addComponent(jl_source)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jchb_source_grid)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jchb_source_mint)))
                                .addContainerGap(30, Short.MAX_VALUE))
        );

        jsl_ppiThreshold.setMajorTickSpacing(100);
        jsl_ppiThreshold.setMaximum(1000);
        jsl_ppiThreshold.setMinorTickSpacing(1);
        jsl_ppiThreshold.setPaintLabels(true);
        jsl_ppiThreshold.setPaintTicks(true);
        jsl_ppiThreshold.setSnapToTicks(true);

        jl_ppiThreshold.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        jl_ppiThreshold.setText("Set ppi threshold");

        GroupLayout jp_ppiThresholdLayout = new GroupLayout(jp_ppiThreshold);
        jp_ppiThreshold.setLayout(jp_ppiThresholdLayout);
        jp_ppiThresholdLayout.setHorizontalGroup(
                jp_ppiThresholdLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_ppiThresholdLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_ppiThresholdLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_ppiThresholdLayout.createSequentialGroup()
                                                .addComponent(jsl_ppiThreshold, GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                                                .addGap(24, 24, 24))
                                        .addGroup(jp_ppiThresholdLayout.createSequentialGroup()
                                                .addComponent(jl_ppiThreshold)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jtxt_ppiThreshold, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
                                                .addGap(40, 40, 40))))
        );
        jp_ppiThresholdLayout.setVerticalGroup(
                jp_ppiThresholdLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_ppiThresholdLayout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jp_ppiThresholdLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jl_ppiThreshold)
                                        .addComponent(jtxt_ppiThreshold, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jsl_ppiThreshold, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
        );

        GroupLayout jp_ppiSettingsLayout = new GroupLayout(jp_ppiSettings);
        jp_ppiSettings.setLayout(jp_ppiSettingsLayout);
        jp_ppiSettingsLayout.setHorizontalGroup(
                jp_ppiSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_ppiSettingsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_ppiSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jp_source, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jp_ppiThreshold, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(22, Short.MAX_VALUE))
        );
        jp_ppiSettingsLayout.setVerticalGroup(
                jp_ppiSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_ppiSettingsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jp_source, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jp_ppiThreshold, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(73, Short.MAX_VALUE))
        );

        tp_tuningSettings.addTab("Protein-protein interaction Settings", jp_ppiSettings);

        jl_tune.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        jl_tune.setText("Tune the network based on");

        jb_tune.setText("Tune");

        jb_cancel.setText("Cancel");

        jrb_generateNetwork.setText("Generate new network");


        jrb_changeNetwork.setText("Change current network");

        jb_saveSettings.setText("Save settings");

        jl_tuningMode.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        jl_tuningMode.setText("Tuning mode");

        jrb_geneExp.setText("Gene expression");
        jrb_geneExp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jrb_geneExpActionPerformed();
            }
        });

        jrb_ppi.setText("Protein-protein interaction");

        GroupLayout jp_generalLayout = new GroupLayout(jp_general);
        jp_general.setLayout(jp_generalLayout);
        jp_generalLayout.setHorizontalGroup(
                jp_generalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_generalLayout.createSequentialGroup()
                                .addGroup(jp_generalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jp_generalLayout.createSequentialGroup()
                                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jb_tune, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jb_saveSettings, GroupLayout.PREFERRED_SIZE, 188, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jb_cancel))
                                        .addGroup(jp_generalLayout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addGroup(jp_generalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jl_tune)
                                                        .addComponent(jrb_geneExp, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jrb_ppi))
                                                .addGap(133, 133, 133)
                                                .addGroup(jp_generalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jrb_generateNetwork)
                                                        .addComponent(jrb_changeNetwork)
                                                        .addComponent(jl_tuningMode))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jp_generalLayout.setVerticalGroup(
                jp_generalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_generalLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_generalLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jl_tune)
                                        .addComponent(jl_tuningMode))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_generalLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jrb_generateNetwork)
                                        .addComponent(jrb_geneExp))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jp_generalLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jrb_changeNetwork)
                                        .addComponent(jrb_ppi))
                                .addGap(18, 18, 18)
                                .addGroup(jp_generalLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jb_cancel)
                                        .addComponent(jb_tune)
                                        .addComponent(jb_saveSettings))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jl_selectNetwork.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        jl_selectNetwork.setText("Select network");

        jp_attrs.setBorder(BorderFactory.createEtchedBorder());

        jl_selectGeneIdAttr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        jl_selectGeneIdAttr.setText("Select attribute containing Entrez geneID");

        jl_selectTypeAttr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        jl_selectTypeAttr.setText("Select attribute specifying entity type");

        jsp_type.setViewportView(jlist_type);

        GroupLayout jp_attrsLayout = new GroupLayout(jp_attrs);
        jp_attrs.setLayout(jp_attrsLayout);
        jp_attrsLayout.setHorizontalGroup(
                jp_attrsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jp_attrsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jp_attrsLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jl_selectGeneIdAttr)
                                        .addComponent(jl_selectTypeAttr)
                                        .addComponent(jcb_typeAttr, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jsp_type, GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                                        .addComponent(jcb_geneIdAttr, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(23, Short.MAX_VALUE))
        );
        jp_attrsLayout.setVerticalGroup(
                jp_attrsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, jp_attrsLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jl_selectGeneIdAttr)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jcb_geneIdAttr, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                                .addComponent(jl_selectTypeAttr)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcb_typeAttr, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jsp_type, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jl_selectNetwork)
                                                                .addGap(39, 39, 39)
                                                                .addComponent(jcb_network, GroupLayout.PREFERRED_SIZE, 508, GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(tp_tuningSettings, GroupLayout.PREFERRED_SIZE, 353, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jp_attrs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                                .addGap(0, 6, Short.MAX_VALUE))
                                        .addComponent(jp_general, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jl_selectNetwork)
                                        .addComponent(jcb_network, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(tp_tuningSettings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jp_attrs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(17, 17, 17)
                                .addComponent(jp_general, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );


        jcb_selectTissue.setModel(jcb_tissueModel());

        addActionListeres();
        setParameters();
        loadProperties();


        pack();

    }// </editor-fold>

    private void addActionListeres() {
        jcb_network.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jcb_networkActionPerformed();
            }
        });
        jb_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        jb_tune.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performTuning();
            }
        });
        jb_loadDataset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_loadDatasetActionPerformed();
            }
        });
        jrb_userSuppliedData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_userSuppliedDataActionPerformed();
            }
        });
        jrb_BioGPS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_BioGPSActionPerformed();
            }
        });
        jb_saveSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
            }
        });
        jrb_geneExp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jrb_geneExpActionPerformed();
            }
        });
        jb_chooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_chooseFileActionPerformed();
            }
        });
        jrb_geneExp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_geneExpActionPerformed();
            }
        });
        jrb_ppi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jrb_ppiActionPerformed();
            }
        });
        jtxt_threshold.addActionListener(new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e) {
                jtxt_thresholdActionPerformed();

            }
        });
        jtxt_ppiThreshold.addActionListener(new ActionListener() {
            int value;

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    value = Integer.parseInt(jtxt_ppiThreshold.getText());
                } catch (Exception exc) {
                    value = jsl_ppiThreshold.getValue();
                    jtxt_ppiThreshold.setText("" + jsl_ppiThreshold.getValue());
                }
                if (value < jsl_ppiThreshold.getMinimum())
                    value = jsl_ppiThreshold.getMinimum();
                else if (value > jsl_ppiThreshold.getMaximum())
                    value = jsl_ppiThreshold.getMaximum();
                jsl_ppiThreshold.setValue(value);
            }
        });

        jsl_threshold.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                jtxt_threshold.setText("" + jsl_threshold.getValue());
                if (tseDataSet != null)
                    jtxt_absValue.setText(String.format("%.1f", tseDataSet.getAbsValue(jsl_threshold.getValue())));
            }
        });
        jsl_ppiThreshold.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                jtxt_ppiThreshold.setText("" + jsl_ppiThreshold.getValue());
            }
        });

        if (tunableNetwork != null)
            jcb_typeAttr.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadTypeAttrValues();
                }
            });
        jcb_selectTissue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jcb_selectTissueActionPerformed();
            }
        });
    }

    private void jcb_networkActionPerformed() {
        setTunableNetwork();
    }

    private void jtxt_thresholdActionPerformed() {
        int value;
        try {
            value = Integer.parseInt(jtxt_threshold.getText());
        } catch (Exception exc) {
            value = jsl_threshold.getValue();
            jtxt_threshold.setText("" + jsl_threshold.getValue());
        }
        if (value < jsl_threshold.getMinimum())
            value = jsl_threshold.getMinimum();
        else if (value > jsl_threshold.getMaximum())
            value = jsl_threshold.getMaximum();
        jsl_threshold.setValue(value);
        if (tseDataSet != null) {
            jtxt_absValue.setText(String.format("%.1f", tseDataSet.getAbsValue(jsl_threshold.getValue())));
        }
    }

    private void jcb_selectTissueActionPerformed() {
        tseDataSet = null;
        enableComponents();
    }

    private void jb_loadDatasetActionPerformed() {
        //Retrieve the chosen type attribute values
        int[] typeIndices = jlist_type.getSelectedIndices();
        ArrayList<String> selectedTypes = new ArrayList<String>();
        for (int i = 0; i < typeIndices.length; i++) {
            selectedTypes.add(jlist_type.getModel().getElementAt(typeIndices[i]).toString());
        }

        tuner = new Tuner(tunableNetwork,
                jcb_geneIdAttr.getSelectedItem().toString(),
                jcb_typeAttr.getSelectedItem().toString(), selectedTypes,
                jsl_threshold.getValue());
        tuner.setXmlFile(xmlFile);
        try {
            tseDataSet = tuner.loadExpressionDataSet(jcb_selectTissue.getSelectedItem().toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Problems occurred during loading the dataset. Make sure the dataset does not contain " +
                            "invalid characters.\n" + e.getMessage());
        }
        if (tseDataSet != null) {
            tseDataSet.sort();
            enableComponents();
        } else
            JOptionPane.showMessageDialog(this,
                    "The dataset was not loaded or is empty.\n\n" +
                            "Make sure that gene id attribute is appropriate and the dataset file is valid");
    }

    private void enableComponents() {
        jb_loadDataset.setEnabled(false);
        jb_loadDataset.setText("Load dataset");
        jb_tune.setEnabled(false);
        jcb_selectTissue.setEnabled(false);
        jsl_threshold.setEnabled(false);
        jtxt_threshold.setEnabled(false);
        jb_chooseFile.setEnabled(false);
        if (tunableNetwork == null)
            return;
        if ((jrb_geneExp.isSelected() && tseDataSet != null) ||
                jrb_ppi.isSelected())
            jb_tune.setEnabled(true);
        if (!jcb_selectTissue.getModel().getSelectedItem().equals(noTissuesAvailable))
            jcb_selectTissue.setEnabled(true);
        if (jrb_userSuppliedData.isSelected())
            jb_chooseFile.setEnabled(true);
        if (xmlFile != null && tseDataSet == null)
            jb_loadDataset.setEnabled(true);
        if (tseDataSet != null) {
            jsl_threshold.setEnabled(true);
            jtxt_threshold.setEnabled(true);
            jtxt_thresholdActionPerformed();
            jb_loadDataset.setText(xmlFile.getName() + "_" +
                    jcb_selectTissue.getSelectedItem().toString() + " loaded");
        }
    }


    private void jrb_ppiActionPerformed() {
        enableComponents();
    }

    private void jrb_geneExpActionPerformed() {
        enableComponents();
    }

    private void jrb_BioGPSActionPerformed() {
        tseDataSet = null;
        jl_selectedFile.setText("No file chosen");
        jb_chooseFile.setEnabled(false);
        xmlFile = getXmlFile();
        if (xmlFile != null)
            jcb_selectTissue.setModel(jcb_tissueModel());
        enableComponents();
    }


    private void jrb_userSuppliedDataActionPerformed() {
        xmlFile = null;
        tseDataSet = null;
        jb_chooseFile.setEnabled(true);
        jcb_selectTissue.setModel(jcb_tissueModel());
        enableComponents();
    }

    private void jb_chooseFileActionPerformed() {

        JFrame geneExpLoadFrame = new JFrame("Gene expression data load window");
        geneExpLoadFrame.setLocation(400, 250);
        geneExpLoadFrame.setSize(400, 200);
        JFileChooser fileChooser = new JFileChooser();
        File recentDir = new File(KEGGParserPlugin.getKEGGParserDir(), "recentExpDir.txt");
        if (!recentDir.exists())
            try {
                recentDir.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }


        try {
            Scanner recentExpDir = new Scanner(recentDir);
            if (recentExpDir.hasNextLine())
                fileChooser.setCurrentDirectory(new File(recentExpDir.nextLine()));
        } catch (FileNotFoundException e1) {
        }

        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                if (f.getName().toLowerCase().endsWith(".txt"))
                    return true;
                else if (f.getName().toLowerCase().endsWith(".csv"))
                    return true;
                else if (f.getName().toLowerCase().endsWith(".dat"))
                    return true;
                return false;
            }

            @Override
            public String getDescription() {
                return "txt, csv, dat";
            }
        });

        fileChooser.setDialogTitle("Load gene expression data");
        fileChooser.showOpenDialog(geneExpLoadFrame);

        if (fileChooser.getSelectedFile() != null) {
            expFileName = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                PrintWriter recentDirWriter = new PrintWriter(recentDir);
                recentDirWriter.write(fileChooser.getSelectedFile().toString());
                recentDirWriter.close();
            } catch (FileNotFoundException e1) {
                return;
            }
        }

        String noFile = "No file selected";
        String name;
        if (expFileName != null) {
            name = fileChooser.getSelectedFile().getName();
            int size = noFile.length();
            if (name.length() > size)
                name = name.substring(0, size) + "...";
            jl_selectedFile.setText(name);
        }

        if (processData(fileChooser.getSelectedFile())) {
            jcb_selectTissue.setModel(jcb_tissueModel());
            tseDataSet = null;
            enableComponents();

        }
    }


    private boolean processData(File selectedFile) {
        GeneExpXmlCreator xmlCreator = new GeneExpXmlCreator();
        boolean success = xmlCreator.loadData(selectedFile);
        if (!success) {
            expFileName = null;
            customTissuesList = null;
            return false;
        }
        File tuningDir = new File(KEGGParserPlugin.getKEGGParserDir(), "tuning");
        if (!tuningDir.exists())
            tuningDir.mkdir();
        xmlFile = new File(tuningDir, selectedFile.getName().substring(0, selectedFile.getName().lastIndexOf(".")) + ".xml");
        try {
            xmlCreator.createXml(xmlFile);
        } catch (Exception e) {
            return false;
        }
        customTissuesList = xmlCreator.getTissueList();
        return true;
    }

    public void setParameters() {
        Font plainFont = new Font("Arial", Font.PLAIN, 11);
        Font boldFont = new Font("Arial", Font.BOLD, 11);
        Font bold12Font = new Font("Arial", Font.BOLD, 12);
        Font plain12Font = new Font("Arial", Font.PLAIN, 12);
        //Panels
        jp_expSettings.setFont(plainFont);
        jp_ppiSettings.setFont(plainFont);
        //Labels
        jl_selectNetwork.setFont(boldFont); // NOI18N
        jl_selectNetwork.setText("Select network");
        jl_dataSource.setFont(boldFont); // NOI18N
        jl_dataSource.setText("Data source");
        jl_threshold.setFont(boldFont); // NOI18N
        jl_threshold.setText("Set expression threshold");
        jl_selectTissue.setFont(boldFont); // NOI18N
        jl_selectTissue.setText("Select the tissue");
        jl_selectGeneIdAttr.setFont(boldFont); // NOI18N
        jl_selectGeneIdAttr.setText("Select attribute containing Entrez geneID");
        jl_selectTypeAttr.setFont(boldFont); // NOI18N
        jl_selectTypeAttr.setText("Select attribute specifying entity type");
        jl_source.setFont(boldFont); // NOI18N
        jl_source.setText("Source");
        jl_ppiThreshold.setFont(boldFont); // NOI18N
        jl_ppiThreshold.setText("Set ppi threshold");
        jl_title.setFont(bold12Font); // NOI18N
        jl_title.setText(("Pathway tuning settings for network " + tunableNetworkTitle));
        jl_tune.setFont(boldFont); // NOI18N
        jl_tune.setText("Tune the network based on");
        jl_tuningMode.setFont(boldFont); // NOI18N
        jl_tuningMode.setText("Tuning mode");
        jl_selectedFile.setFont(plainFont);
        jl_percentile.setFont(plainFont);
        jl_absValue.setFont(plainFont);


        //buttons
        jb_cancel.setText("Cancel");
        jb_cancel.setFont(plainFont); // NOI18N
        jb_tune.setText("Tune");
        jb_tune.setFont(plainFont);
        jb_saveSettings.setText("Save settings");
        jb_saveSettings.setFont(plainFont);
        jb_chooseFile.setFont(plainFont);
        jb_loadDataset.setFont(plain12Font);

        //Combo boxes and lists
        jcb_selectTissue.setModel(jcb_tissueModel());
        jcb_selectTissue.setFont(plainFont);
        jsp_type.setViewportView(jlist_type);
        jsp_type.setFont(plainFont);
        jlist_type.setEnabled(true);
        jlist_type.setFont(plainFont);

        //Radio buttons and button groups
        jbg_databaseGroup.add(jrb_BioGPS);
        jbg_databaseGroup.add(jrb_userSuppliedData);
        jrb_BioGPS.setSelected(true);
        jrb_BioGPS.setText("BioGPS   (only for human genes)");
        jrb_BioGPS.setFont(plainFont);
        jrb_userSuppliedData.setFont(plainFont);
        jbg_tuneBy.add(jrb_geneExp);
        jbg_tuneBy.add(jrb_ppi);
        jrb_geneExp.setText("Gene expression ");
        jrb_geneExp.setFont(plainFont);
        jrb_ppi.setText("Protein-protein interaction");
        jrb_ppi.setFont(plainFont);

        jbg_tuningMode.add(jrb_generateNetwork);
        jbg_tuningMode.add(jrb_changeNetwork);
        jrb_generateNetwork.setText("Generate new network");
        jrb_generateNetwork.setFont(plainFont);
        jrb_changeNetwork.setText("Change current network");
        jrb_changeNetwork.setFont(plainFont);
        jrb_changeNetwork.setEnabled(false);


        //Sliders and text fields

        jsl_threshold.setPaintLabels(true);
        jsl_threshold.setPaintTicks(false);
        jsl_threshold.setFont(plainFont);
        jtxt_threshold.setText("" + jsl_threshold.getValue());
        jtxt_threshold.setFont(plainFont);
        jsl_ppiThreshold.setMajorTickSpacing(100);
        jsl_ppiThreshold.setMinorTickSpacing(5);
        jsl_ppiThreshold.setMaximum(1000);
        jsl_ppiThreshold.setPaintLabels(true);
        jsl_ppiThreshold.setPaintTicks(true);
        jsl_ppiThreshold.setFont(plainFont);
        jtxt_absValue.setEditable(false);
        jtxt_absValue.setFont(plainFont);

        //Check boxes
        jchb_source_grid.setText("GRID");
        jchb_source_mint.setText("MINT");
        jchb_source_kegg.setText("KEGG");
        jchb_source_dip.setText("DIP");
        jchb_source_pdb.setText("PDB");

        //Tabs and panels
        tp_tuningSettings.addTab("Protein-protein interaction Settings", jp_ppiSettings);
        tp_tuningSettings.setFont(plainFont);

    }


    private ComboBoxModel jcb_tissueModel() {
        if (!jrb_userSuppliedData.isSelected())
            return new DefaultComboBoxModel(bioGPSTissues);
        else if (customTissuesList == null)
            return new DefaultComboBoxModel(new String[]{noTissuesAvailable});
        else {
            return new DefaultComboBoxModel(customTissuesList);
        }
    }

    public void loadProperties() {
        Set<CyNetwork> networkSet = KEGGParserPlugin.networkManager.getNetworkSet();
        String[] networkTitles = new String[networkSet.size()];
        int index = 0;
        for (CyNetwork network : networkSet) {
            networkTitles[index++] = network.getRow(network).get("Name", String.class);
        }
        jcb_network.setModel(new DefaultComboBoxModel(networkTitles));
        for (int i = 0; i < jcb_network.getItemCount(); i++) {
            Object item = jcb_network.getItemAt(i);
            CyNetwork currentNetwork = KEGGParserPlugin.cyApplicationManager.getCurrentNetwork();
            if (currentNetwork != null)
                if (item.toString().equals(currentNetwork.getRow(currentNetwork).get("Name", String.class)))
                    jcb_network.setSelectedItem(item);
        }


        jrb_BioGPS.setSelected((keggProps.getProperty(EKEGGTuningProps.Database.getName())).equals("BioGPS"));
        jsl_threshold.setValue(Integer.parseInt(keggProps.getProperty(EKEGGTuningProps.ExpThreshold.getName())));
        String tissue = keggProps.getProperty(EKEGGTuningProps.Tissue.getName());
        for (int i = 0; i < jcb_selectTissue.getItemCount(); i++) {
            Object item = jcb_selectTissue.getModel().getElementAt(i);
            if (tissue.equals(item.toString()))
                jcb_selectTissue.setSelectedItem(item);
        }
        jrb_geneExp.setSelected((keggProps.getProperty(EKEGGTuningProps.GeneExpressionMode.getName())).equals("true"));
        jrb_ppi.setSelected((keggProps.getProperty(EKEGGTuningProps.PPIMode.getName())).equals("true"));
        jrb_generateNetwork.setSelected((keggProps.getProperty(EKEGGTuningProps.GenerateNewNetwork.getName()))
                .equals("true"));
        jrb_changeNetwork.setSelected((keggProps.getProperty(EKEGGTuningProps.GenerateNewNetwork.getName()))
                .equals("false"));

        if (tunableNetwork != null) {
            CyTable nodeAttrs = tunableNetwork.getDefaultNodeTable();
            CyColumn[] attrs = new CyColumn[nodeAttrs.getColumns().size()];
            nodeAttrs.getColumns().toArray(attrs);
            String[] attrsModel = new String[attrs.length + 1];
            attrsModel[0] = "Select attribute";
            for (int i = 1; i < attrsModel.length; i++)
                attrsModel[i] = attrs[i - 1].getName();

            jcb_geneIdAttr.setModel(new DefaultComboBoxModel(attrsModel));
            String geneIdAttr = keggProps.getProperty(EKEGGTuningProps.GeneIdAttr.getName());
            jcb_geneIdAttr.setSelectedItem(jcb_geneIdAttr.getModel().getElementAt(0));

            jcb_typeAttr.setModel(new DefaultComboBoxModel(attrsModel));
            String typeAttr = keggProps.getProperty(EKEGGTuningProps.TypeAttr.getName());
            jcb_typeAttr.setSelectedItem(jcb_typeAttr.getModel().getElementAt(0));

            for (int i = 0; i < jcb_typeAttr.getItemCount(); i++) {
                Object item = jcb_geneIdAttr.getModel().getElementAt(i);
                if (geneIdAttr.equals(item.toString()))
                    jcb_geneIdAttr.setSelectedItem(item);
                if (typeAttr.equals(item.toString()))
                    jcb_typeAttr.setSelectedItem(item);
            }
        }

        //PPI
        jsl_ppiThreshold.setValue(Integer.parseInt(keggProps.getProperty(EKEGGTuningProps.PPITHreshold.getName())));
        jchb_source_dip.setSelected((keggProps.getProperty(EKEGGTuningProps.PPISourceDIP.getName()).equals("true")));
        jchb_source_grid.setSelected((keggProps.getProperty(EKEGGTuningProps.PPISourceGRID.getName()).equals("true")));
        jchb_source_mint.setSelected((keggProps.getProperty(EKEGGTuningProps.PPISourceMINT.getName()).equals("true")));
        jchb_source_kegg.setSelected((keggProps.getProperty(EKEGGTuningProps.PPISourceKEGG.getName()).equals("true")));
        jchb_source_pdb.setSelected((keggProps.getProperty(EKEGGTuningProps.PPISourcePDB.getName()).equals("true")));

        if (jrb_geneExp.isSelected())
            jrb_BioGPSActionPerformed();
        enableComponents();

    }


    private void loadTypeAttrValues() {
        jlist_type.removeAll();
        Object item = jcb_typeAttr.getSelectedItem();
        if (item != null && item != jcb_typeAttr.getModel().getElementAt(0)) {
            String typeAttr = item.toString();
            TreeSet<String> typeValues = new TreeSet<String>();
            for (Object node : tunableNetwork.getNodeList()) {
                CyNode cyNode = (CyNode) node;
                String attr = null;
                try {
                    attr = tunableNetwork.getDefaultNodeTable().getRow(cyNode.getSUID()).get(typeAttr, String.class);
                } catch (Exception e) {
                }
                if (attr != null) {
                    typeValues.add(attr);
                }
            }
            try {
                jlist_type.setListData(typeValues.toArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
            String typeValue = KEGGParserPlugin.getKeggProps().getProperty(EKEGGTuningProps.TypeValues.getName());

            for (int i = 0; i < jlist_type.getModel().getSize(); i++) {
                Object value = jlist_type.getModel().getElementAt(i);
                if (typeValue.equals(value.toString()))
                    jlist_type.setSelectedIndex(i);
            }
        }

    }


    private void performTuning() {
        if (tunableNetwork == null)
            setTunableNetwork();

        if (jrb_geneExp.isSelected())
            if (jrb_BioGPS.isSelected()) {
                String organism = tunableNetwork.getRow(tunableNetwork).get("Organism", String.class);
                if (organism != null)
                    if (!organism.equals("hsa")) {
                        int response = JOptionPane.showConfirmDialog(this,
                                "The BioGPS data contain only human genes. " +
                                        "Are you sure you want to process the " + tunableNetworkTitle +
                                        " pathway with it?",
                                "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (response == JOptionPane.NO_OPTION)
                            return;
                    }
            }
        saveSettings();


        //Retrieve the chosen type attribute values
        int[] typeIndices = jlist_type.getSelectedIndices();
        ArrayList<String> selectedTypes = new ArrayList<String>();
        for (int i = 0; i < typeIndices.length; i++) {
            selectedTypes.add(jlist_type.getModel().getElementAt(typeIndices[i]).toString());
        }


        if (jrb_geneExp.isSelected()) {
            if (tuner == null)
                tuner = new Tuner(tunableNetwork);
            tuner.performTSETuning(tunableNetwork,
                    jcb_selectTissue.getSelectedItem().toString(), jcb_geneIdAttr.getSelectedItem().toString(),
                    jcb_typeAttr.getSelectedItem().toString(), selectedTypes,
                    Integer.parseInt(jtxt_threshold.getText()), jrb_generateNetwork.isSelected());
        } else {
            tuner = new Tuner(tunableNetwork);
            ArrayList<String> sources = new ArrayList<String>();
            if (jchb_source_dip.isSelected())
                sources.add("dip");
            if (jchb_source_grid.isSelected())
                sources.add("grid");
            if (jchb_source_kegg.isSelected())
                sources.add("kegg");
            if (jchb_source_mint.isSelected())
                sources.add("mint");
            if (jchb_source_pdb.isSelected())
                sources.add("pdb");
            tuner.performPPITuning(
                    sources, jcb_geneIdAttr.getSelectedItem().toString(),
                    jcb_typeAttr.getSelectedItem().toString(), selectedTypes,
                    Integer.parseInt(jtxt_ppiThreshold.getText()), jrb_generateNetwork.isSelected());
        }
        this.setVisible(false);
    }

    private void saveSettings() {
        Enumeration<AbstractButton> buttons = jbg_databaseGroup.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.isSelected()) {
                keggProps.setProperty(EKEGGTuningProps.Database.getName(),
                        button.getText());
                break;
            }
        }
        keggProps.setProperty(EKEGGTuningProps.ExpThreshold.getName(),
                "" + jsl_threshold.getValue());
        String tissue;
        if (jcb_selectTissue.getSelectedItem() != null)
            tissue = jcb_selectTissue.getSelectedItem().toString();
        else
            tissue = jcb_selectTissue.getModel().getElementAt(0).toString();
        keggProps.setProperty(EKEGGTuningProps.Tissue.getName(),
                tissue);
        keggProps.setProperty(EKEGGTuningProps.GeneExpressionMode.getName(),
                jrb_geneExp.isSelected() ? "true" : "false");
        keggProps.setProperty(EKEGGTuningProps.PPIMode.getName(),
                jrb_ppi.isSelected() ? "true" : "false");
        keggProps.setProperty(EKEGGTuningProps.GenerateNewNetwork.getName(),
                jrb_generateNetwork.isSelected() ? "true" : "false");
        String geneIdAttr = "";
        if (jcb_geneIdAttr.getSelectedItem() != null)
            geneIdAttr = jcb_geneIdAttr.getSelectedItem().toString();
        keggProps.setProperty(EKEGGTuningProps.GeneIdAttr.getName(),
                geneIdAttr);

        try {
            File file = KEGGParserPlugin.getKeggPropsFile();
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(file);
                keggProps.store(output, "Cytoscape Property File");
                logger.info("wrote KEGG properties file to: " + file.getAbsolutePath());
            } finally {
                if (output != null) {
                    output.close();
                }
            }
        } catch (Exception ex) {
            logger.error("Could not write cytoscape.props file!", ex);
        }

        jb_cancel.setEnabled(false);

        //ppi
        keggProps.setProperty(EKEGGTuningProps.PPITHreshold.getName(), jtxt_ppiThreshold.getText());
        keggProps.setProperty(EKEGGTuningProps.PPISourceDIP.getName(), jchb_source_dip.isSelected() ?
                "true" : "false");
        keggProps.setProperty(EKEGGTuningProps.PPISourceGRID.getName(), jchb_source_grid.isSelected() ?
                "true" : "false");
        keggProps.setProperty(EKEGGTuningProps.PPISourceMINT.getName(), jchb_source_mint.isSelected() ?
                "true" : "false");
        keggProps.setProperty(EKEGGTuningProps.PPISourceKEGG.getName(), jchb_source_kegg.isSelected() ?
                "true" : "false");
        keggProps.setProperty(EKEGGTuningProps.PPISourcePDB.getName(), jchb_source_pdb.isSelected() ?
                "true" : "false");
    }

    public File getXmlFile() {
        if (jrb_BioGPS.isSelected()) {
            File tuningDir = new File(KEGGParserPlugin.getKEGGParserDir(), "tuning");
            if (!tuningDir.exists())
                tuningDir.mkdir();
            File xml = new File(tuningDir, "BioGps.xml");
            if (!xml.exists()) {
                InputStream in = getClass().getClassLoader().getResourceAsStream("bioGps.xml");
                OutputStream out = null;
                try {
                    out = new FileOutputStream(xml);
                    int b;
                    byte[] bytes = new byte[1024];
                    while ((b = in.read(bytes)) != -1) {
                        out.write(bytes, 0, b);
                    }
                } catch (IOException e) {
                    LoggerFactory.getLogger(KEGGTuningDialog.class).error(e.getMessage());

                } finally {
                    if (in != null)
                        try {
                            in.close();
                        } catch (IOException e) {
                            LoggerFactory.getLogger(KEGGTuningDialog.class).error(e.getMessage());
                        }
                    if (out != null)
                        try {
                            out.close();
                        } catch (IOException e) {
                            LoggerFactory.getLogger(KEGGTuningDialog.class).error(e.getMessage());
                        }
                }
            }
            return xml;
        } else if (xmlFile == null) {
            JOptionPane.showMessageDialog(this,
                    "A proper gene expression data file is not loaded.");
            return null;
        } else
            return xmlFile;
    }

    // Variables declaration - do not modify
    private ButtonGroup jbg_tuneBy;
    private JRadioButton jrb_geneExp;
    private JRadioButton jrb_ppi;
    private ButtonGroup jbg_tuningMode;
    private JRadioButton jrb_changeNetwork;
    private JRadioButton jrb_generateNetwork;
    private JRadioButton jrb_BioGPS;
    private JButton jb_cancel;
    private JButton jb_saveSettings;
    private JButton jb_tune;
    private ButtonGroup jbg_databaseGroup;
    private JComboBox jcb_geneIdAttr;
    private JComboBox jcb_network;
    private JComboBox jcb_selectTissue;
    private JComboBox jcb_typeAttr;
    private JCheckBox jchb_source_dip;
    private JCheckBox jchb_source_grid;
    private JCheckBox jchb_source_kegg;
    private JCheckBox jchb_source_mint;
    private JCheckBox jchb_source_pdb;
    private JLabel jl_dataSource;
    private JLabel jl_ppiThreshold;
    private JLabel jl_selectGeneIdAttr;
    private JLabel jl_selectNetwork;
    private JLabel jl_selectTissue;
    private JLabel jl_selectTypeAttr;
    private JLabel jl_source;
    private JLabel jl_threshold;
    private JLabel jl_tune;
    private JLabel jl_tuningMode;
    private JList jlist_type;
    private JPanel jp_attrs;
    private JPanel jp_dataSource;
    private JPanel jp_expSettings;
    private JPanel jp_general;
    private JPanel jp_ppiSettings;
    private JPanel jp_ppiThreshold;
    private JPanel jp_source;
    private JPanel jp_threshold;
    private JPanel jp_tissue;
    private JSlider jsl_ppiThreshold;
    private JSlider jsl_threshold;
    private JScrollPane jsp_type;
    private JTextField jtxt_ppiThreshold;
    private JTextField jtxt_threshold;
    private JTabbedPane tp_tuningSettings;
    private JLabel jl_title;
    private JLabel jl_selectedFile;
    private JRadioButton jrb_userSuppliedData;
    private JButton jb_chooseFile;
    private JButton jb_loadDataset;
    private JLabel jl_absValue;
    private JLabel jl_percentile;
    private JTextField jtxt_absValue;

    // End of variables declaration
}
