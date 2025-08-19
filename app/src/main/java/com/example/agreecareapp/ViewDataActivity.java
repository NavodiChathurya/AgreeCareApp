package com.example.agreecareapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ViewDataActivity extends AppCompatActivity {

    private EditText areaEditText, harvestDateEditText, notesEditText;
    private RadioGroup cropRadioGroup, irrigationRadioGroup, soilRadioGroup;
    private Button saveButton, goToDataPageButton;
    private ImageButton backButton;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        // Initialize views with null checks
        areaEditText = findViewById(R.id.area_edit_text);
        harvestDateEditText = findViewById(R.id.harvest_date_edit_text);
        notesEditText = findViewById(R.id.notes_edit_text);
        cropRadioGroup = findViewById(R.id.crop_radio_group);
        irrigationRadioGroup = findViewById(R.id.irrigation_radio_group);
        soilRadioGroup = findViewById(R.id.soil_radio_group);
        saveButton = findViewById(R.id.save_button);
        goToDataPageButton = findViewById(R.id.go_to_data_page_button);
        backButton = findViewById(R.id.back_button);
        titleText = findViewById(R.id.title_text);

        // Verify all critical views are initialized
        if (saveButton == null || goToDataPageButton == null || areaEditText == null || cropRadioGroup == null ||
                harvestDateEditText == null || notesEditText == null ||
                irrigationRadioGroup == null || soilRadioGroup == null ||
                backButton == null || titleText == null) {
            Toast.makeText(this, "Error loading page components", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set title programmatically for consistency
        titleText.setText("View Your Data");

        // Back button click listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Navigate back to previous activity
            }
        });

        // Save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String areaStr = areaEditText.getText().toString().trim();
                String harvestDate = harvestDateEditText.getText().toString().trim();
                String notes = notesEditText.getText().toString().trim();
                int selectedCropId = cropRadioGroup.getCheckedRadioButtonId();
                int selectedIrrigationId = irrigationRadioGroup.getCheckedRadioButtonId();
                int selectedSoilId = soilRadioGroup.getCheckedRadioButtonId();

                RadioButton selectedCrop = findViewById(selectedCropId);
                RadioButton selectedIrrigation = findViewById(selectedIrrigationId);
                RadioButton selectedSoil = findViewById(selectedSoilId);
                String cropType = (selectedCrop != null) ? selectedCrop.getText().toString() : "";
                String irrigationLevel = (selectedIrrigation != null) ? selectedIrrigation.getText().toString() : "";
                String soilType = (selectedSoil != null) ? selectedSoil.getText().toString() : "";

                // Validate input
                if (areaStr.isEmpty()) {
                    Toast.makeText(ViewDataActivity.this, "Please enter the field area", Toast.LENGTH_SHORT).show();
                } else if (harvestDate.isEmpty()) {
                    Toast.makeText(ViewDataActivity.this, "Please enter the harvest date", Toast.LENGTH_SHORT).show();
                } else if (selectedCropId == -1) {
                    Toast.makeText(ViewDataActivity.this, "Please select a crop type", Toast.LENGTH_SHORT).show();
                } else if (selectedIrrigationId == -1) {
                    Toast.makeText(ViewDataActivity.this, "Please select an irrigation level", Toast.LENGTH_SHORT).show();
                } else if (selectedSoilId == -1) {
                    Toast.makeText(ViewDataActivity.this, "Please select a soil type", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        double area = Double.parseDouble(areaStr);
                        if (area <= 0) {
                            Toast.makeText(ViewDataActivity.this, "Area must be greater than 0", Toast.LENGTH_SHORT).show();
                        } else if (area < 5 || area > 50) {
                            Toast.makeText(ViewDataActivity.this, "Area should be between 5 and 50 acres", Toast.LENGTH_SHORT).show();
                        } else {
                            // Simulate saving data with timestamp
                            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                            Toast.makeText(ViewDataActivity.this, "Saved at " + timestamp + ": Area = " + area + " acres, Crop = " + cropType +
                                    ", Harvest Date = " + harvestDate + ", Irrigation = " + irrigationLevel +
                                    ", Soil = " + soilType + ", Notes = " + notes, Toast.LENGTH_LONG).show();
                            finish(); // Return to Dashboard
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(ViewDataActivity.this, "Please enter a valid number for area", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Go to Data Page button click listener
        goToDataPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewDataActivity.this, ViewSavedDataActivity.class);
                startActivity(intent);
            }
        });
    }
}