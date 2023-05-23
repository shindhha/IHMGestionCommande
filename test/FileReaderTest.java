import main.java.modeles.Article;
import main.java.services.FileReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class FileReaderTest {
    /* Par défaut un fichier de configuration est prévue avec les colonnes :
    * - Numéro d'article
    * - Designation d'article
    * - Format des numéros de séries
    * - Liste des actions réalisable (OF / OC ...)
    * - Emplacement dans le magasin
    * - Url vers une image d'un qr code de configuration pour la scannette
    */
    private static final String path = "C:\\Users\\g.medard\\eclipse-workspace\\IHMGestionCommande\\test\\conf\\";
    @Test
    void testFileReader() {

        // Etant donné un fichier avec les colonnes basique de configuration

        File file = new File(path + "TestRef_articles1.csv");
        // Lors de la lecture de la première ligne du fichier
        ArrayList<String> header = null;
        try {
            header = FileReader.readHeader(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Alors on récupère la liste des colonnes disponnible
        assertEquals(header.get(0), "Numero");
        assertEquals(header.get(1), "Designation");
        assertEquals(header.get(2), "Format");
        assertEquals(header.get(3), "Actions");
        assertEquals(header.get(4), "Emplacement");
        assertEquals(header.get(5), "QrCode");

    }

    @Test
    void testReadConfigFile() {
        // Etant donné un fichier avec les colonnes basique de configuration et 3 articles renseigner
        File file = new File(path + "TestRef_articles1.csv");
        // Lors de la lecture de la première ligne du fichier
        HashMap<String, Article> articles = null;
        try {
            articles = FileReader.readArticleConfigFile(file);
        } catch (Exception e) {

        }
        // Alors on récupère la liste des colonnes disponnible
        assertEquals(articles.size(), 2);
        Article arcticle = articles.get("42400150");
        assertEquals(arcticle.getDesignation(), "DS_Camera Blue Next Network-E4P_FSD-8013-011");
        assertEquals("1",arcticle.getAttributs().get("QrCode"));
        assertEquals("C:\\Users\\g.medard\\eclipse-workspace\\IHMGestionCommande\\target\\classes\\main\\resources\\qrcodes/1.png",arcticle.getQrCode());
    }

    @Test
    void testFile() {
        // Etant donné un fichier avec les colonnes basique de configuration
        File file = new File(path + "TestRef_articles1.csv");
        // Lors de la lecture de la première ligne du fichier
        ArrayList<String> header = null;
        HashMap<String,Article> articles = null;
        Article article = null;
        try {
            header = FileReader.readHeader(file);
            articles = FileReader.readArticleConfigFile(file);
            article = articles.get("42400150");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("42400150",article.getAttributs().get("Numero"));
        assertEquals("DS_Camera Blue Next Network-E4P_FSD-8013-011",article.getAttributs().get("Designation"));
        assertEquals("cnnnn_nnnn",article.getAttributs().get("Format"));
        assertEquals("OF",article.getAttributs().get("Actions"));
        assertEquals("A-26-YB-02",article.getAttributs().get("Emplacement"));
        assertEquals("1",article.getAttributs().get("QrCode"));
    }
    @Test
    void TestArticle() {
        // Etant donné un fichier avec la colonne Numero
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Numero");
        // Et Avec les données suivantes
        String[] data = {"42400150"};
        // Lors de la création d'un article
        Article article = new Article(headers, data);
        // Alors on récupère les données
        assertEquals("42400150",article.getNumero());
    }
    @Test
    void writeConfigFile() throws FileNotFoundException {
        FileReader.writeConfigFile(new File(path + "TestConfigFile.csv"),"Fichier1","Fichier2");
        //String[] paths = FileReader.readConfigFile(new File(path + "TestConfigFile.csv"));
        //assertEquals("Fichier1",paths[0]);
        //assertEquals("Fichier2",paths[1]);
    }
}