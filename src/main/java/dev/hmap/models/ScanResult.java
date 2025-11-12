package dev.hmap.models;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ScanResult {

    private InetAddress ip;
    private List<Port> openPorts;
    private int totalPorts;
    private long startTime;
    private long endTime;
    private boolean completed;

    public ScanResult(InetAddress ip){
        this.ip = ip;
        this.openPorts = new ArrayList<>();
        this.completed = false;
    }

    public void addPort(Port port){
        openPorts.add(port);
    }

    public int getOpenPortsCount(){
        return openPorts.size();
    }

    public double getDurationInSeconds(){
        return (endTime - startTime) / 1000.0;
    }

    public double getPortsPerSeconds(){
        double duration = getDurationInSeconds();
        return duration > 0 ? totalPorts / duration : 0;
    }

    public String getSummary(){
        return String.format(
            "Scan of %s completed in %.2fs: %d/%d ports open (%.0f ports/s)",
                ip.getAddress(),
                getDurationInSeconds(),
                getOpenPortsCount(),
                totalPorts,
                getPortsPerSeconds()
        );
    }

}
