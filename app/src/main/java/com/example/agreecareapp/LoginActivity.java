package com.example.agreecareapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText emailField, passwordField;
    Button btnLogin;
    TextView signupLink, arrowLeft, arrowRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Connect XML views
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        btnLogin = findViewById(R.id.btnLogin);
        signupLink = findViewById(R.id.signupLink);
        arrowLeft = findViewById(R.id.arrow_left);
        arrowRight = findViewById(R.id.arrow_right);

        // 🔐 Login button navigates to Dashboard
        btnLogin.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // You can add real login logic here
            Intent intent = new Intent(LoginActivity.this, Dashboard.class);
            startActivity(intent);
            finish(); // Prevent going back to login
        });

        // 📝 Signup link opens MainActivity (sign-up screen)
        signupLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // 🔙 Left arrow: Back to MainActivity (Signup)
        arrowLeft.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // ➡️ Right arrow: Placeholder (e.g., Guest or Help)
        arrowRight.setOnClickListener(v -> {
            Toast.makeText(this, "Guest login or feature coming soon", Toast.LENGTH_SHORT).show();
            // Or navigate somewhere if needed
            // startActivity(new Intent(LoginActivity.this, GuestDashboard.class));
        });
    }
}
