package main.java.services;


import main.java.modeles.Article;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class FileReader {

    /**
     * Lis un fichier de configuration et retourne une liste des colonnes.
     * @param file Le fichier de configuration
     * @return La liste des colonnes
     * @throws FileNotFoundException Si le fichier n'existe pas
     */
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

    /**
     * Lis un fichier de configuration et retourne une HashMap associant le numéro d'article en tant que clé
     * à un objet Article.
     * @param file Le fichier de configuration
     * @return La HashMap associant le numéro d'article en tant que clé à un objet Article
     * @throws FileNotFoundException Si le fichier n'existe pas
     */
    public static HashMap<String,Article> readConfigFile(File file) throws FileNotFoundException {
        HashMap<String, Article> articles = new HashMap<>();
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
