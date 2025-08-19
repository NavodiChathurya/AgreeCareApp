package com.example.agreecareapp;

public class Crop {
    public String name;
    public String status;
    public String date;
    public String message;
    public long timestamp;
    public String userId;
    public String key;

    // Firebase/serialization needs this
    public Crop() {}

    // Full constructor
    public Crop(String name, String status, String date, String message, long timestamp, String userId) {
        this.name = name;
        this.status = status;
        this.date = date;
        this.message = message;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    // ✅ NEW constructor for testing
    public Crop(String name, String status, String message, long timestamp) {
        this.name = name;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.date = "";
        this.userId = "";
        this.key = "";
    }

    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }
    public String getKey() { return key; }
}
