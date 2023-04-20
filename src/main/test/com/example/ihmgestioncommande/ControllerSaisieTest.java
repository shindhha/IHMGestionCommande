package com.example.ihmgestioncommande;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class ControllerSaisieTest {

    @Test
    void initScene() {
    }

    @Test
    void loadArticle() {
    }

    @Test
    void showArticle() {
    }

    @Test
    void validerSaisie() {
    }

    @Test
    void verifierChamp() {
    }

    @Test
    void startSaisie() {
    }

    @Test
    void updateCompteur() {
    }
    @Test
    void testRegex() {
        boolean d =Pattern.matches("^[a-zA-Z]{5}[0-9]{7}$", "TBAKE1037059");
        assertTrue(d);
    }
}