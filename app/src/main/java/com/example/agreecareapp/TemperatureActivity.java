package com.example.agreecareapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TemperatureActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private TextView tvTempInfo;
    private EditText etSearchPlace;
    private Button btnSearchTemp;

    // Replace with your real OpenWeatherMap API key
    private final String API_KEY = "a42381ad78da19f14da389315fca7de0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // UI Elements
        tvTempInfo = findViewById(R.id.tvTempInfo);
        etSearchPlace = findViewById(R.id.etSearchPlace);
        btnSearchTemp = findViewById(R.id.btnSearchTemp);

        // Sensor Manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }

        if (temperatureSensor == null) {
            tvTempInfo.setText("Ambient temperature sensor not available.");
            Toast.makeText(this, "Sensor not supported on this device", Toast.LENGTH_LONG).show();
        }

        // Search Button Functionality
        btnSearchTemp.setOnClickListener(v -> {
            String city = etSearchPlace.getText().toString().trim();
            if (!city.isEmpty()) {
                getWeatherForCity(city);
            } else {
                Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWeatherForCity(String city) {
        WeatherApi api = ApiClient.getClient().create(WeatherApi.class);
        Call<CityWeatherResponse> call = api.getWeatherByCity(city, API_KEY, "metric");

        call.enqueue(new Callback<CityWeatherResponse>() {
            @Override
            public void onResponse(Call<CityWeatherResponse> call, Response<CityWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    float temp = response.body().main.temp;
                    tvTempInfo.setText("City Temperature: " + temp + "°C");
                } else {
                    tvTempInfo.setText("Unable to fetch temperature.\nError code: " + response.code());
                    Log.e("TempAPI", "Response code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("TempAPI", "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CityWeatherResponse> call, Throwable t) {
                tvTempInfo.setText("Error: " + t.getMessage());
                Log.e("TempAPI", "Network failure: ", t);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (temperatureSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float temperature = event.values[0];
        tvTempInfo.setText("Device Temperature: " + temperature + "°C");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed
    }
}
