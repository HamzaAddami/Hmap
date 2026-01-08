package dev.hmap.controller;

import dev.hmap.enums.HostStatus;
import dev.hmap.model.Host;
import dev.hmap.service.scanner.impl.HostDiscoveryServiceImpl;
import dev.hmap.service.scanner.impl.HostServiceImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class MainController {

    // UI Components - Search
    @FXML private TextField cidrInput;
    @FXML private Button scanButton;
    @FXML private Button clearButton;
    @FXML private Button stopScan;

    // UI Components - Stats
    @FXML private Label totalDevicesLabel;
    @FXML private Label onlineDevicesLabel;
    @FXML private Label scanningLabel;

    // UI Components - Table
    @FXML private TableView<Host> hostTable;
    @FXML private TableColumn<Host, String> ipColumn;
    @FXML private TableColumn<Host, String> hostnameColumn;
    @FXML private TableColumn<Host, String> statusColumn;
    @FXML private TableColumn<Host, String> osColumn;
    @FXML private TableColumn<Host, Long> latencyColumn;
    @FXML private TableColumn<Host, String> macAddressColumn;

    // UI Components - Footer
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;

    // Services
    private final HostDiscoveryServiceImpl discoveryService = new HostDiscoveryServiceImpl();
    private final HostServiceImpl hostService = new HostServiceImpl();
    private final ObservableList<Host> hostList = FXCollections.observableArrayList();

    // State
    private boolean isScanning = false;
    private int discoveredCount = 0;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupTableStyling();
        setupDefaultValues();
        hostTable.setItems(hostList);
    }

    private void setupTableColumns() {
        // Utiliser des cellValueFactory avec callback pour s'assurer que les valeurs sont récupérées
        ipColumn.setCellValueFactory(cellData -> {
            String ip = cellData.getValue().getIpString();
            return new javafx.beans.property.SimpleStringProperty(ip != null ? ip : "N/A");
        });

        hostnameColumn.setCellValueFactory(cellData -> {
            String hostname = cellData.getValue().getHostName();
            return new javafx.beans.property.SimpleStringProperty(hostname != null ? hostname : "Unknown");
        });

        statusColumn.setCellValueFactory(cellData -> {
            HostStatus status = cellData.getValue().getStatus();
            return new javafx.beans.property.SimpleStringProperty(status != null ? status.toString() : "UNKNOWN");
        });

        macAddressColumn.setCellValueFactory(cellData -> {
            String macAddress = cellData.getValue().getMacAddress();
            return new javafx.beans.property.SimpleStringProperty(macAddress != null ? macAddress : "UNKNOWN");
        });

        osColumn.setCellValueFactory(cellData -> {
            var os = cellData.getValue().getOsFamily();
            return new javafx.beans.property.SimpleStringProperty(os != null ? os.toString() : "UNKNOWN");
        });

        latencyColumn.setCellValueFactory(cellData -> {
            long latency = cellData.getValue().getLatency();
            return new javafx.beans.property.SimpleLongProperty(latency).asObject();
        });

        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals(HostStatus.UP.toString())) {
                        setStyle("-fx-text-fill: #10B981; -fx-font-weight: 600;");
                    } else if (status.equals(HostStatus.DOWN.toString())) {
                        setStyle("-fx-text-fill: #EF4444; -fx-font-weight: 600;");
                    } else {
                        setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: 600;");
                    }
                }
            }
        });

        latencyColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Long latency, boolean empty) {
                super.updateItem(latency, empty);
                if (empty || latency == null || latency == 0) {
                    setText("—");
                    setStyle("");
                } else {
                    setText(latency + " ms");
                    if (latency < 50) {
                        setStyle("-fx-text-fill: #10B981;");
                    } else if (latency < 150) {
                        setStyle("-fx-text-fill: #F59E0B;");
                    } else {
                        setStyle("-fx-text-fill: #EF4444;");
                    }
                }
            }
        });

        hostnameColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String hostname, boolean empty) {
                super.updateItem(hostname, empty);
                if (empty || hostname == null || hostname.isEmpty()) {
                    setText("Unknown");
                    setStyle("-fx-text-fill: #94A3B8; -fx-font-style: italic;");
                } else {
                    setText(hostname);
                    setStyle("");
                }
            }
        });

    }

    private void setupTableStyling() {
        Label placeholder = new Label("No devices discovered yet.\nEnter a subnet (e.g., 192.168.1.0/24) and click 'Scan Network' to begin.");
        placeholder.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px; -fx-text-alignment: center;");
        hostTable.setPlaceholder(placeholder);
    }

    private void setupDefaultValues() {
        totalDevicesLabel.setText("0");
        onlineDevicesLabel.setText("0");
        scanningLabel.setText("Ready");
        statusLabel.setText("Ready to scan");
        progressBar.setProgress(0);
        progressBar.setVisible(false);
    }

    @FXML
    private void handleScan() {
        if (isScanning) {
            showAlert("Scan in Progress", "A scan is already running. Please wait for it to complete.");
            return;
        }

        String cidr = cidrInput.getText().trim();
        if (cidr.isEmpty()) {
            showAlert("Invalid Input", "Please enter a valid subnet in CIDR notation (e.g., 192.168.1.0/24)");
            return;
        }

        startScan(cidr);
    }

    private void startScan(String cidr) {
        isScanning = true;
        discoveredCount = 0;
        hostList.clear();

        // Update UI
        updateScanningState(true);

        // Start discovery
        new Thread(() -> {
            try {
                discoveryService.discoverHost(cidr, host -> {
                    try {
                        // S'assurer que les données de base sont définies
                        if (host.getStatus() == null) {
                            host.setStatus(HostStatus.UP);
                        }
                        if (host.getOsFamily() == null) {
                            host.setOsFamily(dev.hmap.enums.OsFamily.UNKNOWN);
                        }

                        Host savedHost = hostService.registerHost(host);

                        Platform.runLater(() -> {
                            // Debug: vérifier les valeurs
                            System.out.println("Adding host: IP=" + savedHost.getIpString() +
                                    ", Hostname=" + savedHost.getHostName() +
                                    ", Status=" + savedHost.getStatus());

                            hostList.add(savedHost);
                            discoveredCount++;
                            updateStats();

                            // Forcer le rafraîchissement du tableau
                            hostTable.refresh();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            statusLabel.setText("Error saving host: " + host.getIpString());
                        });
                    }
                });

                // Scan complete
                Platform.runLater(() -> {
                    finalizeScan();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    updateScanningState(false);
                    statusLabel.setText("Scan failed: " + e.getMessage());
                    isScanning = false;
                });
            }
        }).start();
    }

    private void updateScanningState(boolean scanning) {
        if (scanning) {
            scanButton.setDisable(true);
            scanButton.setText("Scanning...");
            progressBar.setVisible(true);
            progressBar.setProgress(-1); // Indeterminate
            scanningLabel.setText("In Progress");
            scanningLabel.setStyle("-fx-text-fill: #6366F1;");
            statusLabel.setText("Discovering devices on network...");
        } else {
            scanButton.setDisable(false);
            scanButton.setText("Scan Network");
            progressBar.setVisible(false);
            scanningLabel.setText("Complete");
            scanningLabel.setStyle("-fx-text-fill: #10B981;");

            // Reset after 3 seconds
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                scanningLabel.setText("Ready");
                scanningLabel.setStyle("-fx-text-fill: #64748B;");
            }));
            timeline.play();
        }
    }

    private void updateStats() {
        totalDevicesLabel.setText(String.valueOf(hostList.size()));

        long onlineCount = hostList.stream()
                .filter(host -> host.getStatus() == HostStatus.UP)
                .count();
        onlineDevicesLabel.setText(String.valueOf(onlineCount));

        statusLabel.setText("Discovered: " + discoveredCount + " device(s)");
    }

    private void finalizeScan() {
        isScanning = false;
        updateScanningState(false);
        updateStats();

        if (hostList.isEmpty()) {
            statusLabel.setText("Scan complete - No devices found");
        } else {
            statusLabel.setText("Scan complete - Found " + hostList.size() + " device(s)");
        }
    }

    @FXML
    private void handleClear() {
        if (isScanning) {
            showAlert("Cannot Clear", "Please wait for the current scan to complete.");
            return;
        }

        hostList.clear();
        cidrInput.clear();
        setupDefaultValues();
        statusLabel.setText("Table cleared");
    }

    @FXML
    private void handleStopScan(){
        isScanning = !isScanning;
        discoveryService.shutdown();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}