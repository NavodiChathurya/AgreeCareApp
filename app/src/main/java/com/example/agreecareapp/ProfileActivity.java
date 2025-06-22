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
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // ✅ Safe Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // 🔘 Initialize Logout Button
        btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
                finishAffinity();
            });
        }

        // 🔘 Initialize Bottom Navigation
        navHome = findViewById(R.id.navHome);
        navTasks = findViewById(R.id.navTasks);
        navReport = findViewById(R.id.navReport);
        navSettings = findViewById(R.id.navSettings);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                startActivity(new Intent(this, Dashboard.class));
                finish();
            });
        }

        if (navTasks != null) {
            navTasks.setOnClickListener(v ->
                    Toast.makeText(this, "Tasks not implemented", Toast.LENGTH_SHORT).show());
        }

        if (navReport != null) {
            navReport.setOnClickListener(v ->
                    Toast.makeText(this, "Report not implemented", Toast.LENGTH_SHORT).show());
        }

        if (navSettings != null) {
            navSettings.setOnClickListener(v ->
                    Toast.makeText(this, "You're already on Settings", Toast.LENGTH_SHORT).show());
        }
    }
}
