package modeles;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OC extends Commande {
    public OC(String numero, Article article, int quantiteMax, String ligne)throws IllegalArgumentException {
        super(numero, article, quantiteMax, ligne);
        if (numero.matches("[0-9]{8}_[0-9]{3}")) throw new IllegalArgumentException("Format du numéro de commande incohérent avec le choix de l'action");
        if (!numero.matches("OC[0-9]{8}")) throw new IllegalArgumentException("Le numéro de commande doit être de la forme OCXXXXXXXX");

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
