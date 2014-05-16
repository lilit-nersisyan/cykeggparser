package org.cytoscape.keggparser.dialogs;


import org.cytoscape.keggparser.KEGGParserPlugin;
import org.cytoscape.keggparser.com.EKeggProps;
import org.cytoscape.keggparser.tuning.tse.EKEGGTuningProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;

public class KeggPrefsDialog extends JDialog {
    private static final int[] alignment = new int[]{JLabel.LEFT, JLabel.LEFT};
    private static final int[] columnWidth = new int[]{200, 350};

    private static final Color SELECTED_CELL_COLOR = new Color(0, 100, 255, 40);
    private static Logger logger = LoggerFactory.getLogger(KeggPrefsDialog.class);

    public static final String MEAN = "mean";
    public static final String MIN = "min";
    public static final String MAX = "max";

    private JCheckBox chb_bindingDir;
    private JCheckBox chb_compound;
    private JCheckBox chb_groups;
    private JButton jb_Cancel;
    private JButton jb_ok;
    private JLabel jl_autoCorrections;

    private JCheckBox jchb_savePrefs;
    private JLabel jl_geneConflictMode;
    private JRadioButton jrb_max;
    private JRadioButton jrb_mean;
    private JRadioButton jrb_min;
    private ButtonGroup conflictModeGroup = new ButtonGroup();

    private JCheckBox jchb_absentGenes;
    private JLabel jl_tseOptions;
    private JLabel jl_ppiOptions;
    private JCheckBox jchb_indirectInteractions;


    public KeggPrefsDialog() {
        setName("KEGG parsing preferences");
        loadProps();
        initComponents();
        setVisible(true);
    }

    public void loadProps() {
        for (EKeggProps property : EKeggProps.values()) {
            property.setOldValue(Boolean.parseBoolean((String) KEGGParserPlugin.getKeggProps().get(property.getName())));
            property.setNewValue(Boolean.parseBoolean((String) KEGGParserPlugin.getKeggProps().get(property.getName())));
        }
    }

    private void initComponents() {
        chb_groups = new javax.swing.JCheckBox();
        chb_compound = new javax.swing.JCheckBox();
        chb_bindingDir = new javax.swing.JCheckBox();
        jl_autoCorrections = new javax.swing.JLabel();
        jb_ok = new javax.swing.JButton();
        jb_Cancel = new javax.swing.JButton();
        jl_geneConflictMode = new javax.swing.JLabel();
        jrb_mean = new javax.swing.JRadioButton();
        jrb_min = new javax.swing.JRadioButton();
        jrb_max = new javax.swing.JRadioButton();
        jchb_savePrefs = new javax.swing.JCheckBox();
        jchb_absentGenes = new javax.swing.JCheckBox();
        jl_tseOptions = new javax.swing.JLabel();
        jl_ppiOptions = new javax.swing.JLabel();
        jchb_indirectInteractions = new javax.swing.JCheckBox();

        chb_groups.setSelected(true);
        chb_groups.setText("Create metanodes for KEGG group nodes");

        chb_compound.setSelected(true);
        chb_compound.setText("Process protein-compound-protein interactions");

        chb_bindingDir.setSelected(true);
        chb_bindingDir.setText("Correct binding interaction directions");

        jl_autoCorrections.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_autoCorrections.setText("Choose automatic correction options");

        jb_ok.setText("OK");

        jb_Cancel.setText("Cancel");

        jl_geneConflictMode.setText("Multiple value handling in expression data");

        jrb_mean.setText("Mean");

        jrb_min.setText("Minimum");

        jrb_max.setText("Maximum");

        jchb_savePrefs.setText("Save preferences as default");

        jchb_absentGenes.setText("Keep genes with missing values in the nodes");


        jl_tseOptions.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_tseOptions.setText("Tissue-specific tuning options");

        jl_ppiOptions.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jl_ppiOptions.setText("PPI-based drill-down options");

        jchb_indirectInteractions.setText("Keep indirect interactions in the network");


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jl_autoCorrections)
                                                        .addComponent(chb_bindingDir)
                                                        .addComponent(chb_compound)
                                                        .addComponent(chb_groups)
                                                        .addComponent(jchb_absentGenes))
                                                .addContainerGap())
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jchb_indirectInteractions)
                                                        .addComponent(jl_ppiOptions)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jrb_mean)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jrb_min)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jrb_max))
                                                        .addComponent(jl_geneConflictMode, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jl_tseOptions))
                                                .addGap(0, 0, Short.MAX_VALUE))))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jchb_savePrefs)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jb_ok)
                                .addGap(18, 18, 18)
                                .addComponent(jb_Cancel)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jl_autoCorrections)
                                .addGap(14, 14, 14)
                                .addComponent(chb_groups)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chb_compound)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chb_bindingDir)
                                .addGap(18, 18, 18)
                                .addComponent(jl_tseOptions)
                                .addGap(11, 11, 11)
                                .addComponent(jl_geneConflictMode, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jrb_mean)
                                        .addComponent(jrb_min)
                                        .addComponent(jrb_max))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jchb_absentGenes)
                                .addGap(18, 18, 18)
                                .addComponent(jl_ppiOptions)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jchb_indirectInteractions)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jb_ok)
                                        .addComponent(jb_Cancel)
                                        .addComponent(jchb_savePrefs))
                                .addContainerGap())
        );
        addActionListeners();
        setComponentProperties();
        pack();

    }

    private void setComponentProperties() {
        conflictModeGroup.add(jrb_mean);
        conflictModeGroup.add(jrb_min);
        conflictModeGroup.add(jrb_max);
        String conflictMode = KEGGParserPlugin.getKeggProps().getProperty(EKEGGTuningProps.TSEConflictMode.getName());
        if (conflictMode.equals(MEAN))
            jrb_mean.setSelected(true);
        else if (conflictMode.equals(MIN))
            jrb_min.setSelected(true);
        else
            jrb_max.setSelected(true);
        chb_groups.setText("Process group nodes");
        if (KEGGParserPlugin.getKeggProps().getProperty(EKEGGTuningProps.TSEKeepAbsentGenes.getName()).equals("true"))
            jchb_absentGenes.setSelected(true);
        else
            jchb_absentGenes.setSelected(false);
        if (KEGGParserPlugin.getKeggProps().getProperty(EKEGGTuningProps.PPIKeepIndirectInteractions.getName()).equals("true"))
            jchb_indirectInteractions.setSelected(true);
        else
            jchb_indirectInteractions.setSelected(false);
    }

    private void addActionListeners() {
        this.chb_compound.setSelected(Boolean.valueOf(KEGGParserPlugin.getKeggProps().
                getProperty(EKeggProps.ProcessCompounds.getName())));
        this.chb_groups.setSelected(Boolean.valueOf(KEGGParserPlugin.getKeggProps().
                getProperty(EKeggProps.ProcessGroups.getName())));
        this.chb_bindingDir.setSelected(Boolean.valueOf(KEGGParserPlugin.getKeggProps().
                getProperty(EKeggProps.ProcessBindingDirs.getName())));
        this.jchb_savePrefs.setSelected(false);

        chb_groups.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                keggProps.setProperty(EKeggProps.ProcessGroups.getName(),
//                        EKeggProps.ProcessGroups.getValue() ? "false" : "true");
                EKeggProps.ProcessGroups.setNewValue(chb_groups.isSelected());
            }
        });

        chb_compound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                keggProps.setProperty(EKeggProps.ProcessCompounds.getName(),
//                        EKeggProps.ProcessCompounds.getValue() ? "false" : "true");
                EKeggProps.ProcessCompounds.setNewValue(chb_compound.isSelected());
            }
        });

        chb_bindingDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                keggProps.setProperty(EKeggProps.ProcessBindingDirs.getName(),
//                        EKeggProps.ProcessBindingDirs.getValue()? "false" : "true");
                EKeggProps.ProcessBindingDirs.setNewValue(chb_bindingDir.isSelected());
            }
        });

        jb_ok.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                okButtonPressed();
            }
        });

        jb_Cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cancelButtonPressed();
            }
        });
    }

    private void cancelButtonPressed() {
        for (EKeggProps property : EKeggProps.values()) {
            if (property.getNewValue() != property.getOldValue()) {
                property.setNewValue(property.getOldValue());
            }
        }
        setVisible(false);
    }


    private void okButtonPressed() {
        for (EKeggProps property : EKeggProps.values()) {
            if (property.getNewValue() != property.getOldValue()) {
                KEGGParserPlugin.getKeggProps().setProperty(property.getName(), property.getOldValue() ? "false" : "true");
                property.setOldValue(property.getNewValue());
            }
        }
        EKEGGTuningProps property  = EKEGGTuningProps.TSEConflictMode;
        if (jrb_mean.isSelected())
            KEGGParserPlugin.getKeggProps().setProperty(property.getName(), MEAN);
        else if (jrb_max.isSelected())
            KEGGParserPlugin.getKeggProps().setProperty(property.getName(), MAX);
        else
            KEGGParserPlugin.getKeggProps().setProperty(property.getName(), MIN);

        property = EKEGGTuningProps.TSEKeepAbsentGenes;
        if (jchb_absentGenes.isSelected())
            KEGGParserPlugin.getKeggProps().setProperty(property.getName(), "true");
        else
            KEGGParserPlugin.getKeggProps().setProperty(property.getName(), "false");

        property = EKEGGTuningProps.PPIKeepIndirectInteractions;
        if (jchb_indirectInteractions.isSelected())
            KEGGParserPlugin.getKeggProps().setProperty(property.getName(), "true");
        else
            KEGGParserPlugin.getKeggProps().setProperty(property.getName(), "false");

        if (jchb_savePrefs.isSelected()) {
            try {
                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(KEGGParserPlugin.getKeggPropsFile());
                    KEGGParserPlugin.getKeggProps().store(output, "Cytoscape Property File");
                    logger.info("wrote KEGG properties file to: " + KEGGParserPlugin.getKeggPropsFile().getAbsolutePath());
                } finally {
                    if (output != null) {
                        output.close();
                    }
                }
            } catch (Exception ex) {
                logger.error("Could not write cytoscape.props file!", ex);
            }
        }
        this.setVisible(false);
    }

}
