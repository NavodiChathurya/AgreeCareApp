package com.example.agreecareapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChangeThemeLanguageActivity extends AppCompatActivity {

    private RadioGroup radioGroupTheme;
    private RadioButton radioLight, radioDark;
    private Spinner spinnerLanguage;
    private Button btnSaveSettings;
    private SharedPreferences prefs;
    private DatabaseReference dbRef;
    private String currentUserId = "testUser"; // TEMP: Replace with FirebaseAuth.getCurrentUser().getUid()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_theme_language);

        // Initialize Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("user_settings");

        // Initialize SharedPreferences
        prefs = getSharedPreferences("settings_prefs", MODE_PRIVATE);

        // Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Change Theme or Language");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize UI Elements
        radioGroupTheme = findViewById(R.id.radioGroupTheme);
        radioLight = findViewById(R.id.radioLight);
        radioDark = findViewById(R.id.radioDark);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        // Bottom Navigation Setup
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navTasks = findViewById(R.id.navTasks);
        LinearLayout navReport = findViewById(R.id.navReport);
        LinearLayout navSettings = findViewById(R.id.navSettings);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard.class));
            finish();
        });

        navTasks.setOnClickListener(v ->
                Toast.makeText(this, "Tasks screen not implemented", Toast.LENGTH_SHORT).show());

        navReport.setOnClickListener(v ->
                Toast.makeText(this, "Report screen not implemented", Toast.LENGTH_SHORT).show());

        navSettings.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        // Load Current Settings
        boolean isDarkMode = prefs.getBoolean("dark_mode_enabled", false);
        radioLight.setChecked(!isDarkMode);
        radioDark.setChecked(isDarkMode);

        // Language Options
        String[] languages = {"English", "Spanish"};
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(languageAdapter);

        // Load Saved Language
        String savedLanguage = prefs.getString("language", "English");
        spinnerLanguage.setSelection(Arrays.asList(languages).indexOf(savedLanguage));

        // Save Button Logic
        btnSaveSettings.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        // Save Theme
        boolean isDarkMode = radioDark.isChecked();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("dark_mode_enabled", isDarkMode);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // Save Language
        String selectedLanguage = spinnerLanguage.getSelectedItem().toString();
        editor.putString("language", selectedLanguage);
        editor.apply();

        // Update Locale
        String languageCode = selectedLanguage.equals("Spanish") ? "es" : "en";
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Save to Firebase
        Map<String, Object> settings = new HashMap<>();
        settings.put("darkMode", isDarkMode);
        settings.put("language", selectedLanguage);
        dbRef.child(currentUserId).setValue(settings)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save settings: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        // Restart activity to apply changes
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}