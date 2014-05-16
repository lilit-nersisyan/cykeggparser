package org.cytoscape.keggparser.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.keggparser.dialogs.KEGGTuningDialog;

import java.awt.event.ActionEvent;


public class KEGGTuningAction extends AbstractCyAction {
    KEGGTuningDialog keggTuningDialog;

    public KEGGTuningAction() {
        super("Pathway tuning");
        setMenuGravity(5);
        setPreferredMenu("Apps.KEGGParser");
    }

    public void actionPerformed(ActionEvent e) {
            keggTuningDialog = new KEGGTuningDialog();
    }




}
