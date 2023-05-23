package main.java.services;


import main.Launch;
import main.java.modeles.Article;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
    public static HashMap<String,Article> readArticleConfigFile(File file) throws FileNotFoundException {
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

    public static String[] readConfigFile(BufferedReader file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String line = scanner.nextLine();
        String[] params = line.split(";");
        return params;
    }

    public static void writeConfigFile(File file, String... params) {
        try {
            FileWriter writer = new FileWriter(file);
            String line = "";
            for (String param : params) {
                line += param + ";";
            }
            writer.write(line);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void writeConfigFile(String... params) {
        writeConfigFile(new File(Launch.configurationPath), params);
    }

}
