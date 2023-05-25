package modeles;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OF extends Commande {
    private Article article;
    public OF(String numero, Article article, int quantiteMax, String ligne) {
        super(numero, article, quantiteMax, ligne);
        this.article = article;
    }

    @Override
    public String getFileOutPutName() {
        return "OF-"+ numero + "-" + ligne +"L-"+ article.getNumero() + "-" +quantiteMax +".csv";
    }

    @Override
    public void makeOutPutFile(String pathOutPutFolder) throws IOException {
        File outputFile = new File(pathOutPutFolder + getFileOutPutName());
        FileWriter fileWriter = new FileWriter(outputFile);
        fileWriter.write("Numéro de commande : " + numero + ";");
        fileWriter.write("Numéro de ligne : " + ligne + ";");
        fileWriter.write("Article : " + article.getDesignation() + ";");
        fileWriter.write("Quantité : " + quantiteMax + ";");
        fileWriter.write("Personne ayant éffectué la commande : " + System.getProperty("user.name") + "\n");
        for (String numeroSerie : listNumeroSerie) {
            fileWriter.write(numero + ";");
            fileWriter.write(numeroSerie + "\n");
        }
        fileWriter.close();
    }
}
