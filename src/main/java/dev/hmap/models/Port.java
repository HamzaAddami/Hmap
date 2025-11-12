package dev.hmap.models;

import java.util.Objects;

public class Port {

    public enum PortState{
        OPEN("OPEN"),
        CLOSED("CLOSED"),
        UNKNOWN("UNKNOWN");

        final String portStatusName;

        PortState(String portStatusName){
            this.portStatusName = portStatusName;
        }

        public String getPortStatusName() {
            return portStatusName;
        }
    }

    public enum Protocol{
        TCP("TCP"),
        UDP("UDP");

        final String protocolName;

        Protocol(String protocolName){
            this.protocolName = protocolName;
        }

        public String getProtocolName() {
            return protocolName;
        }
    }

    private int portNumber;
    private Protocol protocol;
    private PortState state;

    // Constructors -------------------

    public Port(int portNumber){
        this(portNumber, Protocol.TCP);
    }

    public Port(int portNumber, Protocol protocol){
        this.portNumber = portNumber;
        this.protocol = protocol;
        this.state = PortState.UNKNOWN;
    }

    public Port(int portNumber, boolean isOpen){
        this(portNumber);
        this.state = isOpen ? PortState.OPEN : PortState.CLOSED;
    }

    public String getDefaultServiceName() {
        switch (portNumber){
            case 20: return "FTP-DATA";
            case 21: return "FTP";
            case 22: return "SSH";
            case 23: return "TELNET";
            case 25: return "SMTP";
            case 53: return "DNS";
            case 80: return "HTTP";
            case 110: return "POP3";
            case 143: return "IMAP";
            case 443: return "HTTPS";
            case 445: return "SMB";
            case 3306: return "MySQL";
            case 3389: return "RDP";
            case 5432: return "PostgreSQL";
            case 5900: return "VNC";
            case 8080: return "HTTP-Proxy";
            case 27017: return "MongoDB";
            default: return "UNKNOWN";
        }
    }

    public PortState getState() {
        return state;
    }

    public void setState(PortState state) {
        this.state = state;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Port port = (Port) o;
        return portNumber == port.portNumber && protocol == port.protocol && state == port.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(portNumber, protocol, state);
    }

    @Override
    public String toString() {
        return "Port{" +
                "portNumber=" + portNumber +
                ", protocol=" + protocol +
                ", state=" + state +
                '}';
    }
}
