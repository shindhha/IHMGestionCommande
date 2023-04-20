package com.example.ihmgestioncommande;

import com.example.ihmgestioncommande.modeles.Article;
import com.example.ihmgestioncommande.services.FileReader;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
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
    private Scene primaryScene;
    private int maxNbArticleSaisie;
    private int nbArticleSaisie;
    private HashMap<String,Article> articles;
    private TextField inputNumeroSerie;

    private List<String> listNumeroSerie = new ArrayList<>();
    public ControllerSaisie() {
    }

    public void initScene(Scene scene) {
        this.primaryScene = scene;
        btnTerminerSaisie.setDisable(true);
        loadArticle();
    }
    public void loadArticle() {

        try {
            articles = FileReader.readConfigFile(new File("src/main/resources/com/example/ihmgestioncommande/conf/Ref_articles.csv"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (Article article : articles.values()) {
            listArticles.getItems().add(article.toString());
        }

        listArticles.setOnAction(event -> showArticle());

    }



    public void showArticle() {
        articleView.getChildren().clear();
        listActions.getItems().clear();
        Article article = getSelectedArticle();
        for (String nomDuChamp : article.getAttributs().keySet()) {
            String valeurDuChamp = article.getAttributs().get(nomDuChamp);
            Label label = new Label(nomDuChamp + " : \n " + valeurDuChamp);
            label.getStyleClass().add("article-info");
            articleView.getChildren().add(label);

        }
        inputNumeroSerie = new TextField();
        inputNumeroSerie.getStyleClass().add("article-info");
        Button btnValider = new Button("Valider");
        btnValider.getStyleClass().add("article-info");

        btnValider.setOnAction(event -> validerSaisie());
        articleView.getChildren().addAll(Arrays.asList(inputNumeroSerie, btnValider));
        for (String action : article.getActions()) {
            listActions.getItems().add(action);
        }

    }

    public void validerSaisie() {
        if (nbArticleSaisie >= maxNbArticleSaisie) {
            throw new IllegalStateException("Vous avez atteint le nombre maximum d'article à saisir");
        }
        if (inputNumeroSerie.getText().isEmpty()) {
            throw new IllegalArgumentException("Veuillez saisir un numéro de série");
        }
        if (!Pattern.matches(getSelectedArticle().getFormat(), inputNumeroSerie.getText())) {
            throw new IllegalArgumentException("Le numéro de série ne correspond pas au format préciser (" + getSelectedArticle().getFormat() + ")");
        }

        boiteErreur.setText("");
        String numeroSerie = inputNumeroSerie.getText();
        Label label = new Label(numeroSerie);
        listProduitsSaisie.getChildren().add(label);
        listNumeroSerie.add(numeroSerie);
        inputNumeroSerie.clear();


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
            primaryScene.setOnKeyReleased(event -> {
                inputNumeroSerie.requestFocus();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            try {
                                validerSaisie();
                                updateCompteur();
                            } catch (Exception e) {
                                boiteErreur.setText(e.getMessage());
                            }
                        });
                    }
                }, 100);
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
    }




    public void terminerSaisie() throws IOException {
        primaryScene.setOnKeyReleased(null);
        listArticles.setDisable(false);
        listActions.setDisable(false);
        inputNbArticle.setDisable(false);
        btnCommencerSaisie.setDisable(false);
        listProduitsSaisie.getChildren().clear();
        File outputFile = new File("src/main/java/com/example/ihmgestioncommande/output/" + inputNoCommande.getText() +".csv");
        // Je veut que écrire dans mon fichier la liste des numéro de série séparer par des retour a la ligne
    }

    private Article getSelectedArticle() {
        String articleSelectionner = listArticles.getValue();
        String numero = articleSelectionner.split(" ")[0];
        return articles.get(numero);
    }

    public void updateCompteur() {
        nbArticleSaisie++;
        compteurNbProduitSaisie.setText(nbArticleSaisie + "/" + maxNbArticleSaisie);
        if (nbArticleSaisie == maxNbArticleSaisie) {
            btnTerminerSaisie.setDisable(false);
        }

    }





}