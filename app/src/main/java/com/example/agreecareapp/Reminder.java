
      package com.example.agreecareapp;

public class Reminder {
    public String text;
    public long timestamp;
    public String userId;

    // Required empty constructor for Firebase
    public Reminder() {}

    public Reminder(String text, long timestamp, String userId) {
        this.text = text;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getText() { return text; }
    public long getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }
}