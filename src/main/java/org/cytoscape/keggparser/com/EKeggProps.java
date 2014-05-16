package org.cytoscape.keggparser.com;

public enum EKeggProps {

    ProcessGroups("auto.groupProcessing"),
    ProcessCompounds("auto.compoundProcessing"),
    ProcessBindingDirs("auto.bindingDirProcessing");

    private boolean oldValue = true;
    private boolean newValue = true;
    private String name = "";
    
    EKeggProps(String name){
        this.name = name;
    }

    private boolean initialized = false;

    public void setInitialized(boolean b){
        this.initialized = b;
    }
    public boolean isInitialized(){
        return initialized;
    }


    public String getName(){
        return name;
    }

    public boolean getNewValue() {
        return newValue;
    }

    public void setNewValue(boolean value) {
        this.newValue = value;
    }

    public boolean getOldValue() {
        return oldValue;
    }

    public void setOldValue(boolean oldValue) {
        this.oldValue = oldValue;
    }
}
