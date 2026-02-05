package com.ra1.aplicaciotemps.database.models;

public class SavedLocation {
    private int id;
    private String name;
    private String lat;
    private String lon;

    private boolean isFavorite;

    public SavedLocation(int id, String name, String lat, String lon, boolean isFavorite) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.isFavorite = isFavorite();
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

    public void setName(String name) {
        this.name = name;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
