package dev.hmap.model;

import dev.hmap.enums.HostStatus;
import dev.hmap.enums.OsFamily;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Host {


    // Attributes --------------------------

    private String hostName;
    private InetAddress ipAddress;
    private String ipString;
    private String macAddress;
    private OsFamily osFamily;
    private HostStatus status;
    private boolean isActive;
    private String description;
    private long latency;

    private List<Port> openPorts;
    private List<Service> runningServices;

    private int openPortsCount;
    private int totalPortsScanned;

    private boolean isReachableByPing;
    private boolean isReachableByTCP;

    private LocalDateTime lastSeen;
    private LocalDateTime scanDate;

    // Constructors -------------------------

    public Host(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
        this.ipString = ipAddress.getHostAddress();
        this.status = HostStatus.UNKNOWN;
        this.isActive = false;
        this.osFamily = OsFamily.UNKNOWN;
        this.openPorts = new ArrayList<>();
    }

    public Host(InetAddress ipAddress, String hostName){
        this(ipAddress);
        this.hostName = hostName;
    }

    public Host(String ipString) throws UnknownHostException {
        this(InetAddress.getByName(ipString));
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


    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpString() {
        return ipString;
    }

    public void setIpString(String ipString) {
        this.ipString = ipString;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public OsFamily getOsFamily() {
        return osFamily;
    }

    public void setOsFamily(OsFamily osFamily) {
        this.osFamily = osFamily;
    }

    public HostStatus getStatus() {
        return status;
    }

    public void setStatus(HostStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public List<Port> getOpenPorts() {
        return openPorts;
    }

    public void setOpenPorts(List<Port> openPorts) {
        this.openPorts = openPorts;
    }

    public List<Service> getRunningServices() {
        return runningServices;
    }

    public void setRunningServices(List<Service> runningServices) {
        this.runningServices = runningServices;
    }

    public int getOpenPortsCount() {
        return openPortsCount;
    }

    public void setOpenPortsCount(int openPortsCount) {
        this.openPortsCount = openPortsCount;
    }

    public int getTotalPortsScanned() {
        return totalPortsScanned;
    }

    public void setTotalPortsScanned(int totalPortsScanned) {
        this.totalPortsScanned = totalPortsScanned;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public LocalDateTime getScanDate() {
        return scanDate;
    }

    public void setScanDate(LocalDateTime scanDate) {
        this.scanDate = scanDate;
    }

    public boolean isReachableByPing() {
        return isReachableByPing;
    }

    public void setReachableByPing(boolean reachableByPing) {
        isReachableByPing = reachableByPing;
    }

    public boolean isReachableByTCP() {
        return isReachableByTCP;
    }

    public void setReachableByTCP(boolean reachableByTCP) {
        isReachableByTCP = reachableByTCP;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Host host = (Host) o;
        return isActive == host.isActive && latency == host.latency && openPortsCount == host.openPortsCount && totalPortsScanned == host.totalPortsScanned && Objects.equals(hostName, host.hostName) && Objects.equals(ipAddress, host.ipAddress) && Objects.equals(ipString, host.ipString) && Objects.equals(macAddress, host.macAddress) && osFamily == host.osFamily && status == host.status && Objects.equals(description, host.description) && Objects.equals(openPorts, host.openPorts) && Objects.equals(runningServices, host.runningServices) && Objects.equals(lastSeen, host.lastSeen) && Objects.equals(scanDate, host.scanDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostName, ipAddress, ipString, macAddress, osFamily, status, isActive, description, latency, openPorts, runningServices, openPortsCount, totalPortsScanned, lastSeen, scanDate);
    }

    @Override
    public String toString() {
        return "Host{" +
                "hostName='" + hostName + '\'' +
                ", ipAddress=" + ipAddress.getHostAddress() +
                ", ipString='" + ipString + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", osFamily=" + osFamily +
                ", status=" + status +
                ", isActive=" + isActive +
                ", description='" + description + '\'' +
                ", latency=" + latency +
                ", openPorts=" + openPorts +
                ", runningServices=" + runningServices +
                ", openPortsCount=" + openPortsCount +
                ", totalPortsScanned=" + totalPortsScanned +
                ", lastSeen=" + lastSeen +
                ", scanDate=" + scanDate +
                '}';
    }
}
