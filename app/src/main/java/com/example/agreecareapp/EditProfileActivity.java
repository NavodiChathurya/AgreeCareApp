package com.example.agreecareapp;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etPhone;
    private Button btnSave, btnBack;
    private ImageView ivUserImage;
    private DatabaseHelper dbHelper;
    private int userId; // Consider changing to long if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        ivUserImage = findViewById(R.id.ivUserImage);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        // Pre-fill fields if userId is set (e.g., from intent or login)
        if (userId > 0) {
            loadUserData();
        }

        btnSave.setOnClickListener(v -> saveProfile());
        btnBack.setOnClickListener(v -> finish()); // Back button to return to previous screen
    }

    private void loadUserData() {
        if (userId > 0) {
            Cursor cursor = dbHelper.getUserById(userId);
            if (cursor != null && cursor.moveToFirst()) {
                etName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_2)));
                etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_3)));
                etPassword.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_4)));
                etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_5)));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_6));
                if (imageUri != null) {
                    File imgFile = new File(imageUri);
                    if (imgFile.exists()) {
                        ivUserImage.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                    } else {
                        ivUserImage.setImageResource(R.drawable.dp); // Fallback to default
                    }
                } else {
                    ivUserImage.setImageResource(R.drawable.dp); // Default if no URI
                }
                cursor.close();
            }
        }
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add timestamp for profile update
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String updateTime = sdf.format(calendar.getTime()); // e.g., 2025-08-01 17:28:00

        if (dbHelper.isEmailExists(email) && userId > 0) {
            if (dbHelper.updateUser(userId, name, email, phone, null)) { // Null for imageUri
                Toast.makeText(this, "Profile updated at " + updateTime, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (dbHelper.insertUser(name, email, password, phone, null)) { // Null for imageUri
                userId = (int) dbHelper.getLastInsertedUserId();
                Toast.makeText(this, "Profile saved at " + updateTime + ", User ID: " + userId, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show();
            }
        }
    }
}