package com.example.agreecareapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VoiceRecorderActivity extends AppCompatActivity {

    private static final int REQUEST_MIC_PERMISSION = 1001;

    private ImageView micButton, backArrow;
    private Button startButton, stopButton;
    private TextView timerText;
    private FloatingActionButton btnAdd;

    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;

    private int seconds = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recorder);

        micButton = findViewById(R.id.micButton);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        timerText = findViewById(R.id.timerText);
        btnAdd = findViewById(R.id.btnAdd);
        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(v -> finish());
        stopButton.setEnabled(false);

        startButton.setOnClickListener(v -> {
            if (checkMicPermission()) {
                startRecording();
            } else {
                requestMicPermission();
            }
        });

        stopButton.setOnClickListener(v -> stopRecording());

        btnAdd.setOnClickListener(v -> {
            Toast.makeText(this, "Add button clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean checkMicPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestMicPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_MIC_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_MIC_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRecording() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File musicDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (musicDir != null && !musicDir.exists()) {
            musicDir.mkdirs();
        }
        audioFilePath = musicDir + "/recording_" + timeStamp + ".mp4";

        Log.d("RecorderPath", "Saving to: " + audioFilePath);  // ✅ Debug path

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // ✅ correct format
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);    // ✅ correct encoder
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            micButton.setAlpha(0.3f);
            startTimer();

            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Recording failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (isRecording) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;

                stopTimer();

                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                micButton.setAlpha(1.0f);

                Toast.makeText(this, "Recording saved:\n" + audioFilePath, Toast.LENGTH_LONG).show();

                // 🎧 Launch playback screen
                Intent intent = new Intent(this, PlaybackActivity.class);
                intent.putExtra("audio_path", audioFilePath);
                startActivity(intent);

            } catch (Exception e) {
                Toast.makeText(this, "Error stopping recording", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void startTimer() {
        seconds = 0;
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                int mins = seconds / 60;
                int secs = seconds % 60;
                timerText.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, secs));
                seconds++;
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            stopRecording();
        }
    }
}
