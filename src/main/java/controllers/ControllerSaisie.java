package main.java.controllers;

import main.java.exceptions.FormatInvalideException;
import main.java.modeles.Article;
import main.java.services.FileReader;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import main.Launch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class ControllerSaisie {


    @FXML
    private ComboBox<String> listArticles;
    @FXML
    private ComboBox<String> listActions;
    @FXML
    private TextField inputNbArticle;
    @FXML
    private VBox articleView;
    @FXML
    private Button btnTerminerSaisie;
    @FXML
    private Button btnCommencerSaisie;
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
    private Button cancelSaisie;
    private Scene primaryScene;
    private int maxNbArticleSaisie;
    private HashMap<String,Article> articles;
    private TextField inputNumeroSerie;
    private String numeroSerie;
    private List<String> listNumeroSerie;

    // Mot entrer par l'utilisateur pour la recherche d'un produit dans la liste.
    private StringBuilder input_recherche_article;
    public ControllerSaisie() {
        listNumeroSerie = new ArrayList<>();
    }


    // Contructeur de test
    public ControllerSaisie(List<String> listNumeroSerie, int maxNbArticleSaisie,TextField inputNumeroSerietest) {
        this.listNumeroSerie = listNumeroSerie;
        this.maxNbArticleSaisie = maxNbArticleSaisie;
        this.inputNumeroSerie = inputNumeroSerietest;
    }



    public void initScene(Scene scene) {
        this.primaryScene = scene;
        disableChamp(btnTerminerSaisie,cancelSaisie);
        input_recherche_article = new StringBuilder();
        listArticles.setOnKeyPressed(event -> onRechercheArticle(event));
        loadArticle();
    }


    /**
     * Fonction qui permet de charger les articles dans la liste déroulante
     * Appelé lors d'intreaction avec le clavier elle construit un mot à partir des lettres et chiffres presser
     * qui est ensuite utilisé pour filtrer les articles et ne garder que ceux qui contiennent le mot
     * @param event : KeyEvent qui permet de récupérer la touche presser
     */
    public void onRechercheArticle(KeyEvent event) {

        // Si on appuie sur la touche backspace, on supprime le dernier caractère de la recherche
        if (event.getCode() == KeyCode.BACK_SPACE && input_recherche_article.length() > 0)
            input_recherche_article.deleteCharAt(input_recherche_article.length() - 1);

        // Si la touche presser est une lettre ou un chiffre, on l'ajoute à la recherche (switch case pour les chiffres)
        if (event.getCode().isLetterKey() || event.getCode().isDigitKey()) {
            input_recherche_article.append(event.getText());
        }
        listArticles.setPromptText("Recherche : " + input_recherche_article.toString());
        if (input_recherche_article.length() == 0)
            listArticles.setPromptText("Sélectionner l'article générique");
        loadArticle();
    }

    /**
     *  Charge la liste des articles qui contiennent le mot rechercher par l'utilisateur
     *  et contenue dans le fichier de configuration pour les placer dans la liste déroulante des articles
     */
    public void loadArticle() {

        listArticles.getItems().clear();
        try {
            articles = FileReader.readConfigFile(new File(Launch.class.getResource("resources/Ref_articles.csv").getPath()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (Article article : articles.values()) {
            if (Pattern.matches(".*"+ input_recherche_article + ".*", article.toString().toLowerCase())) {
                listArticles.getItems().add(article.toString());
            }
        }
        listArticles.setOnAction(event -> showArticle());

    }


    /**
     * Met à jour les actions disponibles pour l'article sélectionné en fonction du fichier de configuration
     * affiche le qr code permettant de configurer la scanette pour l'article sélectionné.
     */
    public void showArticle() {
        articleView.getChildren().clear();
        listActions.getItems().clear();
        Article article = getSelectedArticle();

        for (String action : article.getActions()) {
            listActions.getItems().add(action);
        }
        // créer une image avec le qr
        qrCodeView.setImage(new Image(article.getQrCode()));
    }


    /**
     * Affiche le numéro de série saisie et le bouton de suppression pour le supprimer de la liste
     * et vide le champ de saisie
     * @throws IllegalStateException si le nombre maximum d'article est atteint ou si le numéro de série a déjà été saisi
     * @throws FormatInvalideException si le numéro de série ne correspond pas au format défini dans le fichier de configuration
     */
    public void validerSaisie() throws IllegalStateException, FormatInvalideException {
        if (listNumeroSerie.size() >= maxNbArticleSaisie) throw new IllegalStateException("Vous avez atteint le nombre maximum d'article à saisir");
        if (listNumeroSerie.contains(getNumeroSerie())) throw new IllegalStateException("Le numéro de série a déjà été saisi");
        if (!isNumeroValide(getFormat(),getNumeroSerie())) throw new FormatInvalideException();

        resetErrorMessage();
        numeroSerie = getNumeroSerie();
        Label label = new Label(numeroSerie);
        Button button = new Button("X");
        HBox hbox = new HBox();
        hbox.getChildren().addAll(label,button);
        button.setOnAction(event -> onDeleteProduit(hbox));

        listProduitsSaisie.getChildren().add(hbox);
        listNumeroSerie.add(numeroSerie);
        inputNumeroSerie.clear();
        updateCompteur(listNumeroSerie.size());
    }


    /**
     * Supprime le produit de la liste des produits saisis a la fois visuellement et dans la liste des numéros de série
     * @param hbox : HBox qui contient le label et le bouton de suppression du numéro de série
     */
    public void onDeleteProduit(HBox hbox) {
        listProduitsSaisie.getChildren().remove(hbox);
        listNumeroSerie.remove(numeroSerie);
        updateCompteur(listNumeroSerie.size());
        boiteErreur.setText("");
    }


    /**
     *  Actualise l'interface graphique pour permettre la saisie des nouveaux numéros de séries :
     *  Désactive les champs : liste des articles, liste des actions, champ de saisie du nombre d'article, bouton commencer la saisie
     *  Active le champ de saisie du numéro de série, le bouton annuler la saisie.
     *  Ajoute un listener sur toute la scene qui écoute les touches du clavier pour verifier le format des numéros de série
     *  Si une erreur est détecté, son message est affiché dans la boite d'erreur
     */
    public void startSaisie() {

        try {
            verifierChamp();
            maxNbArticleSaisie = Integer.parseInt(inputNbArticle.getText());
            inputNumeroSerie = new TextField();
            inputNumeroSerie.requestFocus();
            inputNumeroSerie.setPromptText("Saisissez le numéro de série");
            inputNumeroSerie.getStyleClass().add("textField");
            articleView.getChildren().add(inputNumeroSerie);
            disableChamp(listArticles, listActions, inputNbArticle, btnCommencerSaisie, inputNoCommande);
            cancelSaisie.setDisable(false);
            primaryScene.setOnKeyReleased(event -> onSaisieNumeroSerie());
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
    public void onSaisieNumeroSerie() {
        try {
            validerSaisie();
        } catch (IllegalStateException | FormatInvalideException e) {
            boiteErreur.setText(e.getMessage());
        }
    }

    /**
     * Verifie que les champs obligatoires sont bien remplis pour pouvoir commencer une saisie.
     * Si un champ obligatoire n'est pas rempli, on le rempli avec une valeur par défaut
     * @throws IllegalStateException Si un champ obligatoire n'est pas rempli et qu'il n'existe aucune valeur
     *                               pour le définir par défaut.
     */
    public void verifierChamp() throws IllegalStateException {
        if (inputNbArticle.getText().isEmpty() || Integer.parseInt(inputNbArticle.getText()) <= 0) {
            inputNbArticle.setText("1");
        }
        if (listArticles.getValue() == null || listArticles.getValue().isEmpty()) {
            if (listArticles.getItems().size() > 0)
                listArticles.setValue(listArticles.getItems().get(0));
            else throw new IllegalStateException("Pas d'article");
        }
        if (listActions.getValue() == null || listActions.getValue().isEmpty()) {
            if (listActions.getItems().size() > 0)
                listActions.setValue(listActions.getItems().get(0));
            else throw new IllegalStateException("Pas d'action pour l'article");
        }
        if (inputNoCommande.getText().isEmpty()) {
            throw new IllegalStateException("Pas de numéro de commande");
        }


    }

    /**
     * Met à jour l'état du programme pour permettre une nouvelle saisie :
     * Supprime le listener de la scene
     * Active les champs : liste des articles, liste des actions, champ de saisie du nombre d'article, bouton commencer la saisie
     * Désactive le champ de saisie du numéro de série, le bouton annuler la saisie.
     * Vide la liste des numéros de série
     */
    public void annulerSaisie() {
        primaryScene.setOnKeyReleased(null);
        listProduitsSaisie.getChildren().clear();
        enableChamp(listArticles, listActions, inputNbArticle, btnCommencerSaisie, inputNoCommande);
        disableChamp(btnTerminerSaisie,cancelSaisie);
        listNumeroSerie.clear();
        articleView.getChildren().clear();
    }


    /**
     * Ecrit dans un fichier csv les informations de la commande et les numéros de série saisis.
     * Puis met à jour l'interface graphique pour permettre une nouvelle saisie.
     * @throws IOException Si le fichier n'a pas pu être créé
     */
    public void terminerSaisie() throws IOException {
        File outputFile = new File("src/main/java/com/example/ihmgestioncommande/output/" + inputNoCommande.getText() +".csv");
        FileWriter fileWriter = new FileWriter(outputFile);
        fileWriter.write("Numero de commande : " + inputNoCommande.getText() + ";");
        fileWriter.write("Article : " + getSelectedArticle().getDesignation() + ";");
        fileWriter.write("Numéro de commande : " + inputNoCommande.getText() + ";");
        fileWriter.write("Personne ayant éffectué la commande : " + System.getProperty("user.name") + "\n");
        for (String numeroSerie : listNumeroSerie) {
            fileWriter.write(numeroSerie + "\n");
        }
        fileWriter.close();
        annulerSaisie();
    }



    public void updateCompteur(int nbArticleSaisie) {
        compteurNbProduitSaisie.setText(nbArticleSaisie + "/" + maxNbArticleSaisie);
        if (nbArticleSaisie == maxNbArticleSaisie) {
            btnTerminerSaisie.setDisable(false);
        }

    }

    public String getNumeroCommande() {
        return inputNoCommande.getText();
    }

    public String getNumeroSerie() {
        return inputNumeroSerie.getText();
    }
    public String getFormat() {
        return getSelectedArticle().getFormat();
    }
    public boolean isNumeroValide(String format, String numeroSerie) {
        return Pattern.matches(format, numeroSerie);
    }

    /**
     * @return Article articles L'article sélectionné par l'utilisateur dans la liste des articles
     */
    private Article getSelectedArticle() {
        String articleSelectionner = listArticles.getValue();
        String numero = articleSelectionner.split(" ")[0];
        return articles.get(numero);
    }
    private void resetErrorMessage() {
        boiteErreur.setText("");
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

    public void printErrorMessage(String message) {
        boiteErreur.setText(message);
    }





}