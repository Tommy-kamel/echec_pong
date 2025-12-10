package com.example.echec_pong;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        HelloController controller = fxmlLoader.getController();
        
        stage.setTitle("Échec Pong");
        stage.setScene(scene);
        
        // Arrêter tous les processus quand on ferme la fenêtre
        stage.setOnCloseRequest(event -> {
            if(controller != null) {
                controller.cleanup();
            }
            Platform.exit();
            System.exit(0);
        });
        
        stage.show();
    }
}
