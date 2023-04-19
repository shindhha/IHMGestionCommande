package com.example.ihmgestioncommande;

import com.example.ihmgestioncommande.modeles.Article;
import com.example.ihmgestioncommande.services.FileReader;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class ControllerSaisie {


    @FXML
    private ChoiceBox<String> listArticles;
    @FXML
    private TextField inputNbArticle;
    @FXML
    private VBox articleView;
    private Stage primaryStage;

    private HashMap<String,Article> articles;
    public ControllerSaisie() {
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

    }

    public void showArticle() {
        String articleSelectionner = listArticles.getValue();
        String numero = articleSelectionner.split(" ")[0];
        Article article = articles.get(numero);
        for (String s : article.getAttributs().keySet()) {
            articleView.getChildren().add(new Label(s + " : " + article.getAttributs().get(s)));
        }
    }





}