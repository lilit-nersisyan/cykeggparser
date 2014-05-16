package org.cytoscape.keggparser.tuning.tse;

public enum Tissue {
    B_lymphoblasts("B_lymphoblasts"),
    Adipocyte("Adipocyte"),
    AdrenalCortex("AdrenalCortex"),
    Adrenalgland("Adrenalgland"),
    Amygdala("Amygdala"),
    Appendix("Appendix"),
    AtrioventricularNode("AtrioventricularNode"),
    BDCA4_DentriticCells("BDCA4_DentriticCells"),
    Bonemarrow("Bonemarrow"),
    BronchialEpithelialCells("BronchialEpithelialCells"),
    CD105_Endothelial("CD105_Endothelial"),
    CD14_Monocytes("CD14_Monocytes"),
    CD19_BCells("CD19_BCells"),
    CD33_Myeloid("CD33_Myeloid"),
    CD34("CD34"),
    CD4_Tcells("CD4_Tcells"),
    CD56_NKCells("CD56_NKCells"),
    CD71_EarlyErythroid("CD71_EarlyErythroid"),
    CD8_Tcells("CD8_Tcells"),
    CardiacMyocytes("CardiacMyocytes"),
    Caudatenucleus("Caudatenucleus"),
    Cerebellum("Cerebellum"),
    CerebellumPeduncles("CerebellumPeduncles"),
    CiliaryGanglion("CiliaryGanglion"),
    CingulateCortex("CingulateCortex"),
    Colon("Colon"),
    Colorectaladenocarcinoma("Colorectaladenocarcinoma"),
    DorsalRootGanglion("DorsalRootGanglion"),
    Fetal_Thyroid("Fetal_Thyroid"),
    Fetal_brain("Fetal_brain"),
    Fetal_liver("Fetal_liver"),
    Fetal_lung("Fetal_lung"),
    GlobusPallidus("GlobusPallidus"),
    Heart("Heart"),
    Hypothalamus("Hypothalamus"),
    Kidney("Kidney"),
    Leukemia_chronicMyelogenousK("Leukemia_chronicMyelogenousK"),
    Leukemia_promyelocytic("Leukemia_promyelocytic"),
    Leukemialymphoblastic("Leukemialymphoblastic"),
    Liver("Liver"),
    Lung("Lung"),
    Lymphnode("Lymphnode"),
    Lymphoma_burkitts_Daudi("Lymphoma_burkitts_Daudi"),
    Lymphoma_burkitts_Raji("Lymphoma_burkitts_Raji"),
    MedullaOblongata("MedullaOblongata"),
    OccipitalLobe("OccipitalLobe"),
    OlfactoryBulb("OlfactoryBulb"),
    Ovary("Ovary"),
    Pancreas("Pancreas"),
    PancreaticIslet("PancreaticIslet"),
    ParietalLobe("ParietalLobe"),
    Pineal_day("Pineal_day"),
    Pineal_night("Pineal_night"),
    Pituitary("Pituitary"),
    Placenta("Placenta"),
    Pons("Pons"),
    PrefrontalCortex("PrefrontalCortex"),
    Prostate("Prostate"),
    Retina("Retina"),
    Salivarygland("Salivarygland"),
    SkeletalMuscle("SkeletalMuscle"),
    Skin("Skin"),
    Small_intestine("Small_intestine"),
    SmoothMuscle("SmoothMuscle"),
    Spinalcord("Spinalcord"),
    SubthalamicNucleus("SubthalamicNucleus"),
    SuperiorCervicalGanglion("SuperiorCervicalGanglion"),
    TemporalLobe("TemporalLobe"),
    Testis("Testis"),
    TestisGermCell("TestisGermCell"),
    TestisIntersitial("TestisIntersitial"),
    TestisLeydigCell("TestisLeydigCell"),
    TestisSeminiferousTubule("TestisSeminiferousTubule"),
    Thalamus("Thalamus"),
    Thymus("Thymus"),
    Thyroid("Thyroid"),
    Tongue("Tongue"),
    Tonsil("Tonsil"),
    Trachea("Trachea"),
    TrigeminalGanglion("TrigeminalGanglion"),
    Uterus("Uterus"),
    UterusCorpus("UterusCorpus"),
    WholeBlood("WholeBlood"),
    Wholebrain("Wholebrain");


    private String tissue;
    private int numProbeSets;
    private double expValue = 0d;
    private double mean;
    private double stdev;

    Tissue(String tissueName) {
        tissue = tissueName;
    }

    public void setExpValue(double value) {
        expValue = value;
        numProbeSets = 1;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getStdev() {
        return stdev;
    }

    public void setStdev(double stdev) {
        this.stdev = stdev;
    }

    public String getTissue() {
        return tissue;
    }

    public double getExpValue() {
        return expValue;
    }

}
