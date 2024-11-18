package com.ashan.mypet;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.util.Calendar;

public class AddpetActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;

    private ImageView profilePicture;
    private Button addPhotoButton, submitButton, dobButton;
    private Spinner petSpecies;
    private RadioGroup petSex;
    private Switch petNeutered, petVaccinated;

    private int year, month, day;  // To store the selected date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpet);

        // Initialize views
        profilePicture = findViewById(R.id.img_profile_picture);
        addPhotoButton = findViewById(R.id.btn_add_photo);
        submitButton = findViewById(R.id.btn_submit);
        dobButton = findViewById(R.id.btn_date_of_birth);

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
        String species = petSpecies.getSelectedItem().toString();
        int selectedSexId = petSex.getCheckedRadioButtonId();
        boolean isNeutered = petNeutered.isChecked();
        boolean isVaccinated = petVaccinated.isChecked();

        String sex = "";
        if (selectedSexId != -1) {
            RadioButton selectedSex = findViewById(selectedSexId);
            sex = selectedSex.getText().toString();
        }

        // TODO: Add further form validation and save data to Firestore
        Toast.makeText(this, "Pet details submitted successfully!", Toast.LENGTH_SHORT).show();
    }
}
