package com.example.ihmgestioncommande;

import com.example.ihmgestioncommande.modeles.Article;
import com.example.ihmgestioncommande.services.FileReader;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class ControllerSaisie {


    @FXML
    private ChoiceBox<String> listArticles;
    @FXML
    private ChoiceBox<String> listActions;
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
    private Scene primaryScene;
    private int maxNbArticleSaisie;
    private int nbArticleSaisie;
    private HashMap<String,Article> articles;
    private TextField inputNumeroSerie;
    private String numeroSerie;
    private List<String> listNumeroSerie;

    private StringBuilder recherche;
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
        btnTerminerSaisie.setDisable(true);
        recherche = new StringBuilder();

        listArticles.setOnKeyPressed(event -> {
            // Si on appuie sur la touche backspace, on supprime le dernier caractère de la recherche
            if (event.getCode() == KeyCode.BACK_SPACE)
                recherche.deleteCharAt(recherche.length() - 1);
            // Sinon on ajoute le caractère à la recherche
            else
            recherche.append(event.getText());
            loadArticle();
        });
        loadArticle();
    }
    public void loadArticle() {

        listArticles.getItems().clear();
        try {
            articles = FileReader.readConfigFile(new File("src/main/resources/com/example/ihmgestioncommande/conf/Ref_articles.csv"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (Article article : articles.values()) {
            if (Pattern.matches(".*"+ recherche + ".*", article.toString().toLowerCase())) {
                listArticles.getItems().add(article.toString());
            }
        }

        listArticles.setOnAction(event -> showArticle());

    }


    /**
     * Vide
     * Affiche les informations de l'article sélectionné dans l'ihm
     * et fait apparaitre un champ de saisie pour le numéro de série
     */
    public void showArticle() {
        articleView.getChildren().clear();
        listActions.getItems().clear();
        Article article = getSelectedArticle();
        inputNumeroSerie = new TextField();
        inputNumeroSerie.getStyleClass().add("textField");
        articleView.getChildren().add(inputNumeroSerie);
        for (String action : article.getActions()) {
            listActions.getItems().add(action);
        }
        // créer une image avec le qr
        qrCodeView.setImage(new Image(article.getQrCode()));
    }



    public void validerSaisie() {
        if (nbArticleSaisie >= maxNbArticleSaisie) {
            throw new IllegalStateException("Vous avez atteint le nombre maximum d'article à saisir");
        }
        if (listNumeroSerie.contains(getNumeroSerie())) {
            throw new IllegalStateException("Le numéro de série a déjà été saisi");
        }
        if (isNumeroValide(getFormat(), getNumeroSerie())) {
            boiteErreur.setText("");
            numeroSerie = getNumeroSerie();
            Label label = new Label(numeroSerie);

            HBox hbox = new HBox();
            hbox.getChildren().add(label);
            Button button = new Button("X");

            button.setOnAction(event -> {
                listProduitsSaisie.getChildren().remove(hbox);
                listNumeroSerie.remove(numeroSerie);
                nbArticleSaisie--;
                updateCompteur();
                boiteErreur.setText("");
            });
            hbox.getChildren().add(button);
            listProduitsSaisie.getChildren().add(hbox);
            listNumeroSerie.add(numeroSerie);
            inputNumeroSerie.clear();
            nbArticleSaisie++;
            updateCompteur();

        }
    }

    public void disableChamp(){

    }

    public void startSaisie() {

        try {

            verifierChamp();
            maxNbArticleSaisie = Integer.parseInt(inputNbArticle.getText());
            nbArticleSaisie = 0;
            listArticles.setDisable(true);
            listActions.setDisable(true);
            inputNbArticle.setDisable(true);
            btnCommencerSaisie.setDisable(true);
            inputNoCommande.setDisable(true);
            inputNumeroSerie.requestFocus();
            primaryScene.setOnKeyReleased(event -> {
                try {
                    validerSaisie();
                } catch (IllegalStateException e) {
                    boiteErreur.setText(e.getMessage());
                }
            });
        } catch (IllegalStateException e) {
            boiteErreur.setText(e.getMessage());
        } catch (NumberFormatException e) {
            boiteErreur.setText("Le nombre d'article doit être un nombre");
        } catch (IllegalArgumentException e) {
            boiteErreur.setText(e.getMessage());
        }

    }


    public void verifierChamp() throws IllegalStateException {
        if (inputNbArticle.getText().isEmpty() || Integer.parseInt(inputNbArticle.getText()) == 0) {
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






    public void terminerSaisie() throws IOException {
        primaryScene.setOnKeyReleased(null);
        listArticles.setDisable(false);
        listActions.setDisable(false);
        inputNbArticle.setDisable(false);
        btnCommencerSaisie.setDisable(false);
        listProduitsSaisie.getChildren().clear();
        btnTerminerSaisie.setDisable(true);
        inputNoCommande.setDisable(false);
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
    }



    public void updateCompteur() {
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

    private Article getSelectedArticle() {
        String articleSelectionner = listArticles.getValue();
        String numero = articleSelectionner.split(" ")[0];
        return articles.get(numero);
    }





}