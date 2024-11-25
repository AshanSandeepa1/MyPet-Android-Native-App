package com.ashan.mypet;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HealthRecordsActivity extends AppCompatActivity {

    private RecyclerView petRecyclerView;
    private RecyclerView healthRecordsRecyclerView;
    private PetAdapter petAdapter;
    private HealthRecordAdapter healthRecordAdapter;
    private List<Pet> petList;
    private List<HealthRecord> healthRecordList;
    private FirebaseFirestore firestore;
    private ImageButton fabAddRecord;
    private Pet selectedPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Transparent status bar
        Window window = getWindow();
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_health_records);

        firestore = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        petRecyclerView = findViewById(R.id.petRecyclerView);
        healthRecordsRecyclerView = findViewById(R.id.healthRecordsRecyclerView);
        fabAddRecord = findViewById(R.id.fabAddRecord);

        petRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        healthRecordsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        petList = new ArrayList<>();
        healthRecordList = new ArrayList<>();

        //Adding Health Record by AddHealthRecordFragment.java
        fabAddRecord.setOnClickListener(v -> {
            if (selectedPet != null) {
                AddHealthRecordFragment fragment = new AddHealthRecordFragment();
                Bundle args = new Bundle();
                args.putString("userId", userId);
                args.putString("petId", selectedPet.getPId());
                fragment.setArguments(args);
                fragment.show(getSupportFragmentManager(), fragment.getTag());
            } else {
                Toast.makeText(this, "Select a pet to add health records", Toast.LENGTH_SHORT).show();
            }
        });

        loadPets(userId);
    }

    private void loadPets(String userId) {
        firestore.collection("users").document(userId).collection("pets")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        petList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Pet pet = document.toObject(Pet.class);
                            pet.setPId(document.getId());
                            petList.add(pet);
                        }
                        petAdapter = new PetAdapter(petList, pet -> {
                            selectedPet = pet;
                            loadHealthRecords(userId, pet.getPId());
                        });
                        petRecyclerView.setAdapter(petAdapter);
                    } else {
                        Log.e("Firestore", "Failed to load pets");
                    }
                });
    }

    private void loadHealthRecords(String userId, String petId) {
        firestore.collection("users").document(userId).collection("pets")
                .document(petId).collection("HealthRecords")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        healthRecordList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HealthRecord record = document.toObject(HealthRecord.class);
                            record.setRecordId(document.getId());
                            healthRecordList.add(record);
                        }
                        healthRecordAdapter = new HealthRecordAdapter(healthRecordList, new HealthRecordAdapter.OnHealthRecordClickListener() {
                            @Override
                            public void onHealthRecordClick(HealthRecord healthRecord) {
                                showHealthRecordPopup(healthRecord);
                            }

                            @Override
                            public void onHealthRecordDelete(HealthRecord healthRecord) {
                                deleteHealthRecord(userId, petId, healthRecord);
                            }
                        });
                        healthRecordsRecyclerView.setAdapter(healthRecordAdapter);
                    }
                });
    }

    private void deleteHealthRecord(String userId, String petId, HealthRecord healthRecord) {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this health record?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Proceed with deletion if confirmed
                    firestore.collection("users").document(userId).collection("pets")
                            .document(petId).collection("HealthRecords")
                            .document(healthRecord.getRecordId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                healthRecordList.remove(healthRecord);
                                healthRecordAdapter.notifyDataSetChanged();
                                Toast.makeText(this, "Health record deleted", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Dismiss the dialog if user cancels
                    dialog.dismiss();
                })
                .show();
    }


    public void showHealthRecordPopup(HealthRecord healthRecord) {
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the layout for the popup
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_health_record, null);

        // Bind the views from the layout
        ImageView recordImage = popupView.findViewById(R.id.health_record_image);
        TextView titleText = popupView.findViewById(R.id.health_record_title);
        TextView typeText = popupView.findViewById(R.id.health_record_type);
        TextView vetNameText = popupView.findViewById(R.id.health_record_vet_name);
        TextView dateText = popupView.findViewById(R.id.health_record_date);
        TextView statusText = popupView.findViewById(R.id.health_record_status);
        TextView notesText = popupView.findViewById(R.id.health_record_notes);

        // Set the data in the popup
        titleText.setText(healthRecord.getTitle());
        typeText.setText("Type: " + healthRecord.getType());
        vetNameText.setText("Vet: " + healthRecord.getVetName());
        dateText.setText("Date: " + healthRecord.getDate().toDate().toString());  // Convert Timestamp to Date
        statusText.setText("Status: " + healthRecord.getStatus());
        notesText.setText("Notes: " + healthRecord.getNotes());

        // Load the image (if it exists) using Picasso or Glide
        if (healthRecord.getImage() != null && !healthRecord.getImage().isEmpty()) {
            Picasso.get().load(healthRecord.getImage()).into(recordImage);
        } else {
            recordImage.setImageResource(R.drawable.ic_vaccination);  // Set a default image if no image is available
        }

        // Set up the AlertDialog with the custom view
        builder.setView(popupView)
                .setCancelable(true)
                .setPositiveButton("CLOSE", (dialog, which) -> dialog.dismiss()) // Dismiss the dialog when "Close" is clicked
                .setNeutralButton("DOWNLOAD IMAGE", (dialog, which) -> downloadImage(healthRecord.getImage())) // Download Image Button
                .create().show();
    }
    public void downloadImage(String imageUrl) {
        // Download the image when clicked on the ImageView
        // This can be an image URL fetched from Firestore

        // Check if the image URL is valid
        if (imageUrl == null || imageUrl.isEmpty()) {
            Toast.makeText(this, "No image URL available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Download image in the background
            new Thread(() -> {
                try {
                    // Use Glide to download the image as a Bitmap
                    Glide.with(this)
                            .downloadOnly()
                            .load(imageUrl)
                            .submit()
                            .get();  // This will download the image

                    // Now you can save the image to external storage
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            saveImageToStorage(imageUrl); // Proceed with saving if permission is granted
                        }
                    }

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error downloading image", Toast.LENGTH_SHORT).show();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveImageToStorage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(storageDir, "downloaded_image.jpg");
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }
}
