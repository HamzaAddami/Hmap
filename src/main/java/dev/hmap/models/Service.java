package dev.hmap.models;

public class Service {

    private String serviceName;
    private int port;
    private String protocol;
    private String description;
    private boolean isVulnerable;

    public Service(int port, String protocol){
        this.port = port;
        this.protocol = protocol;
        this.isVulnerable = false;
    }

    // Getters & Setters ------------------------
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVulnerable() {
        return isVulnerable;
    }

    public void setVulnerable(boolean vulnerable) {
        isVulnerable = vulnerable;
    }
}
