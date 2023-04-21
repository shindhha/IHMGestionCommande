package com.example.ihmgestioncommande;

import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        // Etant donner un numéro de série déjà entrée dans la liste
        String numero = "TBAKE1037059";
        TextField mock = mock(TextField.class);
        ArrayList<String> numeros = new ArrayList<>(Arrays.asList("TBAKE1037059"));
        ControllerSaisie controllerTest = new ControllerSaisie(numeros,2,mock);
        // On attend qu'une erreur soit propagée lors de l'ajout du numéro de série
        Exception e = assertThrowsExactly(IllegalStateException.class,() -> controllerTest.validerSaisie());
        assertEquals(e.getMessage() , "Le numéro de série a déjà été saisi");
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