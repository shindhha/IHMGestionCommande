package com.example.ihmgestioncommande;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionCommandeApplication extends Application {
    public String nomFichierConfig = "configuration.cnfg";
    public String pathFichierConfig = "ressources/conf";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GestionCommandeApplication.class.getResource("vue.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Gestions Commandes");
        stage.setScene(scene);
        stage.show();
        ControllerSaisie c = fxmlLoader.getController();
        c.loadArticle();
    }

    public static void main(String[] args) {

        launch(args);

    }


}