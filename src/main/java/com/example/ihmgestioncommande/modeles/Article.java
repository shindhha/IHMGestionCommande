package com.example.ihmgestioncommande.modeles;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.text.FieldPosition;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Article {


    private HashMap<String, String> data;

    // Constructeur associant la liste des colonnes du fichier de configuration avec une série de données
    public Article(List<String> header, String[] data) {
        this.data = new HashMap<>();
        for (int i = 0; i < header.size() - 1; i++) {
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
}
