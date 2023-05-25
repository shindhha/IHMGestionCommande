package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import controllers.ControllerSaisie;
import exceptions.FormatInvalideException;
import modeles.Article;
import modeles.Commande;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static controllers.ControllerSaisieTest.InputJavaFx.*;
class ControllerSaisieTest extends ApplicationTest {
    public String[] fieldToMock = {
            "listArticles","listActions","inputNbArticle","inputNumeroSerieContainer","btnTerminerSaisie",
            "btnCommencerSaisie","btnAnnulerSaisie","compteurNbProduitSaisie","boiteErreur","listProduitsSaisie",
            "inputNoCommande","qrCodeView","inputNoLigne"
    };

    enum InputJavaFx {
        listArticles("listArticles",ComboBox.class),listActions("listActions",ComboBox.class),inputNbArticle("inputNbArticle",TextField.class),
        inputNumeroSerieContainer("inputNumeroSerieContainer",VBox.class),btnTerminerSaisie("btnTerminerSaisie",Button.class),
        btnCommencerSaisie("btnCommencerSaisie",Button.class),btnAnnulerSaisie("btnAnnulerSaisie",Button.class),
        compteurNbProduitSaisie("compteurNbProduitSaisie",Text.class), boiteErreur("boiteErreur",Text.class),
        listProduitsSaisie("listProduitsSaisie",VBox.class), inputNoCommande("inputNoCommande",TextField.class),
        qrCodeView("qrCodeView",ImageView.class),inputNoLigne("inputNoLigne",TextField.class),inputNumeroSerie("inputNumeroSerie",TextField.class);
        private String fieldName;
        @Mock
        private Object mock;
        InputJavaFx(String fieldName, Class<?> type) {
            this.fieldName = fieldName;
            mock = mock(type);

        }


    }


    void mockField(InputJavaFx... fields) throws NoSuchFieldException {
        for (InputJavaFx inputFx : fields) {
            String s = inputFx.fieldName;
            Field field = ControllerSaisie.class.getDeclaredField(s);
            try {
                field.setAccessible(true);
                field.set(controllerTest, inputFx.mock);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    ControllerSaisie controllerTest;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/vue.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Gestions Commandes");
        stage.setScene(scene);
        stage.show();
        controllerTest = fxmlLoader.getController();

        controllerTest.initScene(scene);
    }

    private List<String> colonnes = Arrays.asList("Numero","Designation","Format","Actions","QrCode");
    private String[][] defaultData = {
            {"42400150","DS_Camera Blue Next Network-E4P_FSD-8013-011","cnnnn_nnnn","OF","A-26-YB-02", "1"},
            {"42400131","CAMERA IP 1080P EN50155 PoE MICROPHONE LENS-6.0MM","cnnnn_nnnn","OF","A-26-YB-02", "1"},
            {"42400138", "Vport P06HC-1MP CAM36-CT (Cubic) V1.0.0", "^[a-zA-Z]{5}[0-9]{7}$", "OF-OC","A-36-GB-07","autres"}

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
        controllerTest.ajouterNumeroSerie("TBAKE1037059");

        assertTrue(controllerTest.getErrorMessage().isEmpty());
        assertEquals(1,controllerTest.getListProduitsSaisie().getChildren().size());
        assertEquals("1/50",compteur.getText());
        assertTrue(inputNumeroSerie.getText().isEmpty());

    }



    @Test
    void ajouterNumeroSerieDejaAjouterTest() throws FormatInvalideException, NoSuchFieldException, IllegalAccessException {
        mockField(inputNumeroSerie);
        // Etant donner le nombre de numéro de série maximum atteint
        String numero = "TBAKE1037059";
        Commande currentCommande = mock(Commande.class);
        IllegalStateException exception = mock(IllegalStateException.class);
        doThrow(exception).when(currentCommande).ajouterNumeroSerie(numero);
        doReturn("Vous avez atteint le nombre maximum d'article à saisir !").when(exception).getMessage();
        controllerTest.currentCommande = currentCommande;
        // On attend que le message d'erreur soit afficher

        controllerTest.ajouterNumeroSerie(numero);
        assertEquals("Vous avez atteint le nombre maximum d'article à saisir !",controllerTest.getErrorMessage());
    }
    @Test
    void ajouterNumeroSerieMaxNbArticleAtteint() throws FormatInvalideException, NoSuchFieldException, IllegalAccessException {
        mockField(inputNumeroSerie);
        // Etant donner le nombre de numéro de série maximum atteint
        String numero = "TBAKE1037059";
        Commande currentCommande = mock(Commande.class);
        IllegalStateException exception = mock(IllegalStateException.class);
        doThrow(exception).when(currentCommande).ajouterNumeroSerie(numero);
        doReturn("Le numéro de série a déjà été saisi !").when(exception).getMessage();
        controllerTest.currentCommande = currentCommande;
        controllerTest.ajouterNumeroSerie(numero);
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
    void commencerSaisieTest() throws NoSuchFieldException {
        // Etant donner les informations d'une commande ayant été saisie par l'utilisateur
        mockField(inputNoCommande,inputNoLigne,inputNbArticle,listArticles,listActions);
        Article article = mock(Article.class);

        doReturn("123456789").when( (TextField) inputNoCommande.mock).getText();
        doReturn("1").when( (TextField) inputNoLigne.mock).getText();
        doReturn("1").when( (TextField) inputNbArticle.mock).getText();
        doReturn("OC").when( (ComboBox) listActions.mock).getValue();
        doReturn("42400131 VPort P06-2M60M V1.2.2").when( (ComboBox) listArticles.mock).getValue();
        doReturn("[a-zA-Z]{8}[0-9]{3}").when(article).getFormat();
        // Lors de la saisie de la commande
        controllerTest = spy(controllerTest);
        doReturn(article).when(controllerTest).getSelectedArticle();

        assertNull(controllerTest.getInputNumeroSerie());
        assertNull(controllerTest.currentCommande);
        controllerTest.commencerSaisie();
        // Alors on attend a ce que l'ihm soit mise à jour pour permettre la saisie des numéros de série
        verify((TextField) inputNoCommande.mock).setDisable(true);
        verify((TextField) inputNoLigne.mock).setDisable(true);
        verify((TextField) inputNbArticle.mock).setDisable(true);
        verify((ComboBox) listArticles.mock).setDisable(true);
        verify((ComboBox) listActions.mock).setDisable(true);
        assertInstanceOf(TextField.class,controllerTest.getInputNumeroSerie());
        assertNotNull(controllerTest.getInputNumeroSerie());
        assertInstanceOf(Commande.class,controllerTest.currentCommande);
        assertNotNull(controllerTest.currentCommande);



    }

    @Test
    void changerFichierArticlesTest() {
        controllerTest.changerFichierArticles();

    }
    @DisplayName("Test de la méthode ajouterNumeroSerie")
    @Test
    void testSoundError() throws JavaLayerException {
        ControllerSaisie controllerTest = new ControllerSaisie();
        AdvancedPlayer player = new AdvancedPlayer(getClass().getResourceAsStream("/sound/beep-beep-6151.mp3"));
        player.play();
    }

}