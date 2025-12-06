package dev.hmap.models;

public class Service {

    private String serviceName;
    private Port port;
    private final String protocol;
    private String description;
    private boolean isVulnerable;

    public Service(Port port){
        this.port = port;
        this.protocol = port.getDefaultServiceName();
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
        return port.getPortNumber();
    }

    public void setPort(int port) {
        this.port = new Port(port);
    }

    public String getProtocol() {
        return port.getProtocol().protocolName;
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
