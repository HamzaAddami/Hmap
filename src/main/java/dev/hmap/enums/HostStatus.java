package dev.hmap.enums;

public enum HostStatus {
    UNKNOWN("UNKNOWN"),
    UP("UP"),
    DOWN("DOWN"),
    SCANNING("SCANNING");

    final String statusName;

    HostStatus(String statusName ){
        this.statusName = statusName;
    }

    public String getStatusName(){
        return statusName;
    }
}

