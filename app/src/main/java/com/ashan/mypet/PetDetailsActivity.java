package com.ashan.mypet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PetDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PetDetailsActivity";

    private ImageView imgPetPicture;
    private TextView textPetName, textPetSpecies, textPetBreed, textPetDob,
            textPetColor, textPetWeight, textPetSex, textPetNeutered, textPetVaccinated;
    private Button btnMedicalRecords, btnDeletePet;

    private FirebaseFirestore firestore;
    private String petId; // Only petId is passed from the previous fragment
    private String userId; // Fetched using FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Transparent status bar and layout inflation
        Window window = getWindow();
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_pet_details);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get petId from intent
        petId = getIntent().getStringExtra("PET_ID");
        if (petId == null) {
            Log.e(TAG, "Invalid Pet ID");
            Toast.makeText(this, "Invalid Pet ID", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
            return;
        } else {
            Log.d(TAG, "Pet ID: " + petId);
        }

        // Get userId using FirebaseAuth
        userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Log.e(TAG, "User not logged in");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
            return;
        } else {
            Log.d(TAG, "User ID: " + userId);
        }

        // Initialize UI components
        imgPetPicture = findViewById(R.id.img_pet_picture);
        textPetName = findViewById(R.id.text_petName);
        textPetSpecies = findViewById(R.id.text_petSpecies);
        textPetBreed = findViewById(R.id.text_petBreed);
        textPetDob = findViewById(R.id.text_petDob);
        textPetColor = findViewById(R.id.text_petColor);
        textPetWeight = findViewById(R.id.text_petWeight);
        textPetSex = findViewById(R.id.text_petSex);
        textPetNeutered = findViewById(R.id.text_petNeutered);
        textPetVaccinated = findViewById(R.id.text_petVaccinated);
        btnMedicalRecords = findViewById(R.id.btn_medical_records);
        btnDeletePet = findViewById(R.id.btn_delete_pet);

        // Load pet details
        loadPetDetails();

        // Handle button click events
        btnMedicalRecords.setOnClickListener(view -> {
            // Open medical records activity
            Intent intent = new Intent(PetDetailsActivity.this, HealthRecordsActivity.class);
            startActivity(intent);
        });

        btnDeletePet.setOnClickListener(view -> {
            // Delete pet logic
            deletePet();
        });
    }

    private void loadPetDetails() {
        Log.d(TAG, "Loading pet details for petId: " + petId);

        firestore.collection("users").document(userId)
                .collection("pets").document(petId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Pet details found: " + document.getData());

                                // Set pet details
                                textPetName.setText(document.getString("name"));
                                textPetSpecies.setText(document.getString("species"));
                                textPetBreed.setText(document.getString("breed"));

                                // Handle dob (Date of Birth) which might be a Timestamp
                                Object dobObj = document.get("dob");
                                if (dobObj instanceof com.google.firebase.Timestamp) {
                                    com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) dobObj;
                                    // Format the Timestamp to a String, e.g., "MM/dd/yyyy"
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault());
                                    textPetDob.setText(sdf.format(timestamp.toDate()));
                                } else if (dobObj instanceof String) {
                                    textPetDob.setText((String) dobObj);
                                } else {
                                    textPetDob.setText("Unknown");
                                }

                                // Handle weight (which is stored as a number)
                                Object weightObj = document.get("weight");
                                if (weightObj instanceof Number) {
                                    double weight = ((Number) weightObj).doubleValue();
                                    textPetWeight.setText(String.valueOf(weight));
                                } else {
                                    textPetWeight.setText("Unknown");
                                }

                                textPetColor.setText(document.getString("color"));
                                textPetSex.setText(document.getString("gender"));
                                textPetNeutered.setText(Boolean.TRUE.equals(document.getBoolean("neutered")) ? "Yes" : "No");
                                textPetVaccinated.setText(Boolean.TRUE.equals(document.getBoolean("vaccinated")) ? "Yes" : "No");

                                // Load pet image using Glide
                                String imageUrl = document.getString("image");
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(PetDetailsActivity.this)
                                            .load(imageUrl)
                                            .into(imgPetPicture);
                                } else {
                                    imgPetPicture.setImageResource(R.drawable.ic_add_photo);
                                }
                            } else {
                                Log.e(TAG, "Pet document does not exist");
                                Toast.makeText(PetDetailsActivity.this, "Pet details not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "Failed to load pet details: " + task.getException());
                            Toast.makeText(PetDetailsActivity.this, "Failed to load pet details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void deletePet() {
        Log.d(TAG, "Attempting to delete pet with petId: " + petId);

        // Create the confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Delete Pet")
                .setMessage("Are you sure you want to delete this pet?")
                .setCancelable(false) // Prevents closing the dialog by tapping outside
                .setPositiveButton("Yes", (dialog, id) -> {
                    // If the user confirms, delete the pet
                    Log.d(TAG, "User confirmed pet deletion");
                    firestore.collection("users").document(userId)
                            .collection("pets").document(petId)
                            .delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Pet deleted successfully");
                                    Toast.makeText(PetDetailsActivity.this, "Pet deleted successfully", Toast.LENGTH_SHORT).show();
                                    finish(); // Close the activity
                                } else {
                                    Log.e(TAG, "Failed to delete pet: " + task.getException());
                                    Toast.makeText(PetDetailsActivity.this, "Failed to delete pet", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("No", (dialog, id) -> {
                    // If the user cancels, dismiss the dialog
                    Log.d(TAG, "User canceled pet deletion");
                    dialog.dismiss();
                })
                .show(); // Show the dialog
    }

}
