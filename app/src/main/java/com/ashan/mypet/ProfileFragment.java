package com.ashan.mypet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements PetAdapter.OnPetClickListener {

    // UI Components
    private TextView profileNameTextView, profileEmailTextView;
    private ImageView profilePictureImageView;
    private Button editProfileButton, addNewPetButton, logoutButton, changePasswordButton;
    private RecyclerView petRecyclerView;

    // Firebase components
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    // Pet List Components
    private PetAdapter petAdapter;
    private List<Pet> petList;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize UI elements
        profileNameTextView = view.findViewById(R.id.profile_name_textview);
        profileEmailTextView = view.findViewById(R.id.profile_email_textview);
        profilePictureImageView = view.findViewById(R.id.profile_picture_imageview);
        //editProfileButton = view.findViewById(R.id.edit_profile_button);
        addNewPetButton = view.findViewById(R.id.add_new_pet_button);
        logoutButton = view.findViewById(R.id.logout_button);
        changePasswordButton = view.findViewById(R.id.change_password_button);
        petRecyclerView = view.findViewById(R.id.pet_recycler_view);

        // Set up RecyclerView
        petRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        petList = new ArrayList<>();
        petAdapter = new PetAdapter(petList, this);
        petRecyclerView.setAdapter(petAdapter);

        // Load profile and pet data
        loadUserData();
        loadUserPets();

        // Set button listeners
        //editProfileButton.setOnClickListener(v -> navigateToActivity(AddpetActivity.class));
        addNewPetButton.setOnClickListener(v -> navigateToActivity(AddpetActivity.class));
        logoutButton.setOnClickListener(this::logoutUser);
        changePasswordButton.setOnClickListener(this::resetPassword);

        return view;
    }

    // Load user profile details
    private void loadUserData() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        String displayName = document.getString("username");
                        String email = document.getString("email");
                        String profilePictureUrl = document.getString("profilePicture");

                        profileNameTextView.setText(displayName != null ? displayName : "N/A");
                        profileEmailTextView.setText(email != null ? email : "N/A");

                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            Glide.with(this).load(profilePictureUrl).into(profilePictureImageView);
                        } else {
                            profilePictureImageView.setImageResource(R.drawable.ic_profile); // Fallback image
                        }
                    } else {
                        Toast.makeText(getContext(), "Error loading profile data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Load user pets from Firestore
    private void loadUserPets() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        db.collection("users").document(userId).collection("pets")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        petList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Pet pet = document.toObject(Pet.class);
                            if (pet != null) {
                                pet.setPId(document.getId());
                                petList.add(pet);
                            }
                        }
                        petAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error loading pets", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Logout user
    private void logoutUser(View view) {
        mAuth.signOut();
        navigateToActivity(LoginActivity.class);
        if (getActivity() != null) getActivity().finish();
    }

    // Send password reset email
    private void resetPassword(View view) {
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Password reset email sent.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    // Navigate to another activity
    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(getContext(), activityClass);
        startActivity(intent);
    }

    @Override
    public void onPetClick(Pet pet) {
        if (pet.getPId() != null && !pet.getPId().isEmpty()) {
            Intent intent = new Intent(getContext(), PetDetailsActivity.class);
            intent.putExtra("PET_ID", pet.getPId());
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Invalid Pet ID", Toast.LENGTH_SHORT).show();
        }
    }
}
