
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import controllers.ControllerSaisie;

import java.io.IOException;

public class GestionCommandeApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/vue.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Scan numéro de série");
        stage.setScene(scene);
        stage.show();
        ControllerSaisie controller = fxmlLoader.getController();

        controller.initScene(scene);

    }


    public static void main(String[] args) {

        launch(args);

    }


}