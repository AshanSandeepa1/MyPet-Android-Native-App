package com.ashan.mypet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

public class AddHealthRecordFragment extends BottomSheetDialogFragment {

    private EditText etTitle, etVetName, etNotes;
    private Button btnSelectDate, btnUploadImage, btnSave;
    private Spinner spType;
    private ImageView ivImage;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private Timestamp selectedDate;
    private String imageUrl; // Store the URL of uploaded image
    private TextView textDate;
    private int year, day, month;

    private static final int IMAGE_REQUEST_CODE = 100;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_health_record_add, container, false);

        // Initialize Firebase and UI components
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        etTitle = rootView.findViewById(R.id.etTitle);
        etVetName = rootView.findViewById(R.id.etVetName);
        etNotes = rootView.findViewById(R.id.etNotes);
        spType = rootView.findViewById(R.id.spinnerType);
        ivImage = rootView.findViewById(R.id.ivImage);
        btnSelectDate = rootView.findViewById(R.id.selectDateButton);
        btnUploadImage = rootView.findViewById(R.id.btnSelectImage);
        btnSave = rootView.findViewById(R.id.btnSave);
        textDate = rootView.findViewById(R.id.textDate);

        // Retrieve arguments passed to this fragment
        Bundle args = getArguments();
        if (args == null || !args.containsKey("userId") || !args.containsKey("petId")) {
            Toast.makeText(getActivity(), "Missing user or pet information", Toast.LENGTH_SHORT).show();
            dismiss(); // Close the fragment if arguments are missing
            return rootView;
        }

        String userId = args.getString("userId");
        String petId = args.getString("petId");

        // Handle date selection
        btnSelectDate.setOnClickListener(v -> openDatePicker());

        // Handle image upload
        btnUploadImage.setOnClickListener(v -> openImagePicker());

        // Handle saving health record
        btnSave.setOnClickListener(v -> {
            if (userId == null || petId == null) {
                Toast.makeText(getActivity(), "User ID or Pet ID is missing", Toast.LENGTH_SHORT).show();
            } else {
                saveHealthRecord(userId, petId);
            }
        });

        return rootView;
    }


    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    String dateOfBirth = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    textDate.setText(dateOfBirth);

                    // Corrected Date initialization
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year, monthOfYear, dayOfMonth);
                    selectedDate = new Timestamp(selectedCal.getTime());
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                ivImage.setImageBitmap(bitmap);
                uploadImageToStorage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToStorage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference imageRef = storageRef.child("health_records/" + System.currentTimeMillis() + ".jpg");

        imageRef.putBytes(data).addOnSuccessListener(taskSnapshot ->
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrl = uri.toString();
                    Toast.makeText(getActivity(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                })
        ).addOnFailureListener(e -> Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT).show());
    }

    private void saveHealthRecord(String userId, String petId) {
        String title = etTitle.getText().toString().trim();
        String vetName = etVetName.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String type = spType.getSelectedItem().toString();

        if (title.isEmpty() || vetName.isEmpty() || selectedDate == null || notes.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        HealthRecord record = new HealthRecord(type, title, selectedDate, vetName, notes, imageUrl, "Completed", new Timestamp(new Date()));

        db.collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .collection("HealthRecords")
                .add(record)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Health record added", Toast.LENGTH_SHORT).show();
                    Log.d("AddHealthRecord", "Document ID: " + documentReference.getId());

                    // Redirect to HealthRecordActivity
                    Intent intent = new Intent(getActivity(), HealthRecordsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("petId", petId);
                    startActivity(intent);
                    if (getActivity() != null) getActivity().finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error adding health record", Toast.LENGTH_SHORT).show();
                    Log.e("AddHealthRecord", "Error: " + e.getMessage(), e);
                });
    }

}
