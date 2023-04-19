package com.example.ihmgestioncommande.services;

import com.example.ihmgestioncommande.modeles.Article;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class FileReader {

    // Fonction permettant de lire les entête d'un fichier
    public static ArrayList<String> readHeader(File file) throws FileNotFoundException {
        ArrayList<String> header = new ArrayList<>();
        Scanner scanner = new Scanner(file);
        String line = scanner.nextLine();
        String[] data = line.split(";");
        for (String s : data) {
            header.add(s);
        }
        return header;
    }

    // Lis le fichier de configuration et retourne une liste d'articles
    public static HashMap<String,Article> readConfigFile(File file) throws FileNotFoundException {
        HashMap<String,Article> articles = new HashMap<>();
        ArrayList<String> header = readHeader(file);
        Scanner scanner = new Scanner(file);
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] data = line.split(";");
            Article article = new Article(header, data);
            articles.put(article.getNumero(), article);
        }
        return articles;
    }

}
