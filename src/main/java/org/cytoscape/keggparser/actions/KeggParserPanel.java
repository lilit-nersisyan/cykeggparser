package org.cytoscape.keggparser.actions;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

import javax.swing.*;
import java.awt.*;

public class KeggParserPanel extends JPanel implements CytoPanelComponent {
	
	private static final long serialVersionUID = 8292806967891823933L;

	public KeggParserPanel() {
		JLabel lbXYZ = new JLabel("KEGGParser Panel label ");
        this.setPreferredSize(new Dimension(300, getHeight()));
		this.add(lbXYZ);
		this.setVisible(true);
	}

	public Component getComponent() {
		return this;
	}


	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}


	public String getTitle() {
		return "KeggParserPanel";
	}


	public Icon getIcon() {
		return null;
	}
}
