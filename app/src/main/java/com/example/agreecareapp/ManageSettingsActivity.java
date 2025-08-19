package com.example.agreecareapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ManageSettingsActivity extends AppCompatActivity {

    private CheckBox chkEnableNotifications, chkDarkMode; // Removed chkWeatherNotifications, chkLocationUpdates
    private Button btnClearCache;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_settings);

        // Setup toolbar with back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Settings");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize UI elements
        chkEnableNotifications = findViewById(R.id.chkEnableNotifications);
        chkDarkMode = findViewById(R.id.chkDarkMode);
        btnClearCache = findViewById(R.id.btnClearCache);

        // Initialize SharedPreferences
        prefs = getSharedPreferences("settings_prefs", MODE_PRIVATE);

        // Load saved settings
        chkEnableNotifications.setChecked(prefs.getBoolean("notifications_enabled", true));
        chkDarkMode.setChecked(prefs.getBoolean("dark_mode_enabled", false));

        // Set listeners for checkboxes
        chkEnableNotifications.setOnCheckedChangeListener((btn, isChecked) ->
                prefs.edit().putBoolean("notifications_enabled", isChecked).apply());

        chkDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("dark_mode_enabled", isChecked).apply();
            Toast.makeText(this, "Dark mode " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        // Clear cache button
        btnClearCache.setOnClickListener(v -> {
            Toast.makeText(this, "Cache cleared!", Toast.LENGTH_SHORT).show();
        });

        // Bottom navigation setup
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard.class));
            finish();
        });

        findViewById(R.id.navTasks).setOnClickListener(v ->
                Toast.makeText(this, "Tasks screen not implemented", Toast.LENGTH_SHORT).show());

        findViewById(R.id.navReport).setOnClickListener(v ->
                Toast.makeText(this, "Report screen not implemented", Toast.LENGTH_SHORT).show());

        findViewById(R.id.navSettings).setOnClickListener(v ->
                Toast.makeText(this, "You are already in Settings", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}