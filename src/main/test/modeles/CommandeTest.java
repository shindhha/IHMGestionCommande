package modeles;

import exceptions.FormatInvalideException;
import modeles.Article;
import modeles.Commande;
import modeles.OF;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class CommandeTest {

    @Test
    void makeOutPutFile() {
        // TODO Vérifier le nom du fichier de sortie OF/OC
        // TODO Vérifier le contenu du fichier de sortie OF/OC
    }

    @Test
    void ajouterNumeroSerieDejaPresent() throws FormatInvalideException {

        // Etant donner un numéro de série déjà entrée dans la liste
        String numero = "TBAKE1037059";
        Article article = mock(Article.class);
        doReturn("^[a-zA-Z]{5}[0-9]{7}$").when(article).getFormat();
        doReturn(numero).when(article).getNumero();
        Commande currentCommande = new OF("A-26-YB-02",article,50,"5");
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
        Commande currentCommande = new OF("A-26-YB-02",article,1,"5");
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
        Commande currentCommande = new OF("A-26-YB-02",article,1,"5");
        // On attend qu'une erreur soit propagée lors de l'ajout du numéro de série
        Exception e = assertThrowsExactly(FormatInvalideException.class,() -> currentCommande.ajouterNumeroSerie(numero));
        assertEquals(e.getMessage() , "Le format du numéro de série est invalide !");
    }
    @Test
    void regex() {
        String numero = "1000000050%WCV11275    ";
        String regex = "[a-zA-Z]{3}[0-9]{5}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(numero);
        assertTrue(m.find());
        System.out.println(m.group());
    }
}