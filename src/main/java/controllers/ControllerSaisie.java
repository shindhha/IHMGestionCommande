package controllers;
import javafx.scene.control.ScrollPane;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import exceptions.FormatInvalideException;
import modeles.*;
import services.Configuration;

import java.awt.*;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
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
    private MenuButton outilsConfig;
    @FXML
    private TextField searchBar;
    @FXML
    private ScrollPane scrollNum;
    private Scene primaryScene;
    private TextField inputNumeroSerie;
    private HashMap<String,Article> articles;
    public Commande currentCommande;
    private AdvancedPlayer soundPlayer;

    private Configuration config;
    public static final String nombreMaxArticleDefaut = "1";


    // Mot entrer par l'utilisateur pour la recherche d'un produit dans la liste.
    private StringBuilder mot_recherche_article;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        outilsConfig.setOnMouseClicked(event -> popUpMdp());
        mot_recherche_article = new StringBuilder();
        try {
            config = new Configuration();
        } catch (FileNotFoundException e) {
            boiteErreur.setText("Le fichier de configuration n'a pas été trouvé");
        } catch (IOException e) {
            e.printStackTrace();
        }
        listActions.getItems().add("Ordre de Fabrication");
        listActions.getItems().add("Ordre de Commande");

    }
    public void popUpMdp() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Mot de passe");
        alert.setHeaderText("Veuillez entrer le mot de passe");
        alert.setContentText("Mot de passe : ");
        TextField textField = new TextField();
        textField.setPromptText("Mot de passe");
        alert.getDialogPane().setContent(textField);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            if (isMdpCorrect(textField.getText())) {
                alert.close();
                outilsConfig.show();
            } else {
                alert.close();
                popUpMdp();
            }
        } else {
            alert.close();
        }
    }

    public boolean isMdpCorrect(String mdp) {
        return mdp.equals("0000");
    }
    public ControllerSaisie() {
    }


    /**
     * Etat par defaut de la page de saisie
     * @param scene : Scene principale
     */
    public void initScene(Scene scene) {
        this.primaryScene = scene;
        searchBar.setOnKeyPressed(event -> printArticle(filtrerArticle(searchBar.getText(), articles)));
        loadArticle();
        listArticles.setOnAction(event -> useArticle(getSelectedArticle()));
        enableChamp(listArticles, listActions, inputNbArticle, btnCommencerSaisie, inputNoCommande,searchBar);
        disableChamp(btnTerminerSaisie, btnAnnulerSaisie);
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
        List<String> articlesString = new ArrayList<>();
        articles.values().forEach(article -> articlesString.add(article.toString()));
        Collections.sort(articlesString);
        articlesString.forEach(article -> listArticles.getItems().add(article));

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
        // créer une image avec le qr code de l'article sélectionné
        qrCodeView.setImage(new Image(config.pathQrCodes + selectedArticle.getQrCode()));
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
            Label label = new Label(finalNumeroSerie);
            Button button = new Button("X");
            HBox hbox = new HBox();
            label.widthProperty().addListener((observable, oldValue, newValue) -> {
                double availableSpace = hbox.getWidth() - newValue.intValue() - 50;
                label.setMinWidth(newValue.intValue());
                hbox.setSpacing(availableSpace);
                hbox.layout();
            });
            hbox.getChildren().addAll(label,button);
            button.setOnAction(event -> onDeleteProduit(hbox, finalNumeroSerie));
            listProduitsSaisie.getChildren().add(hbox);
            updateCompteur(currentCommande.size());
            playSound(true);
            boiteErreur.setText("");
        } catch (FormatInvalideException | IllegalStateException e) {
            boiteErreur.setText(e.getMessage());
            playSound(false);
            e.printStackTrace();
        } finally {
            inputNumeroSerie.clear();
        }
    }

    private void playSound(boolean isOk) {
        if (isOk) {
            try {
                soundPlayer = new AdvancedPlayer(getClass().getResourceAsStream("/sound/correct.mp3"));
                soundPlayer.play();
            } catch (JavaLayerException ex) {
                boiteErreur.setText("Erreur lors de la lecture du son");
            }
        } else {
            try {
                soundPlayer = new AdvancedPlayer(getClass().getResourceAsStream("/sound/incorrect.mp3"));
                soundPlayer.play();
            } catch (JavaLayerException ex) {
                boiteErreur.setText("Erreur lors de la lecture du son");
            }
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

            inputNumeroSerie.setPromptText("Saisissez le numéro de série");
            inputNumeroSerie.getStyleClass().add("textField");

            inputNumeroSerieContainer.getChildren().add(inputNumeroSerie);
            inputNumeroSerie.requestFocus();
            disableChamp(listArticles, listActions, inputNbArticle, btnCommencerSaisie, inputNoCommande,searchBar);
            btnAnnulerSaisie.setDisable(false);
            primaryScene.setOnKeyReleased(event -> onSaisieNumeroSerie(event));
            boiteErreur.setText("");
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

                listProduitsSaisie.heightProperty().addListener((observable, oldValue, newValue) -> scrollNum.setVvalue(newValue.doubleValue()));
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
        if (inputNbArticle.getText().isEmpty() || Integer.parseInt(inputNbArticle.getText()) <= 0) throw new IllegalStateException("Vous devez saisir un nombre d'article valide");

        if (listActions.getValue() == null || listActions.getValue().isEmpty()) {
            throw new IllegalStateException("Veuillez sélectionner l'action a effectuer sur l'article");
        }
        CommandeFactory factory = new CommandeFactory(inputNoCommande.getText(),
                getSelectedArticle(),
                Integer.parseInt(inputNbArticle.getText()));

        return factory.createCommande(type);
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
        enableChamp(listArticles, listActions, inputNbArticle, btnCommencerSaisie, inputNoCommande,searchBar);
        disableChamp(btnTerminerSaisie, btnAnnulerSaisie);
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
            currentCommande.makeOutPutFile(config.pathOutPutFolder);
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
        if (currentCommande.size() == 0) compteurNbProduitSaisie.setText("");
        else compteurNbProduitSaisie.setText(nbArticleSaisie + "/" + currentCommande.getNbMaxNumeroSerie());
        if (nbArticleSaisie == currentCommande.getNbMaxNumeroSerie()) btnTerminerSaisie.setDisable(false);
        else if (btnCommencerSaisie.isDisable()) btnTerminerSaisie.setDisable(true);


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
        content.putString(config.pathOutPutFolder);
        clipboard.setContent(content);
    }

    public void changerRepertoireSortie() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choisir le répertoire de sortie");
        File selectedDirectory = directoryChooser.showDialog(primaryScene.getWindow());
        if (selectedDirectory != null) {
            config.pathOutPutFolder = selectedDirectory.getAbsolutePath();
        }
        config.writeConfigFile();
    }

    public void changerFichierArticles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir le fichier d'articles");
        File selectedFile = fileChooser.showOpenDialog(primaryScene.getWindow());
        if (selectedFile != null) {
            config.listArticleConfiguration = selectedFile.getAbsolutePath();
        }
        config.writeConfigFile();
        listArticles.getItems().clear();
        loadArticle();
    }

    private void loadArticle() {
        try {
            articles = config.readArticleConfigFile();
            printArticle(articles);
            boiteErreur.setText("");
        } catch (Exception e) {
            boiteErreur.setText("Le fichier de configuration des articles n'a pas été trouvé");
        }
    }

    public void afficherDocumentation() {

        try {
            Desktop.getDesktop().open(new File(config.pathDocumentation));
        } catch (IOException e) {
            boiteErreur.setText("Le fichier de documentation n'a pas été trouvé");
        }
    }

    public void loadDocumentation() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir le fichier d'articles");
        File selectedDirectory = fileChooser.showOpenDialog(primaryScene.getWindow());
        if (selectedDirectory != null) {
            config.pathDocumentation = selectedDirectory.getAbsolutePath() + "\\";
        }
        config.writeConfigFile();
    }

    public void loadQrCodes() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choisir le répertoire des qrcodes");
        File selectedDirectory = directoryChooser.showDialog(primaryScene.getWindow());
        if (selectedDirectory != null) {
            config.pathQrCodes = selectedDirectory.getAbsolutePath() + "\\";
        }
        config.writeConfigFile();
    }


    /**
     * @return Article articles L'article sélectionné par l'utilisateur dans la liste des articles
     * @throws NullPointerException Si aucun article n'est sélectionné
     */
    public Article getSelectedArticle() throws NullPointerException {

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



    public VBox getListProduitsSaisie() {
        return listProduitsSaisie;
    }





}