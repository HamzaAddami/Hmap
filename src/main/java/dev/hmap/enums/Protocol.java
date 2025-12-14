package dev.hmap.enums;

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

