package org.cytoscape.keggparser.actions;

import org.cytoscape.keggparser.parsing.KGMLConverter;


public class KeggSaveAsBioPAX3Action extends KeggSaveAsBioPAXAction {
    public KeggSaveAsBioPAX3Action() {
        super(KGMLConverter.BioPAX3, "Save as BioPAX_level3");
        setMenuGravity(4);
        setPreferredMenu("Apps.KEGGParser.Save network");
    }

}

