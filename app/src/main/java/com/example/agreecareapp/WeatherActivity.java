package com.example.agreecareapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    private final String API_KEY = "cadc9f878e64ad3e50abad24fd6825fd";

    private TextView tvCity, tvTemp, tvDesc, tvWind, tvSunrise, tvSunset;
    private AutoCompleteTextView actvCityInput;
    private Button btnGetWeather, btnRefresh;
    private ImageView ivWeatherIcon;
    private LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // Setup toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Bind views
        actvCityInput = findViewById(R.id.actvCityInput);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        btnRefresh = findViewById(R.id.btnRefresh); // 🔹 New refresh button
        tvCity = findViewById(R.id.tvCity);
        tvTemp = findViewById(R.id.tvTemp);
        tvDesc = findViewById(R.id.tvDesc);
        tvWind = findViewById(R.id.tvWind);
        tvSunrise = findViewById(R.id.tvSunrise);
        tvSunset = findViewById(R.id.tvSunset);
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
        rootLayout = findViewById(R.id.weatherRoot);

        // Setup autocomplete city list
        String[] cities = getResources().getStringArray(R.array.sri_lanka_cities);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        actvCityInput.setAdapter(adapter);
        actvCityInput.setThreshold(1);

        // Load last saved city or default
        String lastCity = getSharedPreferences("weather_prefs", MODE_PRIVATE)
                .getString("last_city", "Colombo");
        actvCityInput.setText(lastCity);
        fetchWeather(lastCity);

        // Get Weather button
        btnGetWeather.setOnClickListener(v -> {
            String city = actvCityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                getSharedPreferences("weather_prefs", MODE_PRIVATE)
                        .edit()
                        .putString("last_city", city)
                        .apply();
                fetchWeather(city);
            } else {
                Toast.makeText(WeatherActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });

        // 🔁 Refresh Button click
        btnRefresh.setOnClickListener(v -> {
            String city = actvCityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeather(city);
                Toast.makeText(this, "Weather refreshed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "City is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWeather(String cityName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeather(cityName, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();

                    tvCity.setText(cityName);
                    tvTemp.setText(String.format("%.1f°C", weather.main.temp));

                    if (weather.weather != null && !weather.weather.isEmpty()) {
                        String iconCode = weather.weather.get(0).icon;
                        tvDesc.setText(weather.weather.get(0).description);

                        // Load icon
                        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                        Glide.with(WeatherActivity.this).load(iconUrl).into(ivWeatherIcon);
                    } else {
                        tvDesc.setText("No description");
                        ivWeatherIcon.setImageDrawable(null);
                    }

                    if (weather.wind != null) {
                        tvWind.setText(String.format("Wind Speed: %.1f m/s", weather.wind.speed));
                    } else {
                        tvWind.setText("Wind Speed: N/A");
                    }

                    if (weather.sys != null) {
                        tvSunrise.setText("Sunrise: " + unixToTime(weather.sys.sunrise));
                        tvSunset.setText("Sunset: " + unixToTime(weather.sys.sunset));
                    } else {
                        tvSunrise.setText("Sunrise: N/A");
                        tvSunset.setText("Sunset: N/A");
                    }
                } else {
                    Toast.makeText(WeatherActivity.this, "Error getting data", Toast.LENGTH_SHORT).show();
                    clearUI();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(WeatherActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                clearUI();
            }
        });
    }

    private void clearUI() {
        tvTemp.setText("");
        tvDesc.setText("");
        tvWind.setText("");
        tvSunrise.setText("Sunrise: N/A");
        tvSunset.setText("Sunset: N/A");
        ivWeatherIcon.setImageDrawable(null);
    }

    private String unixToTime(long unixSeconds) {
        java.util.Date date = new java.util.Date(unixSeconds * 1000L);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a");
        sdf.setTimeZone(java.util.TimeZone.getDefault());
        return sdf.format(date);
    }
}
