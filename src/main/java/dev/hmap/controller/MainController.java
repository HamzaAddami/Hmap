package dev.hmap.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import dev.hmap.config.ThreadPoolManager;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.io.IOException;

public class MainController {

    @FXML private VBox hostDiscoveryTab;
    @FXML private VBox portScannerTab;
    @FXML private StackPane contentArea;
    @FXML private Label threadStatsLabel;

    private final ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
    private HostDiscoveryController hostDiscoveryController;
    private PortScanController portScannerController;

    @FXML
    public void initialize() {
        System.out.println("[*] MainController initialized");
        loadHostDiscoveryView();
        startThreadMonitoring();
    }

    @FXML
    private void handleHostDiscoveryTab() {
        System.out.println("[*] Switching to Host Discovery view");
        loadHostDiscoveryView();
        updateTabStyles(hostDiscoveryTab, portScannerTab);
    }

    @FXML
    private void handlePortScannerTab() {
        System.out.println("[*] Switching to Port Scanner view");
        loadPortScannerView();
        updateTabStyles(portScannerTab, hostDiscoveryTab);
    }

    private void loadHostDiscoveryView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/dev/hmap/fxml/HostDiscoveryView.fxml"));

            if (loader.getLocation() == null) {
                System.err.println("[✗] HostDiscoveryView.fxml not found!");
                System.err.println("[i] Tried path: /dev/hmap/fxml/HostDiscoveryView.fxml");
                return;
            }

            Parent view = loader.load();
            hostDiscoveryController = loader.getController();
            hostDiscoveryController.setPortScannerCallback(this::switchToPortScannerWithHost);
            contentArea.getChildren().setAll(view);

            System.out.println("[✓] Host Discovery view loaded successfully");

        } catch (IOException e) {
            System.err.println("[✗] Error loading HostDiscoveryView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPortScannerView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/dev/hmap/fxml/PortScannerView.fxml"));

            if (loader.getLocation() == null) {
                System.err.println("[✗] PortScannerView.fxml not found!");
                System.err.println("[i] Tried path: /dev/hmap/fxml/PortScannerView.fxml");
                return;
            }

            Parent view = loader.load();
            portScannerController = loader.getController();
            contentArea.getChildren().setAll(view);

            System.out.println("[✓] Port Scanner view loaded successfully");

        } catch (IOException e) {
            System.err.println("[✗] Error loading PortScannerView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void switchToPortScannerWithHost(String hostIp) {
        System.out.println("[*] Switching to Port Scanner with host: " + hostIp);
        loadPortScannerView();
        updateTabStyles(portScannerTab, hostDiscoveryTab);
        if (portScannerController != null) {
            portScannerController.setTargetHost(hostIp);
        }
    }

    private void updateTabStyles(VBox activeTab, VBox inactiveTab) {
        activeTab.getStyleClass().removeAll("tab-inactive");
        if (!activeTab.getStyleClass().contains("tab-active")) {
            activeTab.getStyleClass().add("tab-active");
        }

        inactiveTab.getStyleClass().removeAll("tab-active");
        if (!inactiveTab.getStyleClass().contains("tab-inactive")) {
            inactiveTab.getStyleClass().add("tab-inactive");
        }
    }

    private void startThreadMonitoring() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            String stats = threadPoolManager.getSummary();
            threadStatsLabel.setText(stats);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        System.out.println("[✓] Thread monitoring started");
    }
}