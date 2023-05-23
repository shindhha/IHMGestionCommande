package main.java.modeles;

import main.Launch;
import main.java.GestionCommandeApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OF extends Commande {

    public OF(String numero, Article article, int quantiteMax, String ligne) {
        super(numero, article, quantiteMax, ligne);
    }

    @Override
    public String getFileOutPutName() {
        return "OF-"+ numero + "-" + ligne +"-"+ article.getNumero() + "-" +quantiteMax +".csv";
    }

    @Override
    public void makeOutPutFile() throws IOException {
        File outputFile = new File(Launch.pathOutPutFolder + getFileOutPutName());
        FileWriter fileWriter = new FileWriter(outputFile);
        fileWriter.write("Article : " + article.getDesignation() + ";");
        fileWriter.write("Numéro de commande : " + numero + ";");
        fileWriter.write("Personne ayant éffectué la commande : " + System.getProperty("user.name") + "\n");
        for (String numeroSerie : listNumeroSerie) {
            fileWriter.write(numero + ";");
            fileWriter.write(numeroSerie + "\n");
        }
        fileWriter.close();
    }
}
