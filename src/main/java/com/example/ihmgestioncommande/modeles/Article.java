package com.example.ihmgestioncommande.modeles;

import com.example.ihmgestioncommande.GestionCommandeApplication;

import java.io.File;
import java.util.HashMap;
import java.util.List;
public class Article {


    private HashMap<String, String> data;

    // Constructeur associant la liste des colonnes du fichier de configuration avec une série de données
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
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Article))
            return false;
        obj = (Article) obj;
        for (String key : data.keySet()) {

        }
        return true;
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

    public String getQrCode() {
        int id;
        id = Integer.parseInt(data.get("QrCode"));
        File folder = new File(GestionCommandeApplication.class.getResource("qrcodes").getPath());
        return folder.listFiles()[id].getPath();
    }
}
