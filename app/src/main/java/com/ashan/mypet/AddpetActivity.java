package com.ashan.mypet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddpetActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;

    private ImageView profilePicture;
    private Button addPhotoButton;
    private EditText petName, petSpecies, petBreed, petDateOfBirth, petColor, petWeight, petSex, petNeutered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpet);

        // Initialize views
        profilePicture = findViewById(R.id.img_profile_picture);
        addPhotoButton = findViewById(R.id.btn_add_photo);

        petName = findViewById(R.id.petName);
        petSpecies = findViewById(R.id.petSpecies);
        petBreed = findViewById(R.id.petBreed);
        petDateOfBirth = findViewById(R.id.petDateOfBirth);
        petColor = findViewById(R.id.petColor);
        petWeight = findViewById(R.id.petWeight);
        petSex = findViewById(R.id.petSex);
        petNeutered = findViewById(R.id.petNeutered);

        // Set up add photo button to open gallery
        addPhotoButton.setOnClickListener(v -> {
            // Choose between camera or gallery
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
        });

        // Set up profile picture click to open camera
        profilePicture.setOnClickListener(v -> {
            // Open Camera
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                // Get the captured image from camera
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profilePicture.setImageBitmap(photo);
            } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                // Get the image selected from gallery
                Uri selectedImage = data.getData();
                profilePicture.setImageURI(selectedImage);
            }
        } else {
            // Handle if action is cancelled
            Toast.makeText(this, "Action Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    // This is where you would save the data to Firestore later
    // You can collect the data from the EditText views like this:
    // String name = petName.getText().toString();
    // String species = petSpecies.getText().toString();
    // etc.
}
