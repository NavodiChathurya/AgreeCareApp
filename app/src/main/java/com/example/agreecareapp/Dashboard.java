package com.example.agreecareapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Dashboard extends AppCompatActivity {

    private static final String TAG = "Dashboard";

    private Button btnCrop, btnLivestock, btnMultimedia, btnViewData, btnProfile, btnLogout;
    private LinearLayout navHome, navTasks, navReport, navSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 🔘 Button bindings with null checks
        btnCrop = findViewById(R.id.btnCrop);
        btnLivestock = findViewById(R.id.btnLivestock);
        btnMultimedia = findViewById(R.id.btnMultimedia);
        btnViewData = findViewById(R.id.btnViewData);
        btnProfile = findViewById(R.id.btnProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // 🔘 Bottom navigation bindings with null checks
        navHome = findViewById(R.id.navHome);
        navTasks = findViewById(R.id.navTasks);
        navReport = findViewById(R.id.navReport);
        navSettings = findViewById(R.id.navSettings);

        // Verify bindings
        if (btnCrop == null) Log.e(TAG, "btnCrop not found in layout");
        if (btnLivestock == null) Log.e(TAG, "btnLivestock not found in layout");
        if (btnMultimedia == null) Log.e(TAG, "btnMultimedia not found in layout");
        if (btnViewData == null) Log.e(TAG, "btnViewData not found in layout");
        if (btnProfile == null) Log.e(TAG, "btnProfile not found in layout");
        if (btnLogout == null) Log.e(TAG, "btnLogout not found in layout");
        if (navHome == null) Log.e(TAG, "navHome not found in layout");
        if (navTasks == null) Log.e(TAG, "navTasks not found in layout");
        if (navReport == null) Log.e(TAG, "navReport not found in layout");
        if (navSettings == null) Log.e(TAG, "navSettings not found in layout");

        // 👉 Crop Management
        if (btnCrop != null) {
            btnCrop.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to CropManagementActivity");
                try {
                    startActivity(new Intent(Dashboard.this, CropManagementActivity.class));
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start CropManagementActivity: " + e.getMessage());
                    Toast.makeText(this, "Error navigating to Crop Management", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 👉 Sensor and Location
        if (btnLivestock != null) {
            btnLivestock.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to SensorLocationActivity");
                try {
                    startActivity(new Intent(Dashboard.this, SensorLocationActivity.class));
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start SensorLocationActivity: " + e.getMessage());
                    Toast.makeText(this, "Error navigating to Sensor and Location", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 👉 Multimedia Integration
        if (btnMultimedia != null) {
            btnMultimedia.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to MultimediaIntegrationActivity");
                try {
                    startActivity(new Intent(Dashboard.this, MultimediaIntegrationActivity.class));
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start MultimediaIntegrationActivity: " + e.getMessage());
                    Toast.makeText(this, "Error navigating to Multimedia Integration", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 👉 View Data
        if (btnViewData != null) {
            btnViewData.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to ViewDataActivity");
                try {
                    startActivity(new Intent(Dashboard.this, ViewDataActivity.class));
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start ViewDataActivity: " + e.getMessage());
                    Toast.makeText(this, "Error navigating to View Data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 👉 Profile
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to ProfileActivity");
                try {
                    Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Dashboard.this, ProfileActivity.class));
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start ProfileActivity: " + e.getMessage());
                    Toast.makeText(this, "Error navigating to Profile", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 👉 Logout
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Log.d(TAG, "Logout clicked");
                try {
                    Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
                    finishAffinity(); // Exit the app
                } catch (Exception e) {
                    Log.e(TAG, "Logout failed: " + e.getMessage());
                    Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 👉 Bottom navigation
        if (navHome != null) {
            navHome.setOnClickListener(v ->
                    Toast.makeText(this, "You are already on the Home screen", Toast.LENGTH_SHORT).show());
        }
        if (navTasks != null) {
            navTasks.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to SensorLocationActivity from Tasks");
                try {
                    startActivity(new Intent(Dashboard.this, SensorLocationActivity.class));
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start SensorLocationActivity from Tasks: " + e.getMessage());
                    Toast.makeText(this, "Error navigating to Sensor and Location", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (navReport != null) {
            navReport.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to ViewDataActivity from Report");
                try {
                    startActivity(new Intent(Dashboard.this, ViewDataActivity.class));
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start ViewDataActivity from Report: " + e.getMessage());
                    Toast.makeText(this, "Error navigating to View Data", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (navSettings != null) {
            navSettings.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to ProfileActivity from Settings");
                try {
                    startActivity(new Intent(Dashboard.this, ProfileActivity.class));
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start ProfileActivity from Settings: " + e.getMessage());
                    Toast.makeText(this, "Error navigating to Settings", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}