package org.cryptochatbotxxx;

import java.time.Instant;

import org.cryptochatbotxxx.network.NetworkManager;
import org.cryptochatbotxxx.network.utils.User;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import static javafx.geometry.HPos.RIGHT;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class App extends Application {

    private static NetworkManager network;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Welcome");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 6);
        grid.setColumnSpan(actiontarget, 2);
        grid.setHalignment(actiontarget, RIGHT);
        actiontarget.setId("actiontarget");

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                User old = network.getUser();

                actiontarget.setText("");

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
                                actiontarget.setFill(Color.FIREBRICK);
                                actiontarget.setText("Pseudo déjà pris, change le");
                            });
                        } else {
                            System.out.println("[App] Bienvenu : " + userTextField.getText());
                            network.getUDPSender().broadcastPresence();
                        }
                    } catch (InterruptedException ex) {
                    }
                });
            }
        });

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    @Override 
    public void stop() 
    {
        network.stop();
    } 

    public static void main(String[] args) {
        network = new NetworkManager(2, "unknown");
        network.start();
        launch(args);
    }
}
