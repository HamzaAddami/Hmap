package dev.hmap;

import dev.hmap.config.ThreadPoolManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class HmapMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dev/hmap/fxml/MainView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        String css = getClass().getResource("/dev/hmap/fxml/styles/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("HMap - Network Security Scanner");
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            System.out.println("[*] Closing application...");
            ThreadPoolManager.getInstance().shutdown();
        });

        stage.show();

    }

    public static void main(String[] args)  {
        launch(args);
    }

}

