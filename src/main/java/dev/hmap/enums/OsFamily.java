package dev.hmap.enums;

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

