package controllers;

import exceptions.FormatInvalideException;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import modeles.Article;
import modeles.Commande;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.osgi.service.TestFx;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class ControllerSaisieTest extends ApplicationTest {


    @Spy
    private ComboBox<String> listArticles;
    @Spy
    private ComboBox<String> listActions;
    @Spy
    private TextField inputNbArticle;
    @Spy
    private VBox inputNumeroSerieContainer;
    @Mock
    private Button btnTerminerSaisie;
    @Mock
    private Button btnCommencerSaisie;
    @Mock
    private Button btnAnnulerSaisie;
    @Spy
    private Text compteurNbProduitSaisie;
    @Spy
    private Text boiteErreur;
    @Spy
    private VBox listProduitsSaisie;
    @Spy
    private TextField inputNoCommande;
    @Mock
    private ImageView qrCodeView;
    @Spy
    private TextField inputNoLigne;
    @Mock
    private MenuButton outilsConfig;
    @Spy
    private TextField inputNumeroSerie;
    @Spy
    private StringBuilder mot_recherche_article;
    private Scene primaryScene;


    @InjectMocks
    private ControllerSaisie controllerTest;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/vue.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Gestions Commandes");
        stage.setScene(scene);
        stage.show();
        controllerTest = fxmlLoader.getController();

        controllerTest.initScene(scene);
        primaryScene = scene;
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
    void ajouterNumeroSerie() throws FormatInvalideException {
        MockitoAnnotations.openMocks(this);
        Commande currentCommande = mock(Commande.class);
        doReturn(50).when(currentCommande).getNbMaxNumeroSerie();
        doReturn(1).when(currentCommande).size();
        controllerTest.currentCommande = currentCommande;
        controllerTest.ajouterNumeroSerie("TBAKE1037059");

        assertTrue(controllerTest.getErrorMessage().isEmpty());
        assertEquals(1,controllerTest.getListProduitsSaisie().getChildren().size());
        assertEquals("1/50",compteurNbProduitSaisie.getText());
        assertTrue(inputNumeroSerie.getText().isEmpty());

    }

    @Test
    void ajouterNumeroSerieDejaAjouter() throws FormatInvalideException, NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
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
        MockitoAnnotations.openMocks(this);
        // Etant donner le nombre de numéro de série maximum atteint
        String numero = "TBAKE1037059";
        Commande currentCommande = mock(Commande.class);
        IllegalStateException exception = mock(IllegalStateException.class);
        doThrow(exception).when(currentCommande).ajouterNumeroSerie(numero);
        doReturn("Le numéro de série a déjà été saisi !").when(exception).getMessage();
        controllerTest.currentCommande = currentCommande;
        doNothing().when(inputNumeroSerie).clear();
        controllerTest.ajouterNumeroSerie(numero);
        // On attend qu'une erreur soit propagée lors de l'ajout du numéro de série

        controllerTest.ajouterNumeroSerie(numero);
        assertEquals("Le numéro de série a déjà été saisi !",controllerTest.getErrorMessage());
    }


    @Test
    void filtrerArticleMotPasContenue() {
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
    void commencerSaisie() throws NoSuchFieldException {
        MockitoAnnotations.openMocks(this);
        // Etant donner les informations d'une commande ayant été saisie par l'utilisateur
        Article article = mock(Article.class);

        doReturn("123456789").when(inputNoCommande).getText();
        doReturn("1").when(inputNoLigne).getText();
        doReturn("1").when(inputNbArticle).getText();
        doReturn("OC").when(listActions).getValue();
        doReturn("42400131 VPort P06-2M60M V1.2.2").when(listArticles).getValue();
        doReturn("[a-zA-Z]{8}[0-9]{3}").when(article).getFormat();
        // Lors de la saisie de la commande
        controllerTest = spy(controllerTest);
        doReturn(article).when(controllerTest).getSelectedArticle();

        controllerTest.commencerSaisie();
        // Alors on attend a ce que l'ihm soit mise à jour pour permettre la saisie des numéros de série
        verify(listArticles).setDisable(true);
        verify(listActions).setDisable(true);
        verify(inputNbArticle).setDisable(true);
        verify(btnCommencerSaisie).setDisable(true);
        verify(inputNoCommande).setDisable(true);
        verify(inputNoLigne).setDisable(true);
        verify(btnAnnulerSaisie).setDisable(false);
    }

    @Test
    void commencerSaisieNbNumeroSerieInvalide() throws NoSuchFieldException {
        MockitoAnnotations.openMocks(this);
        // Etant donner les informations d'une commande ayant été saisie par l'utilisateur
        Article article = mock(Article.class);

        doReturn("123456789").when(inputNoCommande).getText();
        doReturn("1").when(inputNoLigne).getText();
        doReturn("deux").when(inputNbArticle).getText();
        doReturn("OC").when(listActions).getValue();
        doReturn("42400131 VPort P06-2M60M V1.2.2").when(listArticles).getValue();
        doReturn("[a-zA-Z]{8}[0-9]{3}").when(article).getFormat();
        // Lors de la saisie de la commande
        controllerTest = spy(controllerTest);
        doReturn(article).when(controllerTest).getSelectedArticle();

        controllerTest.commencerSaisie();
        // Alors on attend a ce que l'ihm soit mise à jour pour permettre la saisie des numéros de série
        verify(listArticles,never()).setDisable(true);
        verify(listActions,never()).setDisable(true);
        verify(inputNbArticle,never()).setDisable(true);
        verify(btnCommencerSaisie,never()).setDisable(true);
        verify(btnAnnulerSaisie,never()).setDisable(false);
        verify(inputNoLigne,never()).setDisable(true);
        verify(inputNoLigne,never()).setDisable(true);
        verify(inputNumeroSerieContainer,never()).getChildren();
        verify(boiteErreur).setText("Le nombre d'article doit être un nombre");
    }

    @Test
    void commencerSaisieListArticleVide() throws NoSuchFieldException {
        MockitoAnnotations.openMocks(this);
        // Etant donner les informations d'une commande ayant été saisie par l'utilisateur
        Article article = mock(Article.class);

        doReturn("123456789").when(inputNoCommande).getText();
        doReturn("1").when(inputNoLigne).getText();
        doReturn("2").when(inputNbArticle).getText();
        doReturn("OC").when(listActions).getValue();
        doReturn("").when(listArticles).getValue();
        doReturn("[a-zA-Z]{8}[0-9]{3}").when(article).getFormat();
        ObservableList listArticlesContent = mock(ObservableList.class);
        doReturn(0).when(listArticlesContent).size();
        doReturn(listArticlesContent).when(listArticles).getItems();
        // Lors de la saisie de la commande
        controllerTest = spy(controllerTest);
        doReturn(article).when(controllerTest).getSelectedArticle();

        controllerTest.commencerSaisie();
        // Alors on attend a ce que l'ihm soit mise à jour pour permettre la saisie des numéros de série
        verify(listArticles,never()).setDisable(true);
        verify(listActions,never()).setDisable(true);
        verify(inputNbArticle,never()).setDisable(true);
        verify(btnCommencerSaisie,never()).setDisable(true);
        verify(btnAnnulerSaisie,never()).setDisable(false);
        verify(inputNoLigne,never()).setDisable(true);
        verify(inputNoLigne,never()).setDisable(true);
        verify(inputNumeroSerieContainer,never()).getChildren();
        verify(boiteErreur).setText("Aucun article n'a pus être chargé .");
    }

    @Test
    void commencerSaisieListActionVide() throws NoSuchFieldException {
        MockitoAnnotations.openMocks(this);
        // Etant donner les informations d'une commande ayant été saisie par l'utilisateur
        Article article = mock(Article.class);

        doReturn("123456789").when(inputNoCommande).getText();
        doReturn("1").when(inputNoLigne).getText();
        doReturn("2").when(inputNbArticle).getText();
        doReturn("").when(listActions).getValue();
        doReturn("42400131 VPort P06-2M60M V1.2.2").when(listArticles).getValue();
        doReturn("[a-zA-Z]{8}[0-9]{3}").when(article).getFormat();
        ObservableList listArticlesContent = mock(ObservableList.class);
        doReturn(0).when(listArticlesContent).size();
        doReturn(listArticlesContent).when(listActions).getItems();
        // Lors de la saisie de la commande
        controllerTest = spy(controllerTest);
        doReturn(article).when(controllerTest).getSelectedArticle();

        controllerTest.commencerSaisie();
        // Alors on attend a ce que l'ihm soit mise à jour pour permettre la saisie des numéros de série
        verify(listArticles,never()).setDisable(true);
        verify(listActions,never()).setDisable(true);
        verify(inputNbArticle,never()).setDisable(true);
        verify(btnCommencerSaisie,never()).setDisable(true);
        verify(btnAnnulerSaisie,never()).setDisable(false);
        verify(inputNoLigne,never()).setDisable(true);
        verify(inputNoLigne,never()).setDisable(true);
        verify(inputNumeroSerieContainer,never()).getChildren();
        verify(boiteErreur).setText("Il n'y a aucune action renseigner pour cet article .");
    }
    @Test
    void commencerSaisieArticleNonSelectionner() throws NoSuchFieldException {
        MockitoAnnotations.openMocks(this);
        // Etant donner les informations d'une commande ayant été saisie par l'utilisateur
        Article article = mock(Article.class);

        doReturn("123456789").when(inputNoCommande).getText();
        doReturn("1").when(inputNoLigne).getText();
        doReturn("1").when(inputNbArticle).getText();
        doReturn("OC").when(listActions).getValue();
        doReturn("").when(listArticles).getValue();
        doReturn("[a-zA-Z]{8}[0-9]{3}").when(article).getFormat();
        ObservableList listArticlesContent = mock(ObservableList.class);
        doReturn(1).when(listArticlesContent).size();
        doReturn("42400131 VPort P06-2M60M V1.2.2").when(listArticlesContent).get(0);
        doReturn(listArticlesContent).when(listArticles).getItems();
        // Lors de la saisie de la commande
        controllerTest = spy(controllerTest);
        doReturn(article).when(controllerTest).getSelectedArticle();

        controllerTest.commencerSaisie();
        // Alors on attend a ce que l'ihm soit mise à jour pour permettre la saisie des numéros de série
        verify(listArticles).setDisable(true);
        verify(listActions).setDisable(true);
        verify(inputNbArticle).setDisable(true);
        verify(btnCommencerSaisie).setDisable(true);
        verify(inputNoCommande).setDisable(true);
        verify(inputNoLigne).setDisable(true);
        verify(btnAnnulerSaisie).setDisable(false);
        verify(listArticles,atLeast(1)).setValue(anyString());
    }

    @Test
    void commencerSaisieActionNonSelectionner() throws NoSuchFieldException {
        MockitoAnnotations.openMocks(this);
        // Etant donner les informations d'une commande ayant été saisie par l'utilisateur
        Article article = mock(Article.class);

        doReturn("123456789").when(inputNoCommande).getText();
        doReturn("1").when(inputNoLigne).getText();
        doReturn("1").when(inputNbArticle).getText();
        doReturn("").when(listActions).getValue();
        doReturn("42400131 VPort P06-2M60M V1.2.2").when(listArticles).getValue();
        doReturn("[a-zA-Z]{8}[0-9]{3}").when(article).getFormat();
        ObservableList listArticlesContent = mock(ObservableList.class);
        doReturn(1).when(listArticlesContent).size();
        doReturn("OF").when(listArticlesContent).get(0);
        doReturn(listArticlesContent).when(listActions).getItems();
        // Lors de la saisie de la commande
        controllerTest = spy(controllerTest);
        doReturn(article).when(controllerTest).getSelectedArticle();

        controllerTest.commencerSaisie();
        // Alors on attend a ce que l'ihm soit mise à jour pour permettre la saisie des numéros de série
        verify(listArticles).setDisable(true);
        verify(listActions).setDisable(true);
        verify(inputNbArticle).setDisable(true);
        verify(btnCommencerSaisie).setDisable(true);
        verify(inputNoCommande).setDisable(true);
        verify(inputNoLigne).setDisable(true);
        verify(btnAnnulerSaisie).setDisable(false);
        verify(listActions,atLeast(1)).setValue(anyString());
    }


    @Test
    void commencerSaisieNumeroCommandeVide() {
        // Etant donner les informations d'une commande ayant été saisie par l'utilisateur
        MockitoAnnotations.openMocks(this);
        Article article = mock(Article.class);
        doReturn("").when(inputNoCommande).getText();
        doReturn("1").when(inputNoLigne).getText();
        doReturn("1").when(inputNbArticle).getText();
        doReturn("OC").when(listActions).getValue();
        doReturn("42400131 VPort P06-2M60M V1.2.2").when(listArticles).getValue();
        doReturn("[a-zA-Z]{8}[0-9]{3}").when(article).getFormat();
        // Lors de la saisie de la commande
        controllerTest.commencerSaisie();
        // Alors on attend a ce que l'ihm soit mise à jour pour permettre la saisie des numéros de série
        verify(listArticles,never()).setDisable(true);
        verify(listActions,never()).setDisable(true);
        verify(inputNbArticle,never()).setDisable(true);
        verify(btnCommencerSaisie,never()).setDisable(true);
        verify(btnAnnulerSaisie,never()).setDisable(false);
        verify(inputNoLigne,never()).setDisable(true);
        verify(inputNoLigne,never()).setDisable(true);
        verify(inputNumeroSerieContainer,never()).getChildren();

    }

    @Test
    void onRechercheArticle() {
        MockitoAnnotations.openMocks(this);
        FxRobot robot = new FxRobot();
        // Etant donner les informations d'une commande ayant été saisie par l'utilisateur
        Article article = mock(Article.class);
        listArticles.setOnKeyPressed(event -> controllerTest.onRechercheArticle(event));
        assertNotNull(listArticles.getOnKeyPressed());
        doReturn(primaryScene).when(listArticles).getScene();
        robot.clickOn(listArticles);
        robot.press(KeyCode.S);
        robot.release(KeyCode.S);

        assertEquals("S",mot_recherche_article.toString());
        robot.press(KeyCode.A);
        robot.release(KeyCode.A);
        assertEquals("SA",mot_recherche_article.toString());
        robot.press(KeyCode.I);
        robot.release(KeyCode.I);
        assertEquals("SAI",mot_recherche_article.toString());
        robot.press(KeyCode.BACK_SPACE);
        robot.release(KeyCode.BACK_SPACE);
        assertEquals("SA",mot_recherche_article.toString());
    }


    @DisplayName("Test de la méthode ajouterNumeroSerie")
    @Test
    void soundError() throws JavaLayerException {
        ControllerSaisie controllerTest = new ControllerSaisie();
        AdvancedPlayer player = new AdvancedPlayer(getClass().getResourceAsStream("/sound/beep-beep-6151.mp3"));
        player.play();
    }
    // TODO Tester si quand on clique sur le bouton "Annuler" l'ihm est mise à jour pour permettre la saisie d'une nouvelle commande
    void TestPopUpMdp() throws NoSuchFieldException {
        // Etant donner un utilisateur
        // Lorsqu'il clique sur la liste déroulante des outils de configuration

        // Alors une popup s'affiche pour demander un mot de passe

    }

}