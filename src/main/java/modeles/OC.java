package modeles;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OC extends Commande {
    public OC(String numero, Article article, int quantiteMax, String ligne) {
        super(numero, article, quantiteMax, ligne);
    }

    @Override
    public String getFileOutPutName() {
        return "OC-"+ numero + "-" + ligne +"L-"+ article.getNumero() + "-" +quantiteMax +".csv";
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
            fileWriter.write(numeroSerie + "\n");
        }
        fileWriter.close();
    }
}
