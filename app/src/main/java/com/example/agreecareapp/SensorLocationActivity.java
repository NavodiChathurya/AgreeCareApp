package com.example.agreecareapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SensorLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sensor_location);

        // Handle system insets (for full screen padding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 🗺 Google Map Button
        Button btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(view -> {
            startActivity(new Intent(this, MapActivity.class));
        });

        // 🌡 Temperature Button
        Button btnTemp = findViewById(R.id.btnTemp);
        btnTemp.setOnClickListener(view -> {
            startActivity(new Intent(this, TemperatureActivity.class));
        });

        // 🌦 Weather Button → opens WeatherActivity with city name
        Button btnWeather = findViewById(R.id.btnRain);
        btnWeather.setText("Weather");
        btnWeather.setOnClickListener(view -> {
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("city_name", "Colombo");  // 👈 pass city name
            startActivity(intent);
        });

        // ⛳ Bottom Navigation - Home
        TextView navHome = findViewById(R.id.navHome);
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard.class));
        });

        // 📋 Bottom Navigation - Tasks
        TextView navTasks = findViewById(R.id.navTasks);
        navTasks.setOnClickListener(v -> {
            Toast.makeText(this, "You are already on the Sensor and Location page", Toast.LENGTH_SHORT).show();
        });

        // 📊 Bottom Navigation - Report
        TextView navReport = findViewById(R.id.navReport);
        navReport.setOnClickListener(v -> {
            startActivity(new Intent(this, ViewDataActivity.class));
        });

        // ⚙️ Bottom Navigation - Setting
        TextView navSetting = findViewById(R.id.navSetting);
        navSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }
}