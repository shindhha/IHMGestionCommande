package modeles;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OC extends Commande {
    public OC(String numero, Article article, int quantiteMax)throws IllegalArgumentException {
        super(numero, article, quantiteMax);
        if (numero.matches("[0-9]{8}_[0-9]{3}")) throw new IllegalArgumentException("Format du numéro de commande incohérent avec le choix de l'action");
        if (!numero.matches("OC[0-9]{8}")) throw new IllegalArgumentException("Le numéro de commande doit être de la forme OCXXXXXXXX");

    }

    @Override
    public String getFileOutPutName() {
        return "OC-"+ numero + "-" + article.getNumero() + "-" +quantiteMax + "-" + date;
    }

    @Override
    public void makeOutPutFile(String pathOutPutFolder) throws IOException {
        File outputFile = new File(pathOutPutFolder + "\\" + getFileOutPutName() + ".csv");
        FileWriter fileWriter = new FileWriter(outputFile,StandardCharsets.UTF_8);
        fileWriter.write("Numéro de commande : " + numero + ";");
        fileWriter.write("Article : " + article.getDesignation() + ";");
        fileWriter.write("Quantité : " + quantiteMax + ";");
        fileWriter.write("Personne ayant éffectué la commande : " + System.getProperty("user.name") + "\r\n");
        for (String numeroSerie : listNumeroSerie) {
            fileWriter.write(numeroSerie + "\r\n");
        }
        fileWriter.close();
    }
}
