package com.example.agreecareapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Dashboard extends AppCompatActivity {

    private Button btnCrop, btnLivestock, btnMultimedia, btnViewData, btnProfile, btnLogout;
    private LinearLayout navHome, navTasks, navReport, navSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 🔘 Initialize buttons
        btnCrop = findViewById(R.id.btnCrop);
        btnLivestock = findViewById(R.id.btnLivestock);
        btnMultimedia = findViewById(R.id.btnMultimedia);
        btnViewData = findViewById(R.id.btnViewData);
        btnProfile = findViewById(R.id.btnProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // 🔘 Initialize bottom navigation
        navHome = findViewById(R.id.navHome);
        navTasks = findViewById(R.id.navTasks);
        navReport = findViewById(R.id.navReport);
        navSettings = findViewById(R.id.navSettings);

        // 👉 Crop Management
        btnCrop.setOnClickListener(v ->
                Toast.makeText(this, "Crop Management not implemented yet", Toast.LENGTH_SHORT).show());

        // 👉 Sensor and Location
        btnLivestock.setOnClickListener(v ->
                startActivity(new Intent(this, SensorLocationActivity.class)));

        // 👉 Multimedia
        btnMultimedia.setOnClickListener(v ->
                Toast.makeText(this, "Multimedia not implemented yet", Toast.LENGTH_SHORT).show());

        // 👉 View Data
        btnViewData.setOnClickListener(v ->
                Toast.makeText(this, "View Data not implemented yet", Toast.LENGTH_SHORT).show());

        // 👉 Profile Button
        btnProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // 👉 Logout
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            finishAffinity();
        });

        // ✅ Bottom Navigation Actions
        navHome.setOnClickListener(v ->
                Toast.makeText(this, "You are already on the Home screen", Toast.LENGTH_SHORT).show());

        navTasks.setOnClickListener(v ->
                Toast.makeText(this, "Tasks page not yet implemented", Toast.LENGTH_SHORT).show());

        navReport.setOnClickListener(v ->
                Toast.makeText(this, "Report page not yet implemented", Toast.LENGTH_SHORT).show());

        navSettings.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}
