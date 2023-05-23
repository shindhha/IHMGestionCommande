import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Launch;
import main.java.controllers.ControllerSaisie;
import main.java.exceptions.FormatInvalideException;
import main.java.modeles.Article;
import main.java.modeles.Commande;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.runners.model.MultipleFailureException.assertEmpty;
import static org.mockito.Mockito.*;

class ControllerSaisieTest extends ApplicationTest {
    ControllerSaisie controllerTest;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Launch.class.getResource("resources/views/vue.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Gestions Commandes");
        stage.setScene(scene);
        stage.show();
        controllerTest = fxmlLoader.getController();

        controllerTest.initScene(scene);
    }

    private List<String> colonnes = Arrays.asList("Numero","Designation","Format","Actions","QrCode");
    private String[][] defaultData = {
            {"42400150"	,"DS_Camera Blue Next Network-E4P_FSD-8013-011"	,"cnnnn_nnnn"	,"OF"	,"A-26-YB-02",	"1"	},
            {"42400131"	,"CAMERA IP 1080P EN50155 PoE MICROPHONE LENS-6.0MM"	,"cnnnn_nnnn"	,"OF"	,"A-26-YB-02",	"1"	},
            {"42400138", "Vport P06HC-1MP CAM36-CT (Cubic) V1.0.0",	"^[a-zA-Z]{5}[0-9]{7}$",	"OF-OC"	,"A-36-GB-07","autres"}

    };

    HashMap<String,Article> getArticleList(List<String> colonnes, String[][] datas) {

        HashMap<String,Article> articles = new HashMap<>();
        for (String[] data : datas) {
            Article article = new Article(colonnes,data);
            articles.put(article.getDesignation(),article);
        }
        return articles;
    }

    HashMap<String,Article> getDefaultArticleList() {
        return getArticleList(colonnes,defaultData);
    }

    @Test
    void ajouterNumeroSerieTest() throws FormatInvalideException {
        Commande currentCommande = mock(Commande.class);
        TextField inputNumeroSerie = new TextField();
        Text compteur = spy(new Text());
        ControllerSaisie controllerTest = new ControllerSaisie(inputNumeroSerie,compteur);
        doReturn(50).when(currentCommande).getNbMaxNumeroSerie();
        doReturn(1).when(currentCommande).size();
        controllerTest.currentCommande = currentCommande;
        doNothing().when(currentCommande).ajouterNumeroSerie(anyString());
        controllerTest.ajouterNumeroSerie("TBAKE1037059");

        assertTrue(controllerTest.getErrorMessage().isEmpty());
        assertEquals(1,controllerTest.getListProduitsSaisie().getChildren().size());
        assertEquals("1/50",compteur.getText());
        assertTrue(inputNumeroSerie.getText().isEmpty());

    }



    @Test
    void ajouterNumeroSerieDejaAjouterTest() throws FormatInvalideException {

        // Etant donner le nombre de numéro de série maximum atteint
        String numero = "TBAKE1037059";
        Commande currentCommande = mock(Commande.class);
        IllegalStateException exception = mock(IllegalStateException.class);
        doThrow(exception).when(currentCommande).ajouterNumeroSerie(numero);
        doReturn("Vous avez atteint le nombre maximum d'article à saisir !").when(exception).getMessage();
        controllerTest.currentCommande = currentCommande;
        // On attend qu'une erreur soit propagée lors de l'ajout du numéro de série
        controllerTest.ajouterNumeroSerie(numero);
        assertEquals("Vous avez atteint le nombre maximum d'article à saisir !",controllerTest.getErrorMessage());
    }
    @Test
    void ajouterNumeroSerieMaxNbArticleAtteint() throws FormatInvalideException {

        // Etant donner le nombre de numéro de série maximum atteint
        String numero = "TBAKE1037059";
        Commande currentCommande = mock(Commande.class);
        IllegalStateException exception = mock(IllegalStateException.class);
        doThrow(exception).when(currentCommande).ajouterNumeroSerie(numero);
        doReturn("Le numéro de série a déjà été saisi !").when(exception).getMessage();
        controllerTest.currentCommande = currentCommande;
        // On attend qu'une erreur soit propagée lors de l'ajout du numéro de série
        controllerTest.ajouterNumeroSerie(numero);
        assertEquals("Le numéro de série a déjà été saisi !",controllerTest.getErrorMessage());
    }


    @Test
    void filtrerArticleMotPasContenueTest() {
        // Etant donner un mot rechercher par l'utilisateur et une liste d'article dont le mot n'est contenue par aucun des articles
        String motRechercher = "Mot inconue";
        HashMap<String, Article> articles = getDefaultArticleList();
        // Lors de la recherche d'article
        ControllerSaisie controllerTest = new ControllerSaisie();
        HashMap<String,Article> articlesFiltrer = controllerTest.filtrerArticle(motRechercher,articles);
        // Alors on attend que la liste d'article filtrer soit vide
        assertEquals(articlesFiltrer.size(),0);
    }
    @Test
    void filtrerArticleMotContenueDansNumero() {
        // Etant donner un mot rechercher par l'utilisateur étant compris dans le numero de l'un des articles
        String motRechercher = "42400150";
        HashMap<String,Article> articles = getDefaultArticleList();
        // Lors de la recherche d'article
        ControllerSaisie controllerTest = new ControllerSaisie();
        HashMap<String,Article> articlesFiltrer = controllerTest.filtrerArticle(motRechercher,articles);
        // Alors on retrouve le mot dans la liste d'article filtrer
        assertNotNull(articlesFiltrer.get(motRechercher));
        assertEquals(1,articlesFiltrer.size());
    }
    @Test
    void filtrerArticleMotContenueDansDesignation() {
        // Etant donner un mot rechercher par l'utilisateur étant compris dans le numero de l'un des articles
        String motRechercher = "DS_camera";
        HashMap<String,Article> articles = getDefaultArticleList();
        // Lors de la recherche d'article
        ControllerSaisie controllerTest = new ControllerSaisie();
        HashMap<String,Article> articlesFiltrer = controllerTest.filtrerArticle(motRechercher,articles);
        // Alors on retrouve le mot dans la liste d'article filtrer
        assertNotNull(articlesFiltrer.get("42400150"));
        assertEquals(1,articlesFiltrer.size());
    }

    @Test
    void filtrerArticleCaseSensitive() {
        // Etant donner un mot rechercher par l'utilisateur étant compris dans le numero de l'un des articles
        String motMajuscule = "DS_CAMERA";
        String motMinuscule = "ds_camera";
        HashMap<String,Article> articles = getDefaultArticleList();
        // Lors de la recherche d'article
        ControllerSaisie controllerTest = new ControllerSaisie();
        HashMap<String,Article> articlesFiltrerMajuscule = controllerTest.filtrerArticle(motMajuscule,articles);
        HashMap<String,Article> articlesFiltrerMinuscule = controllerTest.filtrerArticle(motMinuscule,articles);
        // Alors on retrouve le mot dans les deux listes d'article filtrer
        assertNotNull(articlesFiltrerMajuscule.get("42400150"));
        assertEquals(1,articlesFiltrerMajuscule.size());

        assertNotNull(articlesFiltrerMinuscule.get("42400150"));
        assertEquals(1,articlesFiltrerMinuscule.size());

    }

    @Test
    void commencerSaisieTest() {
        // Etant donner les informations d'une commande ayant été saisie par l'utilisateur
        String numeroCommande = "123456789";
        String numeroLigne = "1";
        String nombreArticle = "1";
        Article article = mock(Article.class);
        String action = "OC";
        TextField inputNumeroCommande = mock(TextField.class);
        TextField inputNumeroLigne = mock(TextField.class);
        TextField inputNbArticle = mock(TextField.class);
        ComboBox inputArticle = mock(ComboBox.class);
        ComboBox inputAction = mock(ComboBox.class);
        doReturn(numeroCommande).when(inputNumeroCommande).getText();
        doReturn(numeroLigne).when(inputNumeroLigne).getText();
        doReturn(nombreArticle).when(inputNbArticle).getText();
        doReturn(action).when(inputAction).getValue();
        doReturn("42400131 VPort P06-2M60M V1.2.2").when(inputArticle).getValue();
        // Lors de la saisie de la commande
        ControllerSaisie controllerTest = spy(new ControllerSaisie(inputNumeroCommande,inputNumeroLigne,inputNbArticle,inputArticle,inputAction));
        doReturn(article).when(controllerTest).getSelectedArticle();
        assertNull(controllerTest.getInputNumeroSerie());
        assertNull(controllerTest.currentCommande);
        controllerTest.commencerSaisie();
        // Alors on attend a ce que l'ihm soit mise à jour pour permettre la saisie des numéros de série
        verify(inputNumeroCommande).setDisable(true);
        verify(inputNumeroLigne).setDisable(true);
        verify(inputNbArticle).setDisable(true);
        verify(inputArticle).setDisable(true);
        verify(inputAction).setDisable(true);
        assertInstanceOf(TextField.class,controllerTest.getInputNumeroSerie());
        assertNotNull(controllerTest.getInputNumeroSerie());
        assertInstanceOf(Commande.class,controllerTest.currentCommande);
        assertNotNull(controllerTest.currentCommande);



    }

    @Test
    void changerFichierArticlesTest() {
        ControllerSaisie controllerTest = new ControllerSaisie();
        controllerTest.changerFichierArticles();

    }

}