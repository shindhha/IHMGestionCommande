package com.example.ihmgestioncommande;

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
        for (int i = 0; i < header.size(); i++) {
            this.data.put(header.get(i), data[i]);
        }
    }



}
