package com.example.ihmgestioncommande;

import com.example.ihmgestioncommande.modeles.Article;
import com.example.ihmgestioncommande.services.FileReader;
import org.junit.jupiter.api.Test;

import java.io.File;
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
    @Test
    void testFileReader() {

        // Etant donné un fichier avec les colonnes basique de configuration
        String name = "src/main/test/com/example/ihmgestioncommande/conf/Ref_articles.csv";
        File file = new File(name);
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
        String name = "src/main/test/com/example/ihmgestioncommande/conf/Ref_articles.csv";
        File file = new File(name);
        // Lors de la lecture de la première ligne du fichier
        HashMap<String, Article> articles = null;
        try {
            articles = FileReader.readConfigFile(file);
        } catch (Exception e) {

        }
        // Alors on récupère la liste des colonnes disponnible
        assertEquals(articles.size(), 2);
        Article arcticle = articles.get("42400150");
        assertEquals(arcticle.getDesignation(), "DS_Camera Blue Next Network-E4P_FSD-8013-011");

    }
}