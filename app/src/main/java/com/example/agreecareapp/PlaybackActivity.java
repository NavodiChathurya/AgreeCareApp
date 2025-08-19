package com.example.agreecareapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class PlaybackActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Button btnPlay, btnPause, btnStop;
    private TextView audioFileName, txtDuration;
    private ProgressBar playbackProgress;
    private ImageView backArrow;

    private String audioPath;
    private Handler handler = new Handler();
    private Runnable updateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        // 🧩 Bind UI Elements
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        audioFileName = findViewById(R.id.audioFileName);
        txtDuration = findViewById(R.id.txtDuration);
        playbackProgress = findViewById(R.id.playbackProgress);
        backArrow = findViewById(R.id.backArrow);

        // 🔙 Handle Back Arrow
        backArrow.setOnClickListener(v -> finish());

        // 🎯 Retrieve audio path from Intent
        audioPath = getIntent().getStringExtra("audio_path");

        if (audioPath == null || audioPath.isEmpty()) {
            Toast.makeText(this, "No audio file received", Toast.LENGTH_LONG).show();
            audioFileName.setText("No audio selected");
            disableControls();
            return;
        } else {
            String fileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
            audioFileName.setText(fileName);
        }

        mediaPlayer = new MediaPlayer();

        // ▶️ Playback Controls
        btnPlay.setOnClickListener(v -> playAudio());
        btnPause.setOnClickListener(v -> pauseAudio());
        btnStop.setOnClickListener(v -> stopAudio());
    }

    private void playAudio() {
        try {
            if (mediaPlayer.isPlaying()) return;

            if (audioPath == null) {
                Toast.makeText(this, "No audio file found!", Toast.LENGTH_SHORT).show();
                return;
            }

            File file = new File(audioPath);
            if (!file.exists()) {
                Toast.makeText(this, "Audio file does not exist", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("Playback", "Playing file: " + audioPath); // ✅ Log file path

            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            playbackProgress.setMax(mediaPlayer.getDuration());

            updateRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        int current = mediaPlayer.getCurrentPosition();
                        int total = mediaPlayer.getDuration();
                        playbackProgress.setProgress(current);
                        txtDuration.setText(formatTime(current) + " / " + formatTime(total));
                        handler.postDelayed(this, 500);
                    }
                }
            };
            handler.post(updateRunnable);

        } catch (IOException e) {
            Log.e("Playback", "Error during playback", e);
            Toast.makeText(this, "Playback failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            handler.removeCallbacks(updateRunnable);
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            handler.removeCallbacks(updateRunnable);
            playbackProgress.setProgress(0);
            txtDuration.setText("00:00 / 00:00");

            try {
                mediaPlayer.prepare(); // Allow replay
            } catch (IOException e) {
                Toast.makeText(this, "Could not reset player", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String formatTime(int milliseconds) {
        int mins = (milliseconds / 1000) / 60;
        int secs = (milliseconds / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
    }

    private void disableControls() {
        btnPlay.setEnabled(false);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
        playbackProgress.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
