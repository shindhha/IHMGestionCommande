package modeles;

import exceptions.FormatInvalideException;
import modeles.Article;
import modeles.Commande;
import modeles.OF;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class CommandeTest {


    @Test
    void ajouterNumeroSerieDejaPresent() throws FormatInvalideException {

        // Etant donner un numéro de série déjà entrée dans la liste
        String numero = "TBAKE1037059";
        Article article = mock(Article.class);
        doReturn("^[a-zA-Z]{5}[0-9]{7}$").when(article).getFormat();
        doReturn(numero).when(article).getNumero();
        Commande currentCommande = new OF("11122233_666",article,50,"5");
        currentCommande.ajouterNumeroSerie(numero);
        // On attend qu'une erreur soit propagée lors de l'ajout du numéro de série
        Exception e = assertThrowsExactly(IllegalStateException.class,() -> currentCommande.ajouterNumeroSerie(numero));
        assertEquals(e.getMessage() , "Le numéro de série a déjà été saisi !");
    }

    @Test
    void ajouterNumeroSerieMaxNbArticleAtteint() throws FormatInvalideException {

        // Etant donner le nombre de numéro de série maximum atteint
        String numero = "TBAKE1037059";
        Article article = mock(Article.class);
        doReturn("^[a-zA-Z]{5}[0-9]{7}$").when(article).getFormat();
        doReturn(numero).when(article).getNumero();
        Commande currentCommande = new OF("11122233_666",article,1,"5");
        currentCommande.ajouterNumeroSerie(numero);
        // On attend qu'une erreur soit propagée lors de l'ajout du numéro de série
        Exception e = assertThrowsExactly(IllegalStateException.class,() -> currentCommande.ajouterNumeroSerie(numero));
        assertEquals(e.getMessage() , "Vous avez atteint le nombre maximum d'article à saisir !");
    }

    @Test
    void ajouterNumeroSerieFormatInvalide() throws NoSuchFieldException, IllegalAccessException {

        // Etant donner un numéro de série qui ne correspond pas au format de l'article
        String numero = "TBAKE10379";
        Article article = mock(Article.class);
        doReturn("^[a-zA-Z]{5}[0-9]{7}$").when(article).getFormat();
        Commande currentCommande = new OF("11122233_666",article,1,"5");
        // On attend qu'une erreur soit propagée lors de l'ajout du numéro de série
        Exception e = assertThrowsExactly(FormatInvalideException.class,() -> currentCommande.ajouterNumeroSerie(numero));
        assertEquals(e.getMessage() , "Le format du numéro de série est invalide !");
    }
    @DisplayName("Test de la méthode makeOutPutFile sur un objet OF")
    @Test
    // Attention : lorque l'on appelle getNumero() sur le mock de l'article, il retourne null
    void makeOutPutFileOF() throws FormatInvalideException, IOException {
        // Etant donner une commande sur ordre de fabrication avec tous ses numéros de série
        Article article = mock(Article.class);
        doReturn("\\d").when(article).getFormat();
        doReturn("960000").when(article).getNumero();
        Commande currentCommande = new OF("11122233_666",article,5,"5");
        currentCommande.ajouterNumeroSerie("1");
        currentCommande.ajouterNumeroSerie("2");
        currentCommande.ajouterNumeroSerie("3");
        currentCommande.ajouterNumeroSerie("4");
        currentCommande.ajouterNumeroSerie("5");
        // Lorsque l'on appelle la méthode makeOutPutFile

        currentCommande.makeOutPutFile(getClass().getResource("../output/").getPath());
        // Alors on s'attend a ce qu'un fichier soit créer et nommer de la façon suivante :
        assertEquals("OF-11122233_666-5L-960000-5.csv",currentCommande.getFileOutPutName());
        // OF-<numero de l'OF>-<Numéro de la ligne>L-<Numéro de l'article>-<quantite>.csv
        File file = new File(getClass().getResource("../output/").getPath() + currentCommande.getFileOutPutName());
        Scanner fileReader = new Scanner(file);
        // Que sa première ligne soit au format suivant :
        // Numéro de commande; Numéro de ligne;Article;Quantite;Nom d'utilisateur du compte
        assertEquals("Numéro de commande : 11122233_666;Numéro de ligne : 5;Article : null;Quantité : 5;Personne ayant éffectué la commande : " + System.getProperty("user.name"),fileReader.nextLine());
        // Et que les lignes suivantes soient au format suivant :
        // Numéro de traitement;Numéro de série
        for (int i = 1; i <= 5; i++) {
            assertEquals("11122233_666" + ";"+ i,fileReader.nextLine());
        }
    }
    // Attention : lorque l'on appelle getNumero() sur le mock de l'article, il retourne null
    @DisplayName("Test de la méthode makeOutPutFile sur un objet OC")
    @Test
    void makeOutPutFileOC() throws FormatInvalideException, IOException {
        // Etant donner une commande sur ordre de fabrication avec tous ses numéros de série
        Article article = mock(Article.class);
        doReturn("\\d").when(article).getFormat();
        doReturn("960000").when(article).getNumero();
        Commande currentCommande = new OC("OC11122233",article,5,"5");
        currentCommande.ajouterNumeroSerie("1");
        currentCommande.ajouterNumeroSerie("2");
        currentCommande.ajouterNumeroSerie("3");
        currentCommande.ajouterNumeroSerie("4");
        currentCommande.ajouterNumeroSerie("5");
        // Lorsque l'on appelle la méthode makeOutPutFile

        currentCommande.makeOutPutFile(getClass().getResource("../output/").getPath());
        // Alors on s'attend a ce qu'un fichier soit créer et nommer de la façon suivante :
        assertEquals("OC-OC11122233-5L-960000-5.csv",currentCommande.getFileOutPutName());
        // OC-<numero de l'OC>-<Numéro de la ligne>L-<Numéro de l'article>-<quantite>.csv
        File file = new File(getClass().getResource("../output/").getPath() + currentCommande.getFileOutPutName());
        Scanner fileReader = new Scanner(file);
        // Que sa première ligne soit au format suivant :
        // Numéro de commande; Numéro de ligne;Article;Quantite;Nom d'utilisateur du compte
        assertEquals("Numéro de commande : OC11122233;Numéro de ligne : 5;Article : null;Quantité : 5;Personne ayant éffectué la commande : " + System.getProperty("user.name"),fileReader.nextLine());
        // Et que les lignes suivantes soient au format suivant :
        // Numéro de série
        for (int i = 1; i <= 5; i++) {
            assertEquals("" + i,fileReader.nextLine());
        }
    }

}