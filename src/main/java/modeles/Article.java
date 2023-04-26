package main.java.modeles;

import main.Launch;
import java.io.File;
import java.util.HashMap;
import java.util.List;
public class Article {


    private HashMap<String, String> data;

    /**
     * Prend en paramètre une liste de colonnes et une ligne de données et crée un objet Article.
     * @param header La liste des colonnes
     * @param data La ligne de données
     */
    public Article(List<String> header, String[] data) {
        this.data = new HashMap<>();
        for (int i = 0; i < header.size(); i++) {
            this.data.put(header.get(i), data[i]);
        }
    }

    @Override
    public String toString() {
        return data.get("Numero") + " " + data.get("Designation");
    }
    public String getNumero() {
        return data.get("Numero");
    }
    public String[] getActions() {
        return data.get("Actions").split("-");
    }
    public String getFormat() {
        return data.get("Format");
    }
    public String getDesignation() {
        return data.get("Designation");
    }
    public HashMap<String, String> getAttributs() {
        return data;
    }

    /**
     * @return Le path relatif a la class "GestionCommandeApplication" vers le fichier contenant le QR Code de l'article
     */
    public String getQrCode() {
        String nomQrCode = data.get("QrCode");
        File file = new File(Launch.class.getResource("resources/qrcodes").getFile());
        return file.getPath() + "/" + nomQrCode + ".png";
    }
}
