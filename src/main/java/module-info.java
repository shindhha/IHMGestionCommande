module com.example.ihmgestioncommande {
    requires javafx.fxml;
    requires javafx.controls;


    opens com.example.ihmgestioncommande to javafx.fxml;
    exports com.example.ihmgestioncommande;
    exports com.example.ihmgestioncommande.modeles;
    opens com.example.ihmgestioncommande.modeles to javafx.fxml;
    exports com.example.ihmgestioncommande.services;
    opens com.example.ihmgestioncommande.services to javafx.fxml;
}