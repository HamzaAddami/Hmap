package dev.hmap.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import dev.hmap.enums.PortState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "scan_results")
@NoArgsConstructor
@Getter
@Setter
public class ScanResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)

    @JoinColumn(name = "host_id")
    private Host host;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "scan_result_id")
    private List<Port> scannedPorts = new ArrayList<>();

    private int totalPorts;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean completed;

    public ScanResult(Host host) {
        this.host = host;
        this.scannedPorts = new CopyOnWriteArrayList<>();
        this.startTime = LocalDateTime.now();
        this.completed = false;
    }

    public void finalizeScan() {
        this.endTime = LocalDateTime.now();
        this.completed = true;
    }

    @Transient
    public long getDurationInSeconds() {
        return Duration.between(startTime, endTime).getSeconds();
    }

    public double getPortsPerSeconds() {
        double duration = getDurationInSeconds();
        return duration > 0 ? scannedPorts.size() / duration : 0;
    }

    public synchronized void addScannedPort(Port port) {
        this.scannedPorts.add(port);
    }

    public List<Port> getOpenPorts() {
        return scannedPorts.stream()
                .filter(p -> p.getState().equals(PortState.OPEN))
                .toList();
    }

    public String getSummary() {
//        List<Port> openPorts = getOpenPorts();
        int openPortsCount = host.getOpenPortsCount();
        int portsCount = scannedPorts.size();
        StringBuilder summary = new StringBuilder();
        summary.append("--- Scan Report ---\n");
        summary.append(String.format("Target Host: %s\n", host.getIpAddress().getHostAddress()));
        summary.append(String.format("Total Ports to Scan: %d\n", portsCount));
        summary.append(String.format("Ports Scanned So Far: %d\n", scannedPorts.size()));
        summary.append(String.format("Scan Duration: %.2f seconds\n", getDurationInSeconds()));
        summary.append(String.format("Scan Speed: %.0f ports/second\n", getPortsPerSeconds()));
        summary.append(String.format("Open Ports Found: %d\n", openPortsCount));
        summary.append(String.format("Status: %s\n", completed ? "COMPLETED" : "IN PROGRESS"));
        summary.append("-------------------\n\n");

        if (openPortsCount > 0) {
            summary.append("OPEN PORT DETAILS:\n");

            summary.append(String.format("%-8s %-12s %-18s \n", "PORT", "STATUS", "SERVICE"));
            summary.append("-------- ------------ ------------------ \n");

            for (Port p : host.getOpenPorts()) {
                String portDetails = String.format(
                        "%-8d %-12s %-18s ",
                        p.getPortNumber(),
                        p.getState().getPortStatusName(),
                        p.getDefaultServiceName()
                );
                summary.append(portDetails).append("\n");
            }
        } else if (completed) {
            summary.append("No open ports found on target host.\n");
        }
        return summary.toString();
    }
}
