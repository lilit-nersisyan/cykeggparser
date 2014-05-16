package org.cytoscape.keggparser.actions;

import org.cytoscape.keggparser.parsing.KGMLConverter;


public class KeggSaveAsBioPAX2Action extends KeggSaveAsBioPAXAction {
    public KeggSaveAsBioPAX2Action() {
        super(KGMLConverter.BioPAX2, "Save as BioPAX_level2");
        setMenuGravity(3);
        setPreferredMenu("Apps.KEGGParser.Save network");
    }

}

