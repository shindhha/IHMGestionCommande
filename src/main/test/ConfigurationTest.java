import modeles.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {
    /* Par défaut un fichier de configuration est prévue avec les colonnes :
    * - Numéro d'article
    * - Designation d'article
    * - Format des numéros de séries
    * - Liste des actions réalisable (OF / OC ...)
    * - Emplacement dans le magasin
    * - Url vers une image d'un qr code de configuration pour la scannette
    */
    private Configuration configuration;
    @BeforeEach
    void setUp() throws IOException {
        configuration = new Configuration(getClass().getResource("ConfigTest1.csv").getPath());
    }
    @Test
    void testFileReader() {

        // Etant donné un fichier avec les colonnes basique de configuration

        File file = new File(getClass().getResource("articles/configurationArticle.csv").getPath());
        // Lors de la lecture de la première ligne du fichier
        ArrayList<String> header = null;
        try {
            header = configuration.readHeader(file);
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
        File file = new File(getClass().getResource("articles/configurationArticle.csv").getPath());

        // Lors de la lecture de la première ligne du fichier
        HashMap<String, Article> articles = null;
        try {
            articles = configuration.readArticleConfigFile(file);
        } catch (Exception e) {

        }
        // Alors on récupère la liste des colonnes disponnible
        assertEquals(3, articles.size());
        Article arcticle = articles.get("95006177");
        assertNotNull(arcticle);
        assertEquals("CAMERA-DOME-DIRECTIVE-VP-TER",arcticle.getDesignation());
        assertEquals("autres.png",arcticle.getQrCode());
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
        String path = getClass().getResource("").getPath() + "/ConfigTestTemp.csv";
        configuration.writeConfigFile(new File(path),"Fichier1","Fichier2","Fichier3","Fichier4");
        String[] paths = configuration.readConfigFile(new File(path));
        assertEquals("Fichier1",paths[0]);
        assertEquals("Fichier2",paths[1]);
        assertEquals("Fichier3",paths[2]);
        assertEquals("Fichier4",paths[3]);
    }

    @Test
    void createConfigFile() throws IOException {
        // Etant donner un utilisateur lançant l'application pour la premiere fois et n'ayant pas de fichier de configuration

        // Quand on créer l'objet configuration
        Configuration configuration = new Configuration();

        // Alors une copie du fichier de configuration interne est créer dans le dossier utilisateur window
        File file = new File(System.getProperty("user.home") + "\\IHMGestionCommande\\configuration.csv");
        assertTrue(file.exists());
        configuration.deleteConfigFile();
    }
}