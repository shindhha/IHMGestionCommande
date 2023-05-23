package main;

import main.java.GestionCommandeApplication;

public class Launch {
    public static String listArticleConfiguration;
    public static final String nombreMaxArticleDefaut = "1";
    public static String pathOutPutFolder;
    public static final String defaultPathOutPutFolder = "src/main/resources/output/";

    public static final String configurationPath = "resources/config/configurationPath.csv";

    public static void main(String[] args) {
        GestionCommandeApplication.main(args);
    }


}
