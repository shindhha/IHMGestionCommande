package main.java.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import main.Launch;
import main.java.exceptions.FormatInvalideException;
import main.java.modeles.Article;
import main.java.modeles.Commande;
import main.java.modeles.OC;
import main.java.modeles.OF;
import main.java.services.FileReader;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ControllerSaisie implements Initializable {


    @FXML
    private ComboBox<String> listArticles;
    @FXML
    private ComboBox<String> listActions;
    @FXML
    private TextField inputNbArticle;
    @FXML
    private VBox inputNumeroSerieContainer;
    @FXML
    private Button btnTerminerSaisie;
    @FXML
    private Button btnCommencerSaisie;
    @FXML
    private Button btnAnnulerSaisie;
    @FXML
    private Text compteurNbProduitSaisie;
    @FXML
    private Text boiteErreur;
    @FXML
    private VBox listProduitsSaisie;
    @FXML
    private TextField inputNoCommande;
    @FXML
    private ImageView qrCodeView;
    @FXML
    private TextField inputNoLigne;
    private Scene primaryScene;
    private TextField inputNumeroSerie;
    private HashMap<String,Article> articles;
    public Commande currentCommande;


    // Mot entrer par l'utilisateur pour la recherche d'un produit dans la liste.
    private StringBuilder mot_recherche_article;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] config = new String[0];
        mot_recherche_article = new StringBuilder();
        try {
            config = FileReader.readConfigFile(new BufferedReader(new InputStreamReader(Launch.class.getResourceAsStream(Launch.configurationPath))));
            Launch.pathOutPutFolder = config[0] == "" ? Launch.defaultPathOutPutFolder : config[0];
            Launch.listArticleConfiguration = config[1];
        } catch (FileNotFoundException e) {
            boiteErreur.setText("Le fichier de configuration n'a pas été trouvé");
        }
    }
    public ControllerSaisie() {
    }
    public ControllerSaisie(TextField inputNumeroSerie, Text compteurNbProduitSaisie) {
        this.compteurNbProduitSaisie = compteurNbProduitSaisie;
        this.inputNumeroSerie = inputNumeroSerie;
        this.inputNumeroSerieContainer = new VBox();
        this.btnTerminerSaisie = new Button();
        this.btnCommencerSaisie = new Button();
        this.btnAnnulerSaisie = new Button();
        this.primaryScene = new Scene(new HBox());
        this.boiteErreur = new Text();
        this.listProduitsSaisie = new VBox();
    }


    // Contructeur de test
    public ControllerSaisie(Commande currentCommande) {
        this.currentCommande = currentCommande;
    }

    public ControllerSaisie(TextField inputNoCommande,
                            TextField inputNoLigne ,
                            TextField inputNbArticle ,
                            ComboBox<String> listArticles ,
                            ComboBox<String> listActions) {
        this.inputNoCommande = inputNoCommande;
        this.inputNoLigne = inputNoLigne;
        this.inputNbArticle = inputNbArticle;
        this.listArticles = listArticles;
        this.listActions = listActions;
        this.inputNumeroSerieContainer = new VBox();
        this.btnTerminerSaisie = new Button();
        this.btnCommencerSaisie = new Button();
        this.btnAnnulerSaisie = new Button();
        this.primaryScene = new Scene(new HBox());

    }




    /**
     * Etat par defaut de la page de saisie
     * @param scene : Scene principale
     */
    public void initScene(Scene scene) {
        this.primaryScene = scene;
        listArticles.setOnKeyPressed(event -> onRechercheArticle(event));
        loadArticle();
        listArticles.setOnAction(event -> useArticle(getSelectedArticle()));
    }



    /**
     * Fonction qui permet de charger les articles dans la liste déroulante
     * Appelé lors d'intreaction avec le clavier elle construit un mot à partir des lettres et chiffres presser
     * qui est ensuite utilisé pour filtrer les articles et ne garder que ceux qui contiennent le mot
     * @param event : KeyEvent permet de récupérer la touche presser
     */
    public void onRechercheArticle(KeyEvent event) {

        // Si on appuie sur la touche backspace, on supprime le dernier caractère de la recherche
        if (event.getCode() == KeyCode.BACK_SPACE && mot_recherche_article.length() > 0)
            mot_recherche_article.deleteCharAt(mot_recherche_article.length() - 1);

        // Si la touche presser est une lettre ou un chiffre, on l'ajoute à la recherche (switch case pour les chiffres)
        if (event.getCode().isLetterKey() || event.getCode().isDigitKey()) {
            mot_recherche_article.append(event.getText());
        }
        listArticles.setPromptText("Recherche : " + mot_recherche_article.toString());
        if (mot_recherche_article.length() == 0)
            listArticles.setPromptText("Sélectionner l'article générique");
        printArticle(filtrerArticle(mot_recherche_article.toString(), articles));
    }



    /**
     * Affiche les articles dans la liste déroulante
     * @param articles : liste des articles à afficher
     */
    public void printArticle(HashMap<String,Article> articles) {
        listArticles.getItems().clear();
        for (Article article : articles.values()) {
            listArticles.getItems().add(article.toString());
        }
    }

    /**
     * Filtre les articles en fonction du mot rechercher par l'utilisateur.
     * @param mot_recherche_article : mot rechercher par l'utilisateur
     * @param aFiltrer : liste des articles à filtrer
     * @return : la liste des articles qui contiennent le mot rechercher.
     */
    public HashMap<String,Article> filtrerArticle(String mot_recherche_article, HashMap<String,Article> aFiltrer) {
        HashMap<String,Article> articlesFiltres = new HashMap<>();
        for (Article article : aFiltrer.values()) {
            if (Pattern.matches(".*"+ mot_recherche_article.toLowerCase() + ".*", article.toString().toLowerCase())) {
                articlesFiltres.put(article.getNumero(), article);
            }
        }
        return articlesFiltres;
    }


    /**
     * Met à jour les actions disponibles pour l'article sélectionné en fonction du fichier de configuration
     * affiche le qr code permettant de configurer la scanette pour l'article sélectionné.
     * @param selectedArticle : article sélectionné dans la liste déroulante.
     */
    public void useArticle(Article selectedArticle) {
        inputNumeroSerieContainer.getChildren().clear();
        listActions.getItems().clear();
        for (String action : selectedArticle.getActions()) {
            listActions.getItems().add(action);
        }
        // créer une image avec le qr
        qrCodeView.setImage(new Image(selectedArticle.getQrCode()));
    }


    /**
     * Affiche le numéro de série saisie et le bouton de suppression pour le supprimer de la liste
     * et vide le champ de saisie
     * @throws IllegalStateException si le nombre maximum d'article est atteint ou si le numéro de série a déjà été saisi
     * @throws FormatInvalideException si le numéro de série ne correspond pas au format défini dans le fichier de configuration
     */
    public void ajouterNumeroSerie(String numeroSerie)  {

        try {
            String finalNumeroSerie = currentCommande.ajouterNumeroSerie(numeroSerie);
            boiteErreur.setText("");
            Label label = new Label(finalNumeroSerie);
            Button button = new Button("X");
            HBox hbox = new HBox();
            hbox.getChildren().addAll(label,button);
            button.setOnAction(event -> onDeleteProduit(hbox, finalNumeroSerie));
            listProduitsSaisie.getChildren().add(hbox);
            updateCompteur(currentCommande.size());
        } catch (FormatInvalideException | IllegalStateException e) {
            boiteErreur.setText(e.getMessage());
        } finally {
            inputNumeroSerie.clear();
        }
    }


    /**
     * Supprime le produit de la liste des produits saisis a la fois visuellement et dans la liste des numéros de série
     * @param hbox : HBox qui contient le label et le bouton de suppression du numéro de série
     * @param aSupprimer : numéro de série du produit à supprimer.
     */
    public void onDeleteProduit(HBox hbox,String aSupprimer) {
        listProduitsSaisie.getChildren().remove(hbox);
        currentCommande.remove(aSupprimer);
        updateCompteur(currentCommande.size());
        boiteErreur.setText("");
    }


    /**
     *  Actualise l'interface graphique pour permettre la saisie des nouveaux numéros de séries :
     *  Désactive les champs : liste des articles, liste des actions, champ de saisie du nombre d'article, bouton commencer la saisie
     *  Active le champ de saisie du numéro de série, le bouton annuler la saisie.
     *  Ajoute un listener sur toute la scene qui écoute les touches du clavier pour verifier le format des numéros de série
     *  Si une erreur est détecté, son message est affiché dans la boite d'erreur
     */
    public void commencerSaisie() {

        try {
            currentCommande = makeCommande(listActions.getValue());
            inputNumeroSerie = new TextField();
            inputNumeroSerie.requestFocus();
            inputNumeroSerie.setPromptText("Saisissez le numéro de série");
            inputNumeroSerie.getStyleClass().add("textField");
            inputNumeroSerieContainer.getChildren().add(inputNumeroSerie);
            disableChamp(listArticles, listActions, inputNbArticle, btnCommencerSaisie, inputNoCommande,inputNoLigne);
            btnAnnulerSaisie.setDisable(false);
            primaryScene.setOnKeyReleased(event -> onSaisieNumeroSerie(event));

        } catch (NumberFormatException e) {
            boiteErreur.setText("Le nombre d'article doit être un nombre");
        } catch (IllegalStateException | IllegalArgumentException e) {
            boiteErreur.setText(e.getMessage());
        }

    }


    /**
     * Lance la fonction validerSaisie et catch les exceptions potentielles
     * pour afficher le message d'erreur dans la boite d'erreur.
     */
    public void onSaisieNumeroSerie(KeyEvent event) {
        try {
            if (event.getCode() == KeyCode.DOWN) {
                inputNumeroSerie.requestFocus();
                ajouterNumeroSerie(inputNumeroSerie.getText());
            }
        } catch (IllegalStateException e) {
            boiteErreur.setText(e.getMessage());
        }
    }

    /**
     * Verifie que les champs obligatoires sont bien remplis pour pouvoir commencer une saisie.
     * Si un champ obligatoire n'est pas rempli, on le rempli avec une valeur par défaut
     * @throws IllegalStateException Si un champ obligatoire n'est pas rempli et qu'il n'existe aucune valeur
     *                               pour le définir par défaut.
     */
    private Commande makeCommande(String type) throws IllegalStateException {
        if (inputNbArticle.getText().isEmpty() || Integer.parseInt(inputNbArticle.getText()) <= 0) inputNbArticle.setText(Launch.nombreMaxArticleDefaut);
        if (listArticles.getValue() == null || listArticles.getValue().isEmpty()) {
            if (listArticles.getItems().size() > 0)
                listArticles.setValue(listArticles.getItems().get(0));
            else throw new IllegalStateException("Aucun article n'a pus être chargé .");
        }
        if (listActions.getValue() == null || listActions.getValue().isEmpty()) {
            if (listActions.getItems().size() > 0)
                listActions.setValue(listActions.getItems().get(0));
            else throw new IllegalStateException("Il n'y a aucune action renseigner pour cet article .");
        }


        Commande commande = null;
        String noCommande = inputNoCommande.getText();
        String noLigne = inputNoLigne.getText();
        int nbArticle = Integer.parseInt(inputNbArticle.getText());
        Article article = getSelectedArticle();
        if (type.equalsIgnoreCase("OF")) {
            commande = new OF(noCommande, article, nbArticle,noLigne);
        }
        if (type.equalsIgnoreCase("OC")) {
            commande = new OC(noCommande, article, nbArticle,noLigne);
        }
        return commande;
    }

    /**
     * Met à jour l'état du programme pour permettre une nouvelle saisie :
     * Supprime le listener de la scene
     * Active les champs : liste des articles, liste des actions, champ de saisie du nombre d'article, bouton commencer la saisie
     * Désactive le champ de saisie du numéro de série, le bouton annuler la saisie.
     * Vide la liste des numéros de série
     */
    public void setDefaultIHMState() {
        primaryScene.setOnKeyReleased(null);
        listProduitsSaisie.getChildren().clear();
        enableChamp(listArticles, listActions, inputNbArticle, btnCommencerSaisie, inputNoCommande,inputNoLigne);
        disableChamp(btnTerminerSaisie, btnAnnulerSaisie);
        currentCommande.clear();
        inputNumeroSerieContainer.getChildren().clear();
        updateCompteur(0);
    }


    /**
     * Ecrit dans un fichier csv les informations de la commande et les numéros de série saisis.
     * Puis met à jour l'interface graphique pour permettre une nouvelle saisie.
     * @throws IOException Si le fichier n'a pas pu être créé
     */
    public void makeOutputFile()  {
        try {
            currentCommande.makeOutPutFile();
            setDefaultIHMState();
            copyFileNameToClipBoard();
        } catch (IOException e) {
            boiteErreur.setText("Aucun dossier de sortie n'a été défini ou est invalide .");
        }

    }


    /**
     * Met à jour l'interface graphique pour indiquer à l'utilisateur le nombre d'article qu'il a saisi.
     */
    public void updateCompteur(int nbArticleSaisie) {
        compteurNbProduitSaisie.setText(nbArticleSaisie + "/" + currentCommande.getNbMaxNumeroSerie());
        if (nbArticleSaisie == currentCommande.getNbMaxNumeroSerie()) {
            btnTerminerSaisie.setDisable(false);
        }

    }

    public void copyFileNameToClipBoard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(currentCommande.getFileOutPutName());
        clipboard.setContent(content);
    }

    public void copyOutPutFolderNameToClipBoard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(Launch.pathOutPutFolder);
        clipboard.setContent(content);
    }

    public void changerRepertoireSortie() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choisir le répertoire de sortie");
        File selectedDirectory = directoryChooser.showDialog(primaryScene.getWindow());
        if (selectedDirectory != null) {
            Launch.pathOutPutFolder = selectedDirectory.getAbsolutePath() + "\\";
        }
        FileReader.writeConfigFile(Launch.pathOutPutFolder, Launch.listArticleConfiguration);
    }

    public void changerFichierArticles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir le fichier d'articles");
        File selectedFile = fileChooser.showOpenDialog(primaryScene.getWindow());
        if (selectedFile != null) {
            Launch.listArticleConfiguration = selectedFile.getAbsolutePath();
        }
        FileReader.writeConfigFile(Launch.pathOutPutFolder, Launch.listArticleConfiguration);
        listArticles.getItems().clear();
        loadArticle();
    }

    private void loadArticle() {
        try {
            articles = FileReader.readArticleConfigFile(new File(Launch.listArticleConfiguration));
            printArticle(articles);
            boiteErreur.setText("");
        } catch (Exception e) {
            boiteErreur.setText("Le fichier de configuration des articles n'a pas été trouvé");
        }
    }




    /**
     * @return Article articles L'article sélectionné par l'utilisateur dans la liste des articles
     */
    public Article getSelectedArticle() {
        String articleSelectionner = listArticles.getValue();
        String numero = articleSelectionner.split(" ")[0];
        return articles.get(numero);
    }

    private void disableChamp(Control... fields) {
        for (Control field : fields) {
            field.setDisable(true);
        }
    }

    private void enableChamp(Control... fields) {
        for (Control field : fields) {
            field.setDisable(false);
        }
    }
    public String getErrorMessage() {
        return boiteErreur.getText();
    }

    public TextField getInputNumeroSerie() {
        return inputNumeroSerie;
    }

    public VBox getListProduitsSaisie() {
        return listProduitsSaisie;
    }





}