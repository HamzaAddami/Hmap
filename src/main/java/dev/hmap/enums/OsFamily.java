package dev.hmap.enums;

public enum OsFamily {
    UNKNOWN("UNKNOWN"),
    WINDOWS("WINDOWS"),
    MACOS("MACOS"),
    LINUX("LINUX"),
    ANDROID("ANDROID"),
    IOS("IOS"),
    NETWORK_DEVICE("NETWORK_DEVICE");

    final String osFamilyName;

    OsFamily(String osFamilyName) {
        this.osFamilyName = osFamilyName;
    }

    public String getOsFamilyName() {
        return osFamilyName;
    }
}

