package dev.hmap.models;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScanResult {

    private final Host host;
    private final List<Port> scannedPorts;
    private int totalPorts;
    private long startTime;
    private long endTime;
    private boolean completed;

    public ScanResult(Host host){
        this.host = host;
        this.scannedPorts = new CopyOnWriteArrayList<>();
        this.startTime = System.currentTimeMillis();
        this.completed = false;
    }

    public void finalizeScan(){
        this.endTime = System.currentTimeMillis();
        this.completed = true;
    }

    public double getDurationInSeconds(){
        return (endTime - startTime) / 1000.0;
    }

    public double getPortsPerSeconds(){
        double duration = getDurationInSeconds();
        return duration > 0 ? totalPorts / duration : 0;
    }

    public void addScannedPort(Port port){
        this.scannedPorts.add(port);
    }

    public List<Port> getOpenPorts(){
        return scannedPorts.stream()
                .filter(p -> p.getState().equals(Port.PortState.OPEN))
                .toList();
    }
    public String getSummary(){
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

        if(openPortsCount > 0){
            summary.append("OPEN PORT DETAILS:\n");

            summary.append(String.format("%-8s %-12s %-18s \n", "PORT", "STATUS", "SERVICE"));
            summary.append("-------- ------------ ------------------ \n");

            for (Port p : host.getOpenPorts()) {
                String portDetails = String.format(
                        "%-8d %-12s %-18s ",
                        p.getPortNumber(),
                        p.getState().portStatusName,
                        p.getDefaultServiceName()
                );
                summary.append(portDetails).append("\n");
            }
        } else if (completed) {
            summary.append("No open ports found on target host.\n");
        }
        return summary.toString();
    }

    public Host getHost() {
        return host;
    }

    public int getTotalPorts() {
        return totalPorts;
    }

    public void setTotalPorts(int totalPorts) {
        this.totalPorts = totalPorts;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime){
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
