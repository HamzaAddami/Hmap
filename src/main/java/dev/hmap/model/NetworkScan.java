package dev.hmap.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "network_scans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NetworkScan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scanName;

    private String subnet;


    private LocalDateTime startTime = LocalDateTime.now();

    private LocalDateTime endTime;

    private boolean completed = false;

    private int totalHostsScanned = 0;
    private int hostsFound = 0;
    private int totalOpenPorts = 0;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "network_scan_id")
    private List<ScanResult> scanResults = new ArrayList<>();

    public NetworkScan(String scanName, String subnet) {
        this.scanName = scanName;
        this.subnet = subnet;
        this.startTime = LocalDateTime.now();
    }


    public void addScanResult(ScanResult result) {
        if (result != null) {
            this.scanResults.add(result);
            if (result.isCompleted()) {
                this.hostsFound++;
                this.totalOpenPorts += result.getOpenPorts().size();
            }
        }
    }

    public void finalizeScan() {
        this.endTime = LocalDateTime.now();
        this.completed = true;
        this.totalHostsScanned = scanResults.size();
    }

    @Transient
    public long getDurationInSeconds() {
        if (endTime == null) {
            return java.time.Duration.between(startTime, LocalDateTime.now()).getSeconds();
        }
        return java.time.Duration.between(startTime, endTime).getSeconds();
    }

    @Transient
    public String getFormattedStartTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return startTime.format(formatter);
    }

    @Transient
    public String getFormattedEndTime() {
        if (endTime == null) return "En cours...";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return endTime.format(formatter);
    }

    public String generateFullReport() {
        StringBuilder report = new StringBuilder();

        report.append("═══════════════════════════════════════════════════════════════\n");
        report.append("           RAPPORT DE SCAN RÉSEAU\n");
        report.append("═══════════════════════════════════════════════════════════════\n\n");

        report.append(String.format("Nom du scan      : %s\n", scanName));
        report.append(String.format("Réseau scanné    : %s\n", subnet));
        report.append(String.format("Date de début    : %s\n", getFormattedStartTime()));
        report.append(String.format("Date de fin      : %s\n", getFormattedEndTime()));
        report.append(String.format("Durée totale     : %d secondes\n", getDurationInSeconds()));
        report.append(String.format("Statut           : %s\n", completed ? "TERMINÉ" : "EN COURS"));

        report.append("\n--- STATISTIQUES ---\n");
        report.append(String.format("Hôtes scannés    : %d\n", totalHostsScanned));
        report.append(String.format("Hôtes trouvés    : %d\n", hostsFound));
        report.append(String.format("Ports ouverts    : %d\n", totalOpenPorts));

        report.append("\n═══════════════════════════════════════════════════════════════\n");
        report.append("           DÉTAILS DES HÔTES DÉCOUVERTS\n");
        report.append("═══════════════════════════════════════════════════════════════\n\n");

        if (scanResults.isEmpty()) {
            report.append("Aucun hôte trouvé sur ce réseau.\n");
        } else {
            int hostNumber = 1;
            for (ScanResult result : scanResults) {
                if (result.getHost() != null && result.getHost().isReachable()) {
                    report.append(String.format("\n[HÔTE #%d]\n", hostNumber++));
                    report.append(result.getSummary());
                    report.append("\n");
                }
            }
        }

        report.append("\n═══════════════════════════════════════════════════════════════\n");
        report.append(String.format("Rapport généré le : %s\n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        report.append("═══════════════════════════════════════════════════════════════\n");

        return report.toString();
    }

    public String getShortSummary() {
        return String.format("[%s] %s - %s (%d hôtes, %d ports ouverts)",
                completed ? "✓" : "⏳",
                scanName,
                subnet,
                hostsFound,
                totalOpenPorts);
    }
}