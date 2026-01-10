package dev.hmap;

import dev.hmap.config.ThreadPoolManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class HmapMain extends Application {

    private static final String APP_TITLE = "HMap - Network Security Scanner";
    private static final int MIN_WIDTH = 1200;
    private static final int MIN_HEIGHT = 800;
    private static final int DEFAULT_WIDTH = 1400;
    private static final int DEFAULT_HEIGHT = 900;

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dev/hmap/fxml/MainView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            String css = getClass().getResource("/dev/hmap/fxml/styles/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);


            primaryStage.setOnCloseRequest(event -> {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("[*] Closing HMap Application...");
                System.out.println("=".repeat(60));

                // Shutdown ThreadPoolManager
                try {
                    ThreadPoolManager.getInstance().shutdown();
                    System.out.println("[✓] Thread pools shut down successfully");
                } catch (Exception e) {
                    System.err.println("[✗] Error shutting down thread pools: " + e.getMessage());
                }

                Platform.exit();
                System.exit(0);
            });

            primaryStage.show();

            printStartupBanner();

        } catch (IOException e) {
            System.err.println("[✗] Failed to load application: " + e.getMessage());
            e.printStackTrace();
            showErrorAndExit("Failed to load application resources.\n" + e.getMessage());
        } catch (Exception e) {
            System.err.println("[✗] Unexpected error during startup: " + e.getMessage());
            e.printStackTrace();
            showErrorAndExit("Unexpected error occurred.\n" + e.getMessage());
        }
    }

    private void printStartupBanner() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("   ██╗  ██╗███╗   ███╗ █████╗ ██████╗ ");
        System.out.println("   ██║  ██║████╗ ████║██╔══██╗██╔══██╗");
        System.out.println("   ███████║██╔████╔██║███████║██████╔╝");
        System.out.println("   ██╔══██║██║╚██╔╝██║██╔══██║██╔═══╝ ");
        System.out.println("   ██║  ██║██║ ╚═╝ ██║██║  ██║██║     ");
        System.out.println("   ╚═╝  ╚═╝╚═╝     ╚═╝╚═╝  ╚═╝╚═╝     ");
        System.out.println();
        System.out.println("   Hmap Security Scanner v1.0");
        System.out.println("   Host Discovery & Port Analysis Tool");
        System.out.println("=".repeat(60));
        System.out.println("[✓] Application started successfully");
        System.out.println("[✓] Thread Pool Manager initialized");
        System.out.println("[i] Available CPU Cores: " + Runtime.getRuntime().availableProcessors());
        System.out.println("[i] Max Memory: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB");
        System.out.println("=".repeat(60) + "\n");
    }

    private void showErrorAndExit(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle("HMap - Fatal Error");
        alert.setHeaderText("Application Failed to Start");
        alert.setContentText(message);
        alert.showAndWait();
        Platform.exit();
        System.exit(1);
    }

    @Override
    public void stop() throws Exception {
        System.out.println("[*] Application stop() method called");
        super.stop();
    }

    public static void main(String[] args) {

        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        launch(args);
    }
}