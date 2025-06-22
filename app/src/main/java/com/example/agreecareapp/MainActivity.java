package com.example.agreecareapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText nameField, emailField, passwordField;
    Button signupButton;
    TextView loginLink, arrowLeft;
    DatabaseHelper db; // Database instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        nameField = findViewById(R.id.editTextName);
        emailField = findViewById(R.id.editTextEmail);
        passwordField = findViewById(R.id.editTextPassword);
        signupButton = findViewById(R.id.signupButton);
        loginLink = findViewById(R.id.loginLink);
        arrowLeft = findViewById(R.id.arrow_left);

        // Initialize database
        db = new DatabaseHelper(this);

        // Back to HomeActivity
        arrowLeft.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        });

        // Signup logic
        signupButton.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(MainActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean inserted = db.insertUser(name, email, password);
            if (inserted) {
                Toast.makeText(MainActivity.this, "Signup successful! Please log in.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Signup failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate to LoginActivity
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }
}
