package org.cryptochatbotxxx.ui.gui;

import java.util.function.Consumer;

import org.cryptochatbotxxx.network.NetworkManager;
import org.cryptochatbotxxx.network.UsernameCallback;
import org.cryptochatbotxxx.network.User;
import org.cryptochatbotxxx.ui.UserView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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

public class GuiView extends Application implements UserView 
{
    private static final String APP_TITLE = "CryptoChatBotXXX";

    // JavaFX instantiates GuiView via the no-arg constructor, so shared state is static
    private static NetworkManager network;
    private static String[] launchArgs = new String[0];

    private final ObservableList<User> onlineUsers = FXCollections.observableArrayList();

    public GuiView(NetworkManager network, String[] args) 
    {
        GuiView.network = network;
        GuiView.launchArgs = args;
    }

    /**
     * Required by JavaFX to instantiate the UI.
     */
    public GuiView() {}

    /**
     * Launches the JavaFX application thread (blocking until the window is closed).
     */
    @Override
    public void run() 
    {
        Application.launch(GuiView.class, launchArgs);
    }

    // started by the javafx thread
    @Override
    public void start(Stage primaryStage) 
    {
        network.setView(this);
        primaryStage.setTitle(APP_TITLE + " - Connexion");
        showLoginScene(primaryStage);
        primaryStage.show();
    }

    // Scene builders
    private void showLoginScene(Stage stage) 
    {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25));

        Text sceneTitle = new Text("Connexion");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        grid.add(new Label("Username :"), 0, 1);

        TextField userField = new TextField();
        Button loginBtn = new Button("Se connecter");
        Text statusText = new Text();

        grid.add(userField, 1, 1);
        grid.add(loginBtn, 1, 2);
        grid.add(statusText, 0, 3, 2, 1);

        Runnable submitLogin = () -> handleUsernameRequest(stage, userField, loginBtn, statusText);
        loginBtn.setOnAction(e -> submitLogin.run());
        userField.setOnAction(e -> submitLogin.run());

        stage.setScene(new Scene(grid, 350, 275));
    }

    private void showMainScene(Stage stage) 
    {
        ListView<User> contactList = createContactListView();
        VBox sidebar = new VBox(10, new Label("Utilisateurs en ligne"), contactList);
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefWidth(220);

        TextField renameField = new TextField();
        renameField.setPromptText("Nouveau pseudo...");
        Button renameBtn = new Button("Changer de pseudo");
        Text statusText = new Text();

        Runnable submitRename = () -> handleUsernameRequest(stage, renameField, renameBtn, statusText);
        renameBtn.setOnAction(e -> submitRename.run());
        renameField.setOnAction(e -> submitRename.run());

        HBox topBar = new HBox(10, renameField, renameBtn, statusText);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(sidebar);
        root.setCenter(new Label("Sélectionnez un utilisateur pour démarrer une session."));

        stage.setScene(new Scene(root, 700, 450));
    }

    private ListView<User> createContactListView() 
    {
        ListView<User> listView = new ListView<>(onlineUsers);
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText((empty || user == null) ? null : formatUser(user));
            }
        });
        return listView;
    }

    // UI action handlers
    private void handleUsernameRequest(Stage stage, TextField userField, Button btn, Text statusText) 
    {
        statusText.setFill(Color.GRAY);
        statusText.setText("Vérification de la disponibilité du pseudo...");

        submitUsernameRequest(
            userField.getText().toLowerCase().trim(),
            btn,
            acceptedUsername -> {
                stage.setTitle(APP_TITLE + " - " + acceptedUsername);
                showMainScene(stage);
            },
            reason -> {
                statusText.setFill(Color.FIREBRICK);
                statusText.setText(reason);
            }
        );
    }

    private void submitUsernameRequest(String rawUsername, Button triggerBtn, 
        Consumer<String> onSuccessUi, Consumer<String> onFailureUi) 
    {
        triggerBtn.setDisable(true);

        network.requestUsername(rawUsername.trim(), new UsernameCallback() {
            @Override
            public void onSuccess(String acceptedUsername) {
                Platform.runLater(() -> {
                    triggerBtn.setDisable(false);
                    onSuccessUi.accept(acceptedUsername);
                });
            }

            @Override
            public void onFailure(String reason) {
                Platform.runLater(() -> {
                    triggerBtn.setDisable(false);
                    onFailureUi.accept(reason);
                });
            }
        });
    }

    // Event methods
    @Override
    public void onMessageReceived(User sender, String message) 
    {
        // Pending messaging implementation
    }

    @Override
    public void onUserConnected(User user) 
    {
        Platform.runLater(() -> {
            if (!onlineUsers.contains(user)) {
                onlineUsers.add(user);
            }
        });
    }

    @Override
    public void onUserDisconnected(User user) 
    {
        Platform.runLater(() -> onlineUsers.remove(user));
    }

    @Override
    public void onError(String errorMessage) 
    {
        System.err.println("[GUI Error] " + errorMessage);
    }

    @Override
    public void stop() 
    {
        if (network != null) {
            network.stop();
        }
    }

    private String formatUser(User user) 
    {
        return user.getUsername() + " (" + user.getAddress().getHostAddress() + ")";
    }
}