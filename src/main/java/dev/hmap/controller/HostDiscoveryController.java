package dev.hmap.controller;

import dev.hmap.enums.HostStatus;
import dev.hmap.model.Host;
import dev.hmap.service.scanner.HostDiscoveryService;
import dev.hmap.service.scanner.HostService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.util.function.Consumer;

public class HostDiscoveryController {

    // ========== UI Components - Input ==========
    @FXML private TextField cidrInput;
    @FXML private Button scanButton;
    @FXML private Button clearButton;
    @FXML private Button stopScanButton;

    // ========== UI Components - Stats ==========
    @FXML private Label totalDevicesLabel;
    @FXML private Label onlineDevicesLabel;
    @FXML private Label scanningLabel;

    // ========== UI Components - Table ==========
    @FXML private TableView<Host> hostTable;
    @FXML private TableColumn<Host, String> ipColumn;
    @FXML private TableColumn<Host, String> hostnameColumn;
    @FXML private TableColumn<Host, String> statusColumn;
    @FXML private TableColumn<Host, String> osColumn;
    @FXML private TableColumn<Host, Long> latencyColumn;
    @FXML private TableColumn<Host, String> macAddressColumn;
    @FXML private TableColumn<Host, Void> actionColumn;

    // ========== UI Components - Footer ==========
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;

    // ========== Services ==========
    private final HostDiscoveryService discoveryService = new HostDiscoveryService();
    private final HostService hostService = new HostService();
    private final ObservableList<Host> hostList = FXCollections.observableArrayList();

    // ========== State ==========
    private boolean isScanning = false;
    private int discoveredCount = 0;
    private Consumer<String> portScannerCallback;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupTableStyling();
        setupDefaultValues();
        setupActionColumn();
        hostTable.setItems(hostList);
    }

    /**
     * Callback pour naviguer vers Port Scanner avec l'IP sélectionnée
     */
    public void setPortScannerCallback(Consumer<String> callback) {
        this.portScannerCallback = callback;
    }

    /**
     * Configuration des colonnes du tableau
     */
    private void setupTableColumns() {
        // IP Column
        ipColumn.setCellValueFactory(cellData -> {
            String ip = cellData.getValue().getIpString();
            return new javafx.beans.property.SimpleStringProperty(ip != null ? ip : "N/A");
        });

        // Hostname Column
        hostnameColumn.setCellValueFactory(cellData -> {
            String hostname = cellData.getValue().getHostName();
            return new javafx.beans.property.SimpleStringProperty(hostname != null ? hostname : "Unknown");
        });

        // Status Column
        statusColumn.setCellValueFactory(cellData -> {
            HostStatus status = cellData.getValue().getStatus();
            return new javafx.beans.property.SimpleStringProperty(status != null ? status.toString() : "UNKNOWN");
        });

        // MAC Address Column
        macAddressColumn.setCellValueFactory(cellData -> {
            String macAddress = cellData.getValue().getMacAddress();
            return new javafx.beans.property.SimpleStringProperty(macAddress != null ? macAddress : "UNKNOWN");
        });

        // OS Column
        osColumn.setCellValueFactory(cellData -> {
            var os = cellData.getValue().getOsFamily();
            return new javafx.beans.property.SimpleStringProperty(os != null ? os.toString() : "UNKNOWN");
        });

        // Latency Column
        latencyColumn.setCellValueFactory(cellData -> {
            long latency = cellData.getValue().getLatency();
            return new javafx.beans.property.SimpleLongProperty(latency).asObject();
        });

        // ========== Custom Cell Factories ==========

        // Status Column - Colored
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
                        setStyle("-fx-text-fill: #10B981; -fx-font-weight: 700; -fx-font-size: 13px;");
                    } else if (status.equals(HostStatus.DOWN.toString())) {
                        setStyle("-fx-text-fill: #EF4444; -fx-font-weight: 700; -fx-font-size: 13px;");
                    } else {
                        setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: 700; -fx-font-size: 13px;");
                    }
                }
            }
        });

        // Latency Column - Colored with ms
        latencyColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Long latency, boolean empty) {
                super.updateItem(latency, empty);
                if (empty || latency == null || latency == 0) {
                    setText("—");
                    setStyle("-fx-text-fill: #64748B;");
                } else {
                    setText(latency + " ms");
                    if (latency < 50) {
                        setStyle("-fx-text-fill: #10B981; -fx-font-weight: 600;");
                    } else if (latency < 150) {
                        setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: 600;");
                    } else {
                        setStyle("-fx-text-fill: #EF4444; -fx-font-weight: 600;");
                    }
                }
            }
        });

        // Hostname Column - Italic if unknown
        hostnameColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String hostname, boolean empty) {
                super.updateItem(hostname, empty);
                if (empty || hostname == null || hostname.isEmpty() || hostname.equals("Unknown")) {
                    setText("Unknown");
                    setStyle("-fx-text-fill: #64748B; -fx-font-style: italic;");
                } else {
                    setText(hostname);
                    setStyle("-fx-text-fill: #F1F5F9; -fx-font-weight: 500;");
                }
            }
        });

        // MAC Address Column
        macAddressColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String mac, boolean empty) {
                super.updateItem(mac, empty);
                if (empty || mac == null || mac.equals("UNKNOWN")) {
                    setText("UNKNOWN");
                    setStyle("-fx-text-fill: #64748B; -fx-font-style: italic;");
                } else {
                    setText(mac);
                    setStyle("-fx-text-fill: #94A3B8; -fx-font-family: 'Consolas', 'Monaco', monospace;");
                }
            }
        });
    }

    /**
     * Configuration de la colonne Actions avec bouton "Scan Ports"
     */
    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button scanPortsBtn = new Button("Scan Ports");

            {
                scanPortsBtn.getStyleClass().add("action-button");
                scanPortsBtn.setOnAction(event -> {
                    Host host = getTableView().getItems().get(getIndex());
                    if (portScannerCallback != null && host != null) {
                        portScannerCallback.accept(host.getIpString());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(scanPortsBtn);
                }
            }
        });
    }

    /**
     * Configuration du style du tableau
     */
    private void setupTableStyling() {
        Label placeholder = new Label(
                "🔍 No devices discovered yet\n\n" +
                        "Enter a subnet in CIDR notation (e.g., 192.168.1.0/24)\n" +
                        "and click 'Scan Network' to begin discovery"
        );
        placeholder.setStyle(
                "-fx-text-fill: #64748B; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-alignment: center; " +
                        "-fx-padding: 40;"
        );
        hostTable.setPlaceholder(placeholder);
    }

    /**
     * Initialisation des valeurs par défaut
     */
    private void setupDefaultValues() {
        totalDevicesLabel.setText("0");
        onlineDevicesLabel.setText("0");
        scanningLabel.setText("Ready");
        statusLabel.setText("Ready to scan network");
        progressBar.setProgress(0);
        progressBar.setVisible(false);
        stopScanButton.setVisible(false);
    }

    /**
     * Handler du bouton Scan
     */
    @FXML
    private void handleScan() {
        if (isScanning) {
            showAlert("Scan in Progress",
                    "A network scan is already running.\n" +
                            "Please wait for it to complete or stop it using the Stop button.",
                    Alert.AlertType.WARNING);
            return;
        }

        String cidr = cidrInput.getText().trim();
        if (cidr.isEmpty()) {
            showAlert("Invalid Input",
                    "Please enter a valid subnet in CIDR notation.\n\n" +
                            "Examples:\n" +
                            "  • 192.168.1.0/24\n" +
                            "  • 10.0.0.0/16\n" +
                            "  • 172.16.0.0/12",
                    Alert.AlertType.ERROR);
            return;
        }

        // Validation CIDR basique
        if (!cidr.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}/\\d{1,2}$")) {
            showAlert("Invalid CIDR Format",
                    "The subnet format is incorrect.\n\n" +
                            "Please use the format: IP/MASK\n" +
                            "Example: 192.168.1.0/24",
                    Alert.AlertType.ERROR);
            return;
        }

        startScan(cidr);
    }

    /**
     * Démarrage du scan réseau
     */
    private void startScan(String cidr) {
        isScanning = true;
        discoveredCount = 0;
        hostList.clear();

        updateScanningState(true);

        new Thread(() -> {
            try {
                discoveryService.discoverHost(cidr, host -> {
                    try {
                        // S'assurer que les données sont définies
                        if (host.getStatus() == null) {
                            host.setStatus(HostStatus.UP);
                        }
                        if (host.getOsFamily() == null) {
                            host.setOsFamily(dev.hmap.enums.OsFamily.UNKNOWN);
                        }

                        Host savedHost = hostService.registerHost(host);

                        Platform.runLater(() -> {
                            if (isScanning) {
                                hostList.add(savedHost);
                                discoveredCount++;
                                updateStats();
                                hostTable.refresh();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            statusLabel.setText("❌ Error saving host: " + host.getIpString());
                        });
                    }
                });

                // Attendre un peu pour les dernières tâches
                Thread.sleep(2000);

                Platform.runLater(() -> {
                    if (isScanning) {
                        finalizeScan();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    updateScanningState(false);
                    statusLabel.setText("❌ Scan failed: " + e.getMessage());
                    isScanning = false;
                });
            }
        }, "ScanThread").start();
    }

    /**
     * Mise à jour de l'état de scanning
     */
    private void updateScanningState(boolean scanning) {
        if (scanning) {
            scanButton.setDisable(true);
            scanButton.setText("⏳ Scanning...");
            stopScanButton.setVisible(true);
            progressBar.setVisible(true);
            progressBar.setProgress(-1); // Indeterminate
            scanningLabel.setText("In Progress");
            scanningLabel.setStyle("-fx-text-fill: #6366F1; -fx-font-weight: 700;");
            statusLabel.setText("🔍 Discovering devices on network...");
        } else {
            scanButton.setDisable(false);
            scanButton.setText("🚀 Scan Network");
            stopScanButton.setVisible(false);
            progressBar.setVisible(false);
            scanningLabel.setText("Complete");
            scanningLabel.setStyle("-fx-text-fill: #10B981; -fx-font-weight: 700;");

            // Reset après 3 secondes
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                scanningLabel.setText("Ready");
                scanningLabel.setStyle("-fx-text-fill: #64748B; -fx-font-weight: 600;");
            }));
            timeline.play();
        }
    }

    /**
     * Mise à jour des statistiques
     */
    private void updateStats() {
        totalDevicesLabel.setText(String.valueOf(hostList.size()));

        long onlineCount = hostList.stream()
                .filter(host -> host.getStatus() == HostStatus.UP)
                .count();
        onlineDevicesLabel.setText(String.valueOf(onlineCount));

        statusLabel.setText("✅ Discovered: " + discoveredCount + " device(s)");
    }

    /**
     * Finalisation du scan
     */
    private void finalizeScan() {
        isScanning = false;
        updateScanningState(false);
        updateStats();

        if (hostList.isEmpty()) {
            statusLabel.setText("⚠️ Scan complete - No devices found on this network");
            showAlert("Scan Complete",
                    "No active devices were found on the network.\n\n" +
                            "This could mean:\n" +
                            "  • The network is empty\n" +
                            "  • Devices have firewalls blocking ICMP\n" +
                            "  • The subnet mask is incorrect",
                    Alert.AlertType.INFORMATION);
        } else {
            statusLabel.setText("✅ Scan complete - Found " + hostList.size() + " device(s)");
        }
    }

    /**
     * Handler du bouton Clear
     */
    @FXML
    private void handleClear() {
        if (isScanning) {
            showAlert("Cannot Clear",
                    "A scan is currently in progress.\n" +
                            "Please wait for it to complete or stop it first.",
                    Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Clear Table");
        confirmation.setHeaderText("Clear all discovered devices?");
        confirmation.setContentText("This will remove all " + hostList.size() + " device(s) from the table.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                hostList.clear();
                cidrInput.clear();
                setupDefaultValues();
                statusLabel.setText("🗑️ Table cleared");
            }
        });
    }

    /**
     * Handler du bouton Stop Scan
     */
    @FXML
    private void handleStopScan() {
        if (isScanning) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Stop Scan");
            confirmation.setHeaderText("Stop the current network scan?");
            confirmation.setContentText("Already discovered devices will be kept in the table.");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    isScanning = false;
                    discoveryService.shutdown();
                    updateScanningState(false);
                    statusLabel.setText("🛑 Scan stopped by user - Found " + hostList.size() + " device(s)");
                }
            });
        }
    }

    /**
     * Affichage d'une alerte
     */
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}