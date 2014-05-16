package org.cytoscape.keggparser.actions;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;

import java.awt.event.ActionEvent;


public class KeggParserPanelAction extends AbstractCyAction {

	private CySwingApplication desktopApp;
	private final CytoPanel cytoPanelWest;
	private KeggParserPanel keggParserPanel;
	
	public KeggParserPanelAction(CySwingApplication desktopApp,
                                 KeggParserPanel KeggParserPanel){
		// Add a menu item -- Apps->sample02
		super("KEGGParser");
		setPreferredMenu("Apps");

		this.desktopApp = desktopApp;

		//Note: keggParserPanel is bean we defined and registered as a service
		this.cytoPanelWest = this.desktopApp.getCytoPanel(CytoPanelName.WEST);
		this.keggParserPanel = KeggParserPanel;
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		/*// If the state of the cytoPanelWest is HIDE, show it
		if (cytoPanelWest.getState() == CytoPanelState.HIDE) {
			cytoPanelWest.setState(CytoPanelState.DOCK);
		}	

		// Select my panel
		int index = cytoPanelWest.indexOfComponent(keggParserPanel);
		if (index == -1) {
			return;
		}
		cytoPanelWest.setSelectedIndex(index);        */

	}

}
