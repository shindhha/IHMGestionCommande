package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Launch;
import main.java.controllers.ControllerSaisie;

import java.io.IOException;

public class GestionCommandeApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launch.class.getResource("resources/views/vue.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Gestions Commandes");
        stage.setScene(scene);
        stage.show();
        ControllerSaisie controller = fxmlLoader.getController();

        controller.initScene(scene);

    }


    public static void main(String[] args) {

        launch(args);

    }


}