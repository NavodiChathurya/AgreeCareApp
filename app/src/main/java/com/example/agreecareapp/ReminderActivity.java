package com.example.agreecareapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor; // Added missing import

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReminderActivity extends AppCompatActivity {

    private EditText etNewReminder;
    private Button btnPickDateTime, btnAddReminder;
    private CheckBox chkDailyWeatherNotification;
    private RecyclerView rvReminders;
    private ImageView backArrow;
    private ReminderAdapter reminderAdapter;
    private final List<Reminder> reminderList = new ArrayList<>();
    private DatabaseReference dbRef;
    private Calendar selectedDateTime;
    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ?
            FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reminders");
        }

        etNewReminder = findViewById(R.id.etNewReminder);
        btnPickDateTime = findViewById(R.id.btnPickDateTime);
        btnAddReminder = findViewById(R.id.btnAddReminder);
        chkDailyWeatherNotification = findViewById(R.id.chkDailyWeatherNotification);
        rvReminders = findViewById(R.id.rvReminders);
        backArrow = findViewById(R.id.backArrow);

        dbRef = FirebaseDatabase.getInstance().getReference("reminders");

        rvReminders.setLayoutManager(new LinearLayoutManager(this));
        reminderAdapter = new ReminderAdapter(reminderList);
        rvReminders.setAdapter(reminderAdapter);

        SharedPreferences prefs = getSharedPreferences("settings_prefs", MODE_PRIVATE);
        chkDailyWeatherNotification.setChecked(prefs.getBoolean("weather_notifications_enabled", false));

        backArrow.setOnClickListener(v -> finish());

        toolbar.setNavigationOnClickListener(v -> finish());

        btnPickDateTime.setOnClickListener(v -> showDateTimePicker());

        btnAddReminder.setOnClickListener(v -> addReminder());

        chkDailyWeatherNotification.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("weather_notifications_enabled", isChecked).apply());

        setupBottomNavigation();

        loadReminders();
    }

    private void showDateTimePicker() {
        selectedDateTime = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDateTime.set(year, month, dayOfMonth);
            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                btnPickDateTime.setText(sdf.format(selectedDateTime.getTime()));
            }, selectedDateTime.get(Calendar.HOUR_OF_DAY), selectedDateTime.get(Calendar.MINUTE), true).show();
        }, selectedDateTime.get(Calendar.YEAR), selectedDateTime.get(Calendar.MONTH), selectedDateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void addReminder() {
        String reminderText = etNewReminder.getText().toString().trim();
        if (TextUtils.isEmpty(reminderText)) {
            Toast.makeText(this, "Please enter a reminder", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDateTime == null) {
            Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentUserId == null) {
            Toast.makeText(this, "Please sign in to add a reminder", Toast.LENGTH_SHORT).show();
            return;
        }

        String key = dbRef.push().getKey();
        long timestamp = selectedDateTime.getTimeInMillis();
        Reminder reminder = new Reminder(reminderText, timestamp, currentUserId);
        android.util.Log.d("ReminderActivity", "Attempting to add reminder with timestamp: " + timestamp);

        if (isNetworkAvailable()) {
            dbRef.child(key).setValue(reminder).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Reminder added", Toast.LENGTH_SHORT).show();
                    saveToLocalDB(reminder);
                } else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        Toast.makeText(this, "Firebase failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                        android.util.Log.e("ReminderActivity", "Firebase add failed", exception);
                        saveToLocalDB(reminder);
                    } else {
                        Toast.makeText(this, "Failed to add reminder: Unknown error", Toast.LENGTH_SHORT).show();
                    }
                }
                clearInput();
            });
        } else {
            if (saveToLocalDB(reminder)) {
                Toast.makeText(this, "Reminder saved locally (offline)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save reminder locally", Toast.LENGTH_SHORT).show();
            }
            clearInput();
        }
    }

    private boolean saveToLocalDB(Reminder reminder) {
        return dbHelper.insertReminder(reminder.getText(), reminder.getTimestamp(), reminder.getUserId());
    }

    private void clearInput() {
        etNewReminder.setText("");
        btnPickDateTime.setText("Pick Date and Time");
        selectedDateTime = null;
    }

    private void loadReminders() {
        if (isNetworkAvailable() && currentUserId != null) {
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    reminderList.clear();
                    for (DataSnapshot reminderSnap : snapshot.getChildren()) {
                        Reminder reminder = reminderSnap.getValue(Reminder.class);
                        if (reminder != null && currentUserId.equals(reminder.getUserId())) {
                            reminderList.add(reminder);
                        }
                    }
                    loadLocalReminders();
                    reminderAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ReminderActivity.this, "Failed to load reminders from Firebase", Toast.LENGTH_SHORT).show();
                    loadLocalReminders();
                }
            });
        } else {
            loadLocalReminders();
        }
    }

    private void loadLocalReminders() {
        if (currentUserId != null) {
            Cursor cursor = dbHelper.getRemindersByUserId(currentUserId); // Line 213
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String text = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.REM_COL_2));
                    long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.REM_COL_3));
                    Reminder reminder = new Reminder(text, timestamp, currentUserId);
                    if (!reminderList.contains(reminder)) {
                        reminderList.add(reminder);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
            reminderAdapter.notifyDataSetChanged();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navTasks = findViewById(R.id.navTasks);
        LinearLayout navReport = findViewById(R.id.navReport);
        LinearLayout navSettings = findViewById(R.id.navSettings);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard.class));
            finish();
        });
        // Add similar listeners for navTasks, navReport, navSettings if needed
    }
}