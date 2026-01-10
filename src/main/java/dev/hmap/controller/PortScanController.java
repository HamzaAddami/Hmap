package dev.hmap.controller;

import dev.hmap.enums.PortState;
import dev.hmap.enums.ScanType;
import dev.hmap.model.Host;
import dev.hmap.model.Port;
import dev.hmap.model.ScanResult;
import dev.hmap.service.scanner.HostService;
import dev.hmap.service.scanner.PortScanService;
import dev.hmap.utils.PortGenerator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PortScanController {

    @FXML private TextField targetInput;
    @FXML private ComboBox<String> scanTypeCombo;
    @FXML private ComboBox<String> portRangeCombo;
    @FXML private TextField customPortsInput;
    @FXML private Button scanButton;
    @FXML private Button stopScanButton;
    @FXML private Button clearButton;

    @FXML private Label totalPortsLabel;
    @FXML private Label openPortsLabel;
    @FXML private Label closedPortsLabel;
    @FXML private Label filteredPortsLabel;
    @FXML private Label scanningStatusLabel;

    @FXML private TableView<Port> portTable;
    @FXML private TableColumn<Port, Integer> portNumberColumn;
    @FXML private TableColumn<Port, String> stateColumn;
    @FXML private TableColumn<Port, String> serviceColumn;
    @FXML private TableColumn<Port, String> protocolColumn;

    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;

    private final PortScanService portScanService = new PortScanService();
    private final HostService hostService = new HostService();
    private final ObservableList<Port> portList = FXCollections.observableArrayList();

    private boolean isScanning = false;
    private Future<ScanResult> currentScanFuture;
    private Timeline progressTimeline;

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTableColumns();
        setupTableStyling();
        setupDefaultValues();
        portTable.setItems(portList);
    }

    public void setTargetHost(String hostIp) {
        targetInput.setText(hostIp);
    }

    private void setupComboBoxes() {
        scanTypeCombo.setItems(FXCollections.observableArrayList(
                "TCP Connect",
                "SYN Scan",
                "UDP Scan"
        ));
        scanTypeCombo.setValue("TCP Connect");

        portRangeCombo.setItems(FXCollections.observableArrayList(
                "Common Ports",
                "Well Known (1-100)",
                "Common UDP",
                "Custom Range"
        ));
        portRangeCombo.setValue("Common Ports");

        portRangeCombo.setOnAction(e -> {
            customPortsInput.setDisable(!portRangeCombo.getValue().equals("Custom Range"));
        });
    }

    private void setupTableColumns() {
        portNumberColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getPortNumber()).asObject());

        stateColumn.setCellValueFactory(cellData -> {
            PortState state = cellData.getValue().getState();
            return new javafx.beans.property.SimpleStringProperty(state != null ? state.toString() : "UNKNOWN");
        });

        serviceColumn.setCellValueFactory(cellData -> {
            String service = cellData.getValue().getDefaultServiceName();
            return new javafx.beans.property.SimpleStringProperty(service != null ? service : "Unknown");
        });

        protocolColumn.setCellValueFactory(cellData -> {
            String protocol = cellData.getValue().getProtocol().getProtocolName();
            return new javafx.beans.property.SimpleStringProperty(protocol != null ? protocol : "TCP");
        });

        stateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String state, boolean empty) {
                super.updateItem(state, empty);
                if (empty || state == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(state);
                    if (state.equals("OPEN")) {
                        setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
                    } else if (state.equals("CLOSED")) {
                        setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
                    } else if (state.equals("FILTERED")) {
                        setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupTableStyling() {
        Label placeholder = new Label("No ports scanned yet.\nEnter a target and click 'Start Scan'.");
        placeholder.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
        portTable.setPlaceholder(placeholder);
    }

    private void setupDefaultValues() {
        totalPortsLabel.setText("0");
        openPortsLabel.setText("0");
        closedPortsLabel.setText("0");
        filteredPortsLabel.setText("0");
        scanningStatusLabel.setText("Ready");
        statusLabel.setText("Ready to scan");
        progressBar.setProgress(0);
        progressBar.setVisible(false);
        progressLabel.setVisible(false);
        stopScanButton.setVisible(false);
        customPortsInput.setDisable(true);
    }

    @FXML
    private void handleScan() {
        if (isScanning) {
            showAlert("Scan in Progress", "A scan is already running. Please wait or stop it.");
            return;
        }

        String target = targetInput.getText().trim();
        if (target.isEmpty()) {
            showAlert("Invalid Input", "Please enter a valid IP address or hostname");
            return;
        }

        List<Integer> ports = getSelectedPorts();
        if (ports.isEmpty()) {
            showAlert("Invalid Ports", "Please select a valid port range or enter custom ports");
            return;
        }

        ScanType scanType = getScanType();
        startScan(target, ports, scanType);
    }

    private List<Integer> getSelectedPorts() {
        String selected = portRangeCombo.getValue();
        switch (selected) {
            case "Common Ports":
                return PortGenerator.COMMON_PORTS;
            case "Well Known (1-100)":
                return PortGenerator.WELL_KNOWN_PORTS;
            case "Common UDP":
                return PortGenerator.COMMONS_UDP_PORTS;
            case "Custom Range":
                return parseCustomPorts(customPortsInput.getText());
            default:
                return new ArrayList<>();
        }
    }

    private List<Integer> parseCustomPorts(String input) {
        List<Integer> ports = new ArrayList<>();
        if (input == null || input.trim().isEmpty()) {
            return ports;
        }

        String[] parts = input.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.contains("-")) {
                String[] range = part.split("-");
                try {
                    int start = Integer.parseInt(range[0].trim());
                    int end = Integer.parseInt(range[1].trim());
                    for (int i = start; i <= end && i <= 65535; i++) {
                        ports.add(i);
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid range
                }
            } else {
                try {
                    int port = Integer.parseInt(part);
                    if (port >= 1 && port <= 65535) {
                        ports.add(port);
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid port
                }
            }
        }
        return ports;
    }

    private ScanType getScanType() {
        String selected = scanTypeCombo.getValue();
        switch (selected) {
            case "SYN Scan":
                return ScanType.TCP_SYN;
            case "UDP Scan":
                return ScanType.UDP;
            default:
                return ScanType.TCP_CONNECT;
        }
    }

    private void startScan(String target, List<Integer> ports, ScanType scanType) {
        isScanning = true;
        portList.clear();

        updateScanningState(true);
        Platform.runLater(() -> totalPortsLabel.setText(String.valueOf(ports.size())));

        new Thread(() -> {
            try {
                InetAddress addr = portScanService.resolveHost(target);
                Host host = new Host(addr);
                host.setIpString(target);

                currentScanFuture = portScanService.scanAsync(host, ports, scanType);

                // Monitor progress
                monitorScanProgress(ports.size());

                // Wait for scan to complete
                ScanResult result = currentScanFuture.get();

                // Update UI with all results
                Platform.runLater(() -> {
                    if (result != null && result.getScannedPorts() != null) {
                        System.out.println("[DEBUG] Adding " + result.getScannedPorts().size() + " ports to table");
                        portList.clear();
                        portList.addAll(result.getScannedPorts());
                        portTable.refresh();
                        finalizeScan(result);
                    } else {
                        System.err.println("[ERROR] Scan result is null or has no ports");
                        statusLabel.setText("Error: No scan results received");
                        updateScanningState(false);
                        isScanning = false;
                    }
                });

            } catch (UnknownHostException e) {
                Platform.runLater(() -> {
                    updateScanningState(false);
                    statusLabel.setText("Error: Unknown host - " + target);
                    isScanning = false;
                });
            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    updateScanningState(false);
                    statusLabel.setText("Scan interrupted");
                    isScanning = false;
                });
            } catch (ExecutionException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    updateScanningState(false);
                    statusLabel.setText("Scan failed: " + e.getMessage());
                    isScanning = false;
                });
            }
        }).start();
    }

    private void monitorScanProgress(int totalPorts) {
        if (progressTimeline != null) {
            progressTimeline.stop();
        }

        progressTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            if (isScanning && currentScanFuture != null && !currentScanFuture.isDone()) {
                int scanned = portList.size();
                double progress = totalPorts > 0 ? (double) scanned / totalPorts : 0;

                Platform.runLater(() -> {
                    progressBar.setProgress(progress);
                    progressLabel.setText(scanned + "/" + totalPorts + " ports");
                    updatePortStats();
                });
            }
        }));
        progressTimeline.setCycleCount(Timeline.INDEFINITE);
        progressTimeline.play();
    }

    private void updateScanningState(boolean scanning) {
        if (scanning) {
            scanButton.setDisable(true);
            scanButton.setText("⏳ Scanning...");
            stopScanButton.setVisible(true);
            progressBar.setVisible(true);
            progressLabel.setVisible(true);
            progressBar.setProgress(0);
            scanningStatusLabel.setText("In Progress");
            statusLabel.setText("Scanning ports...");
        } else {
            scanButton.setDisable(false);
            scanButton.setText("▶ Start Scan");
            stopScanButton.setVisible(false);
            scanningStatusLabel.setText("Complete");

            if (progressTimeline != null) {
                progressTimeline.stop();
            }

            Timeline resetTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                scanningStatusLabel.setText("Ready");
                progressBar.setVisible(false);
                progressLabel.setVisible(false);
            }));
            resetTimeline.play();
        }
    }

    private void updatePortStats() {
        long open = portList.stream().filter(p -> p.getState() == PortState.OPEN).count();
        long closed = portList.stream().filter(p -> p.getState() == PortState.CLOSED).count();
        long filtered = portList.stream().filter(p -> p.getState() == PortState.OPEN_OR_FILTERED).count();

        openPortsLabel.setText(String.valueOf(open));
        closedPortsLabel.setText(String.valueOf(closed));
        filteredPortsLabel.setText(String.valueOf(filtered));
    }

    private void finalizeScan(ScanResult result) {
        isScanning = false;
        updateScanningState(false);
        updatePortStats();
        progressBar.setProgress(1.0);

        long openPorts = portList.stream().filter(p -> p.getState() == PortState.OPEN).count();
        statusLabel.setText("✓ Scan complete - Found " + openPorts + " open port(s)");

        System.out.println("[INFO] Scan completed successfully");
        System.out.println(result.getSummary());
    }

    @FXML
    private void handleStopScan() {
        if (isScanning && currentScanFuture != null) {
            currentScanFuture.cancel(true);
            isScanning = false;
            if (progressTimeline != null) {
                progressTimeline.stop();
            }
            updateScanningState(false);
            statusLabel.setText("⚠ Scan stopped by user");
        }
    }

    @FXML
    private void handleClear() {
        if (isScanning) {
            showAlert("Cannot Clear", "Please wait for the current scan to complete or stop it.");
            return;
        }

        portList.clear();
        targetInput.clear();
        setupDefaultValues();
        statusLabel.setText("Table cleared");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}