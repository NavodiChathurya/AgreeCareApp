package com.example.agreecareapp;

public class LocationItem {
    public String name;
    public double latitude;
    public double longitude;
    public String type;     // "shop" or "farm"
    public String city;     // Optional: for future filtering by city

    public LocationItem() {
        // Needed for Firebase
    }

    public LocationItem(String name, double latitude, double longitude, String type, String city) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.city = city;
    }
}
