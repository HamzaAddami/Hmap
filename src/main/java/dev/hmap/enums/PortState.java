package dev.hmap.enums;

public enum PortState{
    OPEN("OPEN"),
    CLOSED("CLOSED"),
    OPEN_OR_FILTERED("OPEN_OR_FILTERED"),
    UNKNOWN("UNKNOWN");

    final String portStatusName;

    PortState(String portStatusName){
        this.portStatusName = portStatusName;
    }

    public String getPortStatusName() {
        return portStatusName;
    }
}

