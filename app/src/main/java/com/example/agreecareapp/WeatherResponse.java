package com.example.agreecareapp;

import java.util.List;

public class WeatherResponse {
    public Main main;
    public List<Weather> weather;
    public Wind wind;
    public Sys sys;

    public static class Main {
        public float temp;  // Temperature in Celsius (if requested metric units)
    }

    public static class Weather {
        public String description;  // e.g. "clear sky"
        public String icon;         // e.g. "01d"
    }

    public static class Wind {
        public float speed;  // Wind speed in m/s
        public float deg;    // Wind direction degrees
    }

    public static class Sys {
        public long sunrise;  // UNIX time (seconds)
        public long sunset;   // UNIX time (seconds)
    }
}
