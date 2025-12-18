package com.ra1.aplicaciotemps.savedlocations;

public class SavedLocation {
    private int id;
    private String name;
    private String lat;
    private String lon;

    public SavedLocation(int id, String name, String lat, String lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
    }

    public int getId() {
        return id;
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
