package dev.hmap.models;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Host {

    public enum HostStatus {
        UNKNOWN("UNKNOWN"),
        UP("UP"),
        DOWN("DOWN"),
        SCANNING("Scanning");

        final String statusName;

        HostStatus(String statusName ){
            this.statusName = statusName;
        }

        public String getStatusName(){
            return statusName;
        }
    }

    public enum OsFamily {
        UNKNOWN("UNKNOWN"),
        WINDOWS("WINDOWS"),
        MACOS("MACOS"),
        LINUX("LINUX"),
        ANDROID("ANDROID"),
        IOS("IOS");

        final String osFamilyName;

        OsFamily(String osFamilyName) {
            this.osFamilyName = osFamilyName;
        }

        public String getOsFamilyName() {
            return osFamilyName;
        }
    }

    // Attributes --------------------------

    private String hostName;
    private InetAddress ipAddress;
    private String macAddress;
    private OsFamily osFamily;
    private HostStatus status;
    private boolean isActive;
    private String description;

    private List<Port> openPorts;
    private List<Service> runningServices;

    private int openPortsCount;
    private int totalPortsScanned;

    private LocalDateTime lastSeen;
    private LocalDateTime scanDate;

    // Constructors -------------------------

    public Host(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
        this.status = HostStatus.UNKNOWN;
        this.isActive = false;
        this.osFamily = OsFamily.UNKNOWN;
        this.openPorts = new ArrayList<>();
    }

    public Host(InetAddress ipAddress, String hostName){
        this(ipAddress);
        this.hostName = hostName;
    }

    // Methods -----------------------------

    // Ports method

    public void addPort(Port port){
         if(port != null && !openPorts.contains(port)){
             openPorts.add(port);
             openPortsCount++;
         }
    }

    public void addPorts(List<Port> ports){
        if(ports != null){
            openPorts.addAll(ports);
        }
    }

    public boolean hasOpenPorts() {
        return openPorts.isEmpty();
    }

    public Port getPort(int portNumber){
        for(Port port: openPorts){
            if(port.getPortNumber() == portNumber){
                return port;
            }
        }
        return null;
    }

    // Service methods

    public void addService(Service service){
        if(service != null && !runningServices.contains(service)){
            runningServices.add(service);
        }
    }

    public void addServices(List<Service> services){
        if(services != null && !services.isEmpty()){
            runningServices.addAll(services);
        }
    }

    public Service getServiceByPort(int portNumber){
        for(Service service: runningServices){
            if(service.getPort() == portNumber){
                return service;
            }
        }
        return null;
    }

    // Control methods

    public void setActive(){
        this.isActive = true;
        this.status = HostStatus.UP;
        this.lastSeen = LocalDateTime.now();
    }

    public void setInactive(){
        this.isActive = false;
        this.status = HostStatus.DOWN;
    }


    // Getters & Setters

}
