package com.ashan.mypet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;
    private TextView createAccount, guestLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the status bar transparent and extend content behind it
        Window window = getWindow();
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // Makes icons and text dark
        );

        setContentView(R.layout.activity_login);

        // Initialize Views
        emailField = findViewById(R.id.editTextEmail);
        passwordField = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        createAccount = findViewById(R.id.textViewCreateAccount);
        guestLogin = findViewById(R.id.textViewGuestLogin);

        // Login Button Logic
        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Firebase Authentication Logic Here
                loginUser(email, password);
            }
        });

        // Create Account Logic
        createAccount.setOnClickListener(v -> {
            // Navigate to Create Account Screen
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Guest Login Logic
        guestLogin.setOnClickListener(v -> {
            // Navigate to Main Screen
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String email, String password) {
        // Add Firebase Authentication Logic Here
        Toast.makeText(this, "Login Attempt for " + email, Toast.LENGTH_SHORT).show();
    }
}
