package services;


import modeles.Article;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
public class Configuration {

    public String pathDocumentation;
    public String pathQrCodes;
    public String listArticleConfiguration;
    public String pathOutPutFolder;
    private String pathConfiguration;
    private final String defaultPathConfiguration = "resources/config/configurationPath.csv";
    private final String applicationName = "IHMGestionCommande";
    private final String configFileName = "configuration.csv";
    private File configurationFile;


    public Configuration(String pathConfiguration) throws FileNotFoundException {
        this.pathConfiguration = pathConfiguration;
        configurationFile = new File(pathConfiguration);
        readConfigFile(configurationFile);
    }

    public Configuration() throws IOException {
        // On récupère le chemin de configuration dans le système pour l'utilisateur courant.
        Path pathPrograms = Path.of(System.getProperty("user.home"));
        // On crée le chemin vers le dossier de configuration de l'application.
        pathConfiguration = pathPrograms + "/" + applicationName;
        Files.createDirectories(pathPrograms.resolve(pathConfiguration));
        configurationFile = new File(pathConfiguration + "/" + configFileName);
        // Si aucune configuration n'est trouvable dans le système.
        if (!configurationFile.exists())
        {
            // On copie la configuration par défaut dans le système.
            Files.copy(getClass().getResourceAsStream(defaultPathConfiguration), Path.of(configurationFile.getPath()));
        }
        // On lit le fichier de configuration.
        readConfigFile(configurationFile);
    }


    /**
     * Lis un fichier de configuration et retourne une liste des colonnes.
     * @param file Le fichier de configuration
     * @return La liste des colonnes
     * @throws FileNotFoundException Si le fichier n'existe pas
     */
    public ArrayList<String> readHeader(File file) throws FileNotFoundException {
        ArrayList<String> header = new ArrayList<>();
        Scanner scanner = new Scanner(file);
        String line = scanner.nextLine();
        String[] data = line.split(";");
        for (String s : data) {
            header.add(s);
        }
        return header;
    }

    public void deleteConfigFile() {
        configurationFile.delete();
    }

    /**
     * Lis un fichier de configuration et retourne une HashMap associant le numéro d'article en tant que clé
     * à un objet Article.
     * @param file Le fichier de configuration
     * @return La HashMap associant le numéro d'article en tant que clé à un objet Article
     * @throws FileNotFoundException Si le fichier n'existe pas
     */
    public HashMap<String,Article> readArticleConfigFile(File file) throws FileNotFoundException {
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

    public HashMap<String,Article> readArticleConfigFile() throws FileNotFoundException {
        return readArticleConfigFile(new File(listArticleConfiguration));
    }

    public String[] readConfigFile(File file) throws FileNotFoundException , ArrayIndexOutOfBoundsException {
        Scanner scanner = new Scanner(file);
        String line = scanner.nextLine();
        String[] params = line.split(";");
        pathOutPutFolder = params[0];
        listArticleConfiguration = params[1];
        pathDocumentation = params[2];
        pathQrCodes = params[3];
        return params;
    }

    public void writeConfigFile(File file, String... params) {

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
    public void writeConfigFile(String... params) {
        writeConfigFile(configurationFile, params);
    }
    public void writeConfigFile() {
        writeConfigFile(pathOutPutFolder, listArticleConfiguration, pathDocumentation, pathQrCodes);
    }

}
