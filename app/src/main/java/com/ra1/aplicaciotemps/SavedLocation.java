package com.ra1.aplicaciotemps;

public class SavedLocation {
    private String name;
    private String lat;
    private String lon;

    public SavedLocation(String name, String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}
