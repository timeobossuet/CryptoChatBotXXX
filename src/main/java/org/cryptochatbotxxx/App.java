package org.cryptochatbotxxx;

import java.time.Instant;

import org.cryptochatbotxxx.network.NetworkManager;
import org.cryptochatbotxxx.network.utils.User;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class App extends Application {

    private static NetworkManager network;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CryptoChatBotXXX - Connexion");

        this.showLoginScene(primaryStage);
        primaryStage.show(); 
    }

    public void showLoginScene(Stage stage) 
    {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Connexion");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Button btn = new Button("Se connecter");
        grid.add(btn, 1, 2);

        Text errorText = new Text();
        errorText.setFill(Color.FIREBRICK);
        grid.add(errorText, 0, 3, 2, 1);
        
        btn.setOnAction(e -> {
            String pseudo = userTextField.getText().trim();

            // pseudo is valid ?
            if(false) {
                errorText.setText("Format invalide (3-20 caractères alphanumériques, _ ou -).");
            } else {
                btn.setDisable(true);
                errorText.setText("Vérification de la disponibilité du pseudo ...");

                User old = network.getUser();

                network.setUsername(userTextField.getText(), Instant.now());
                network.setUsernameDoesntNeedToChange();
                network.setUsernameCanChange();
                network.getUDPSender().broadcastTryUsername();

                network.getThreadPool().submit(() -> {
                    try {
                        Thread.sleep(1000);
                        if(network.getUsernameNeedToChange()) {
                            network.setUsername(old.getUsername(), old.getDateCreation());
                            Platform.runLater(() -> {
                                errorText.setText("Pseudo déjà pris, change le");
                                btn.setDisable(false);
                            });
                        } else {
                            System.out.println("[App] Bienvenu : " + userTextField.getText());
                            network.getUDPSender().broadcastPresence();

                            Platform.runLater(() -> {
                                this.showMainScene(stage);
                            });
                        }
                    } catch (InterruptedException ex) {
                    }
                });
            }
        });

        Scene scene = new Scene(grid, 300, 275);
        stage.setScene(scene);
    }

    public void showMainScene(Stage stage) 
    {
        ListView<String> contactList = new ListView<>();
        VBox sidebar = new VBox(10, new Label("Utilisateurs en ligne"), contactList);
        sidebar.setPadding(new Insets(10, 10, 10, 10));
        sidebar.setPrefWidth(220);

        TextField renameField = new TextField();
        renameField.setPromptText("Nouveau pseudo...");
        Button renameBtn = new Button("Changer de pseudo");
        Label statusLabel = new Label();

        renameBtn.setOnAction(e -> {
            String newPseudo = renameField.getText().trim();
            //continue
        });

        HBox topBar = new HBox(10, renameField, renameBtn, statusLabel);
        topBar.setPadding(new Insets(10, 10, 10, 10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(sidebar);
        root.setCenter(new Label("Sélectionnez un utilisateur pour démarrer une session."));

        stage.setScene(new Scene(root, 700, 450));
    }
    
    @Override 
    public void stop() 
    {
        network.stop();
    } 

    public static void main(String[] args) {
        network = new NetworkManager(2, "+unknown");
        network.start();
        launch(args);
    }
}
