package com.ashan.mypet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText editName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonCreate;
    private TextView textViewLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Adjust padding for Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        editName = findViewById(R.id.editName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonCreate = findViewById(R.id.buttonLogin);
        textViewLogin = findViewById(R.id.textViewCreateAccount);

        // Set up button click listeners
        buttonCreate.setOnClickListener(v -> registerUser());
        textViewLogin.setOnClickListener(v -> {
            // Navigate to Login Activity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String name = editName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            editName.setError("Name is required!");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required!");
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match!");
            return;
        }

        // Register the user with Firebase
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the user's unique ID
                String userId = mAuth.getCurrentUser().getUid();

                // Create a HashMap to store user details
                HashMap<String, Object> user = new HashMap<>();
                user.put("username", name); // Username entered by the user
                user.put("email", email);   // Email entered by the user

                // Save user details to Firestore
                db.collection("users").document(userId).set(user)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(RegisterActivity.this, "User registered and data saved successfully!", Toast.LENGTH_SHORT).show();

                            // Navigate to LoginActivity
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(RegisterActivity.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Registration failed
                Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
