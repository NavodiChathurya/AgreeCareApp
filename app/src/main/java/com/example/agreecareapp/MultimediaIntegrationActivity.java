package com.example.agreecareapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MultimediaIntegrationActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private Uri photoUri;
    private ImageView imageView;
    private Button btnCaptureImage, btnViewGallery, btnOpenVoiceRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multimedia_integration);

        // 🖼️ UI Elements
        imageView = findViewById(R.id.imagePreview);
        btnCaptureImage = findViewById(R.id.btnCaptureImage);
        btnViewGallery = findViewById(R.id.btnViewGallery);
        btnOpenVoiceRecorder = findViewById(R.id.btnOpenVoiceRecorder);

        // 🔽 Bottom Navigation
        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, Dashboard.class)));

        findViewById(R.id.navTasks).setOnClickListener(v ->
                startActivity(new Intent(this, SensorLocationActivity.class)));

        findViewById(R.id.navReport).setOnClickListener(v ->
                startActivity(new Intent(this, ViewDataActivity.class)));

        // ✅ Settings now navigates to ProfileActivity
        findViewById(R.id.navSettings).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        // 📸 Capture Image
        btnCaptureImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_CODE);
            } else {
                dispatchTakePictureIntent();
            }
        });

        // 🖼️ View Gallery
        btnViewGallery.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_VIEW,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");
            startActivity(galleryIntent);
        });

        // 🎙️ Open Voice Recorder
        btnOpenVoiceRecorder.setOnClickListener(v -> {
            startActivity(new Intent(this, VoiceRecorderActivity.class));
        });
    }

    // Launch Camera Intent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            if (isEmulator()) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                try {
                    File photoFile = createImageFile();
                    if (photoFile != null) {
                        photoUri = FileProvider.getUriForFile(this,
                                getPackageName() + ".fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } catch (IOException e) {
                    Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    // Create File for Image
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "IMG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    // Handle Camera Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (isEmulator() && data != null && data.getExtras() != null) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(thumbnail);
            } else if (photoUri != null) {
                imageView.setImageURI(photoUri);
            }
        }
    }

    // Handle Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Emulator Check
    private boolean isEmulator() {
        return Build.FINGERPRINT.contains("generic")
                || Build.MODEL.contains("Emulator")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
}