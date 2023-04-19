package com.example.ihmgestioncommande;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

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
        String name = "ressources/conf/configuration.cnfg";
        File file = new File(name);
        // Lors de la lecture de la première ligne du fichier
        ArrayList<String> header = null;
        try {
            header = FileReader.readHeader(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Alors on récupère la liste des colonnes disponnible
        assertEquals(header.get(0), "Numéro");
        assertEquals(header.get(1), "Designation");
        assertEquals(header.get(2), "Format");
        assertEquals(header.get(3), "Actions");
        assertEquals(header.get(4), "Emplacement");
        assertEquals(header.get(5), "QrCode");

    }
}