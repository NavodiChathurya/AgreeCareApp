package com.example.agreecareapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddCropActivity extends AppCompatActivity {

    private EditText editName, editStatus, editDate, editMessage;
    private Button btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crop);

        // Initialize CropDataManager
        CropDataManager.init(this);

        // Setup toolbar with back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Crop");
        }

        // Views
        editName = findViewById(R.id.editCropName);
        editStatus = findViewById(R.id.editCropStatus);
        editDate = findViewById(R.id.editCropDate);
        editMessage = findViewById(R.id.editCropMessage);
        btnSave = findViewById(R.id.btnSaveCrop);

        // Date picker for planting/harvest date
        editDate.setOnClickListener(v -> showDatePickerDialog());
        editDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDatePickerDialog();
        });

        // Save button logic
        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String status = editStatus.getText().toString().trim();
            String dateStr = editDate.getText().toString().trim();
            String message = editMessage.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(status) || TextUtils.isEmpty(dateStr)) {
                Toast.makeText(this, "Please fill name, status, and date fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create crop and add to local storage
            String userId = "anonymous";
            long timestamp = System.currentTimeMillis();
            Crop crop = new Crop(name, status, dateStr, message, timestamp, userId);
            CropDataManager.addCrop(crop);

            Toast.makeText(this, "Crop added successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, month1, dayOfMonth);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    editDate.setText(dateFormat.format(selectedDate.getTime()));
                }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}