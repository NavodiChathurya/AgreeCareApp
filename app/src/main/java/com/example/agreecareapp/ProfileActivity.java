package com.example.agreecareapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout navHome, navTasks, navReport, navSettings;
    private Button btnLogout, btnSetReminder, btnManageSettings, btnEditProfile, btnChangeThemeLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Initialize Buttons
        btnLogout = findViewById(R.id.btnLogout);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        btnManageSettings = findViewById(R.id.btnManageSettings);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangeThemeLanguage = findViewById(R.id.btnChangeThemeLanguage); // ✅ NEW

        // Logout button click
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            finishAffinity();
        });

        // Set Reminder button click
        btnSetReminder.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ReminderActivity.class);
            startActivity(intent);
        });

        // Manage Settings button click
        btnManageSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ManageSettingsActivity.class);
            startActivity(intent);
        });

        // Edit Profile button click
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Change Theme/Language button click
        btnChangeThemeLanguage.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangeThemeLanguageActivity.class);
            startActivity(intent);
        });

        // Bottom Navigation Setup
        navHome = findViewById(R.id.navHome);
        navTasks = findViewById(R.id.navTasks);
        navReport = findViewById(R.id.navReport);
        navSettings = findViewById(R.id.navSettings);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard.class));
            finish();
        });

        navTasks.setOnClickListener(v -> {
            startActivity(new Intent(this, SensorLocationActivity.class));
        });

        navReport.setOnClickListener(v -> {
            startActivity(new Intent(this, ViewDataActivity.class));
        });

        navSettings.setOnClickListener(v ->
                Toast.makeText(this, "You're already on Settings", Toast.LENGTH_SHORT).show());
    }
}