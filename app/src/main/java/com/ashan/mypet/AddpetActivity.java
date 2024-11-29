package com.ashan.mypet;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import android.widget.EditText;


public class AddpetActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;

    private ImageView profilePicture;
    private Button addPhotoButton, submitButton, dobButton, btnMedicalRecords;
    private Spinner petSpecies;
    private RadioGroup petSex;
    private Switch petNeutered, petVaccinated;

    private int year, month, day;  // To store the selected date

    // Firestore instance
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;

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

        setContentView(R.layout.activity_addpet);

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();  // Get current user's ID

        // Initialize views
        profilePicture = findViewById(R.id.img_profile_picture);
        addPhotoButton = findViewById(R.id.btn_add_photo);
        submitButton = findViewById(R.id.btn_submit);
        dobButton = findViewById(R.id.btn_date_of_birth);
        btnMedicalRecords = findViewById(R.id.btn_medical_records);

        petSpecies = findViewById(R.id.petSpecies);
        petSex = findViewById(R.id.petSex);
        petNeutered = findViewById(R.id.petNeutered);
        petVaccinated = findViewById(R.id.petVaccinated);

        // Set up Add Photo button
        addPhotoButton.setOnClickListener(v -> showPhotoOptions());

        // Handle Date of Birth selection
        dobButton.setOnClickListener(v -> showDatePickerDialog());

        // Set up Submit button
        submitButton.setOnClickListener(v -> submitPetDetails());

        // Set up Medical Records button
        btnMedicalRecords.setOnClickListener(v -> openHealthRecordsActivity());
    }

    private void showPhotoOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Photo")
                .setItems(new CharSequence[]{"Capture from Camera", "Choose from Gallery"},
                        (dialog, which) -> {
                            if (which == 0) {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                            } else if (which == 1) {
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                            }
                        })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profilePicture.setImageBitmap(photo);
            } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                Uri selectedImage = data.getData();
                profilePicture.setImageURI(selectedImage);
            }
        } else {
            Toast.makeText(this, "Action Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    // Format the selected date (optional)
                    String dateOfBirth = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    dobButton.setText(dateOfBirth);  // Set the date on the button
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void submitPetDetails() {
        // Collect data from form
        String species = petSpecies.getSelectedItem().toString();

        // Correct casting to EditText for breed, color, and weight
        EditText petName = findViewById(R.id.petName);
        EditText petBreed = findViewById(R.id.petBreed);  // Assuming these are EditText views
        EditText petColor = findViewById(R.id.petColor);  // Assuming these are EditText views
        EditText petWeight = findViewById(R.id.petWeight);  // Assuming these are EditText views

        String name = petName.getText().toString();
        String breed = petBreed.getText().toString();  // Correctly get text from EditText
        String color = petColor.getText().toString();  // Correctly get text from EditText
        String weight = petWeight.getText().toString();  // Correctly get text from EditText

        // Get sex from selected radio button
        String sex = getSelectedSex();

        // Get neutered and vaccinated status from switches
        boolean isNeutered = petNeutered.isChecked();
        boolean isVaccinated = petVaccinated.isChecked();

        // Get the selected Date of Birth (dob)
        String dob = dobButton.getText().toString();  // Get the selected Date of Birth

        // Validation
        if (name.isEmpty() || species.isEmpty() || breed.isEmpty() || color.isEmpty() || weight.isEmpty() || dob.isEmpty() || sex.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert dob to Timestamp
        String[] dateParts = dob.split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1; // Adjust for zero-based month
        int year = Integer.parseInt(dateParts[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Timestamp dobTimestamp = new Timestamp(calendar.getTime());

        // Prepare pet data
        Map<String, Object> pet = new HashMap<>();
        pet.put("name", name);
        pet.put("species", species);
        pet.put("breed", breed);
        pet.put("color", color);
        pet.put("dob", dobTimestamp);
        pet.put("weight", Double.parseDouble(weight));
        pet.put("gender", sex);
        pet.put("neutered", isNeutered);
        pet.put("vaccinated", isVaccinated);
        //pet.put("image", "https://i.pinimg.com/736x/3b/37/cd/3b37cd80d4f092ed392b1453b64cf0d0.jpg");
        pet.put("image", "https://i.pinimg.com/564x/d2/c4/20/d2c4208fef220c81e815b4693c36995a.jpg");  // Test image URL

        // Get the current user ID from Firebase Authentication
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        // Save pet details to Firestore under the user's document
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .collection("pets")
                .add(pet)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Success: Display success message and open HealthRecordsActivity
                            Toast.makeText(AddpetActivity.this, "Pet details added successfully!", Toast.LENGTH_SHORT).show();
                            //openHealthRecordsActivity();  // After successfully adding, open HealthRecordsActivity
                        } else {
                            // Error: Display error message
                            Toast.makeText(AddpetActivity.this, "Error adding pet details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private String getSelectedSex() {
        int selectedSexId = petSex.getCheckedRadioButtonId();
        if (selectedSexId != -1) {
            RadioButton selectedSex = findViewById(selectedSexId);
            return selectedSex.getText().toString();
        }
        return "";  // Return empty if no sex is selected
    }

    private void openHealthRecordsActivity() {
        // Navigate to HealthRecordsActivity after saving pet details
        Intent intent = new Intent(AddpetActivity.this, HealthRecordsActivity.class);
        startActivity(intent);  // Launch HealthRecordsActivity
        finish(); // Finish AddpetActivity so that it's removed from the back stack
    }
}
