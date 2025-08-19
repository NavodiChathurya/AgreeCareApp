package com.example.agreecareapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    /**
     * Get current weather data from OpenWeatherMap API.
     *
     * @param city   City name (e.g., "Colombo")
     * @param apiKey Your OpenWeatherMap API key
     * @param units  Units of measurement ("metric" for Celsius)
     * @return Call object for Retrofit enqueue
     */
    @GET("weather")
    Call<WeatherResponse> getCurrentWeather(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
}
