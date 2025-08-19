package com.example.agreecareapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    EditText emailField, passwordField;
    Button btnLogin;
    TextView signupLink, arrowLeft, arrowRight;
    DatabaseHelper db; // Database instance

    private static final String SENDER_EMAIL = "chathunavodi2@gmail.com"; // Your Gmail address
    private static final String SENDER_APP_PASSWORD = "your_16_char_app_password"; // Replace with 16-char app password from Google

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Request internet permission at runtime (Android 6.0+)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET}, 100);
        }

        // Connect XML views
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        btnLogin = findViewById(R.id.btnLogin);
        signupLink = findViewById(R.id.signupLink);
        arrowLeft = findViewById(R.id.arrow_left);
        arrowRight = findViewById(R.id.arrow_right);

        // Initialize database
        db = new DatabaseHelper(this);

        // Login button validates credentials and navigates to Dashboard
        btnLogin.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(LoginActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate user credentials
            Cursor cursor = db.getUserByEmail(email);
            if (cursor != null && cursor.moveToFirst()) {
                int passwordIndex = cursor.getColumnIndex(DatabaseHelper.COL_4);
                String storedPassword = cursor.getString(passwordIndex);
                cursor.close();

                if (password.equals(storedPassword)) {
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                    startActivity(intent);
                    finish(); // Prevent going back to login
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                if (cursor != null) cursor.close();
            }
        });

        // Signup link opens MainActivity (sign-up screen)
        signupLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Left arrow: Back to MainActivity (Signup)
        arrowLeft.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Right arrow: Forgot Password functionality with OTP
        arrowRight.setOnClickListener(v -> {
            Log.d(TAG, "Forgot Password button clicked");
            if (!isNetworkAvailable()) {
                handler.post(() -> Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_SHORT).show());
                Log.e(TAG, "No internet connection available");
                return;
            }
            // Create a dialog for forgot password (enter email)
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Reset Password");

            final EditText emailInput = new EditText(LoginActivity.this);
            emailInput.setHint("Enter your email");
            builder.setView(emailInput);

            builder.setPositiveButton("Send OTP", (dialog, which) -> {
                String email = emailInput.getText().toString().trim();
                Log.d(TAG, "Entered email: " + email);

                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if email exists in the database
                Cursor cursor = db.getUserByEmail(email.toLowerCase()); // Case-insensitive check
                if (cursor != null && cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex(DatabaseHelper.COL_1);
                    final int userId = cursor.getInt(idIndex);
                    Log.d(TAG, "User found with ID: " + userId);
                    cursor.close();

                    // Generate 6-digit OTP
                    final String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
                    Log.d(TAG, "Generated OTP: " + otp);

                    // Send OTP via email in background thread
                    new Thread(() -> {
                        try {
                            Properties props = new Properties();
                            props.put("mail.smtp.host", "smtp.gmail.com");
                            props.put("mail.smtp.socketFactory.port", "465");
                            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                            props.put("mail.smtp.auth", "true");
                            props.put("mail.smtp.port", "465");
                            props.put("mail.smtp.ssl.enable", "true");

                            Session session = Session.getDefaultInstance(props, new Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD);
                                }
                            });
                            session.setDebug(true); // Enable debug output for SMTP

                            MimeMessage message = new MimeMessage(session);
                            message.setFrom(new InternetAddress(SENDER_EMAIL));
                            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                            message.setSubject("OTP for Password Reset");
                            message.setText("Your OTP is: " + otp);

                            Transport.send(message);
                            Log.d(TAG, "OTP email sent successfully to: " + email);

                            handler.post(() -> showOtpDialog(otp, userId));

                        } catch (MessagingException e) {
                            Log.e(TAG, "Email sending failed", e); // Log full stack trace
                            handler.post(() -> Toast.makeText(LoginActivity.this, "Failed to send OTP: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        } catch (Exception e) {
                            Log.e(TAG, "Unexpected error", e);
                            handler.post(() -> Toast.makeText(LoginActivity.this, "An unexpected error occurred", Toast.LENGTH_LONG).show());
                        }
                    }).start();

                } else {
                    Toast.makeText(LoginActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                    if (cursor != null) cursor.close();
                    Log.d(TAG, "Email not found in database: " + email);
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
    }

    // Method to show OTP input dialog
    private void showOtpDialog(String expectedOtp, int userId) {
        Log.d(TAG, "Showing OTP dialog with expected OTP: " + expectedOtp);
        AlertDialog.Builder otpBuilder = new AlertDialog.Builder(LoginActivity.this);
        otpBuilder.setTitle("Enter OTP");

        final EditText otpInput = new EditText(LoginActivity.this);
        otpInput.setHint("Enter 6-digit OTP");
        otpBuilder.setView(otpInput);

        otpBuilder.setPositiveButton("Validate", (dialog, which) -> {
            String enteredOtp = otpInput.getText().toString().trim();
            Log.d(TAG, "Entered OTP: " + enteredOtp);
            if (enteredOtp.equals(expectedOtp)) {
                showNewPasswordDialog(userId);
            } else {
                Toast.makeText(LoginActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            }
        });

        otpBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        otpBuilder.show();
    }

    // Method to show new password input dialog
    private void showNewPasswordDialog(int userId) {
        Log.d(TAG, "Showing new password dialog for user ID: " + userId);
        AlertDialog.Builder passwordDialog = new AlertDialog.Builder(LoginActivity.this);
        passwordDialog.setTitle("Set New Password");

        final EditText newPasswordInput = new EditText(LoginActivity.this);
        newPasswordInput.setHint("Enter new password");
        passwordDialog.setView(newPasswordInput);

        passwordDialog.setPositiveButton("Save", (dialog, which) -> {
            String newPassword = newPasswordInput.getText().toString().trim();
            if (newPassword.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter a new password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update password in the database
            boolean updated = db.updatePassword(userId, newPassword);
            if (updated) {
                Toast.makeText(LoginActivity.this, "Password reset successful! Please log in.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Failed to reset password. Try again.", Toast.LENGTH_LONG).show();
            }
        });

        passwordDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        passwordDialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Internet permission granted");
            } else {
                Log.e(TAG, "Internet permission denied");
                Toast.makeText(this, "Internet permission required for OTP", Toast.LENGTH_SHORT).show();
            }
        }
    }
}